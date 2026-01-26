import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sedapp.R
import com.example.sedapp.core.navigation.Routes
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.presentation.dashboard.bag.BagViewModel
import com.example.sedapp.presentation.dashboard.component.AnimatedNavigationBar
import com.example.sedapp.presentation.dashboard.component.ButtonData

@Composable
fun DashboardScaffold(
    navController: NavHostController,
    bagViewModel: BagViewModel = hiltViewModel(),
    content: @Composable (PaddingValues, LazyListState) -> Unit
) {
    val bagState by bagViewModel.uiState.collectAsStateWithLifecycle()
    val totalItemsInBag = remember(bagState.items) {
        bagState.items.size
    }

    val items = remember {
        listOf(
            Routes.HOME to R.drawable.home_icon,
            Routes.BAG to R.drawable.bag,
            Routes.ORDERS to R.drawable.orders,
            Routes.PROFILE to R.drawable.frame
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var selectedIndex by rememberSaveable(currentDestination) {
        val index = items.indexOfFirst { (route, _) ->
            currentDestination?.hierarchy?.any { it.route == route } == true
        }
        mutableIntStateOf(if (index != -1) index else 0)
    }

    val listState = rememberLazyListState()

    val shouldHideBottomBar by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                    listState.firstVisibleItemScrollOffset > 24
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White
        ) { paddingValues ->
            content(paddingValues, listState)
        }

        AnimatedVisibility(
            visible = !shouldHideBottomBar,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            AnimatedNavigationBar(
                buttons = items.map { (route, icon) ->
                    ButtonData(
                        text = route.lowercase().replaceFirstChar { it.uppercase() },
                        icon = icon,
                        badgeCount = if (route == Routes.BAG) totalItemsInBag else 0
                    )
                },
                selectedItem = selectedIndex,
                onItemSelected = { index ->
                    selectedIndex = index
                    val route = items[index].first

                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                barColor = SedAppOrange,
                circleColor = SedAppOrange,
                selectedColor = SoftGold,
                unselectedColor = WarmWhite,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    .navigationBarsPadding()
            )
        }
    }
}
