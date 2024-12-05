package hu.ait.connect.ui.screen.category

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.connect.data.category.Category
import hu.ait.connect.data.category.CategoryDAO
import hu.ait.connect.data.person.PersonDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryDAO: CategoryDAO
) : ViewModel() {

    fun getAllCategories(): Flow<List<Category>> {
        return categoryDAO.getAllCategories()
    }

    suspend fun insertUncategorizedCategory() {
        val uncategorizedCategory = Category(name = Category.UncategorizedCategoryName, color = Color.Gray.toArgb())

        val uncategorized = categoryDAO.getCategoryByName(Category.UncategorizedCategoryName)
        if (uncategorized == null) {
            categoryDAO.insert(uncategorizedCategory)
        }
    }

     suspend fun getUncategorizedCategory(): Category {
        return categoryDAO.getCategoryByName(Category.UncategorizedCategoryName)
            ?: throw IllegalStateException("Uncategorized category not found")
    }

    fun addCategory(categoryName: String, categoryColor: Color, onComplete: () -> Unit) {
        viewModelScope.launch {
            val category = Category(name = categoryName, color = categoryColor.toArgb())
            categoryDAO.insert(category)
            onComplete()
        }
    }
}