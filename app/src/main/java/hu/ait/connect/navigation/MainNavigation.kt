package hu.ait.connect.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainNavigation(val route: String, val icon: ImageVector, val label: String) {
    object HomeScreen : MainNavigation("home", Icons.Default.Home, "Home")
    object PersonDetailsScreen : MainNavigation("persondetails?personId={personId}", Icons.Default.Image, "Person Details")
    object CategoryDetailsScreen : MainNavigation("categorydetails?categoryId={categoryId}", Icons.Default.Image, "Category Details")
    object CategoryScreen : MainNavigation("category", Icons.Default.Category, "Categories")
    object AiAssistance : MainNavigation("assistance", Icons.Default.ChatBubble, "Assistance")
    object NewPersonScreen : MainNavigation("newperson", Icons.Default.Image, "New Person")
}