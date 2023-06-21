import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.Thesis_Project.ui.components.LeaveRequestCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LeaveRequestCardUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun leaveRequestCard_DisplaysCorrectText() {
        val leaveRequestData = com.example.Thesis_Project.backend.db.db_models.LeaveRequest(
            leaverequestid = "1",
            userid = "user1",
            leavestart = null,
            leaveend = null,
            duration = null,
            permissionflag = null,
            reason = "Vacation",
            approvedflag = true,
            approvedtime = null,
            approvedby = null,
            rejectedflag = false,
            rejectedtime = null,
            rejectedby = null,
            createdate = null
        )

        composeTestRule.setContent {
            LeaveRequestCard(leaveRequestData)
        }

        // Perform assertions using composeTestRule.onNode and other functions
        composeTestRule.onNodeWithText("Leave Request").assertExists()
        // Add more assertions as needed
    }
}
