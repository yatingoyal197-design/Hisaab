package com.hisaab.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final int SMS_REQUEST = 42;
    private WebView web;
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor("#14212B"));
        w.setNavigationBarColor(Color.parseColor("#14212B"));

        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loaded = true;
            }
        });
        web.addJavascriptInterface(new Bridge(), "Android");
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loaded) {
            web.evaluateJavascript("window.onAndroidResume && onAndroidResume()", null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == SMS_REQUEST) {
            boolean granted = results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED;
            web.evaluateJavascript("window.onSmsPermission && onSmsPermission(" + granted + ")", null);
        }
    }

    private boolean smsAllowed() {
        return checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public class Bridge {
        @JavascriptInterface
        public void shareText(String text) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Hisaab entries");
            i.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(i, "Share entries"));
        }

        @JavascriptInterface
        public boolean hasSmsPermission() {
            return smsAllowed();
        }

        @JavascriptInterface
        public void requestSmsPermission() {
            runOnUiThread(() -> requestPermissions(new String[]{Manifest.permission.READ_SMS}, SMS_REQUEST));
        }

        @JavascriptInterface
        public void openAppSettings() {
            runOnUiThread(() -> {
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                startActivity(i);
            });
        }

        /** Returns inbox SMS newer than sinceMillis as a JSON array of {id, address, body, date}. */
        @JavascriptInterface
        public String readInbox(double sinceMillis) {
            JSONArray out = new JSONArray();
            if (!smsAllowed()) return out.toString();
            String[] cols = {Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE};
            try (Cursor c = getContentResolver().query(
                    Telephony.Sms.Inbox.CONTENT_URI, cols,
                    Telephony.Sms.DATE + " > ?", new String[]{String.valueOf((long) sinceMillis)},
                    Telephony.Sms.DATE + " ASC")) {
                if (c != null) {
                    int count = 0;
                    while (c.moveToNext() && count < 3000) {
                        JSONObject o = new JSONObject();
                        o.put("id", c.getLong(0));
                        o.put("address", c.getString(1));
                        o.put("body", c.getString(2));
                        o.put("date", c.getLong(3));
                        out.put(o);
                        count++;
                    }
                }
            } catch (Exception e) {
                // Permission revoked or provider unavailable: return what we have.
            }
            return out.toString();
        }
    }

    @Override
    public void onBackPressed() {
        web.evaluateJavascript("window.onAndroidBack ? onAndroidBack() : false", value -> {
            if (!"true".equals(value)) {
                MainActivity.super.onBackPressed();
            }
        });
    }
}
