package de.adorsys.android.shared

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query


object FirebaseProvider {
    fun createPost(post: Post, actionSuccess: (documentReference: DocumentReference) -> Unit, actionFailure: (e: Exception) -> Unit) {
        val fireStore = getFireStore()
        fireStore.collection("summerparty")
                .add(post)
                .addOnSuccessListener { actionSuccess(it) }
                .addOnFailureListener { actionFailure(it) }
    }

    fun getFireStore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        FirebaseFirestore.setLoggingEnabled(true)
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        firestore.firestoreSettings = settings
        return firestore
    }

    fun getFeed(): Query {
        return getFireStore()
                .collection("summerparty")
                .orderBy("timestamp", Query.Direction.DESCENDING)
    }
}