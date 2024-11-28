package hu.ait.connect.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonDetailsViewModel  @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var personName by mutableStateOf("")

    init {
        val tmpPersonName = savedStateHandle.get<String>("personName") ?: ""
        personName = tmpPersonName
    }
}