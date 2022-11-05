package com.igorunderplayer.unreminder

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.igorunderplayer.unreminder.models.Reminder
import java.util.UUID

class CreateReminderDialogFragment : DialogFragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var titleInput: EditText
    private lateinit var detailsInput: EditText


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        db = Firebase.firestore
        auth = Firebase.auth

        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_createreminder, null)

        titleInput = dialogView.findViewById(R.id.createReminderTitle)
        detailsInput = dialogView.findViewById(R.id.createReminderDetails)

        builder.setView(dialogView)
            .setPositiveButton("Criar") { _, _ ->

                Toast.makeText(activity, "Criando...", Toast.LENGTH_LONG).show()

                val userId = auth.currentUser?.uid

                val reminderData = Reminder()
                reminderData.title = titleInput.text.toString()
                reminderData.details = detailsInput.text.toString()
                reminderData.id = UUID.randomUUID().toString()


                db.collection("users/$userId/reminders").add(reminderData).addOnSuccessListener {
                    Log.d("-==-=-=-=-=-=-=- Criado", "documento criado")
                }
            }
            .setNegativeButton("Cancelar") { _, _ ->
                Toast.makeText(activity, "cancelando", Toast.LENGTH_SHORT).show()
                getDialog()?.cancel()
            }


        return builder.create()
    }
}