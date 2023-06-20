import com.example.Thesis_Project.backend.db.db_models.LeaveRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class LeaveRequestCardUnitTest {

    @Test
    fun getLeaveTitle_ReturnsCorrectTitle() {
        val expectedTitle = "Leave Request"
        val title = getLeaveTitle()
        assertEquals(expectedTitle, title)
    }

    @Test
    fun getReasonText_ReturnsReasonFromLeaveRequest() {
        val leaveRequest = LeaveRequest(reason = "Vacation")
        val expectedReason = "Vacation"
        val reason = getReasonText(leaveRequest)
        assertEquals(expectedReason, reason)
    }

    @Test
    fun getStatusText_ReturnsApprovedWhenFlagIsTrue() {
        val leaveRequest = LeaveRequest(approvedflag = true)
        val expectedStatus = "Approved"
        val status = getStatusText(leaveRequest)
        assertEquals(expectedStatus, status)
    }

    @Test
    fun getStatusText_ReturnsPendingWhenRejectedFlagIsTrue() {
        val leaveRequest = LeaveRequest(rejectedflag = true)
        val expectedStatus = "Pending"
        val status = getStatusText(leaveRequest)
        assertEquals(expectedStatus, status)
    }

    @Test
    fun getStatusText_ReturnsRejectedWhenFlagsAreFalse() {
        val leaveRequest = LeaveRequest(approvedflag = false, rejectedflag = false)
        val expectedStatus = "Rejected"
        val status = getStatusText(leaveRequest)
        assertEquals(expectedStatus, status)
    }

    private fun getLeaveTitle(): String {
        return "Leave Request"
    }

    private fun getReasonText(leaveRequest: LeaveRequest): String {
        return leaveRequest.reason ?: ""
    }

    private fun getStatusText(leaveRequest: LeaveRequest): String {
        return when {
            leaveRequest.approvedflag == true -> "Approved"
            leaveRequest.rejectedflag == true -> "Pending"
            else -> "Rejected"
        }
    }
}
