package hu.ait.connect.ui.screen.category

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.connect.data.category.Category
import hu.ait.connect.data.category.CategoryDAO
import hu.ait.connect.data.person.PersonDAO
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(
    private val categoryDAO: CategoryDAO,
    private val personDAO: PersonDAO
) : ViewModel() {

    var _category : Category? = null

    fun getCategoryById(categoryId: Int): Category? {
        viewModelScope.launch {
            val category = categoryDAO.getCategoryById(categoryId)
            _category = category

    }
        return _category
    }
//    suspend fun getCategoryById(categoryId: Int) = categoryDAO.getCategoryById(categoryId)
    fun getPeopleByCategory(categoryId: Int) = personDAO.getPeopleByCategory(categoryId)
}