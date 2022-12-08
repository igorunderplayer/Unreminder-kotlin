package com.igorunderplayer.unreminder

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.igorunderplayer.unreminder.models.PostReminderObject
import com.igorunderplayer.unreminder.models.Reminder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.*


class CreateReminderDialogFragment : DialogFragment() {

    private lateinit var okClient: OkHttpClient
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var titleInput: EditText
    private lateinit var detailsInput: EditText

    private lateinit var setDateButton: Button
    private lateinit var setTimeButton: Button

    private lateinit var calendar: Calendar

    private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        okClient = OkHttpClient()
        db = Firebase.firestore
        auth = Firebase.auth

        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_createreminder, null)

        titleInput = dialogView.findViewById(R.id.createReminderTitle)
        detailsInput = dialogView.findViewById(R.id.createReminderDetails)

        setDateButton = dialogView.findViewById(R.id.setDateButton)
        setTimeButton = dialogView.findViewById(R.id.setTimeButton)

        calendar = Calendar.getInstance()


        setDateButton.setOnClickListener {
            val dateTimePickerDialog = DatePickerDialog(dialogView.context)
            
            dateTimePickerDialog.setOnDateSetListener { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
            }

            dateTimePickerDialog.show()
        }

        setTimeButton.setOnClickListener {
            val actualHour = calendar.get(Calendar.HOUR)
            val actualMinute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(dialogView.context, { _, hour, minute ->
                calendar.set(Calendar.HOUR, hour)
                calendar.set(Calendar.MINUTE, minute)
            }, actualHour, actualMinute, true)

            timePickerDialog.show()
        }

        builder.setView(dialogView)
            .setPositiveButton("Criar") { _, _ ->

                Toast.makeText(activity, "Criando...", Toast.LENGTH_LONG).show()

                createReminder()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                dialog?.cancel()
            }


        return builder.create()
    }

    private fun createReminder() {
        val gson = Gson()
        val userId = auth.currentUser?.uid

        val reminderData = Reminder()
        reminderData.title = titleInput.text.toString()
        reminderData.details = detailsInput.text.toString()
        reminderData.id = UUID.randomUUID().toString()
        reminderData.createdAt = Timestamp(Calendar.getInstance().time)
        reminderData.date = Timestamp(calendar.time)

        createDatabaseReminder(reminderData)

        Log.d("Data em string", calendar.time.toString())

        if (userId == null) return

        val obj = PostReminderObject(userId, reminderData.title, calendar.time.toString(), mapOf(
            "en" to reminderData.title
        ))

        val notificationApiUrl = resources.getString(R.string.notification_api_url)
        val json = gson.toJson(obj)

        val requestBody = json.toRequestBody(JSON)

        Log.d("objeto reminder",json)

        val request = Request.Builder()
            .url("$notificationApiUrl/schedule")
            .post(requestBody)
            .build()

        okClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.let { Log.d("Notification api response", it.string()) }
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error on notification api", e.toString())
            }
        })


    }

    private fun createDatabaseReminder(data: Reminder) {
        val userId = auth.currentUser?.uid
        db.collection("users/$userId/reminders")
            .document(data.id)
            .set(data)
            .addOnSuccessListener {
                Log.d("Documento criadao", "Lembrete adidcionado ao banco de dados")
            }
    }
}