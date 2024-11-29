package hu.ait.connect.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun PersonDetailsScreen(
    personId: String,
    personViewModel: PersonViewModel = hiltViewModel(),
) {
    val person = personViewModel.getPersonById(personId.toInt()).collectAsState(initial = null)

    Scaffold(modifier = Modifier.fillMaxSize()) {
        innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
//            Text(text = "$person.value.name, $personId")
            if (person.value != null) {
                Text(text = "${person.value?.name}, $personId")
            } else {
                Text(text = "Loading person details...")
            }
        }

    }
}
