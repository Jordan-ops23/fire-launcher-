package com.jordan.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    // Change the order or swap apps here: {label, package name}
    static final String[][] APPS = {
        {"Crunchyroll", "com.crunchyroll.crunchyroid"},
        {"YouTube", "com.amazon.firetv.youtube"},
        {"Netflix", "com.netflix.ninja"},
        {"Disney+", "com.disney.disneyplus"},
        {"Paramount+", "com.cbs.ott"},
        {"Tubi", "com.tubitv.ott"},
        {"Hulu", "com.hulu.plus"},
        {"Spotify", "com.spotify.tv.android"},
    };

    float d;

    GradientDrawable box(int color) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(16 * d);
        return g;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d = getResources().getDisplayMetrics().density;
        PackageManager pm = getPackageManager();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xFF2A0A4D, 0xFF5B21B6}));
        int pad = (int) (40 * d);
        root.setPadding(pad, pad / 2, pad, pad / 2);

        TextClock clock = new TextClock(this);
        clock.setFormat12Hour("h:mm a");
        clock.setTextColor(Color.WHITE);
        clock.setTextSize(24);
        clock.setGravity(Gravity.END);
        root.addView(clock, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        FrameLayout holder = new FrameLayout(this);
        root.addView(holder, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        holder.addView(grid, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        for (String[] app : APPS) {
            final String pkg = app[1];

            LinearLayout tile = new LinearLayout(this);
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setGravity(Gravity.CENTER);
            tile.setFocusable(true);
            tile.setClickable(true);
            tile.setBackground(box(0x33FFFFFF));

            ImageView icon = new ImageView(this);
            try {
                icon.setImageDrawable(pm.getApplicationIcon(pkg));
            } catch (PackageManager.NameNotFoundException e) {
                // app not installed, tile shows label only
            }
            tile.addView(icon, new LinearLayout.LayoutParams((int) (72 * d), (int) (72 * d)));

            TextView label = new TextView(this);
            label.setText(app[0]);
            label.setTextColor(Color.WHITE);
            label.setTextSize(16);
            label.setGravity(Gravity.CENTER);
            label.setPadding(0, (int) (8 * d), 0, 0);
            tile.addView(label);

            tile.setOnFocusChangeListener((v, hasFocus) -> {
                float s = hasFocus ? 1.12f : 1f;
                v.animate().scaleX(s).scaleY(s).setDuration(120).start();
                v.setBackground(box(hasFocus ? 0x66FFFFFF : 0x33FFFFFF));
            });
            tile.setOnClickListener(v -> launch(pkg));

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = (int) (180 * d);
            lp.height = (int) (140 * d);
            int m = (int) (10 * d);
            lp.setMargins(m, m, m, m);
            grid.addView(tile, lp);
        }

        setContentView(root);
        if (grid.getChildCount() > 0) {
            grid.getChildAt(0).requestFocus();
        }
    }

    void launch(String pkg) {
        PackageManager pm = getPackageManager();
        Intent i = pm.getLeanbackLaunchIntentForPackage(pkg);
        if (i == null) {
            i = pm.getLaunchIntentForPackage(pkg);
        }
        if (i != null) {
            startActivity(i);
        } else {
            Toast.makeText(this, "Not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing, this is a home screen
    }
}
