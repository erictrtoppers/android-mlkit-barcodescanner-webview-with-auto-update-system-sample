package com.example.barcodewebapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.barcodewebapp.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private Boolean debugMode = true;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    public WebView mainWebView;
    public WebView mainWebView2;

    public MenuItem view1;

    public MenuItem view2;

    public String deviceName;
    public String deviceId;
    public String deviceMan;

    public Menu optionsMenu;

    public LinearLayout mainMenu;
    public LinearLayout shortMenu;

    public TextView shortMenuText;

    public ImageView logo;

    public int TimesLogoTapped = 0;

    public MenuItem testAreaMenuItem;

    public String urlBase = "https://dinofly.com/android/barcode_example_webapp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set thread policy:
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        deviceName = android.os.Build.MODEL;
        deviceMan = android.os.Build.MANUFACTURER;
        deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        mainWebView = (WebView) findViewById(R.id.mainWebView);
        mainWebView2 = (WebView) findViewById(R.id.mainWebView2);

        mainMenu = (LinearLayout) findViewById(R.id.mainToolbarLinLayout);

        shortMenu = (LinearLayout) findViewById(R.id.subtoolbar);
        shortMenuText = (TextView) findViewById(R.id.subtoolbarTextView);
        logo = (ImageView) findViewById(R.id.logoimage);

        shortMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shortMenuText.getText().toString().equals(getResources().getString(R.string.show_menu))){
                    mainMenu.setVisibility(View.VISIBLE);
                    shortMenuText.setText(getString(R.string.hide_menu));
                }else{
                    mainMenu.setVisibility(View.GONE);
                    shortMenuText.setText(getString(R.string.show_menu));
                }
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimesLogoTapped++;
                if(TimesLogoTapped == 3){
                    testAreaMenuItem.setVisible(true);
                }
            }
        });

        if(!deviceMan.toUpperCase().equals("CHAINWAY")){
            mainMenu.setVisibility(View.VISIBLE);
            shortMenu.setVisibility(View.GONE);
        }

        checkCameraPermissions(this);

        CheckForUpdates(this);

        setupWebView(mainWebView);
        setupWebView(mainWebView2);
        mainWebView.requestFocus();
        mainWebView.requestFocusFromTouch();

        if (debugMode) {
            Toast.makeText(MainActivity.this, "Model is " + deviceName + " and manufacturer is " + deviceMan,
                    Toast.LENGTH_LONG).show();
        }
    }

    public WebView getVisibleWebView(){
        if(mainWebView.getVisibility() == View.VISIBLE){
            return mainWebView;
        }else{
            return mainWebView2;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;
        testAreaMenuItem = optionsMenu.findItem(R.id.testarea);
        view1 = optionsMenu.findItem(R.id.view1);
        view2 = optionsMenu.findItem(R.id.view2);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String text = item.getTitle().toString();

        //noinspection SimplifiableIfStatement
        if (id == R.id.go_home) {
            getVisibleWebView().loadUrl(urlBase + "barcode_test.html");
        }

        if (id == R.id.logout) {
           mainWebView.loadUrl(urlBase + "logout.html");
           mainWebView2.loadUrl(urlBase + "logout.html");
        }

        if(id == R.id.view1){
            // Refresh view in case they're on the login screen... because they probably logged in the previous view
            String currentUrl = mainWebView.getUrl();

            mainWebView2.setVisibility(View.INVISIBLE);
            mainWebView.setVisibility(View.VISIBLE);
            mainWebView.requestFocus();
            mainWebView.requestFocusFromTouch();

            view2.setVisible(true);
            view1.setVisible(false);
        }

        if(id == R.id.view2){
            String currentUrl = mainWebView2.getUrl();

            mainWebView.setVisibility(View.INVISIBLE);
            mainWebView2.setVisibility(View.VISIBLE);
            mainWebView2.requestFocus();
            mainWebView2.requestFocusFromTouch();

            view2.setVisible(false);
            view1.setVisible(true);
        }

        if(id == R.id.testarea){
            getVisibleWebView().loadUrl(urlBase + "barcode_test_2.html");
        }

        if(id == R.id.disable_scaling){
            if(text == getString(R.string.disable_scaling)){
                item.setTitle(R.string.enable_scaling);
                mainWebView.evaluateJavascript("window.localStorage.setItem('disScalCSS', 'true');", null);
                mainWebView.evaluateJavascript("disableScalingCSS = true;", null);
                mainWebView2.evaluateJavascript("window.localStorage.setItem('disScalCSS', 'true');", null);
                mainWebView2.evaluateJavascript("disableScalingCSS = true;", null);
                mainWebView.reload();
                mainWebView2.reload();
            }else{
                item.setTitle(R.string.disable_scaling);
                mainWebView.evaluateJavascript("window.localStorage.setItem('disScalCSS', 'false');", null);
                mainWebView.evaluateJavascript("disableScalingCSS = false;", null);
                mainWebView2.evaluateJavascript("window.localStorage.setItem('disScalCSS', 'false');", null);
                mainWebView2.evaluateJavascript("disableScalingCSS = false;", null);
                mainWebView.reload();
                mainWebView2.reload();
            }
        }

        if(id == R.id.checkforupdates){
            CheckForUpdates(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                String resultStr = data.getStringExtra("barcode");
                getVisibleWebView().evaluateJavascript("processBarcodeValue('" + resultStr + "');", null);
            }
        }
    }

    public static void checkCameraPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CAMERA},
                    100);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            WebView visibleView = getVisibleWebView();
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    String currentUrl = visibleView.getUrl();
                    if((currentUrl.indexOf("index.html") == -1 || currentUrl.indexOf("?menu") != -1) && currentUrl.indexOf("lblPicking.html") == -1) {
                        // If sweet alert 2 dialog is showing, do not allow back button
                        visibleView.evaluateJavascript("(function(){return(document.querySelector('.swal2-shown') ? 'yes' : 'no'); })();",
                                new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String resp) {
                                        if(resp.equals("\"no\"")){
                                            if (visibleView.canGoBack()) {
                                                visibleView.goBack();
                                            }
                                        }
                                    }
                                });
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setupWebView(WebView webViewInstance){
        webViewInstance.clearCache(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(webViewInstance, true);
            cookieManager.setAcceptCookie(true);
        }

        webViewInstance.getSettings().setJavaScriptEnabled(true);
        webViewInstance.getSettings().setDomStorageEnabled(true); // Turn on DOM storage
        webViewInstance.getSettings().setAllowFileAccess(true); // can read the file cache
        webViewInstance.getSettings().setDatabaseEnabled(true);
        webViewInstance.getSettings().setAllowContentAccess(true);
        webViewInstance.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewInstance.getSettings().setUseWideViewPort(true);
        webViewInstance.getSettings().setLoadWithOverviewMode(true);

        webViewInstance.addJavascriptInterface(new WebViewJavaScriptInterface(this, MainActivity.this), "app"); // Call android code from the web using the app name

        webViewInstance.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("MyApplication", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
        });

        webViewInstance.setWebViewClient(new WebViewClient() {

            public void onLoadResource(WebView view, String url){
                super.onLoadResource(view, url);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon){
                super.onPageStarted(view, url, favicon);
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            public void onPageFinished(WebView view, String url) {
                // For phone devices, we'll use barcode scanner, but for MUNBYN android scanner
                // we will use its scanner interface
                Log.d("MyApplication", "Device name is " + deviceName);
                Log.d("MyApplication", "Device manufacturer is " + deviceMan);
                Log.d("MyApplication", "Device ID is " + deviceId);

                view.evaluateJavascript("window.localStorage.setItem('deviceMan', '" + deviceMan + "');", null);
                view.evaluateJavascript("window.localStorage.setItem('deviceId', '" + deviceId + "');", null);

                // HANDTOP = MUNBYN Scanner without Keyboard
                // CHAINWAY = MUNBYN Scanner with Keyboard
                // SAMSUNG = Tablet with Bluetooth Scanner

                if (!deviceMan.toUpperCase().equals("HANDTOP") && !deviceMan.toUpperCase().equals("CHAINWAY") && !deviceMan.toUpperCase().equals("SAMSUNG")) {
                    Log.d("MyApplication", "Setting up as phone app");
                    view.evaluateJavascript("window.localStorage.setItem('isPhoneDevice', 'true');", null);
                    webViewInstance.evaluateJavascript("isPhoneDevice = true;", null);
                }else{
                    Log.d("MyApplication", "Setting up as scanner trigger device");
                    view.evaluateJavascript("window.localStorage.setItem('isPhoneDevice', 'false');", null);
                    if(deviceMan.toUpperCase().equals("CHAINWAY")){
                        webViewInstance.evaluateJavascript("isSmallScreenDevice = true;", null);
                        view.evaluateJavascript("window.localStorage.setItem('isSmallScreenDevice', 'true');", null);
                    }
                }

                // Load the magic
                view.evaluateJavascript("loadLocalStorage();focusFirstBarcode();", null);
                super.onPageFinished(view, url);
            }
        });

        webViewInstance.loadUrl(urlBase + "barcode_test.html");
    }

    public void CheckForUpdates(Context context) {
        // Check for latest version
        try {
            String currentVersion = Helpers.VersionCode;
            String latestVersion = Helpers.GetTextFromUrl(urlBase + "latest.txt");
            if (latestVersion != null && !latestVersion.trim().isEmpty()) {
                if (!currentVersion.equals(latestVersion)) {
                    int currentVersionInt = Integer.parseInt(currentVersion);
                    int latestVersionInt = Integer.parseInt(latestVersion);

                    if(currentVersionInt < latestVersionInt) {
                        Toast.makeText(MainActivity.this, "Version " + latestVersion + " is available as an update. Downloading update now...",
                                Toast.LENGTH_LONG).show();

                        // Download the APK and then install it
                        try {
                            String packageName = "latest_" + latestVersion + ".apk";
                            Helpers.DownloadFile(packageName, urlBase + "latest.apk", this);
                            File apkFilePath = new File(context.getFilesDir(), packageName);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Uri uri = FileProvider.getUriForFile(context,
                                        context.getApplicationContext().getPackageName() + ".provider", apkFilePath);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                                startActivity(intent);

                            } else {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(apkFilePath),
                                        "application/vnd.android.package-archive");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } catch (IOException e) {
                            Log.w("Error", e.toString());
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "App is already up-to-date and is the latest version.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e){
            Log.w("Error", e.toString());
        }
    }
}