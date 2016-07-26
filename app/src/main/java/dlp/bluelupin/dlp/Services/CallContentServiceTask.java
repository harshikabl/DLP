package dlp.bluelupin.dlp.Services;

import android.content.Context;
import android.os.AsyncTask;

import dlp.bluelupin.dlp.Models.ContentData;
import dlp.bluelupin.dlp.Models.ContentServiceRequest;

/**
 * Created by subod on 22-Jul-16.
 */
public class CallContentServiceTask extends AsyncTask<ContentServiceRequest, Integer, String>{
    IServiceManager service;
    Context context;
    public CallContentServiceTask() {
        super();
    }

    public CallContentServiceTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(ContentServiceRequest... params) {
        ContentServiceRequest request = params[0];
        ServiceHelper sh = new ServiceHelper(context);
        sh.callContentService(request, new IServiceSuccessCallback<ContentData>() {
            @Override
            public void onDone(String callerUrl, ContentData result, String error) {
                if (result != null) {
                   if(result.getCurrent_page() <= result.getLast_page())
                   {
                       //request.
                   }
                } else {

                }
            }
        });

        return "completed";
    }



    @Override
    protected void onPreExecute() {
        //super.onPreExecute();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
