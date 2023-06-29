import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object AdminCreateUserDialogIntegrationTest {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun logUserInputs(name: String, email: String, password: String, adminFlag: Boolean) {
        // Log user inputs
        println("Name: $name")
        println("Email: $email")
        println("Password: $password")
        println("Admin Flag: $adminFlag")

        // Perform integration test
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "password" to password,
            "adminFlag" to adminFlag
        )

        // Send data to Firebase and verify the result
        GlobalScope.launch(Dispatchers.IO) {
            firestore.collection("users")
                .add(user)
                .addOnSuccessListener {
                    println("Data added to Firebase successfully")
                }
                .addOnFailureListener { exception ->
                    println("Failed to add data to Firebase: ${exception.message}")
                }
        }
    }
}

//UNCOMMENT IF WANT TO RUN, and UNCOMMENT AdminCreateUserDialogIntegrationTest.logUserInputs TO RUN INTEGRATION TEST IN ADMINCREATEUSERDIALOG.KT
