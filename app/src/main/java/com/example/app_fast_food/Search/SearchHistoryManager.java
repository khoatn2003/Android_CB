package com.example.app_fast_food.Search;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Shader;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SearchHistoryManager {
    private static final String PREF_NAME = "search_history";
    private static final String KEY_HISTORY = "keywords";

    public static void saveKeyword(Context context, String keyword) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> keywords = new LinkedHashSet<>(prefs.getStringSet(KEY_HISTORY, new LinkedHashSet<>()));

        // Thêm vào đầu, tránh trùng
        keywords.remove(keyword);
        keywords.add(keyword);

        // Giới hạn 10 từ khóa gần nhất
        while (keywords.size() > 10) {
            Iterator<String> iterator = keywords.iterator();
            iterator.next();
            iterator.remove();
        }

        prefs.edit().putStringSet(KEY_HISTORY, keywords).apply();
    }

    public static List<String> getHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> keywords = prefs.getStringSet(KEY_HISTORY, new LinkedHashSet<>());
        return new ArrayList<>(keywords);
    }
}

