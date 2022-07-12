package com.dergoogler.lib.markdown;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.webkit.JavascriptInterface;

import androidx.core.content.ContextCompat;

public class JavaScriptBridge {
    private final Context ctx;

    public JavaScriptBridge(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * Return current android sdk-int version code, see:
     * https://source.android.com/setup/start/build-numbers
     */
    @JavascriptInterface
    public int getAndroidVersionCode() {
        return Build.VERSION.SDK_INT;
    }
    /**
     * Return current hex string of monet theme
     */
    @JavascriptInterface
    public String getMonetColor(String id) {
        int nameResourceID = this.ctx.getResources().getIdentifier("@android:color/" + id,
                "color", this.ctx.getApplicationInfo().packageName);
        if (nameResourceID == 0) {
            throw new IllegalArgumentException(
                    "No resource string found with name " + id);
        } else {
            int color = ContextCompat.getColor(this.ctx, nameResourceID);
            int red = Color.red(color);
            int blue = Color.blue(color);
            int green = Color.green(color);
            return String.format("#%02x%02x%02x", red, green, blue);
        }
    }
}
