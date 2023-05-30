package com.example.Thesis_Project.backend.db.db_models

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

object db_util {
    fun GetUser(db: FirebaseFirestore, userid: String): User {
        var user:User = User();
        db.collection("users").document(userid).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {
                user = documentSnapshot.toObject<User>()!!;
                return@addOnSuccessListener;
            }
        };
        return user;
    }
}