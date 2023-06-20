import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.Thesis_Project.ui.components.ButtonHalfWidth
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ButtonHalfWidthUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buttonHalfWidth_shouldTriggerOnClick() {
        var isClicked = false

        composeTestRule.setContent {
            ButtonHalfWidth(onClick = { isClicked = true }, buttonText = "Click Me")
        }

        composeTestRule.onNodeWithText("Click Me").performClick()

        assertEquals(true, isClicked)
    }
}
