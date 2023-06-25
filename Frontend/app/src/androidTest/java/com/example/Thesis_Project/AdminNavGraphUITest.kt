import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.Thesis_Project.ui.navgraphs.AdminNavGraph
import com.example.Thesis_Project.viewmodel.MainViewModel
import org.junit.Rule
import org.junit.Test

class AdminNavGraphTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAdminNavGraph() {
        val mainViewModel = MainViewModel()

        composeTestRule.setContent {
            AdminNavGraph(
                rootNavController = NavHostController(LocalContext.current),
                navController = NavHostController(LocalContext.current),
                mainViewModel = mainViewModel
            )
        }

        // Perform actions to navigate to a specific destination
        Espresso.onView(ViewMatchers.withText("Navigate")).perform(ViewActions.click())

        // Verify that the expected destination is reached
        Espresso.onView(ViewMatchers.withText("Destination Fragment")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
