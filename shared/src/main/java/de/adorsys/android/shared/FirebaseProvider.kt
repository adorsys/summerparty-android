package de.adorsys.android.shared

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.io.File




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

    fun uploadImage(
            file: File,
            progressAction: (progress: Int) -> Unit,
            successAction: (storageReference: String) -> Unit,
            failureAction: (e: Exception) -> Unit) {

        if (file.path.isNullOrBlank()) {
            Log.e("TAG_IMAGE_UPLOAD", "imagePath is empty or null")
            return
        }

        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val imagesReference = storageReference.child("images/${file.name}")

        val uploadTask = imagesReference.putFile(Uri.fromFile(file))
        uploadTask
                .addOnProgressListener {
                    val rawProgress = 100.0 * it.bytesTransferred / it.totalByteCount
                    val progress = Math.round(rawProgress).toInt()
                    progressAction(progress)
                }
                .addOnSuccessListener {
                    successAction(it.storage.path)
                }
                .addOnFailureListener(failureAction)
    }

    fun downloadImage(
            fileReference: String?,
            successAction: (file: File) -> Unit,
            failureAction: (e: Exception) -> Unit) {

        if (fileReference.isNullOrBlank()) {
            Log.e("TAG_IMAGE_DOWNLOAD", "imagePath is empty or null")
            return
        }

        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val imagesReference = storageReference.child(fileReference!!)

        val localFile = File.createTempFile("images", "jpg")

        imagesReference.getFile(localFile)
                .addOnSuccessListener {
                    successAction(localFile)
                }
                .addOnFailureListener(failureAction)
    }
}