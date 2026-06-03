package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.*
import com.example.data.DailyNote
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewRitualDialog(
    viewModel: TaskViewModel,
    isMonthly: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var step1Text by remember { mutableStateOf("") }
    var step2Text by remember { mutableStateOf("") }

    val prompt1 = if (isMonthly) "What went well this month?" else "What went well this week?"
    val prompt2 = "What distracted me?"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isMonthly) "Monthly Review" else "Weekly Review",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge,
                    color = CosmosTextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Look back at what you did, set new goals, and do even better!",
                    style = MaterialTheme.typography.labelSmall,
                    color = CosmosTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Input panel 1 for "What went well"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = prompt1,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectroPurple
                    )
                    OutlinedTextField(
                        value = step1Text,
                        onValueChange = { step1Text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("review_input_well"),
                        placeholder = {
                            Text(
                                text = "Enter your achievements or positive changes...",
                                fontSize = 11.sp,
                                color = CosmosTextSecondary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectroPurple,
                            unfocusedBorderColor = CosmosSurfaceLight,
                            focusedTextColor = CosmosTextPrimary,
                            unfocusedTextColor = CosmosTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Input panel 2 for "What distracted me"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = prompt2,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectroPurple
                    )
                    OutlinedTextField(
                        value = step2Text,
                        onValueChange = { step2Text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("review_input_distracted"),
                        placeholder = {
                            Text(
                                text = "Enter any obstacles or distractions you faced...",
                                fontSize = 11.sp,
                                color = CosmosTextSecondary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectroPurple,
                            unfocusedBorderColor = CosmosSurfaceLight,
                            focusedTextColor = CosmosTextPrimary,
                            unfocusedTextColor = CosmosTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val noteKey = if (isMonthly) "Monthly Review ($todayStr)" else "Weekly Review ($todayStr)"
                    
                    val combinedReflection = """
                        $prompt1
                        $step1Text
                        
                        $prompt2
                        $step2Text
                    """.trimIndent()

                    viewModel.saveDailyNote(noteKey, combinedReflection)

                    viewModel.saveReview(
                        reviewType = if (isMonthly) "Monthly" else "Weekly",
                        wentWell = step1Text,
                        distractions = step2Text
                    )

                    // Store reflection-completed flag in SharedPreferences to prevent duplicate popups
                    val sharedPrefs = context.getSharedPreferences("gamified_pref", android.content.Context.MODE_PRIVATE)
                    val cal = Calendar.getInstance()
                    if (isMonthly) {
                        val monthString = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}"
                        sharedPrefs.edit().putBoolean("monthly_review_completed_$monthString", true).apply()
                    } else {
                        val weekString = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.WEEK_OF_YEAR)}"
                        sharedPrefs.edit().putBoolean("weekly_review_completed_$weekString", true).apply()
                    }

                    onDismiss()
                },
                enabled = step1Text.isNotBlank() || step2Text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectroPurple,
                    disabledContainerColor = CosmosSurfaceLight
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_reflection_button")
            ) {
                Text(
                    text = "Save Reflection",
                    fontWeight = FontWeight.Black,
                    color = if (step1Text.isNotBlank() || step2Text.isNotBlank()) Color.White else CosmosTextSecondary
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "DISMISS",
                    fontWeight = FontWeight.Bold,
                    color = CosmosTextSecondary,
                    fontSize = 11.sp
                )
            }
        },
        containerColor = CosmosBackground,
        shape = RoundedCornerShape(20.dp)
    )
}
