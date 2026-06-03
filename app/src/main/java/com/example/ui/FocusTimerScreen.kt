package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.CosmosBackground
import com.example.CosmosSurface
import com.example.CosmosSurfaceLight
import com.example.ElectroPink
import com.example.ElectroPurple
import com.example.NeonCyan
import com.example.CosmosTextPrimary
import com.example.CosmosTextSecondary
import com.example.getTaskDisplayName
import com.example.data.FocusSession
import com.example.data.Task
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FocusTimerScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
    val activeTask by viewModel.activeFocusTask.collectAsState()
    val remainingSeconds by viewModel.timerSecondsRemaining.collectAsState()
    val totalSeconds by viewModel.timerDurationTotal.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    val focusSessionLogs by viewModel.focusSessionsFlow.collectAsState(initial = emptyList())
    val activePotionEffects by viewModel.activePotionEffects.collectAsState()
    val isSpartansVowActive = remember(activePotionEffects) {
        activePotionEffects["spartans_vow"]?.let { it > System.currentTimeMillis() } ?: false
    }

    var customMinutes by remember { mutableStateOf(25) }
    var showTaskSelector by remember { mutableStateOf(false) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 1f
    val minutesLeft = remainingSeconds / 60
    val secondsLeft = remainingSeconds % 60
    val formattedTime = String.format(Locale.ROOT, "%02d:%02d", minutesLeft, secondsLeft)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Core Section: Focus Title
        Text(
            text = "COSMIC FOCUS TIMER",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = ElectroPurple,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Focus on your tasks to finish them faster and earn more points.",
            style = MaterialTheme.typography.bodySmall,
            color = CosmosTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // Binding Target Habit Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, CosmosSurfaceLight)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "CHOSEN HABIT / TASK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ElectroPurple,
                    letterSpacing = 1.sp
                )

                Row(
                     modifier = Modifier.fillMaxWidth(),
                     verticalAlignment = Alignment.CenterVertically,
                     horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (activeTask != null) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(ElectroPurple.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Star, contentDescription = "Bound Task", tint = ElectroPurple, modifier = Modifier.size(16.dp))
                            }
                            Column {
                                Text(getTaskDisplayName(activeTask!!.title), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CosmosTextPrimary)
                                Text(activeTask!!.category, color = ElectroPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text(
                            text = "No task chosen (Just Focus)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CosmosTextSecondary
                        )
                    }

                    if (timerState == TaskViewModel.TimerState.IDLE) {
                        Button(
                            onClick = { showTaskSelector = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmosSurfaceLight),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("bind_task_button")
                        ) {
                            Text(if (activeTask == null) "SELECT TASK" else "CHANGE", color = ElectroPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Circular Pomodoro Timer Dial Visual
        Box(
            modifier = Modifier
                .size(240.dp)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)

                // Background track
                drawCircle(
                    color = CosmosSurfaceLight.copy(alpha = 0.35f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth)
                )

                // Active foreground sweeping arc represent timing progress
                drawArc(
                    brush = Brush.linearGradient(listOf(ElectroPurple, ElectroPink, Color.Magenta)),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Outer ambient decorative clock intervals
                for (angle in 0 until 360 step 30) {
                    val radian = Math.toRadians(angle.toDouble())
                    val innerR = radius - 16.dp.toPx()
                    val outerR = radius - 6.dp.toPx()
                    val startX = center.x + innerR * Math.cos(radian).toFloat()
                    val startY = center.y + innerR * Math.sin(radian).toFloat()
                    val endX = center.x + outerR * Math.cos(radian).toFloat()
                    val endY = center.y + outerR * Math.sin(radian).toFloat()

                    drawLine(
                        color = if (angle % 90 == 0) ElectroPurple.copy(alpha = 0.6f) else CosmosSurfaceLight,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (angle % 90 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                    )
                }
            }

            // Inner Countdown Timer Texts
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formattedTime,
                    fontSize = 42.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    color = ElectroPurple,
                    letterSpacing = (-1).sp
                )
                
                Text(
                    text = when (timerState) {
                        TaskViewModel.TimerState.RUNNING -> "FLOW ACTIVE"
                        TaskViewModel.TimerState.PAUSED -> "SUSPENDED"
                        else -> "STANDBY"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (timerState == TaskViewModel.TimerState.RUNNING) Color(0xFF146C2E) else ElectroPurple,
                    letterSpacing = 1.sp
                )
            }
        }

        // Configuration: Selection for custom duration in standby state
        if (timerState == TaskViewModel.TimerState.IDLE) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ADJUST MINUTES",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ElectroPurple,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        1 to "1 min (Sprint)",
                        15 to "15 min (Short)",
                        25 to "25 min (Pomodoro)",
                        45 to "45 min (Deep Focus)"
                    ).forEach { (mins, desc) ->
                        val isSelected = customMinutes == mins
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ElectroPurple else CosmosSurface)
                                .border(BorderStroke(1.dp, if (isSelected) ElectroPurple else CosmosSurfaceLight), RoundedCornerShape(10.dp))
                                .clickable { customMinutes = mins }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = desc.split(" ")[0] + "m",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else CosmosTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Tappable Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (timerState) {
                TaskViewModel.TimerState.IDLE -> {
                    Button(
                        onClick = { viewModel.startTimer(customMinutes) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp)
                            .testTag("start_timer_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start Program")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("START TIMER", fontWeight = FontWeight.Bold)
                    }
                }
                TaskViewModel.TimerState.RUNNING -> {
                    Button(
                        onClick = { viewModel.pauseTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmosSurfaceLight),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Pause", tint = ElectroPurple)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PAUSE", color = ElectroPurple, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showExitConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD8E4)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("stop_timer_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Stop", tint = Color(0xFFBC1F1F))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CANCEL", color = Color(0xFFBC1F1F), fontWeight = FontWeight.Bold)
                    }
                }
                TaskViewModel.TimerState.PAUSED -> {
                    Button(
                        onClick = { viewModel.resumeTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESUME", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showExitConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD8E4)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Stop", tint = Color(0xFFBC1F1F))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CANCEL", color = Color(0xFFBC1F1F), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Focus Log History List Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, CosmosSurfaceLight)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "TIMER HISTORY",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = CosmosTextPrimary
                )

                if (focusSessionLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No timer history found yet.",
                            color = CosmosTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    val reversedLogs = focusSessionLogs.take(5) // show up to latest 5
                    reversedLogs.forEach { log ->
                        val formatter = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }
                        val timeStr = formatter.format(Date(log.completedAt))
                        val isComplete = log.status == "Completed"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CosmosSurface.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            if (isComplete) Color(0xFFC2EFD4) else Color(0xFFFFD8E4),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isComplete) "✔️" else "⚠️",
                                        fontSize = 10.sp
                                    )
                                }

                                Column {
                                    Text(
                                        text = getTaskDisplayName(log.taskTitle),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CosmosTextPrimary
                                    )
                                    Text(
                                        text = "$timeStr | Duration: ${log.durationSeconds / 60}m ${log.durationSeconds % 60}s",
                                        fontSize = 9.sp,
                                        color = CosmosTextSecondary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isComplete) ElectroPurple.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isComplete) "+${log.xpEarned} XP" else "No XP",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    color = if (isComplete) ElectroPurple else CosmosTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet Task Selector overlay
    if (showTaskSelector) {
        AlertDialog(
            onDismissRequest = { showTaskSelector = false },
            title = {
                Text(
                    text = "SELECT TARGET HABIT",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = ElectroPurple
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if (tasks.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No habits exist. Create one first!", fontWeight = FontWeight.Bold, color = CosmosTextSecondary)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectTaskForFocus(null)
                                            showTaskSelector = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.CenterStart) {
                                        Text(
                                            "Free Focus Session (No associated task)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectroPurple
                                        )
                                    }
                                }
                            }

                            items(tasks) { task ->
                                val isSelected = activeTask?.id == task.id
                                val isVowDisabled = isSpartansVowActive && (task.category.lowercase(Locale.ROOT) == "entertainment" || task.category.lowercase(Locale.ROOT) == "others")
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = !isVowDisabled) {
                                            viewModel.selectTaskForFocus(task)
                                            showTaskSelector = false
                                        }
                                        .graphicsLayer(alpha = if (isVowDisabled) 0.38f else 1f)
                                        .border(
                                            BorderStroke(
                                                1.5.dp,
                                                if (isSelected) ElectroPurple else Color.Transparent
                                            ),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) CosmosSurfaceLight else CosmosSurface
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(getTaskDisplayName(task.title), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CosmosTextPrimary)
                                            Text(task.category, fontSize = 10.sp, color = ElectroPurple, fontWeight = FontWeight.Bold)
                                        }
                                        Text("+${task.xp} XP", fontWeight = FontWeight.Black, fontSize = 11.sp, color = ElectroPurple)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTaskSelector = false }) {
                    Text("CLOSE", fontWeight = FontWeight.Bold, color = ElectroPurple)
                }
            },
            containerColor = CosmosBackground,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showExitConfirmDialog) {
        var exitCountdown by remember { mutableStateOf(5) }

        LaunchedEffect(Unit) {
            exitCountdown = 5
            while (exitCountdown > 0) {
                delay(1000)
                exitCountdown -= 1
            }
        }

        AlertDialog(
            onDismissRequest = { 
                showExitConfirmDialog = false 
            },
            title = {
                Text(
                    text = "Stop Focus Session?",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = ElectroPurple,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Are you sure you want to stop? 🥺",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CosmosTextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().testTag("exit_session_confirm_text")
                    )
                    Text(
                        text = "If you stop now, you will lose all the points for this session.",
                        fontSize = 12.sp,
                        color = CosmosTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (exitCountdown > 0) {
                        Text(
                            text = "Stop button unlocking soon...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Light,
                            color = CosmosTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        LinearProgressIndicator(
                            progress = { exitCountdown / 5f },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ElectroPurple,
                            trackColor = CosmosSurfaceLight
                        )
                    } else {
                        Text(
                            text = "Stop button open. You can now leave.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBC1F1F),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            showExitConfirmDialog = false
                            viewModel.stopTimer(interrupted = true)
                        },
                        enabled = exitCountdown == 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBC1F1F),
                            disabledContainerColor = CosmosSurfaceLight
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("confirm_stop_timer_button")
                    ) {
                        Text(
                            text = if (exitCountdown > 0) "End Focus in $exitCountdown..." else "End Focus",
                            fontWeight = FontWeight.Bold,
                            color = if (exitCountdown == 0) Color.White else CosmosTextSecondary
                        )
                    }

                    Button(
                        onClick = {
                            showExitConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("resume_focus_from_exit_intent_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume Program")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Resume Focus", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            },
            containerColor = CosmosBackground,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
