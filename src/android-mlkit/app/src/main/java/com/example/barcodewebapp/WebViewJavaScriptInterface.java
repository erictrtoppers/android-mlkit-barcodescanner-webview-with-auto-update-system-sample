package com.example.barcodewebapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewJavaScriptInterface extends AppCompatActivity {

    private Context context;

    private Activity activity;

    /*
     * Need a reference to the context in order to sent a post message
     */
    public WebViewJavaScriptInterface(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    @JavascriptInterface
    public void initBarcodeScan(){
        try {
            Intent intent = new Intent(context, BarcodeScanningActivity.class);
            this.activity.startActivityForResult(intent, 1);
        }catch(Exception e){
            String error = e.toString();
        }
    }

}
