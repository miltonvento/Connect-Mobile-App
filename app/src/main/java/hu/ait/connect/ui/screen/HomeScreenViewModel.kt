package hu.ait.connect.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.connect.data.PersonDAO
import hu.ait.connect.data.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val personDAO: PersonDAO
) : ViewModel() {

    fun getAllPeople(): Flow<List<Person>> {
        return personDAO.getAllPeople()
    }

    fun addPerson(person: Person) {
        viewModelScope.launch(Dispatchers.IO) {
            personDAO.insert(person)
        }
    }

    fun deletePerson(person: Person) {
        viewModelScope.launch(Dispatchers.IO) {
            personDAO.delete(person)
        }
    }

    fun editPerson(editingPerson: Person) {
        viewModelScope.launch(Dispatchers.IO) {
            personDAO.update(editingPerson)
        }
    }

//    fun changeItemState(shoppingItem: Person, value: Boolean) {
//        val updatedItem = shoppingItem.copy()
//        updatedItem.isBought = value
//        viewModelScope.launch(Dispatchers.IO) {
//            shoppingDAO.update(updatedItem)
//        }
//    }

    fun clearAllPeople() {
        viewModelScope.launch(Dispatchers.IO) {
           personDAO.deleteAllItems()
        }
    }
}