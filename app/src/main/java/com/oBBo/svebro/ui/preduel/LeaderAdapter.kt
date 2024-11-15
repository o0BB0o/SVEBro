package com.oBBo.svebro.ui.preduel

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oBBo.svebro.R
import com.oBBo.svebro.databinding.ItemLeaderBinding
import com.oBBo.svebro.model.Leader

class LeaderAdapter(private var leaders: List<Leader>,
                    private val onLeaderSelected: (Leader) -> Unit,
                    private val onDeleteClick: (Leader) -> Unit): RecyclerView.Adapter<LeaderAdapter.LeaderViewHolder>() {

    inner class LeaderViewHolder(private val binding: ItemLeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(leader: Leader) {
            // Bind your data to the views
            binding.recyclerLeaderName.text = leader.name
            binding.root.setOnClickListener { onLeaderSelected(leader) }
            binding.deleteLeader.setOnClickListener {
                showDeleteConfirmationDialog(leader)
            }
            Glide.with(binding.recyclerLeaderImage.context)
                .load(leader.bgPath)
                .into(binding.recyclerLeaderImage)
        }

        private fun showDeleteConfirmationDialog(leader: Leader) {
            val builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle("Delete Leader")
                .setMessage("Are you sure you want to delete this leader?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    removeLeaderAtPosition(leaders.indexOf(leader))
                    onDeleteClick(leader)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderViewHolder {
        val binding = ItemLeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderViewHolder, position: Int) {
        val leader = leaders[position]
        holder.bind(leader)
    }

    override fun getItemCount(): Int {
        return leaders.size
    }


    fun removeLeaderAtPosition(position: Int) {
        leaders = leaders.toMutableList().apply {
            removeAt(position)
        }
        notifyItemRemoved(position)
    }

    fun updateLeaders(newLeaders: List<Leader>) {
        leaders = newLeaders
        notifyDataSetChanged() // Refresh the RecyclerView with new data
    }

}