package com.pixelfrost.launcher.home

import android.content.Context

object HomeOrderStore {
    private const val PREFS = "home_order"
    private const val KEY = "packages"

    fun load(context: Context): List<String> {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, "")
            .orEmpty()
            .split(",")
            .filter { it.isNotEmpty() }
    }

    fun save(context: Context, packages: List<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, packages.joinToString(","))
            .apply()
    }
}
