package ru.kpfu.itis.liiceberg.github_storage.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun setToString(set: Set<String>): String {
        return Gson().toJson(set)
    }

    @TypeConverter
    fun stringToSet(json: String): Set<String?> {
        return Gson().fromJson(json, object : TypeToken<Set<String>>() {}.type)
    }
}