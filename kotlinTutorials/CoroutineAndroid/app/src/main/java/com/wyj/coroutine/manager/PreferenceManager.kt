package com.wyj.coroutine.manager

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREFS_NAME = "config.xml"
    private fun preferences(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun readString(context: Context, key: String, defaultValue: String = ""): String {
        return preferences(context).getString(key, defaultValue) ?: ""
    }

    fun writeString(context: Context, key: String, value: String) {
        preferences(context).edit().putString(key, value).commit()
    }
}