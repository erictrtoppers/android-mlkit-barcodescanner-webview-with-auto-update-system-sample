package com.example.barcodewebapp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Helpers {
    public static String VersionCode = "2";

    public static String GetTextFromUrl(String link){
        ArrayList<String> al=new ArrayList<>();

        try{
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.connect();

            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;

            try {
                while ((line = br.readLine()) != null) {
                    al.add(line);
                }
            } finally {
                br.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        if(al.isEmpty()) {
            return "";
        }

        return al.get(0).toString();
    }

    public static void DownloadFile(String title, String url, Context context) throws IOException {
        File directory = context.getFilesDir();

        if (!directory.exists()) {
            directory.mkdir();
        }

        File fileName = new File(directory,title);

        if(fileName.exists()){
            fileName.delete();
        }

        if(!fileName.exists()){
            fileName.createNewFile();
        }

        URL u = new URL(url);

        HttpURLConnection connect = (HttpURLConnection) u.openConnection();

        if (connect.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.w("ERROR RETURNED HTTP", connect.getResponseCode() + "");
        }

        try (InputStream is = connect.getInputStream();
             FileOutputStream fos = new FileOutputStream(fileName);) {
            byte[] bytes = new byte[1024];

            int b = 0;

            while ((b = is.read(bytes, 0, 1024)) != -1) {
                fos.write(bytes, 0, b);
            }
        }
    }
}
