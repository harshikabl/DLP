package dlp.bluelupin.dlp.Utilities;

import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.Date;
import java.util.List;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.ContentData;
import dlp.bluelupin.dlp.Models.Data;
import dlp.bluelupin.dlp.Models.LanguageData;
import dlp.bluelupin.dlp.Models.ServiceDate;

/**
 * Created by subod on 10-Aug-16.
 */
public class CardReaderHelper {
    Context context;

    public CardReaderHelper(Context context) {
        this.context = context;
    }

    public void ReadAppDataFolder(String folderLocation) {

        File fileDirectory = new File(folderLocation);
        File[] dirFiles = fileDirectory.listFiles();
        for (File folder : dirFiles) {
            if (folder.isDirectory()) {
                readMetaDataJson(addTrailingSlash(folder.getPath()));
                //readMediaOfFolder(addTrailingSlash(folder.getPath()));
            }
        }
    }

    public void readMetaDataJson(String folderLocation) {

        FileDataReaderHelper fileReaderHelper = new FileDataReaderHelper(context);
        String fileContent = fileReaderHelper.ReadFileContentsFromFolder("metadata.json", folderLocation);
        // determine of the date of zip is recent than the latest service calls stored in database
        if (fileContent != null && fileContent != "") {
            ServiceDate serviceDate = new Gson().fromJson(fileContent, ServiceDate.class);
            if (serviceDate != null && serviceDate.getTimestamp() != "") {
                Date dataDate = Utility.parseDateFromString(serviceDate.getTimestamp());
                if (dataDate != null) {
                    DbHelper dbhelper = new DbHelper(context);
                    CacheServiceCallData cacheSeviceCallData = dbhelper.getCacheServiceCallByUrl(Consts.URL_CONTENT_LATEST);
                    if (cacheSeviceCallData != null) {
                        Date serviceLastcalledDate = Utility.parseDateFromString(cacheSeviceCallData.getLastCalled());
                        // parse data_item from zip ONLY if zip data_item is recent than last called service
                        if (dataDate.after(serviceLastcalledDate)) {
                            if (Consts.IS_DEBUG_LOG) {
                                Log.d(Consts.LOG_TAG, "CardReaderHelper: Starting reading folder " + folderLocation + " as dataDate:" + dataDate + " is after serviceLastcalledDate: " + serviceLastcalledDate);
                               // Toast.makeText(context,"Updated content found in directory " + folderLocation, Toast.LENGTH_LONG);
                            }

                            readFilesOfFolder(folderLocation);
                            readMediaOfFolder(folderLocation);


                        } else {
                            if (Consts.IS_DEBUG_LOG) {
                                Log.d(Consts.LOG_TAG, "CardReaderHelper: NOT reading folder " + folderLocation + "  as dataDate:" + dataDate + " is NOT after serviceLastcalledDate: " + serviceLastcalledDate);
                               // Toast.makeText(context,"No updated content found in directory "  + folderLocation, Toast.LENGTH_LONG);
                            }
                        }
                    }

                }
            }
        }

    }

    private void readMediaOfFolder(String folderLocation) {
        File fileDirectory = new File(folderLocation + "media");
        if (!fileDirectory.exists()) {
            if (Consts.IS_DEBUG_LOG)
                Log.d(Consts.LOG_TAG, "CardReaderHelper: readMediaOfFolder Folder does not exists: " + fileDirectory.getPath());
        }
        else {
            File[] dirFiles = fileDirectory.listFiles();

            if (dirFiles.length != 0) {
                for (int i = 0; i < dirFiles.length; i++) {
                    File file = new File(dirFiles[i].getPath());
                    DbHelper dbHelper = new DbHelper(context);
                    boolean isUpdate = dbHelper.updateMediaLanguageLocalFilePathBasedOnFilePath(dirFiles[i].getName(), dirFiles[i].getPath());
                    if (!isUpdate) {
                        dbHelper.updateMediaThumbnailLocalFilePathBasedOnName(dirFiles[i].getName(), dirFiles[i].getPath());
                    }
                }
            }
        }
    }



    private void readFilesOfFolder(String folderLocation)
    {
        File fileDirectory = new File(folderLocation);
        if (!fileDirectory.exists()) {
            if (Consts.IS_DEBUG_LOG)
                Log.d(Consts.LOG_TAG, "CardReaderHelper: ReadFilesOfFolder Folder does not exists: " + fileDirectory.getPath());
        }
        File[] dirFiles = fileDirectory.listFiles();

        if (dirFiles.length != 0) {
            FileDataReaderHelper fileReaderHelper = new FileDataReaderHelper(context);
            for (int i = 0; i< dirFiles.length; i++) {
               File file = new File(dirFiles[i].getPath());
                String fileExtension = Utility.getFileExtension(file.getName());
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "CardReaderHelper: Reading file " + file.getName() + " fileExtension:" + fileExtension);
                }
               if(fileExtension.equalsIgnoreCase("json"))
               {
                   String fileContent = fileReaderHelper.ReadFileContentsFromFolder("",file.getPath());
                   if(file.getName().contains("content")) {
                       ReadAndUpdateContentDataInDb(fileContent);
                   }
                   else
                   if(file.getName().contains("langresource")) {
                       ReadAndUpdateLanguageResourceDataInDb(fileContent);
                   }
                   else
                   if(file.getName().contains("media")) {
                       ReadAndUpdateMediaInDb(fileContent);
                   }
                   else
                   if(file.getName().contains("medialanguage")) {
                       ReadAndUpdateMediaLanguageInDb(fileContent);
                   }
                   // also read all subfolders and images with in them


               }
                if (Consts.IS_DEBUG_LOG) {
                    Log.d(Consts.LOG_TAG, "CardReaderHelper: NOT Reading file as NOT json extension" + file.getName() + " fileExtension:" + fileExtension);
                }
               // File renamedFile = new File(file.getPath() + "__");
                //file.renameTo(renamedFile);
            }
        }
    }

    private void ReadAndUpdateContentDataInDb(String fileContent) {
//        ContentData contentData = new Gson().fromJson(fileContent, ContentData.class);
        List<Data> datas = new Gson().fromJson(fileContent, new TypeToken<List<Data>>() {}.getType());
        final DbHelper dbhelper = new DbHelper(context);
        if (datas != null) {
            for (Data data : datas) {
                dbhelper.upsertDataEntity(data);
            }
        }
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "Success: ReadAndUpdateContentDataInDb");
        }
    }

    private void ReadAndUpdateLanguageResourceDataInDb(String fileContent) {
        List<Data> datas = new Gson().fromJson(fileContent, new TypeToken<List<Data>>() {}.getType());
        final DbHelper dbhelper = new DbHelper(context);
        if (datas != null) {
            for (Data data : datas) {
                dbhelper.upsertResourceEntity(data);
            }
        }
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "Success: ReadAndUpdateLanguageResourceDataInDb");
        }
    }

    private void ReadAndUpdateMediaInDb(String fileContent) {
        List<Data> datas = new Gson().fromJson(fileContent, new TypeToken<List<Data>>() {}.getType());
        final DbHelper dbhelper = new DbHelper(context);
        if (datas != null) {
            for (Data data : datas) {
                dbhelper.upsertMediaEntity(data);
            }
        }
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "Success: ReadAndUpdateMediaInDb");
        }
    }

    private void ReadAndUpdateMediaLanguageInDb(String fileContent) {
        List<Data> datas = new Gson().fromJson(fileContent, new TypeToken<List<Data>>() {}.getType());
        final DbHelper dbhelper = new DbHelper(context);
        if (datas != null) {
            for (Data data : datas) {
                dbhelper.upsertMedialanguageLatestDataEntity(data);
            }
        }
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "Success: ReadAndUpdateMediaLanguageInDb");
        }
    }

    public Boolean readDataFromSDCard(String locationOnSdCard) {
        Boolean operationSuccess = false;
        // Determine if input data_item exists
        String strInputLocation = Consts.SDPath + locationOnSdCard;
        File inputLocation = new File(strInputLocation);
        if (!inputLocation.exists() || !inputLocation.isDirectory()) {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard Path DOES NOT EXISTS!!" + inputLocation.getPath());
            }
            return false;
        } else {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard scanning path for zips" + inputLocation.getPath());
            }
        }
        // Determine if output path exists
        String strUnzipLocation = Consts.outputDirectoryLocation;
        File unzipLocation = new File(strUnzipLocation);
        if (!unzipLocation.exists()) {
            unzipLocation.mkdirs();
        }
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard: output unzip directory is at: " + unzipLocation.getPath());
        }
        // Read all .zip files in the source directory
        String[] zipFiles = inputLocation.list();
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard: Total Zip files: " + zipFiles.length);
        }
        for (int i = 0; i < zipFiles.length; i++) {
            String zipFilePath = addTrailingSlash(inputLocation.getPath()) + zipFiles[i];
            DecompressZipFile decompressZipFile = new DecompressZipFile(context);
            String strUnzipLocationOfZipFile = addTrailingSlash(unzipLocation.getPath()) + addTrailingSlash(removeExtension(zipFiles[i]));
            File unzipLocationOfZipFile = new File(strUnzipLocationOfZipFile);
            if (!unzipLocation.exists()) {
                unzipLocationOfZipFile.mkdirs();
            }
            strUnzipLocationOfZipFile = addTrailingSlash(unzipLocationOfZipFile.getPath());
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard: unzipping file: " + zipFilePath + " to location: " + strUnzipLocationOfZipFile);
            }
            ReadAndExtractZipFileBasedOnDate(zipFilePath, decompressZipFile, strUnzipLocationOfZipFile);

        }
        return operationSuccess;
    }

    private void ReadAndExtractZipFileBasedOnDate(String zipFilePath, DecompressZipFile decompressZipFile, String strUnzipLocationOfZipFile) {
        // read the metadata file from zip file
        String fileContent = decompressZipFile.getFileContentFromZip(zipFilePath, "metadata.json");
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard: metadata.json: " + fileContent);
        }
        // determine of the date of zip is recent than the latest service calls stored in database
        if (fileContent != null && fileContent != "") {
            ServiceDate serviceDate = new Gson().fromJson(fileContent, ServiceDate.class);
            if (serviceDate != null && serviceDate.getTimestamp() != "") {
                Date dataDate = Utility.parseDateFromString(serviceDate.getTimestamp());
                if (dataDate != null) {
                    DbHelper dbhelper = new DbHelper(context);
                    CacheServiceCallData cacheSeviceCallData = dbhelper.getCacheServiceCallByUrl(Consts.URL_CONTENT_LATEST);
                    if (cacheSeviceCallData != null) {
                        Date serviceLastcalledDate = Utility.parseDateFromString(cacheSeviceCallData.getLastCalled());
                        // parse data_item from zip ONLY if zip data_item is recent than last called service
                        if (dataDate.after(serviceLastcalledDate)) {
                            if (Consts.IS_DEBUG_LOG) {
                                Log.d(Consts.LOG_TAG, "CardReaderHelper: Starting decompressZipFile unzip as dataDate:" + dataDate + " is after serviceLastcalledDate: " + serviceLastcalledDate);
                            }

                            decompressZipFile.unzip(zipFilePath, strUnzipLocationOfZipFile);
                        } else {
                            if (Consts.IS_DEBUG_LOG) {
                                Log.d(Consts.LOG_TAG, "CardReaderHelper: NOT DOING decompressZipFile unzip as dataDate:" + dataDate + " is NOT after serviceLastcalledDate: " + serviceLastcalledDate);
                            }
                        }
                    }
                }
            }
        }
    }

    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfExtension(filename);

        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    private static final char EXTENSION_SEPARATOR = '.';
    private static final char DIRECTORY_SEPARATOR = '/';

    public static int indexOfExtension(String filename) {

        if (filename == null) {
            return -1;
        }

        // Check that no directory separator appears after the
        // EXTENSION_SEPARATOR
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);

        int lastDirSeparator = filename.lastIndexOf(DIRECTORY_SEPARATOR);

        if (lastDirSeparator > extensionPos) {
            return -1;
        }

        return extensionPos;
    }

    public String addTrailingSlash(String path) {
        if (path.length() > 0) {
            if (path.charAt(path.length() - 1) != '/') {
                path += "/";
            }
        }
        return path;
    }

    public String addLeadingSlash(String path) {
        if (path.charAt(0) != '/') {
            path = "/" + path;
        }
        return path;
    }
}
