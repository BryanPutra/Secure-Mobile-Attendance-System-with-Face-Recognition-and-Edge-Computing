package com.example.Thesis_Project.backend.db

import android.util.Log
import com.example.Thesis_Project.backend.db.db_models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

object db_util {

    fun getUser(db: FirebaseFirestore, userId: String, callback: (User?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject<User>()
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error Fetching Data", "getUser: $exception")
                callback(null)
            }
    }
}