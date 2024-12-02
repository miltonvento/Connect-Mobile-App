package hu.ait.connect.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.connect.data.Configuration
import hu.ait.connect.data.ConfigurationDAO
import hu.ait.connect.data.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    val configurationDAO: ConfigurationDAO
) : ViewModel() {

    fun getConfig(): Flow<Configuration> {
        return configurationDAO.getConfiguration()
    }

    fun addConfiguration(configuration: Configuration) {
        viewModelScope.launch(Dispatchers.IO) {
            configurationDAO.insert(configuration)
        }
    }

    suspend fun insertConfiguration(configuration: Configuration) {
        configurationDAO.insert(configuration)
    }

    suspend fun updateConfiguration(configuration: Configuration) {
        configurationDAO.update(configuration)
    }

    init {
        addConfiguration(Configuration(taglist = listOf("Gender", "Meeting Location", "Nationality")))
    }
}