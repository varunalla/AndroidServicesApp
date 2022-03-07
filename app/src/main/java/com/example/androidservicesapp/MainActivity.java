package com.example.androidservicesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int requestCount=0;
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
            new Thread(new FileDownloader(urls.get(i),i+1)).start();
    }
    class FileDownloader implements Runnable{
        int i;
        public URL url;
        public FileDownloader(URL url, int i){
            this.url=url;
            this.i=i;
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
                    File sdcard = getFilesDir();
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
                        Log.i("status","Downloaded File "+file.getName());
                        runOnUiThread(() -> {
                            synchronized (FileDownloader.class){
                                ProgressBar bar=(findViewById(R.id.progressBar));
                                bar.setProgress(bar.getProgress()+20,true);
                                EditText editText=( findViewById(R.id.status));
                                String message=getString(R.string.sdownload,file.getName());
                                editText.setText(message);
                            }
                        });
                    }
                    else{
                        Log.i("status","Could not downloaded File "+file.getName());
                        runOnUiThread(() -> {
                            synchronized (FileDownloader.class){
                                EditText editText=( findViewById(R.id.status));
                                String message=getString(R.string.fdownload,file.getName());
                                editText.setText(message);
                            }
                        });
                    }

                } catch (IOException e) {
                    Log.i("status","Failed to Download File ");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(urlConnection!=null)
                        urlConnection.disconnect();
                }
                runOnUiThread(() -> {
                    synchronized (FileDownloader.class){
                        requestCount++;
                        if(requestCount==5)
                        {
                            Button btn=(findViewById(R.id.button));
                            btn.setText(R.string.pd_act_btn_name);
                            btn.setEnabled(true);
                            EditText editText=( findViewById(R.id.status));
                            String message=getString(R.string.finaldownload);
                            editText.setText(message);
                        }
                    }
                });

        }
    }

}