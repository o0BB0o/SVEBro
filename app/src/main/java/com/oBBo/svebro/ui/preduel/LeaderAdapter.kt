package com.oBBo.svebro.ui.preduel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oBBo.svebro.R
import com.oBBo.svebro.model.Leader

class LeaderAdapter(private var leaders: List<Leader>,
    private val onLeaderSelected: (Leader) -> Unit): RecyclerView.Adapter<LeaderAdapter.LeaderViewHolder>() {

    class LeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.recycler_leader_name)
        val characterImageView: ImageView = view.findViewById(R.id.recycler_leader_image)
        val leaderSelectionView: TextView = view.findViewById(R.id.recycler_leader_select_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leader, parent, false)
        return LeaderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return leaders.size
    }

    override fun onBindViewHolder(holder: LeaderViewHolder, position: Int) {
        val character = leaders[position]
        holder.nameTextView.text = character.name
        // Load the image using Glide, Picasso, or any other image loading library
        Glide.with(holder.itemView.context)
            .load(character.bgPath)
            .into(holder.characterImageView)

        // Handle character selection
        holder.leaderSelectionView.setOnClickListener {
            onLeaderSelected(character)
        }
    }

    fun updateLeaders(newLeaders: List<Leader>) {
        leaders = newLeaders
        notifyDataSetChanged() // Refresh the RecyclerView with new data
    }

}