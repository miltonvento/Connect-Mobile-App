package hu.ait.connect.navigation

sealed class MainNavigation(val route: String) {
    object HomeScreen : MainNavigation("home")
    object PersonDetailsScreen : MainNavigation("persondetails?personName={personName}") {
        fun createRoute(personName: String) = "persondetails?personName=$personName"
    }
}