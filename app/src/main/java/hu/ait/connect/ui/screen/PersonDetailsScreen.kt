package hu.ait.connect.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import hu.ait.connect.navigation.MainNavigation

@Composable
fun PersonDetailsScreen(
    personName: String,
    personDetailsViewModel: PersonDetailsViewModel = hiltViewModel()
) {
    Text(text = "$personName")
}
