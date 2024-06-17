package com.example.plantios.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.plantios.data.model.UserData
import com.example.plantios.repository.ProfileRepository

class UserDataViewModel : ViewModel() {
    private val profileRepository = ProfileRepository()

    fun getUserData(): LiveData<UserData> {
        return profileRepository.getUserData()
    }

    fun saveUserData(userData: UserData) {
        profileRepository.saveUserData(userData)
    }

    fun uploadImage(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
//        profileRepository.uploadImage(uri, onSuccess, onFailure)
    }

    fun updateProfile(userData: UserData, downloadUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        profileRepository.updateProfile(userData, downloadUrl, onSuccess, onFailure)
    }
}
