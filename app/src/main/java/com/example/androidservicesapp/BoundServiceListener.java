package com.example.androidservicesapp;

public interface BoundServiceListener {
     void sendProgress(String fileName,int fileId);
     void finishedDownloading();
}
