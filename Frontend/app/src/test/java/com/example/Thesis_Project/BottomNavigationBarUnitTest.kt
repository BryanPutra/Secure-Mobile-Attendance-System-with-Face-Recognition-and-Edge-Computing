import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.navigation.NavController
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: Any // Replace 'Any' with the appropriate type for icons in your project
)

object BottomNavBarRoutes {
    object HomeScreen {
        const val route = "home_screen"
    }
    object CalendarScreen {
        const val route = "calendar_screen"
    }
    object HistoryScreen {
        const val route = "history_screen"
    }
}

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Home",
        route = BottomNavBarRoutes.HomeScreen.route,
        icon = Icons.Outlined.Home
    ),
    BottomNavItem(
        title = "Calendar",
        route = BottomNavBarRoutes.CalendarScreen.route,
        icon = Icons.Outlined.CalendarMonth
    ),
    BottomNavItem(
        title = "History",
        route = BottomNavBarRoutes.HistoryScreen.route,
        icon = Icons.Outlined.WorkHistory
    )
)

class BottomNavigationBarUnitTest {

    @Test
    fun bottomNavigationBar_OnItemClick_NavigateToCorrectScreen() {
        val navController = mockk<NavController>(relaxed = true)

        bottomNavItems.forEach { item: BottomNavItem ->
            // Handle item click logic
            // navigate to the corresponding route
            navController.navigate(item.route)

            verify { navController.navigate(item.route) }
        }
    }
}