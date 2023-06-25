import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseIntegrationTest {

    private lateinit var db: FirebaseFirestore

    @Before
    fun setup() {
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()
    }

    @Test
    fun testSendAndReceiveData() {
        // Send data to Firestore
        val data = hashMapOf(
            "message" to "Hello, Firebase!"
        )
        db.collection("messages").document("1")
            .set(data)
            .addOnSuccessListener {
                // Data sent successfully, now retrieve it
                db.collection("messages").document("1")
                    .get()
                    .addOnSuccessListener { document ->
                        val message = document.getString("message")
                        assertEquals("Hello, Firebase!", message)
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                    }
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }
}
