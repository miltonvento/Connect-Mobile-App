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

    @TypeConverter
    fun fromListtoString(tags: List<String>): String {
        return Gson().toJson(tags)  // Convert list to JSON string
    }

    @TypeConverter
    fun toStringToList(tags: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(tags, listType)  // Convert JSON string back to List
    }
}