package hu.ait.connect.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    // Convert Map<String, Any> to a JSON string (for storing in Room database)
    @TypeConverter
    fun fromMapToString(map: Map<String, Any>?): String? {
        return if (map == null) null else Gson().toJson(map)
    }

    // Convert a JSON string to Map<String, Any> (for retrieving from Room database)
    @TypeConverter
    fun fromStringToMap(value: String?): Map<String, Any>? {
        return if (value == null) null else {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            Gson().fromJson<Map<String, Any>>(value, type)
        }
    }
}