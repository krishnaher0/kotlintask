package com.example.plantios.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.plantios.data.model.UserData
import com.example.plantios.databinding.FragmentProfileBinding
import com.example.plantios.ui.activities.LoginActivity
import com.example.plantios.viewmodel.UserDataViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.core.Tag
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var profileViewModel: UserDataViewModel
    private var selectedImageUri: Uri? = null
    private var oldImageUrl: String? = null
    private var oldImageName: String? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        profileViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        mAuth = FirebaseAuth.getInstance()

        loadUserData()

        binding.saveButton.setOnClickListener {
            val userData = getUserData()
            if (selectedImageUri != null) {
                profileViewModel.uploadImage(selectedImageUri!!, { downloadUrl ->
                    profileViewModel.updateProfile(userData, downloadUrl, {
                        Toast.makeText(requireActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }, { e ->
                        Log.e("ProfileFragment", "Failed to update profile!", e)
                        Toast.makeText(requireActivity(), "Failed to upload image!", Toast.LENGTH_SHORT).show()
                    })
                }, { e ->
                    Log.e("YourProfileActivity", "Failed to upload image", e)
                    Toast.makeText(requireActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                })
            } else {
                profileViewModel.saveUserData(userData)
                Toast.makeText(requireActivity(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.profilePicture.setOnClickListener {
            requestStoragePermission()
            openGallery()
        }

        binding.deleteAccBtn.setOnClickListener {
            showDeleteAccountConfirmationDialog()
        }

        return binding.root
    }

    private fun loadUserData() {
        profileViewModel.getUserData().observe(requireActivity()) { userProfileData ->
            userProfileData?.let {
                binding.editProfileName.setText(it.fullName)
                binding.editEmail.setText(it.emailAddress)
                it.imageUrl?.let { url ->
                    oldImageUrl = url
                    Glide.with(this).load(url).into(binding.profilePicture)
                }
                oldImageName = it.imageName
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            selectedImageUri = data?.data
            binding.profilePicture.setImageURI(selectedImageUri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE_PERMISSION)
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun getUserData(): UserData {
        val fullName = binding.editProfileName.text.toString()
        val email = binding.editEmail.text.toString()
        val contactNumber = binding.editContact.text.toString()
        val shippingAddress = binding.editShippingAddress.text.toString()
        val bio = binding.editBio.text.toString()
        return UserData(
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            fullName = fullName,
            emailAddress = email,
            imageName = oldImageName,
            imageUrl = oldImageUrl
        )
    }

    private fun showDeleteAccountConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.")

        builder.setPositiveButton("Yes") { dialogInterface, which ->
            deleteAccount()
        }

        builder.setNegativeButton("No") { dialogInterface, which ->
            // Do nothing, dismiss the dialog
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteAccount() {
        val user = mAuth.currentUser
        Log.d("currentUser: ", user.toString())
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Account deleted successfully.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete account. Try again later.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
    }
}
