package hu.ait.connect.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.connect.data.person.Person
import javax.inject.Inject

@HiltViewModel
class PersonDetailsViewModel  @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var personId by mutableStateOf("")
    var person: Person? = null

    init {
        val tmpPersonId = savedStateHandle.get<String>("personId") ?: ""
        personId = tmpPersonId
    }

}