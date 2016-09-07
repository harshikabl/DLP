package dlp.bluelupin.dlp.Utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.ContentData;
import dlp.bluelupin.dlp.Models.Data;

/**
 * Created by Neeraj on 8/3/2016.
 */
public class DecompressZipFile {

    private Context context;

    public DecompressZipFile(Context context) {

        this.context = context;

        //_dirChecker("");
    }

    public void unzip(String zipFile, String strExtractLocation) {
        try {
            File f = new File(zipFile);

            if (!f.exists()) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "zip file NOT located at: " + zipFile);
                }
            }
            File extractLocation = new File(strExtractLocation);

            if (!extractLocation.exists()) {
                extractLocation.mkdirs();
            }
            FileInputStream fin = new FileInputStream(f.getPath());
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;

            while ((ze = zin.getNextEntry()) != null) {
                //byte[] buffer = new byte[1024];
                if (ze.isDirectory()) {

                    _dirChecker(extractLocation.getPath(), ze.getName() + "/");
                } else {
                    String localFilePath;
                    String fileName = ze.getName();
                    if (fileName.contains(".json")) {
                        localFilePath = addTrailingSlash(extractLocation.getPath()) + fileName;
                    } else {
                        String[] file = fileName.split("/");

                        localFilePath = addTrailingSlash(extractLocation.getPath()) + file[1];
                    }
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "Unzipping " + ze.getName() + " to " + localFilePath);
                    }
                    FileOutputStream fout = new FileOutputStream(localFilePath);
                    if (fout != null) {
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }
                    }
                    zin.closeEntry();
                    fout.close();
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "file unzipped at " + localFilePath);
                    }
                }
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "Zip file extracted successfully: " + ze.getName());
                }
            }
            zin.close();
            //copyZipDataIntoDatabase();
        } catch (Exception e) {
            Log.e("Decompress", "unzip", e);
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "DecompressZipFile: unzip " + e);
            }
        }
    }

    public String getFileContentFromZip(String zipFile, String inputFileName) {
        String fileContent = null;
        try {
            File f = new File(zipFile);

            if (!f.exists()) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "zip file NOT located at: " + zipFile);
                }
            }

            FileInputStream fin = new FileInputStream(f.getPath());
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            byte[] buffer = new byte[1024];
            Boolean fileFound = true;
            while ((ze = zin.getNextEntry()) != null) {

                if (!ze.isDirectory()) {
                    String localFilePath;
                    String fileName = ze.getName();

                    if (inputFileName.equalsIgnoreCase(fileName)) {
                        if (Consts.IS_DEBUG_LOG) {
                            Log.d(Consts.LOG_TAG, "reading the file " + ze.getName());
                        }
                        byte[] bytes = new byte[(int) ze.getSize()];
                        zin.read(bytes, 0, bytes.length);
                        fileContent = new String(bytes, "UTF-8");
                        break; // file found; break the while loop...
                    }
                }
            }
            zin.close();
        } catch (Exception e) {
            Log.e("DecompressZipFile", "getFileContentFromZip", e);
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "DecompressZipFile: getFileContentFromZip " + e);
            }
        }
        return fileContent;
    }


    //copy data into database
    public void copyZipDataIntoDatabase() {
        String zipFile = Environment.getExternalStorageDirectory() + "/DlpContentUnzipped/content.json";
        String strContentData = "";
        StringBuilder builder = null;
        try {
            FileInputStream fis = new FileInputStream(zipFile);
            InputStream is = fis;
            builder = new StringBuilder();
            int ch;
            while ((ch = fis.read()) != -1) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "ch : " + ch);
                }
                builder = builder.append((char) ch);
            }

            int size = is.available();
            Log.d(Consts.LOG_TAG, "size : " + builder.toString().length());
           /* byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();*/
            //strContentData = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "builder : " + builder.toString());

        }
        ContentData contentData = new Gson().fromJson(builder.toString(), ContentData.class);
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "data: " + contentData.toString());
        }
        final DbHelper dbhelper = new DbHelper(context);
        if (contentData != null) {
            for (Data data : contentData.getData()) {
                //dbhelper.upsertDataEntity(data);
            }
        }
    }

    public void _dirChecker(String location, String dir) {
        File f = new File(location + dir);
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "_dirchecker. creating directory: " + f.getPath());
        }
        //if (!f.isDirectory()) {
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public String addTrailingSlash(String path) {
        if (path.length() > 0) {
            if (path.charAt(path.length() - 1) != '/') {
                path += "/";
            }
        }
        return path;
    }

}

