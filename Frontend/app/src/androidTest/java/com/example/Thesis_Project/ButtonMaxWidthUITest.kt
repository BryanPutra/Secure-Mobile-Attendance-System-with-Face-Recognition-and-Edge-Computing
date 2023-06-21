import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.Thesis_Project.ui.components.ButtonMaxWidth
import org.junit.Rule
import org.junit.Test

class ButtonMaxWidthUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buttonMaxWidth_shouldTriggerOnClick() {
        var isClicked = false

        composeTestRule.setContent {
            ButtonMaxWidth(onClickCallback = { isClicked = true }, buttonText = "Click Me")
        }

        // Verify that the button is displayed
        composeTestRule.onNodeWithText("Click Me").assertIsDisplayed()

        // Perform a click on the button
        composeTestRule.onNodeWithText("Click Me").performClick()

        // Assert that the button click callback is triggered
        assert(isClicked)
    }
}
