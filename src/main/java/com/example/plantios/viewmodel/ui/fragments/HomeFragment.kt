package com.example.plantios.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.plantios.data.model.MyPlants
import com.example.plantios.databinding.FragmentHomeBinding
import com.example.plantios.ui.adapter.MyPlantsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private val myPlants = mutableListOf<MyPlants>()
    private lateinit var myPlantsAdapter: MyPlantsAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance().getReference("my_plants")

        setupImageSlider()
        setupRecyclerView()
    }

    private fun setupImageSlider() {
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel("https://bit.ly/2YoJ77H", ScaleTypes.FIT))
        imageList.add(SlideModel("https://bit.ly/2BteuF2", ScaleTypes.FIT))
        imageList.add(SlideModel("https://bit.ly/3fLJf72", ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.startSliding(2000)
    }

    private fun setupRecyclerView() {
        myPlantsAdapter = MyPlantsAdapter(requireActivity(), myPlants)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = myPlantsAdapter
        }
        fetchPlantsData()
    }

    private fun fetchPlantsData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                myPlants.clear()
                Log.d("HomeFragment", "Data snapshot exists: ${snapshot.exists()}")
                if (snapshot.exists()) {
                    for (plantsSnapshot in snapshot.children) {
                        Log.d("HomeFragment", "Snapshot Key: ${plantsSnapshot.key}, Value: ${plantsSnapshot.value}")
                        val plant = plantsSnapshot.getValue(MyPlants::class.java)
                        if (plant != null) {
                            Log.d("HomeFragment", "Plant: $plant")
                            myPlants.add(plant)
                        } else {
                            Log.e("HomeFragment", "Null plant found in snapshot: ${plantsSnapshot.value}")
                        }
                    }
                    myPlantsAdapter.notifyDataSetChanged()
                } else {
                    Log.d("HomeFragment", "No plants found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Database error: ${error.message}")
            }
        })
    }
}
