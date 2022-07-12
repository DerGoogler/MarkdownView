package com.dergoogler.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.dergoogler.lib.markdown.MarkdownView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MarkdownView view = findViewById(R.id.view);

        view.setCssFromString("body { color: red; background-color: " + view.getMonetColor("system_accent2_900") + " }");

        Log.d("", "body { color: red; background-color: \"" + view.getMonetColor("system_accent2_900") + "\" }");

        view.setMarkDownText("# I pay for nothing\n\rLOL");
    }
}