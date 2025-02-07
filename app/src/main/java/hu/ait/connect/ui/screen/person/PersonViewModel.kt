package hu.ait.connect.ui.screen.person

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.connect.data.category.Category
import hu.ait.connect.data.category.CategoryDAO
import hu.ait.connect.data.person.PersonDAO
import hu.ait.connect.data.person.Person
import hu.ait.connect.ui.screen.category.CategoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val personDAO: PersonDAO,
    categoryDAO: CategoryDAO
) : ViewModel() {

    private val _uncategorizedCategoryId = MutableLiveData<Int>()
    val uncategorizedCategoryId: LiveData<Int> = _uncategorizedCategoryId

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        viewModelScope.launch {
            try {
                val category = categoryDAO.getCategoryByName(Category.UncategorizedCategoryName)
                    ?: throw IllegalStateException("Uncategorized category not found")

                _uncategorizedCategoryId.value = category.id
            } catch (e: Exception) {
                Log.e("PersonViewModel", "Error fetching uncategorized category", e)
            }
        }
    }

    fun getAllPeople(): Flow<List<Person>> {
        return personDAO.getAllPeople()
    }

    fun getPersonById(id: Int): Flow<Person> {
        return personDAO.getPerson(id)
    }

    fun getPeopleByCategory(categoryId: Int): Flow<List<Person>> {
        return personDAO.getPeopleByCategory(categoryId)
    }

    fun addPerson(name: String, description: String, categoryId: Int? = null, audio: ByteArray? = null, imageUri: String? = null, tags: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            val uncategorizedCategoryId = uncategorizedCategoryId.value
            var person:Person

            if (categoryId == null) {
                if (uncategorizedCategoryId != null) {
                    person = Person(name = name, description = description, categoryId = uncategorizedCategoryId, audio = audio, imageUri = imageUri, tags = tags)
                } else {
                    throw IllegalStateException("Uncategorized category ID is missing")
                }
            } else {
                person = Person(name = name, description = description, categoryId = categoryId, audio = audio, imageUri = imageUri, tags = tags)
            }
            personDAO.insert(person)
        }
    }

    fun deletePerson(person: Person) {
        viewModelScope.launch(Dispatchers.IO) {
            personDAO.delete(person)
        }
    }

    fun deletePeopleByIds(people: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            personDAO.deletePeopleByIds(people)
        }
    }

    fun editPerson(editingPerson: Person) {
        viewModelScope.launch(Dispatchers.IO) {
            personDAO.update(editingPerson)
        }
    }

    fun clearAllPeople() {
        viewModelScope.launch(Dispatchers.IO) {
           personDAO.deleteAllItems()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchPeople(query: String): Flow<List<Person>> = personDAO.searchPeople(query)


}