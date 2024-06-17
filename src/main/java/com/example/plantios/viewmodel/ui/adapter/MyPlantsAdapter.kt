package com.example.plantios.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantios.data.model.MyPlants
import com.example.plantios.databinding.PlantsDesignViewBinding

class MyPlantsAdapter(
    private val context: Context,
    private val myPlantsList: List<MyPlants>
) : RecyclerView.Adapter<MyPlantsAdapter.MyPlantsViewHolder>() {

    class MyPlantsViewHolder(val binding: PlantsDesignViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlantsViewHolder {
        val binding = PlantsDesignViewBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyPlantsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return myPlantsList.size
    }

    override fun onBindViewHolder(holder: MyPlantsViewHolder, position: Int) {
        val myPlants = myPlantsList[position]
        with(holder.binding) {
//            plantName.text = myPlants.plantTitle
        }
    }
}
