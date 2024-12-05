package hu.ait.connect.data.configuration

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "configurationtable")
data class Configuration(
    @PrimaryKey(autoGenerate = false) val id: Int = 1,
    @ColumnInfo(name = "taglist") val taglist: List<String>
) : Serializable