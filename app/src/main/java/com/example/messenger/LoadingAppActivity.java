package com.example.messenger;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import static android.view.View.VISIBLE;

public class LoadingAppActivity extends AppCompatActivity {

    private ProgressBar pgsBar;
    ProgressTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_app);

        pgsBar = (ProgressBar)findViewById(R.id.pBar);

        pgsBar.setVisibility(View.VISIBLE);
        showProgress();

    }

    private class ProgressTask extends AsyncTask<Integer,Integer,Void> {

        protected void onPreExecute() {
            pgsBar.setMax(100);
        }
        protected void onCancelled() {
            pgsBar.setMax(0);
        }
        protected Void doInBackground(Integer... params)
        {
            int start=params[0];
            for(int i=start;i<=100;i+=5){
                try {
                    boolean cancelled=isCancelled();
                    if(!cancelled){
                        publishProgress(i);
                        Log.v("Progress","increment " + i);
                        onProgressUpdate(i);
                        SystemClock.sleep(300);
                    }
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                }
            }
            return null;
        }

        protected void onProgressUpdate(Integer values)
        {
            setProgress(10);
        }

        protected void onPostExecute(Void result)
        {
            Log.v("Progress", "Finished");
            finish();
        }

    }

    @Override
    public void onBackPressed() {

    }

    public void showProgress() {
        task = new ProgressTask();
        task.execute(10);

    }
}
