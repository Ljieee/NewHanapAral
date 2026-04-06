// GroupAdapter.kt
package com.example.hanaparalgroup.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hanaparalgroup.R
import com.example.hanaparalgroup.data.models.StudyGroup

class GroupAdapter(
    private var groups: List<StudyGroup>,
    private val onJoinClicked: (StudyGroup) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvGroupName)
        val desc: TextView = view.findViewById(R.id.tvDescription)
        val count: TextView = view.findViewById(R.id.tvMemberCount)
        val btnJoin: Button = view.findViewById(R.id.btnJoin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.name.text = group.name
        holder.desc.text = group.description
        holder.count.text = "Members: ${group.members.size} / ${group.maxMembers}"

        holder.btnJoin.setOnClickListener { onJoinClicked(group) }
    }

    override fun getItemCount() = groups.size

    fun updateData(newGroups: List<StudyGroup>) {
        this.groups = newGroups
        notifyDataSetChanged()
    }
}