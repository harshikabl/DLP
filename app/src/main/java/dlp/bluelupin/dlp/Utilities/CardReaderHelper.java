package dlp.bluelupin.dlp.Utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.Date;

import dlp.bluelupin.dlp.Consts;
import dlp.bluelupin.dlp.Database.DbHelper;
import dlp.bluelupin.dlp.Models.CacheServiceCallData;
import dlp.bluelupin.dlp.Models.ContentData;
import dlp.bluelupin.dlp.Models.ServiceDate;

/**
 * Created by subod on 10-Aug-16.
 */
public class CardReaderHelper {
    Context context;
    public CardReaderHelper(Context context)
    {
        this.context = context;
    }
    private static String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();


    private static String outputBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String outputDirectoryLocation = outputBasePath + "/dlpUnzipped/";

    public Boolean readDataFromSDCard(String locationOnSdCard)
    {
        Boolean operationSuccess = false;
        // Determine if input data exists
        String strInputLocation = SDPath + locationOnSdCard;
        File inputLocation = new File(strInputLocation);
        if(!inputLocation.exists() || !inputLocation.isDirectory())
        {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard Path DOES NOT EXISTS!!" + inputLocation.getPath());
            }
            return false;
        }
        else
        {
            if (Consts.IS_DEBUG_LOG) {
                Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard scanning path for zips" + inputLocation.getPath());
            }
        }
        // Determine if output path exists
        String strUnzipLocation = outputDirectoryLocation;
        File unzipLocation = new File(strUnzipLocation);
        if(!unzipLocation.exists()) {
            unzipLocation.mkdirs();
        }
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard: output unzip directory is at: " + unzipLocation.getPath());
        }
        // Read all .zip files in the source directory
        String[] zipFiles =  inputLocation.list();
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard: Total Zip files: " + zipFiles.length);
        }
        for (int i = 0; i < zipFiles.length ; i++)
        {
            String zipFilePath = addTrailingSlash(inputLocation.getPath()) + zipFiles[i];
            DecompressZipFile decompressZipFile = new DecompressZipFile(context);
            String strUnzipLocationOfZipFile = addTrailingSlash(unzipLocation.getPath()) + addTrailingSlash(removeExtension(zipFiles[i]));
            File unzipLocationOfZipFile = new File(strUnzipLocationOfZipFile);
            if(!unzipLocation.exists()) {
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
        String fileContent = decompressZipFile.getFileContentFromZip(zipFilePath,"metadata.json");
        if (Consts.IS_DEBUG_LOG) {
            Log.d(Consts.LOG_TAG, "CardReaderHelper: readDataFromSDCard: metadata.json: " + fileContent);
        }
        // determine of the date of zip is recent than the latest service calls stored in database
        if(fileContent!= null && fileContent !="")
        {
            ServiceDate serviceDate =  new Gson().fromJson(fileContent, ServiceDate.class);
            if(serviceDate!= null && serviceDate.getTimestamp() !="") {
                Date dataDate = Utility.parseDateFromString(serviceDate.getTimestamp());
                if(dataDate != null)
                {
                    DbHelper dbhelper = new DbHelper(context);
                    CacheServiceCallData cacheSeviceCallData = dbhelper.getCacheServiceCallByUrl(Consts.URL_CONTENT_LATEST);
                    if (cacheSeviceCallData != null) {
                        Date serviceLastcalledDate = Utility.parseDateFromString(cacheSeviceCallData.getLastCalled());
                        // parse data from zip ONLY if zip data is recent than last called service
                        if(dataDate.after(serviceLastcalledDate)) {
                            if (Consts.IS_DEBUG_LOG) {
                                Log.d(Consts.LOG_TAG, "CardReaderHelper: Starting decompressZipFile unzip as dataDate:" + dataDate + " is after serviceLastcalledDate: " + serviceLastcalledDate);
                            }

                            decompressZipFile.unzip(zipFilePath, strUnzipLocationOfZipFile);
                        }
                        else
                        {
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
    public String addTrailingSlash(String path)
    {
        if (path.length()>0) {
            if ( path.charAt(path.length() - 1) != '/') {
                path += "/";
            }
        }
        return path;
    }

    public String addLeadingSlash(String path)
    {
        if (path.charAt(0) != '/')
        {
            path = "/" + path;
        }
        return path;
    }
}
