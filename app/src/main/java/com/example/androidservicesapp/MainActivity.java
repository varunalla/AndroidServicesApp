package com.example.androidservicesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    boolean isRunning=false;
    boolean mBound=false;
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DownloaderService.MessageBinder binder = (DownloaderService.MessageBinder) service;
            DownloaderService mService = binder.getService();
            binder.setListener(new BoundServiceListener() {

                @Override
                public void sendProgress(String message,int fileNumber) {
                    // Use this method to update our download progress
                    runOnUiThread(()->{
                        ProgressBar bar=(findViewById(R.id.progressBar));
                        bar.setProgress(bar.getProgress()+20,true);
                        Toast.makeText(mService, message, Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void finishedDownloading() {

                    runOnUiThread(()->{
                        Toast.makeText(mService, "Files Downloaded", Toast.LENGTH_SHORT).show();
                        Button btn=(findViewById(R.id.button));
                        btn.setText(R.string.pd_act_btn_name);
                        stopCustomService();
                        isRunning=false;
                    });
                }
            });

            mBound = true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("PDF Download Activity");
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        File deleteFiles = getFilesDir();
        deleteDir(deleteFiles);
    }
    private static boolean deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            if(children!=null)
            for (String child:children)
            {
                boolean success = deleteDir(new File(dir, child));
                if (!success)
                {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return true;
    }
    private ArrayList<URL> fetchUrls(){
        ArrayList<URL> urls=new ArrayList<>();
        ArrayList<EditText> textEdits=new ArrayList<>();
        try {
            EditText url1 =findViewById(R.id.editUrlPDF1);
            EditText url2 =  findViewById(R.id.editUrlPDF2);
            EditText url3 =findViewById(R.id.editUrlPDF3);
            EditText url4 =  findViewById(R.id.editUrlPDF4);
            EditText url5 = findViewById(R.id.editUrlPDF5);
            textEdits.add(url1);
            textEdits.add(url2);
            textEdits.add(url3);
            textEdits.add(url4);
            textEdits.add(url5);
            for(EditText eT:textEdits){
                urls.add(new URL(eT.getText().toString()));
            }
        }
        catch(Exception err){
            urls=new ArrayList<>();
            System.out.println("Error ");
            err.printStackTrace();
        }
        return urls;
    }
    public void startDownload(View view){

        Context context = getApplicationContext();

        Button btn=(findViewById(R.id.button));
        // use this to start and trigger a service
        Intent i= new Intent(context, DownloaderService.class);
        if(!isRunning){
            // potentially add data to the intent
            ArrayList<URL> urls=fetchUrls();
            i.putExtra("urls",urls);
            context.startService(i);
            bindService(i, mConnection, Context.BIND_AUTO_CREATE);

            btn.setText(R.string.pd_act_btn_name_downloading);

            ProgressBar bar=(findViewById(R.id.progressBar));
            bar.setProgress(0,true);
            isRunning=true;
        }
        else{
            context.stopService(i);
            btn.setText(R.string.pd_act_btn_name);
            isRunning=false;
        }
    }
    public void stopCustomService(){
        Context context = getApplicationContext();
        Intent i= new Intent(context, DownloaderService.class);
        context.stopService(i);
    }

}