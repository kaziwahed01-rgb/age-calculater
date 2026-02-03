package com.blackcow.agecalculater

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess

@Composable
fun AgeCalculatorScreen() {
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    var age by remember { mutableStateOf<Period?>(null) }
    var dateInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            val picked = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth)
            birthDate = picked
            age = Period.between(picked, LocalDate.now())
            dateInput = String.format("%02d%02d%04d", selectedDayOfMonth, selectedMonth + 1, selectedYear)
        }, year, month, day
    )

    fun parseDateFromInput(input: String) {
        if (input.length >= 6 && input.length <= 8) {
            try {
                val year = input.takeLast(4).toInt()
                val remaining = input.dropLast(4)

                // Parse day and month from remaining digits
                val (day, month) = when (remaining.length) {
                    2 -> {
                        // Format: Md (e.g., "14" = day 1, month 4)
                        val d = remaining[0].toString().toInt()
                        val m = remaining[1].toString().toInt()
                        Pair(d, m)
                    }
                    3 -> {
                        // Could be DDm or Dmm
                        val firstTwo = remaining.take(2).toInt()
                        val lastOne = remaining.last().toString().toInt()

                        // If first two is valid day (1-31), assume format is DDm
                        if (firstTwo in 1..31) {
                            Pair(firstTwo, lastOne)
                        } else {
                            // Otherwise format is Dmm
                            val d = remaining[0].toString().toInt()
                            val m = remaining.drop(1).toInt()
                            Pair(d, m)
                        }
                    }
                    4 -> {
                        // Format: DDmm (e.g., "1204" = day 12, month 04)
                        val d = remaining.take(2).toInt()
                        val m = remaining.drop(2).toInt()
                        Pair(d, m)
                    }
                    else -> return
                }

                if (month in 1..12 && day in 1..31) {
                    val picked = LocalDate.of(year, month, day)
                    birthDate = picked
                    age = Period.between(picked, LocalDate.now())
                }
            } catch (_: Exception) {
                // Invalid date format
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Text("Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { exitProcess(0) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Exit") },
                    label = { Text("Exit") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Age Calculator",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(onClick = { datePickerDialog.show() }) {
                Text(text = "Select Date of Birth", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = dateInput,
                onValueChange = { input ->
                    dateInput = input.filter { it.isDigit() }.take(8)
                    parseDateFromInput(dateInput)
                },
                label = { Text("Enter date (DM or DDMM + YYYY)") },
                placeholder = { Text("e.g., 1242001 or 12042001") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            birthDate?.let {
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                Text(
                    text = "Selected Date: ${it.format(dateFormatter)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            age?.let {
                Text(
                    text = "Your age is:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "${it.years} years, ${it.months} months, and ${it.days} days",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
