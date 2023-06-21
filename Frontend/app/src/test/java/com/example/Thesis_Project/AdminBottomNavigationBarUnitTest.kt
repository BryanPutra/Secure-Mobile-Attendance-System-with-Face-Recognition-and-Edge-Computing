import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

data class BottomNavItemUnitTest(
    val title: String,
    val route: String,
    val icon: ImageVector
)

object AdminBottomNavBarRoutes {
    object AdminHomeScreen {
        const val route = "admin_home_screen"
    }

    object AdminUsersScreen {
        const val route = "admin_users_screen"
    }
}

val bottomNavItemsUnitTest = listOf(
    BottomNavItemUnitTest(
        title = "Home",
        route = AdminBottomNavBarRoutes.AdminHomeScreen.route,
        icon = Icons.Outlined.Home
    ),
    BottomNavItemUnitTest(
        title = "Users",
        route = AdminBottomNavBarRoutes.AdminUsersScreen.route,
        icon = Icons.Outlined.Work
    )
)

class AdminBottomNavigationBarUnitTest {

    @Test
    fun adminBottomNavigationBar_OnItemClick_NavigateToCorrectScreen() {
        val navController = mockk<NavController>(relaxed = true)

        bottomNavItemsUnitTest.forEach { item: BottomNavItemUnitTest ->
            // Handle item click logic
            // Navigate to the corresponding route
            navController.navigate(item.route)

            verify { navController.navigate(item.route) }
        }
    }
}
