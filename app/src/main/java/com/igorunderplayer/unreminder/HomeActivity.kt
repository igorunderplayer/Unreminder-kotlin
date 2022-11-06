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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.igorunderplayer.unreminder.models.Reminder


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var remindersListView: ListView

    private lateinit var reminders: List<Reminder>

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        remindersListView = findViewById(R.id.remindersList)

        remindersListView.setOnItemClickListener { parent, view, position, id ->
            Log.d("AAAAAAAAAA", "clickeduofbasd")
            val reminder = remindersListView.getChildAt(position)

            if (reminder is ViewGroup) {
                val detailsElement = reminder.getChildAt(1) as ViewGroup
                val newVisibility = if (detailsElement.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                detailsElement.visibility = newVisibility

                val deleteButton = detailsElement.getChildAt(1) as Button

                deleteButton.setOnClickListener {
                    Log.d("Clicasse no delete ein", "!!!!!!!!!!!!!!!!!!!")
                    deleteReminder(reminders[position].id)
                }

            }
        }


        auth = Firebase.auth
        db = Firebase.firestore

        if (auth.currentUser == null) {
            Log.e ("Errinho", "cade o usuaru?")
        }

        db.collection("users/${auth.currentUser?.uid}/reminders").orderBy("createdAt", Query.Direction.DESCENDING).addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                reminders = snapshot.documents.map { doc ->
                    val reminder = Reminder()
                    reminder.title = doc.data!!["title"].toString()
                    reminder.details = doc.data!!["details"].toString()
                    reminder.id = doc.data!!["id"]!!.toString()
                    reminder.createdAt = doc.data!!["createdAt"] as Timestamp
                    reminder.date = doc.data!!["date"] as Timestamp
                    reminder
                }

                val from = arrayOf("title", "details")
                val to = intArrayOf(R.id.reminderTitle, R.id.reminderDetails)

                val data = reminders.map { reminder ->
                    mapOf(
                        "title" to reminder.title,
                        "details" to reminder.details
                    )
                }

                val adapter = SimpleAdapter(this, data, R.layout.reminder, from, to)
                remindersListView.adapter = adapter
            }
        }


        findViewById<Button>(R.id.signOutButton).setOnClickListener {
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.addReminderButton).setOnClickListener {
            val dialog = CreateReminderDialogFragment()
            dialog.show(supportFragmentManager, "CreateReminderDialogFragment")
        }
    }

    private fun deleteReminder(id: String) {
        Log.d("!!!", "deletando...")
        val userId = auth.currentUser?.uid
        db.collection("users/$userId/reminders")
            .document(id)
            .delete()
            .addOnSuccessListener { Log.d("aágou", "DocumentSnapshot successfully deleted!") }
    }
}