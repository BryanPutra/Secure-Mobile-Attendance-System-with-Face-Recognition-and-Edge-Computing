import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.navigation.compose.rememberNavController
import com.example.Thesis_Project.routes.AdminBottomNavBarRoutes
import com.example.Thesis_Project.ui.navgraphs.AdminNavGraph

import com.example.Thesis_Project.viewmodel.MainViewModel
import org.junit.Rule
import org.junit.Test

class AdminNavGraphTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @Composable
    fun testAdminNavGraph() {
        val navController = rememberNavController()
        val mainViewModel = MainViewModel()

        AdminNavGraph(navController, navController, mainViewModel)

        // Verify that the initial screen is AdminHomeScreen
        composeTestRule.onNodeWithContentDescription("Admin Home Screen").assertExists()

        // Simulate navigation to AdminUsersScreen
        navController.navigate(AdminBottomNavBarRoutes.AdminUsersScreen.route)

        // Verify that the destination screen is AdminUsersScreen
        composeTestRule.onNodeWithContentDescription("Admin Users Screen").assertExists()
    }
}
