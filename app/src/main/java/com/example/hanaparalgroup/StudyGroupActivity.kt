package com.example.hanaparalgroup

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hanaparal.adapters.GroupAdapter
import com.example.hanaparal.models.StudyGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_study_group.* // Ensure layout matches

class StudyGroupActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_group)

        setupRecyclerView()
        listenForGroups()

        // Button to trigger Group Creation (Assume Aropo gave you an EditText for name/desc)
        btnCreateGroup.setOnClickListener {
            val name = etGroupName.text.toString()
            val desc = etGroupDesc.text.toString()
            if (name.isNotEmpty()) createGroup(name, desc)
        }
    }

    // --- TASK 1: CREATE GROUP ---
    private fun createGroup(name: String, description: String) {
        val user = auth.currentUser ?: return
        val groupId = db.collection("groups").document().id

        // Note: MaxMembers should be fetched from Cativo's Remote Config
        // Example: val max = remoteConfig.getLong("max_members")

        val newGroup = StudyGroup(
            groupId = groupId,
            name = name,
            description = description,
            adminId = user.uid,
            adminName = user.displayName ?: "Student",
            members = mutableListOf(user.uid), // Admin is the first member
            maxMembers = 10
        )

        db.collection("groups").document(groupId)
            .set(newGroup)
            .addOnSuccessListener {
                Toast.makeText(this, "Group Created Successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { Toast.makeText(this, "Error creating group", Toast.LENGTH_SHORT).show() }
    }

    // --- TASK 2: VIEW GROUPS (REAL-TIME) ---
    private fun listenForGroups() {
        db.collection("groups")
            .orderBy("createdAt")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener

                val groupList = value?.toObjects(StudyGroup::class.java) ?: emptyList()
                adapter.updateData(groupList)
            }
    }

    private fun setupRecyclerView() {
        adapter = GroupAdapter(emptyList()) { group ->
            joinGroup(group)
        }
        rvGroups.layoutManager = LinearLayoutManager(this)
        rvGroups.adapter = adapter
    }

    // --- TASK 3: JOIN GROUP ---
    private fun joinGroup(group: StudyGroup) {
        val userId = auth.currentUser?.uid ?: return

        // Check if full
        if (group.members.size >= group.maxMembers) {
            Toast.makeText(this, "Group is full!", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if already in group
        if (group.members.contains(userId)) {
            Toast.makeText(this, "You are already in this group!", Toast.LENGTH_SHORT).show()
            return
        }

        // Update Firestore
        db.collection("groups").document(group.groupId)
            .update("members", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                Toast.makeText(this, "Joined ${group.name}!", Toast.LENGTH_SHORT).show()
                // Alora's FCM trigger can go here
            }
    }
}