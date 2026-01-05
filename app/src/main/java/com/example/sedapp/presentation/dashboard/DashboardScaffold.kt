// ... other imports
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sedapp.R
import com.example.sedapp.core.navigation.Routes

// ... other imports

@Composable
fun DashboardScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val items = listOf(
        Routes.HOME,
        Routes.BAG,
        Routes.ORDERS,
        Routes.PROFILE
    )

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { route ->
                    NavigationBarItem(
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == route } == true,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = when (route) {
                                    Routes.HOME -> painterResource(R.drawable.home_icon)
                                    Routes.BAG -> painterResource(R.drawable.bag)
                                    Routes.PROFILE -> painterResource(R.drawable.frame)
                                    Routes.ORDERS -> painterResource(R.drawable.orders)
                                    else -> painterResource(R.drawable.home_icon)
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    ) { padding ->
        content(padding)
    }
}
