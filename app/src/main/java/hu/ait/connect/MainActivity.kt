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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.connect.navigation.MainNavigation
import hu.ait.connect.ui.screen.HomeScreen
import hu.ait.connect.ui.screen.person.PersonDetailsScreen
import hu.ait.connect.ui.theme.ConnectTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import hu.ait.connect.ui.screen.assistance.AiAssistanceScreen
import hu.ait.connect.ui.screen.category.CategoryDetailsScreen
import hu.ait.connect.ui.screen.category.CategoryScreen
import hu.ait.connect.ui.screen.category.CategoryViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lateinit var categoryViewModel: CategoryViewModel

            // Get the CategoryViewModel using ViewModelProvider
            categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

            // Ensure the uncategorized category is inserted
            lifecycleScope.launch {
                categoryViewModel.insertUncategorizedCategory()
            }

        setContent {
            ConnectTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    ConnectAppNavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController
                    )
                }
            }
        }

    }
}


@Composable
fun ConnectAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
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
                },
                )
        }

        composable(MainNavigation.PersonDetailsScreen.route) {
            PersonDetailsScreen(
                navController = navController, personId = it.arguments?.getString("personId") ?: ""
            )
        }

        composable(MainNavigation.CategoryDetailsScreen.route) {
            CategoryDetailsScreen(
                navController = navController, categoryId = it.arguments?.getString("categoryId") ?: ""
            )
        }

        composable(MainNavigation.CategoryScreen.route) {
            CategoryScreen(
                navController = navController,
                onNavigateToCategoryDetails = { personId ->
                    navController.navigate("categorydetails?categoryId=$personId")
                },
            )
        }

        composable(MainNavigation.AiAssistance.route) {
            AiAssistanceScreen( navController = navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        MainNavigation.HomeScreen,
        MainNavigation.CategoryScreen,
        MainNavigation.AiAssistance
    )
    NavigationBar {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.route) },
                label = { Text(screen.label) },
                selected = currentDestination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Avoid multiple copies of the same destination in the back stack
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected tab
                        restoreState = true
                        // Clear the backstack to maintain navigation hierarchy
                        popUpTo(navController.graph.startDestinationId) {
//                            saveState = true
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}