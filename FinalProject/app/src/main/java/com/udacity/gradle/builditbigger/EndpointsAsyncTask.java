package com.udacity.gradle.builditbigger;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;

public class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
    private static MyApi myApiService = null;
    private ApiResponseHandler mApiResponseHandler;
    private Exception mException;

    public void setApiResponseHandler(ApiResponseHandler apiResponseHandler){
        mApiResponseHandler = apiResponseHandler;
    }

    @Override
    protected String doInBackground(Void... params) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }
        String joke = null;

        try {
            joke = myApiService.getJokes().execute().getData();
            return joke;
        } catch (IOException e) {
            mException = e;
            return joke;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if(TextUtils.isEmpty(result) &&mException==null){
            mApiResponseHandler.onSuccess(result);
        } else{
            mApiResponseHandler.onFailure(mException);
        }
    }
}