import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.example.Thesis_Project.ui.components.CorrectionRequestCard
import org.junit.Rule
import org.junit.Test
import java.util.*

class CorrectionRequestCardUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun correctionRequestCard_DisplaysCorrectText() {
        val correctionRequest = CorrectionRequest(
            reason = "Sample reason 1"
        )

        composeTestRule.setContent {
            CorrectionRequestCard(correctionRequest)
        }

        // Perform assertions using composeTestRule.onNode and other functions
        composeTestRule.onNodeWithText("Correction Request").assertExists()
        composeTestRule.onNodeWithText(correctionRequest.reason ?: "").assertExists()
        // Add more assertions as needed
    }
}
