package com.example.Thesis_Project.backend.db.db_models

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject

object db_util {

    fun GetUser(db: FirebaseFirestore, userid: String): User {
        var user:User = User();
        try {
            db.collection("users").document(userid).get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    user = documentSnapshot.toObject<User>()!!;
                    return@addOnSuccessListener;
                }
            };
        }
        catch (e: FirebaseFirestoreException) {
            Log.d("Error Fetching Data", "getUser: $e")
        }
        return user;
    }
}