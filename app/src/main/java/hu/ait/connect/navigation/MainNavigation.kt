package hu.ait.connect.navigation

sealed class MainNavigation(val route: String) {
    object HomeScreen : MainNavigation("home")
    object PersonDetailsScreen : MainNavigation("persondetails?personId={personId}") {
        fun createRoute(personId: String) = "persondetails?personId=$personId"
    }
}