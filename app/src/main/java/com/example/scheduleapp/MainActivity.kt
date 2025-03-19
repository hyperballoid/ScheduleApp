package com.example.scheduleapp

import android.media.RingtoneManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var namesInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var daysInput: EditText
    private lateinit var shiftDurationInput: EditText
    private lateinit var generateButton: Button
    private lateinit var scheduleText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        namesInput = findViewById(R.id.namesInput)
        dateInput = findViewById(R.id.dateInput)
        daysInput = findViewById(R.id.daysInput)
        shiftDurationInput = findViewById(R.id.shiftDurationInput)
        generateButton = findViewById(R.id.generateButton)
        scheduleText = findViewById(R.id.scheduleText)

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = sdf.format(Date())
        namesInput.setText("Кібал, Дрон, Вепр, Льолік, Гора")
        dateInput.setText(today)
        daysInput.setText("4")
        shiftDurationInput.setText("3")

        generateButton.setOnClickListener {
            generateSchedule()
        }
    }

    private fun generateSchedule() {
        val names = namesInput.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val startDate: Date = try {
            sdf.parse(dateInput.text.toString()) ?: return
        } catch (e: Exception) {
            scheduleText.text = "Помилка формату дати!"
            return
        }
        val days = daysInput.text.toString().toIntOrNull() ?: return
        val shiftHours = shiftDurationInput.text.toString().toIntOrNull() ?: return

        val calendar = Calendar.getInstance().apply { time = startDate }
        val endDate = Calendar.getInstance().apply { time = startDate; add(Calendar.DAY_OF_YEAR, days) }
        val schedule = StringBuilder()
        var nameIndex = 0

        val monthsUA = mapOf(
            "January" to "січня", "February" to "лютого", "March" to "березня",
            "April" to "квітня", "May" to "травня", "June" to "червня",
            "July" to "липня", "August" to "серпня", "September" to "вересня",
            "October" to "жовтня", "November" to "листопада", "December" to "грудня"
        )
        val daysUA = listOf("Понеділок", "Вівторок", "Середа", "Четвер", "П’ятниця", "Субота", "Неділя")

        while (calendar.before(endDate)) {
            val monthUA = monthsUA[SimpleDateFormat("MMMM", Locale.ENGLISH).format(calendar.time)] ?: ""
            val dayOfWeek = daysUA[calendar.get(Calendar.DAY_OF_WEEK) - 1]
            schedule.append("\n${calendar.get(Calendar.DAY_OF_MONTH)} $monthUA ${calendar.get(Calendar.YEAR)}, $dayOfWeek\n")

            val dayEnd = Calendar.getInstance().apply { time = calendar.time; add(Calendar.HOUR_OF_DAY, 24) }
            while (calendar.before(dayEnd) && calendar.before(endDate)) {
                val shiftEnd = Calendar.getInstance().apply { time = calendar.time; add(Calendar.HOUR_OF_DAY, shiftHours) }
                schedule.append("${SimpleDateFormat("HH:mm").format(calendar.time)} - " +
                        "${SimpleDateFormat("HH:mm").format(shiftEnd.time)}: ${names[nameIndex]}\n")
                calendar.time = shiftEnd.time
                nameIndex = (nameIndex + 1) % names.size
            }
        }

        scheduleText.text = schedule.toString().trim()
    }
}