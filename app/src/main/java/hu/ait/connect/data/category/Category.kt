package hu.ait.connect.data.category

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "category_table")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "description") val description:String? = null,
    @ColumnInfo(name = "color") val color: Int
) : Serializable {
    companion object {
        const val UncategorizedCategoryName = "Uncategorized"
    }
}