package com.ikon.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.graphics.Insets;
import android.os.Build;
import android.view.WindowInsets;
import android.view.Window;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(12, 13, 28);
    private final int WHITE = Color.WHITE;
    private GridLayout grid;
    private EditText search;
    private final List<ResolveInfo> allApps = new ArrayList<>();
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = getPackageManager();

        getWindow().setStatusBarColor(Color.rgb(14, 12, 35));
        getWindow().setNavigationBarColor(Color.rgb(14, 12, 35));

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(16), dp(18), dp(16), dp(12));
        page.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(65, 18, 91), BG, Color.rgb(0, 65, 105)}));

        TextView brand = new TextView(this);
        brand.setText("IKON");
        brand.setTextColor(WHITE);
        brand.setTextSize(34);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        brand.setLetterSpacing(0.12f);
        page.addView(brand);

        TextView tagline = new TextView(this);
        tagline.setText("YOUR APPS. YOUR WORLD.");
        tagline.setTextColor(Color.LTGRAY);
        tagline.setTextSize(12);
        tagline.setPadding(0, 0, 0, dp(18));
        page.addView(tagline);

        search = new EditText(this);
        search.setSingleLine(true);
        search.setHint("Search installed apps...");
        search.setTextColor(WHITE);
        search.setHintTextColor(Color.LTGRAY);
        search.setTextSize(16);
        search.setPadding(dp(16), dp(5), dp(16), dp(5));
        search.setBackground(round(Color.argb(65,255,255,255), dp(22), Color.argb(90,255,255,255)));
        page.addView(search, new LinearLayout.LayoutParams(-1, dp(52)));

        TextView info = new TextView(this);
        info.setText("INSTALLED APPLICATIONS");
        info.setTextColor(Color.WHITE);
        info.setTextSize(13);
        info.setTypeface(null, Typeface.BOLD);
        info.setPadding(dp(3), dp(22), 0, dp(12));
        page.addView(info);

        ScrollView scroll = new ScrollView(this);
        grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setUseDefaultMargins(false);
        scroll.addView(grid);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        TextView footer = new TextView(this);
        footer.setText("IKON  •  V3.0  •  MULTI-COLOUR EDITION");
        footer.setGravity(Gravity.CENTER);
        footer.setTextColor(Color.LTGRAY);
        footer.setTextSize(10);
        footer.setPadding(0, dp(12), 0, dp(4));
        page.addView(footer);

        setContentView(page);
        loadApps();
        renderApps("");

        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int before, int count) {
                renderApps(s.toString());
            }
            public void afterTextChanged(Editable e) {}
        });
    }

    private void loadApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> found = pm.queryIntentActivities(intent, 0);
        allApps.clear();
        allApps.addAll(found);
        Collections.sort(allApps, new Comparator<ResolveInfo>() {
            public int compare(ResolveInfo a, ResolveInfo b) {
                return a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString());
            }
        });
    }

    private void renderApps(String query) {
        grid.removeAllViews();
        String q = query.toLowerCase().trim();
        int count = 0;
        int width = getResources().getDisplayMetrics().widthPixels;
        int cell = (width - dp(32)) / 4;

        for (ResolveInfo app : allApps) {
            String label = app.loadLabel(pm).toString();
            if (!label.toLowerCase().contains(q)) continue;
            count++;

            LinearLayout tile = new LinearLayout(this);
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setGravity(Gravity.CENTER);
            tile.setPadding(dp(3), dp(9), dp(3), dp(12));
            tile.setBackground(round(Color.argb(28,255,255,255), dp(17), Color.argb(35,255,255,255)));
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = cell - dp(5);
            lp.setMargins(dp(2), dp(3), dp(3), dp(8));
            tile.setLayoutParams(lp);

            ImageView icon = new ImageView(this);
            Drawable drawable = app.loadIcon(pm);
            icon.setImageDrawable(drawable);
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(48), dp(48));
            ip.gravity = Gravity.CENTER;
            tile.addView(icon, ip);

            TextView name = new TextView(this);
            name.setText(label);
            name.setTextColor(WHITE);
            name.setTextSize(10);
            name.setGravity(Gravity.CENTER);
            name.setMaxLines(2);
            name.setPadding(0, dp(6), 0, 0);
            tile.addView(name, new LinearLayout.LayoutParams(-1, dp(34)));

            tile.setOnClickListener(v -> {
                try {
                    String packageName = app.activityInfo.packageName;
                    Intent launch = pm.getLaunchIntentForPackage(packageName);
                    if (launch != null) {
                        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launch);
                    } else {
                        Toast.makeText(this, "App cannot be opened", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(this, "Unable to open app", Toast.LENGTH_SHORT).show();
                }
            });
            grid.addView(tile);
        }

        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("No apps found");
            empty.setTextColor(Color.LTGRAY);
            empty.setPadding(dp(12), dp(20), dp(12), dp(20));
            grid.addView(empty);
        }
    }

    private GradientDrawable round(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        d.setStroke(dp(1), stroke);
        return d;
    }

    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
