package com.f0x1d.notes.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.f0x1d.notes.App;
import com.f0x1d.notes.R;
import com.f0x1d.notes.model.Theme;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ThemesEngine {

    public static int background;
    public static int statusBarColor;
    public static int navBarColor;
    public static int textColor;
    public static int accentColor;
    public static int iconsColor;
    public static int textHintColor;
    public static int toolbarColor;
    public static int toolbarTextColor;
    public static int fabColor;
    public static int fabIconColor;
    public static int defaultNoteColor;
    public static boolean dark;
    public static boolean toolbarTransparent;

    public void importTheme(Uri uri, Activity activity){

        Log.e("notes_err", "OPA, tut import");

        File theme = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/theme");

        if (!theme.exists()){
            theme.mkdirs();
        }

        InputStream fstream;

        String all = "";
        try {
            fstream = App.getInstance().getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null){
                all = all + strLine;
            }
        } catch (IOException e) {
            Log.e("notes_err", e.getLocalizedMessage());
        }

        try {
            File copied_theme = new File(theme, UselessUtils.getFileName(uri));
            FileWriter writer = new FileWriter(copied_theme);
            writer.append(all);
            writer.flush();
            writer.close();

            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", true).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putString("path_theme", copied_theme.getAbsolutePath()).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("change", true).apply();

            Intent i = activity.getBaseContext().getPackageManager().
                    getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);
            activity.finish();
        } catch (Exception e){
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
            Log.e("notes_err", e.getLocalizedMessage());
        }
    }

    public List<Theme> getThemes(){
        File theme = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/" + "/theme");

        if (!theme.exists()){
            theme.mkdirs();
        }

        List<Theme> themes = new ArrayList<>();

        File[] listFiles = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Notes/theme/").listFiles();
        if (listFiles != null){
            for (File listFile : listFiles) {
                if (listFile != null){
                    FileInputStream fstream1 = null;

                    String allnew = null;

                    try {
                        fstream1 = new FileInputStream(listFile);

                        BufferedReader br1 = new BufferedReader(new InputStreamReader(fstream1));
                        boolean first = true;
                        String strLine;
                        while ((strLine = br1.readLine()) != null){
                            if (first){
                                allnew = strLine;
                                first = false;
                            } else {
                                allnew = allnew + strLine;
                            }
                        }
                    } catch (IOException e) {
                        Log.e("notes_err", e.getLocalizedMessage());
                    }

                    String name = "error!";
                    String author = "error!";
                    String card_color = "#ffffff";
                    String card_text_color = "#ffffff";

                    try {
                        JSONObject jsonObject = new JSONObject(allnew);
                        name = jsonObject.getString("name");
                        author = jsonObject.getString("author");
                        card_color = jsonObject.getString("card_color");
                        card_text_color = jsonObject.getString("card_text_color");
                    } catch (JSONException | NullPointerException ignored) {
                        Log.e("notes_err", ignored.getLocalizedMessage());
                    }

                    themes.add(new Theme(listFile, name, author, Color.parseColor(card_color), Color.parseColor(card_text_color)));
                }
            }
        }

        return themes;
    }

    public void setupAll(){
        try {
            String all = "";
            File path = new File(PreferenceManager.getDefaultSharedPreferences(App.getContext()).getString("path_theme", ""));
            try {
                FileInputStream fstream = new FileInputStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                boolean first = true;
                String strLine;
                while ((strLine = br.readLine()) != null){
                    if (first){
                        all = strLine;
                        first = false;
                    } else {
                        all = all + strLine;
                    }
                }
            } catch (IOException e) {
                Log.e("notes_err", e.getLocalizedMessage());

                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
            }

            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(all);
            } catch (Exception e) {
                Toast.makeText(App.getContext(), "Error!", Toast.LENGTH_SHORT).show();

                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
            }

            try {
                background = Color.parseColor(jsonObject.getString("background"));
            } catch (Exception e){
                background = 0xffffffff;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                statusBarColor = Color.parseColor(jsonObject.getString("status_bar_color"));
            } catch (Exception e){
                statusBarColor = 0xffffffff;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                textColor = Color.parseColor(jsonObject.getString("text_color"));
            } catch (Exception e){
                textColor = 0xff000000;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                accentColor = Color.parseColor(jsonObject.getString("accent"));
            } catch (Exception e){
                accentColor = 0xff00ff00;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                navBarColor = Color.parseColor(jsonObject.getString("nav_color"));
            } catch (Exception e){
                navBarColor = 0xff000000;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                iconsColor = Color.parseColor(jsonObject.getString("icons_color"));
            } catch (Exception e){
                iconsColor = 0xff000000;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                textHintColor = Color.parseColor(jsonObject.getString("hint_color"));
            } catch (Exception e){
                textHintColor = Color.GRAY;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                dark = jsonObject.getBoolean("dark");
            } catch (Exception e){
                dark = false;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                toolbarColor = Color.parseColor(jsonObject.getString("toolbar_color"));
            } catch (Exception e){
                toolbarColor = Color.TRANSPARENT;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                toolbarTextColor = Color.parseColor(jsonObject.getString("toolbar_text_color"));
            } catch (Exception e){
                toolbarTextColor = 0xff000000;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                toolbarTransparent = jsonObject.getBoolean("transparent_toolbar");
            } catch (Exception e){
                toolbarTransparent = true;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                fabColor = Color.parseColor(jsonObject.getString("fab_color"));
            } catch (Exception e){
                fabColor = 0xffffffff;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                fabIconColor = Color.parseColor(jsonObject.getString("fab_icon_color"));
            } catch (Exception e){
                fabIconColor = 0xff000000;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                defaultNoteColor = Color.parseColor(jsonObject.getString("default_note_color"));
            } catch (Exception e){
                defaultNoteColor = 0xffffffff;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e){
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
        }
    }

    public void setTheme(File path, Activity activity){
        String all = "";

        try {
            FileInputStream fstream = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            boolean first = true;
            String strLine;
            while ((strLine = br.readLine()) != null){
                if (first){
                    all = strLine;
                    first = false;
                } else {
                    all = all + strLine;
                }
            }
        } catch (IOException e) {
            Log.e("notes_err", e.getLocalizedMessage());
        }

        try {
            JSONObject jsonObject = new JSONObject(all);

            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", true).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putString("path_theme", path.getAbsolutePath()).apply();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("change", true).apply();

            try {
                background = Color.parseColor(jsonObject.getString("background"));
            } catch (Exception e){
                background = 0xffffffff;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                statusBarColor = Color.parseColor(jsonObject.getString("status_bar_color"));
            } catch (Exception e){
                statusBarColor = 0xffffffff;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                textColor = Color.parseColor(jsonObject.getString("text_color"));
            } catch (Exception e){
                textColor = 0xff000000;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                accentColor = Color.parseColor(jsonObject.getString("accent"));
            } catch (Exception e){
                accentColor = 0xff00ff00;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                navBarColor = Color.parseColor(jsonObject.getString("nav_color"));
            } catch (Exception e){
                navBarColor = 0xff000000;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                iconsColor = Color.parseColor(jsonObject.getString("icons_color"));
            } catch (Exception e){
                iconsColor = 0xff000000;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                textHintColor = Color.parseColor(jsonObject.getString("hint_color"));
            } catch (Exception e){
                textHintColor = Color.GRAY;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                dark = jsonObject.getBoolean("dark");
            } catch (Exception e){
                dark = false;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                toolbarColor = Color.parseColor(jsonObject.getString("toolbar_color"));
            } catch (Exception e){
                toolbarColor = Color.TRANSPARENT;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                toolbarTextColor = Color.parseColor(jsonObject.getString("toolbar_text_color"));
            } catch (Exception e){
                toolbarTextColor = 0xff000000;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                toolbarTransparent = jsonObject.getBoolean("transparent_toolbar");
            } catch (Exception e){
                toolbarTransparent = true;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                fabColor = Color.parseColor(jsonObject.getString("fab_color"));
            } catch (Exception e){
                fabColor = 0xffffffff;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                fabIconColor = Color.parseColor(jsonObject.getString("fab_icon_color"));
            } catch (Exception e){
                fabIconColor = 0xff000000;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            try {
                defaultNoteColor = Color.parseColor(jsonObject.getString("default_note_color"));
            } catch (Exception e){
                defaultNoteColor = 0xffffffff;

                Toast.makeText(App.getContext(), R.string.not_all_colors_found, Toast.LENGTH_SHORT).show();
            }

            if (dark){
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("night", true).apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("night", false).apply();
            }

            Intent i = activity.getBaseContext().getPackageManager().
                    getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);
            activity.finish();

        } catch (Exception e) {
            Toast.makeText(App.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            PreferenceManager.getDefaultSharedPreferences(App.getContext()).edit().putBoolean("custom_theme", false).apply();
        }
    }
}