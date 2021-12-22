package com.bistu.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


//SharePreference辅助类,用于存储对象

public class CacheUtil {
    private static Context context;
    private static CacheUtil mACache = new CacheUtil();
    public static final String DEFAULT_CATCHE = "data";

    private static SharedPreferences share;

    public synchronized  static CacheUtil get(Context context) {
        CacheUtil.context = context;
        return mACache;
    }

    public synchronized boolean put(String name, String str) {
        share = context.getSharedPreferences(DEFAULT_CATCHE, Context.MODE_PRIVATE
        );
        Editor edit = share.edit();
        edit.putString(name, str);
        return edit.commit();

    }
    public synchronized String getString(String name, String def) {
        share = context.getSharedPreferences(DEFAULT_CATCHE, Context.MODE_PRIVATE
        );
        return share.getString(name, def);
    }
}
