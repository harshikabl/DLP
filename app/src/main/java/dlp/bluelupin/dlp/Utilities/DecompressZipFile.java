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
    private String zipFile;
    private String location;
    private Context context;

    public DecompressZipFile(String zipFile, String location, Context context) {
        this.zipFile = zipFile;
        this.location = location;
        this.context = context;

        //_dirChecker("");
    }

    public void unzip() {
        try {
            File f = new File(zipFile);

            if (!f.exists()) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "zip file NOT located at: " + zipFile);
                }
            }
            FileInputStream fin = new FileInputStream(zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            byte[] buffer = new byte[1024];
            while ((ze = zin.getNextEntry()) != null) {
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "Unzipping " + ze.getName());
                }
                if (ze.isDirectory()) {
                    _dirChecker(ze.getName() + "/");
                } else {
                    String localFilePath;
                    String fileName = ze.getName();
                    if (fileName.contains(".json")) {
                        localFilePath = location + fileName;
                    } else {
                        String[] file = fileName.split("/");

                        localFilePath = location + file[1];
                    }
                    FileOutputStream fout = new FileOutputStream(localFilePath);
                    if (fout != null) {
                        for (int c = zin.read(buffer); c != -1; c = zin.read()) {
                            fout.write(c);
                        }
                    }
                    zin.closeEntry();
                    fout.close();
                    if (Consts.IS_DEBUG_LOG) {
                        Log.d(Consts.LOG_TAG, "file unzipped at " + localFilePath);
                    }
                }
            }
            zin.close();
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "json file read successfully: " + ze.getName());
            }
            copyZipDataIntoDatabase();
        } catch (Exception e) {
            Log.e("Decompress", "unzip", e);
        }

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
            while((ch = fis.read()) != -1){
                if(Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "ch : " + ch);
                }
                builder=builder.append((char)ch);
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

    public void _dirChecker(String dir) {
        File f = new File(location + dir);
        if(Consts.IS_DEBUG_LOG)
        {
            Log.d(Consts.LOG_TAG, "_dirchecker. creating directory: " + f.getPath());
        }
        //if (!f.isDirectory()) {
        if (!f.exists()) {
            f.mkdirs();
        }
    }
}

