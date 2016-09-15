package dlp.bluelupin.dlp.Utilities;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import dlp.bluelupin.dlp.Consts;

/**
 * Created by subod on 09-Sep-16.
 */
public class FileDataReaderHelper {
    private Context context;

    public FileDataReaderHelper(Context context) {
        this.context = context;
    }


    public String ReadFileContentsFromFolder(String inputFileName, String location) {
        String fileContents = null;
        String filePath = location + inputFileName;
        File inputFile = new File(filePath);
        if (inputFile.exists()) {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(inputFile);
                fileContents = convertStreamToString(fin);
                fin.close();
            } catch (Exception ex) {
                if (Consts.IS_DEBUG_LOG)
                    Log.d(Consts.LOG_TAG, "FileDataReaderHelper: ReadFileContentsFromFolder File does not exists: " + filePath);
            }

            String newFileName = Utility.getFileNameWithoutExtension(inputFile.getPath());
            File renamedFile = new File(newFileName);
            inputFile.renameTo(renamedFile);

            if (Consts.IS_DEBUG_LOG)
                Log.d(Consts.LOG_TAG, "FileDataReaderHelper: Renaming file to __: " + inputFile.getPath());
        } else {
            if (Consts.IS_DEBUG_LOG)
                Log.d(Consts.LOG_TAG, "FileDataReaderHelper: ReadFileContentsFromFolder File does not exists: " + filePath);
        }
        return fileContents;
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
