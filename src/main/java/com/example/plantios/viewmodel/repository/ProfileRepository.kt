package com.example.plantios.repository
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.plantios.data.model.UserData
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileRepository {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users").child(mAuth.currentUser?.uid ?: "")
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("profile_images").child(mAuth.currentUser?.uid ?: "")
    private val userDataLiveData = MutableLiveData<UserData>()

    fun getUserData(): LiveData<UserData> {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(UserData::class.java)
                    userData?.let {
                        userDataLiveData.postValue(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        return userDataLiveData
    }

    fun saveUserData(userData: UserData) {
        database.setValue(userData).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                // Handle success
//            } else {
//                // Handle failure
//            }
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val imageName = System.currentTimeMillis().toString() + ".jpg"
        val imageRef = storageReference.child(imageName)

        imageRef.putFile(uri).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                onSuccess(downloadUrl.toString())
            }.addOnFailureListener { e ->
                onFailure(e)
            }
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

    fun updateProfile(userData: UserData, downloadUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(downloadUrl))
            .setDisplayName(userData.fullName)
            .build()

        mAuth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveUserData(userData)
                onSuccess()
            } else {
                task.exception?.let { onFailure(it) }
            }
        }
    }
}
