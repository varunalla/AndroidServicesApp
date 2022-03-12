package com.example.androidservicesapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloaderService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // The service is being created

    }

    @Override
    @SuppressWarnings("unchecked")
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
        if(intent!=null){
           ArrayList<URL> urls=(ArrayList<URL>) intent.getSerializableExtra("urls");
           for(int i=0;i<urls.size();i++){
               new Thread(new FileDownloader(this.getBaseContext(),urls.get(i),i,this)).start();
               Toast.makeText(this, "Downloading "+urls.get(i).getPath(), Toast.LENGTH_SHORT).show();
           }
        }
        return Service.START_STICKY;
    }

    @Override 
    public  void onDestroy(){
        Toast.makeText(this, "Download Stopped", Toast.LENGTH_SHORT).show();
    }
    public void sendProgress(String message){
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        System.out.println(message);
    }
}

class FileDownloader implements Runnable{
    int i;
    public URL url;
    Context context;
    DownloaderService ds;
    public FileDownloader(Context context, URL url, int i,DownloaderService ds){
        this.url=url;
        this.i=i;
        this.context=context;
        this.ds=ds;
    }
    public HttpURLConnection setConnectionProperties(HttpURLConnection urlConnection) throws Exception{
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(false);
        urlConnection.addRequestProperty("Content-Type","application/octet-stream");
        urlConnection.addRequestProperty("Accept","*/*");
        urlConnection.addRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36");
        urlConnection.addRequestProperty("Accept-Encoding","gzip, deflate, br");
        urlConnection.addRequestProperty("Connection","keep-alive");
        urlConnection.addRequestProperty("Host",this.url.getHost());
        urlConnection.setUseCaches(false);
        return urlConnection;
    }
    @Override
    public void run() {

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection=this.setConnectionProperties(urlConnection);
            urlConnection.connect();
            File sdcard = this.context.getFilesDir();
            File new_file=new File(url.getFile());
            File file = new File(sdcard, new_file.getName());
            FileOutputStream fileOutput = new FileOutputStream(file);
            Log.i("responseCode",file.getName()+" : "+urlConnection.getResponseCode());
            InputStream inputStream =urlConnection.getResponseCode()==200? urlConnection.getInputStream():urlConnection.getErrorStream();
            if(urlConnection.getResponseCode()==200) {

                byte[] buffer = new byte[1024];
                int bufferLength;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                }
                fileOutput.close();
                //Log.i("status","Downloaded File "+file.getName());
                ds.sendProgress("Downloaded File "+file.getName());
            }
            else{
              //  Log.i("status","Could not downloaded File "+file.getName());
                ds.sendProgress("Could not downloaded File "+file.getName());
            }
        } catch (IOException e) {
          //  Log.i("status","Failed to Download File ");
            ds.sendProgress("Failed to Download File ");

            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(urlConnection!=null)
                urlConnection.disconnect();
        }
    }
}

