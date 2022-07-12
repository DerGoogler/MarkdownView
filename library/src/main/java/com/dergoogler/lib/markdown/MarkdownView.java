package com.dergoogler.lib.markdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownView extends WebView {
    private static final String TAG = MarkdownView.class.getSimpleName();
    private static final String IMAGE_PATTERN = "!\\[(.*)\\]\\((.*)\\)";

    private final Context mContext;
    private String css;
    private String mPreviewText;
    private boolean mIsOpenUrlInBrowser;

    public MarkdownView(Context context) {
        this(context, null);
    }

    public MarkdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initialize();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initialize() {
        setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                evaluateJavascript(mPreviewText, null);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isOpenUrlInBrowser()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mContext.startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        loadUrl("file:///android_asset/index.html");
        getSettings().setJavaScriptEnabled(true);
        getSettings().setAllowUniversalAccessFromFileURLs(true);
        getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        this.addJavascriptInterface(new JavaScriptBridge(this.mContext), "md");
        this.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Inject CSS when page is done loading
                injectCSS();
                super.onPageFinished(view, url);
            }
        });
    }

    public void loadMarkdownFromFile(File markdownFile) {
        String mdText = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(markdownFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String readText;
            StringBuilder stringBuilder = new StringBuilder();
            while ((readText = bufferedReader.readLine()) != null) {
                stringBuilder.append(readText);
                stringBuilder.append("\n");
            }
            fileInputStream.close();
            mdText = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException:" + e);
        } catch (IOException e) {
            Log.e(TAG, "IOException:" + e);
        }
        setMarkDownText(mdText);
    }

    public void loadMarkdownFromAssets(String assetsFilePath) {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = getContext().getAssets().open(assetsFilePath);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str).append("\n");
            }
            in.close();
            setMarkDownText(buf.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCssFromString(String value) {
        this.css = value;
    }

    public String getMonetColor(String id) {
        int nameResourceID = this.mContext.getResources().getIdentifier("@android:color/" + id,
                "color", this.mContext.getApplicationInfo().packageName);
        if (nameResourceID == 0) {
            throw new IllegalArgumentException(
                    "No resource string found with name " + id);
        } else {
            int color = ContextCompat.getColor(this.mContext, nameResourceID);
            int red = Color.red(color);
            int blue = Color.blue(color);
            int green = Color.green(color);
            return String.format("#%02x%02x%02x", red, green, blue);
        }
    }

    private void injectCSS() {
        try {
            String encoded = Base64.encodeToString(this.css.getBytes(), Base64.NO_WRAP);
            this.evaluateJavascript("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMarkDownText(String markdownText) {
        String bs64MdText = imgToBase64(markdownText);
        String escMdText = escapeForText(bs64MdText);
        mPreviewText = String.format("Markdown('%s')", escMdText);
        initialize();
    }

    private String escapeForText(String mdText) {
        String escText = mdText.replace("\n", "\\\\n");
        escText = escText.replace("'", "\\\'");
        escText = escText.replace("\r", "");
        return escText;
    }

    private String imgToBase64(String mdText) {
        Pattern ptn = Pattern.compile(IMAGE_PATTERN);
        Matcher matcher = ptn.matcher(mdText);
        if (!matcher.find()) {
            return mdText;
        }
        String imgPath = matcher.group(2);
        if (isUrlPrefix(imgPath) || !isPathExCheck(imgPath)) {
            return mdText;
        }
        String baseType = imgEx2BaseType(imgPath);
        if ("".equals(baseType)) {
            return mdText;
        }
        File file = new File(imgPath);
        byte[] bytes = new byte[(int) file.length()];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException:" + e);
        } catch (IOException e) {
            Log.e(TAG, "IOException:" + e);
        }
        String base64Img = baseType + Base64.encodeToString(bytes, Base64.NO_WRAP);
        return mdText.replace(imgPath, base64Img);
    }

    private boolean isUrlPrefix(String text) {
        return text.startsWith("http://") || text.startsWith("https://");
    }

    private boolean isPathExCheck(String text) {
        return text.endsWith(".png")
                || text.endsWith(".jpg")
                || text.endsWith(".jpeg")
                || text.endsWith(".gif");
    }

    private String imgEx2BaseType(String text) {
        if (text.endsWith(".png")) {
            return "data:image/png;base64,";
        } else if (text.endsWith(".jpg") || text.endsWith(".jpeg")) {
            return "data:image/jpg;base64,";
        } else if (text.endsWith(".gif")) {
            return "data:image/gif;base64,";
        } else {
            return "";
        }
    }

    public boolean isOpenUrlInBrowser() {
        return mIsOpenUrlInBrowser;
    }

    public void setOpenUrlInBrowser(boolean openUrlInBrowser) {
        mIsOpenUrlInBrowser = openUrlInBrowser;
    }
}
