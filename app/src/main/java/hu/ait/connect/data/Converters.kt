package hu.ait.connect.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromMapToString(map: Map<String, Any>?): String? {
        return if (map == null) null else Gson().toJson(map)
    }

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

    @TypeConverter
    fun fromColor(color: androidx.compose.ui.graphics.Color): Int {
        return color.toArgb() // Converts the Color to an integer (ARGB)
    }

    @TypeConverter
    fun toColor(colorInt: Int): androidx.compose.ui.graphics.Color {
        return Color(colorInt) // Converts the Int back to a Color
    }
}