package com.example.barcodewebapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomWebChromeClient extends WebChromeClient {

    private Activity mActivity;
    public static final int INPUT_FILE_REQUEST_CODE = 199;
    private ValueCallback<Uri[]> mUploadMessage;
    private Uri mCapturedImageURI = null;

    public CustomWebChromeClient(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {

        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = filePathCallback;

        // 1. Get MIME types from the web input (Dropzone/HTML)
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        String primaryType = (acceptTypes.length > 0 && !acceptTypes[0].isEmpty()) ? acceptTypes[0] : "*/*";

        // 2. Setup Camera Intent (Only if images are allowed)
        Intent captureIntent = null;
        if (primaryType.contains("image") || primaryType.equals("*/*")) {
            captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                File photoFile = createImageFile();
                mCapturedImageURI = FileProvider.getUriForFile(mActivity,
                        mActivity.getApplicationContext().getPackageName() + ".provider",
                        photoFile);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            } catch (IOException ex) {
                captureIntent = null;
            }
        }

        // 3. Setup File Selection Intent
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);

        // Handle multiple MIME types (e.g., if Dropzone says ['image/*', 'application/pdf'])
        if (acceptTypes.length > 1) {
            contentSelectionIntent.setType("*/*");
            contentSelectionIntent.putExtra(Intent.EXTRA_MIME_TYPES, acceptTypes);
        } else {
            contentSelectionIntent.setType(primaryType);
        }

        // 4. Combine into Chooser
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Select Source");

        if (captureIntent != null) {
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{captureIntent});
        }

        mActivity.startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
        return true;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mUploadMessage == null) return;

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent == null || intent.getData() == null) {
                // Check if camera capture was successful
                if (mCapturedImageURI != null) {
                    results = new Uri[]{mCapturedImageURI};
                }
            } else {
                // Standard file selection
                String dataString = intent.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                } else if (intent.getClipData() != null) {
                    // Handle multiple file selection if enabled
                    int count = intent.getClipData().getItemCount();
                    results = new Uri[count];
                    for (int i = 0; i < count; i++) {
                        results[i] = intent.getClipData().getItemAt(i).getUri();
                    }
                }
            }
        }

        mUploadMessage.onReceiveValue(results);
        mUploadMessage = null;
    }
}