package com.example.universitymanagement.util;

import android.content.Context;

import com.example.universitymanagement.database.DatabaseHelper;

public class DBUtil {
    private static DBUtil instance;
    private final Context context;

    private DBUtil(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized DBUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DBUtil(context);
        }
        return instance;
    }

    public void resetDatabase(Runnable onSuccess, Runnable onFailure) {
        try {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
            dbHelper.resetDatabase();
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            android.util.Log.e("DBUtil", "Error resetting database", e);
            if (onFailure != null) {
                onFailure.run();
            }
        }
    }
}
