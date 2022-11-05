package com.igorunderplayer.unreminder


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var remindersListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        remindersListView = findViewById<ListView>(R.id.remindersList)

        remindersListView.setOnItemClickListener { parent, view, position, id ->
            val reminder = remindersListView.getChildAt(position)

            if (reminder is ViewGroup) {
                val detailsElement = reminder.getChildAt(1)
                val newVisibility = if (detailsElement.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                detailsElement.visibility = newVisibility
            }
        }


        auth = Firebase.auth
        db = Firebase.firestore

        if (auth.currentUser == null) {
            Log.e ("Errinho", "cade o usuaru?")
        }

        db.collection("users/${auth.currentUser?.uid}/reminders").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val titles = snapshot.documents.map { doc ->
                    val title = doc.data!!["title"]?.toString()
                    val details = doc.data!!["details"]?.toString()
                    mapOf("title" to title, "details" to details)
                }

                val from = arrayOf("title", "details")
                val to = intArrayOf(R.id.reminderTitle, R.id.reminderDetails)

                val adapter = SimpleAdapter(this, titles, R.layout.reminder, from, to)
                remindersListView.adapter = adapter
            }
        }


        findViewById<Button>(R.id.signOutButton).setOnClickListener {
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}