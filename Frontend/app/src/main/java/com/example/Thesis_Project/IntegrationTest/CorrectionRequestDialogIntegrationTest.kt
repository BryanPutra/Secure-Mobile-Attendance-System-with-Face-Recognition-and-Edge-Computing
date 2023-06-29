import com.example.Thesis_Project.backend.db.db_models.CorrectionRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CorrectionRequestDialogIntegrationTest {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun logUserInputs(
        userid: String,
        attendanceid: String,
        timein: String,
        timeout: String,
        leaveflag: Boolean,
        permissionflag: Boolean,
        presentflag: Boolean,
        reason: String
    ) {
        // Log user inputs
        println("User ID: $userid")
        println("Attendance ID: $attendanceid")
        println("Time In: $timein")
        println("Time Out: $timeout")
        println("Leave Flag: $leaveflag")
        println("Permission Flag: $permissionflag")
        println("Present Flag: $presentflag")
        println("Reason: $reason")

        // Create SimpleDateFormat with the desired format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Parse the timein and timeout strings into Date objects
        val timeinDate: Date? = dateFormat.parse(timein)
        val timeoutDate: Date? = dateFormat.parse(timeout)

        // Create correction request object
        val correctionRequest = CorrectionRequest(
            userid = userid,
            attendanceid = attendanceid,
            timein = timeinDate,
            timeout = timeoutDate,
            leaveflag = leaveflag,
            permissionflag = permissionflag,
            presentflag = presentflag,
            reason = reason
        )

        // Send data to Firebase and verify the result
        GlobalScope.launch(Dispatchers.IO) {
            firestore.collection("correction_requests")
                .add(correctionRequest)
                .addOnSuccessListener {
                    println("Data added to Firebase successfully")
                }
                .addOnFailureListener { exception ->
                    println("Failed to add data to Firebase: ${exception.message}")
                }
        }
    }
}
