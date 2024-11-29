package hu.ait.connect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.connect.navigation.MainNavigation
import hu.ait.connect.ui.screen.HomeScreen
import hu.ait.connect.ui.screen.PersonDetailsScreen
import hu.ait.connect.ui.theme.ConnectTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConnectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ConnectAppNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ConnectAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainNavigation.HomeScreen.route
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable(MainNavigation.HomeScreen.route) {
            HomeScreen(
                modifier = modifier,
                onNavigateToPersonDetails = { personId ->
                    navController.navigate("persondetails?personId=$personId")
                }
            )
        }

        composable(MainNavigation.PersonDetailsScreen.route) {
            PersonDetailsScreen(personId = it.arguments?.getString("personId") ?: "")
        }
    }
}