package com.example.net.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    public static boolean isUserActive(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userStatus = sharedPreferences.getString("USER_STATUS", "Inactive");
        return "Active".equalsIgnoreCase(userStatus);
    }
}
