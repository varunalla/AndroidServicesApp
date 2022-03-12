package com.example.androidservicesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    int requestCount=0;
    boolean isRunning=false;

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
            //i.putExtra("KEY1", "Value to be used by the service");
            context.startService(i);
            btn.setText(R.string.pd_act_btn_name_downloading);
            //btn.setEnabled(false);
            isRunning=true;
        }
        else{
            context.stopService(i);
            btn.setText(R.string.pd_act_btn_name);
            isRunning=false;
        }


        /*
        ProgressBar bar=(findViewById(R.id.progressBar));
        bar.setProgress(0,true);
        Button btn=(findViewById(R.id.button));
        btn.setText(R.string.pd_act_btn_name_downloading);
        btn.setEnabled(false);
        EditText editText=(findViewById(R.id.status));
        editText.setText("");
        ArrayList<URL> urls=fetchUrls();
        this.requestCount=0;
        for(int i=0;i<urls.size();i++)
            new Thread(new FileDownloader(urls.get(i),i+1)).start();*/
    }

}