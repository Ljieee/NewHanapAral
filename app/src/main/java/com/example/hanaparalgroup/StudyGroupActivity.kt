package com.example.hanaparalgroup

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hanaparalgroup.adapters.GroupAdapter
import com.example.hanaparalgroup.data.models.StudyGroup
import com.example.hanaparalgroup.databinding.ActivityStudyGroupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class StudyGroupActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: GroupAdapter
    private lateinit var binding: ActivityStudyGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        listenForGroups()

        binding.btnCreateGroup.setOnClickListener {
            val name = binding.etGroupName.text.toString()
            val desc = binding.etGroupDesc.text.toString()
            if (name.isNotEmpty()) {
                createGroup(name, desc)
            } else {
                Toast.makeText(this, "Please enter a group name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- TASK 1: CREATE GROUP ---
    private fun createGroup(name: String, description: String) {
        val user = auth.currentUser ?: return
        val groupId = db.collection("groups").document().id

        val newGroup = StudyGroup(
            groupId = groupId,
            name = name,
            description = description,
            adminId = user.uid,
            adminName = user.displayName ?: "Student",
            members = mutableListOf(user.uid),
            maxMembers = 10,
            createdAt = System.currentTimeMillis()
        )

        db.collection("groups").document(groupId)
            .set(newGroup)
            .addOnSuccessListener {
                Toast.makeText(this, "Group Created Successfully!", Toast.LENGTH_SHORT).show()
                binding.etGroupName.text.clear()
                binding.etGroupDesc.text.clear()
            }
            .addOnFailureListener { Toast.makeText(this, "Error creating group", Toast.LENGTH_SHORT).show() }
    }

    // --- TASK 2: VIEW GROUPS (REAL-TIME) ---
    private fun listenForGroups() {
        db.collection("groups")
            .orderBy("createdAt")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Error loading groups: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val groupList = value?.toObjects(StudyGroup::class.java) ?: emptyList()
                adapter.updateData(groupList)
            }
    }

    private fun setupRecyclerView() {
        adapter = GroupAdapter(emptyList()) { group ->
            joinGroup(group)
        }
        binding.rvGroups.layoutManager = LinearLayoutManager(this)
        binding.rvGroups.adapter = adapter
    }

    // --- TASK 3: JOIN GROUP ---
    private fun joinGroup(group: StudyGroup) {
        val userId = auth.currentUser?.uid ?: return

        if (group.members.size >= group.maxMembers) {
            Toast.makeText(this, "Group is full!", Toast.LENGTH_SHORT).show()
            return
        }

        if (group.members.contains(userId)) {
            Toast.makeText(this, "You are already in this group!", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("groups").document(group.groupId)
            .update("members", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                Toast.makeText(this, "Joined ${group.name}!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to join group", Toast.LENGTH_SHORT).show()
            }
    }
}