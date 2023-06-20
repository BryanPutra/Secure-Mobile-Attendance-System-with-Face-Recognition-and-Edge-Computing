import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.Thesis_Project.ui.components.CalendarStatus
import com.example.Thesis_Project.ui.component_item_model.CalendarStatusItem
import com.example.Thesis_Project.R
import org.junit.Rule
import org.junit.Test

class CalendarStatusUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun calendarStatus_DisplayedCorrectly() {
        val statusItem = CalendarStatusItem("Red", R.color.red_800)

        composeTestRule.setContent {
            CalendarStatus(statusItem = statusItem)
        }

        composeTestRule.onNodeWithText("Red")
            .assertIsDisplayed()

        // Assert that the Canvas element exists by checking one of its properties
        composeTestRule.onRoot()
            .printToLog("Tree") // Replace with an appropriate assertion or action
    }
}
