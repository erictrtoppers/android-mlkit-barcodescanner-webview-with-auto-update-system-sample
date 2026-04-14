package com.example.barcodewebapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewJavaScriptInterface extends AppCompatActivity {

    private Context context;

    private Activity activity;

    private WebView webView; // Reference stored here

    /*
     * Need a reference to the context in order to sent a post message
     */
    public WebViewJavaScriptInterface(Context context, Activity activity, WebView webView){
        this.context = context;
        this.activity = activity;
        this.webView = webView;
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

    @JavascriptInterface
    public void doAppLogout() {
        activity.runOnUiThread(() -> {
            // We pass null because we are going to handle the logic manually
            // in a custom way that doesn't trigger the "Restricted" check
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).handlePostLogoutManually();
            }
        });
    }

    @JavascriptInterface
    public void rerenderLayout() {
        this.webView.post(() -> {
            this.webView.requestLayout();
            this.webView.invalidate();
        });
    }

    @JavascriptInterface
    public void print() {
        // Must run on the UI thread to access WebView and PrintManager
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);

                // This creates the adapter that converts the current web view content into a PDF for the printer
                PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("3x3_Label_Job");

                String jobName = "Label Print";

                if (printManager != null) {
                    printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
                }
            }
        });
    }

}
