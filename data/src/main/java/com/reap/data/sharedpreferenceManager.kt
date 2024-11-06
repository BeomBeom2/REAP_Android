package com.reap.data

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

fun saveAccessToken(context: Context, token: String) {
    val preferences = context.getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE)
    val editor = preferences.edit()
    editor.putString("jwt_token", token)
    editor.apply()
}

fun saveId(context: Context, userId: String) {
    val preferences = context.getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE)
    val editor = preferences.edit()
    editor.putString("userId", userId)
    editor.apply()
}

fun getAccessToken(context: Context): String? {
    val preferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    return preferences.getString("jwt_token", null)
}

fun saveNickname(context: Context, nickname: String) {
    val preferences = context.getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE)
    val editor = preferences.edit()
    editor.putString("nickname", nickname)
    editor.apply()
}

fun getNickname(context: Context): String? {
    val preferences = context.getSharedPreferences("app_preferences", AppCompatActivity.MODE_PRIVATE)
    return preferences.getString("nickname", null)
}


