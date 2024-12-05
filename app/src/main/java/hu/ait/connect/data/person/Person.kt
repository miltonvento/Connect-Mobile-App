package hu.ait.connect.data.person

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "persontable")
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "description") val description:String,
    @ColumnInfo(name = "tags") val tags:Map<String, Any>? = null,
    @ColumnInfo(name = "audio") val audio: ByteArray? = null,
    @ColumnInfo(name = "imageUri") val imageUri: String? = null // Add this field
) : Serializable