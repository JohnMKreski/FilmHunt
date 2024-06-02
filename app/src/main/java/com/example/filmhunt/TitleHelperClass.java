package com.example.filmhunt;

import android.util.SparseArray;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class TitleHelperClass {

    private static SparseArray<String> titleMap = new SparseArray<>();

    static {
        titleMap.put(R.id.settings, "Settings");
        titleMap.put(R.id.dashboard, "Dashboard");
        titleMap.put(R.id.useraccount, "Account");
        // Add more mappings as needed
    }

    public static void setToolbarTitle(AppCompatActivity activity, int toolbarId, int activityId) {
        Toolbar toolbar = activity.findViewById(toolbarId);
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("");
        }

        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        String title = titleMap.get(activityId);
        if (title != null) {
            toolbarTitle.setText(title);
        }
    }

    public static void setToolbarTitle(AppCompatActivity activity, int toolbarId, String title) {
        Toolbar toolbar = activity.findViewById(toolbarId);
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("");
        }

        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(title);
    }
}


