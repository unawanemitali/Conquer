package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.*
import com.example.data.Task
import com.example.data.TaskCompletion
import com.example.ui.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ApothecaryStoreDialog(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit
) {
    val activeTab = 1 // Fixed to 1 to show Cosmetics directly without potion-related tabs
    
    // User stats collected from ViewModel
    val userLevel by viewModel.userLevelState.collectAsState()
    val potionCounts by viewModel.potionCounts.collectAsState()
    val activePotionEffects by viewModel.activePotionEffects.collectAsState()
    val allTasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
    
    val unlockedCosmetics by viewModel.unlockedCosmetics.collectAsState()
    val equippedSkinId by viewModel.equippedSkinId.collectAsState()
    val equippedThemeId by viewModel.equippedThemeId.collectAsState()

    // Custom secondary interactive dialog states
    var showAmnesiaAction by remember { mutableStateOf(false) }
    var selectedAmnesiaTask by remember { mutableStateOf<Task?>(null) }
    var amnesiaReflectionText by remember { mutableStateOf("") }
    
    var showPhoenixAction by remember { mutableStateOf(false) }
    var selectedPhoenixTask by remember { mutableStateOf<Task?>(null) }

    var selectedDetailPotion by remember { mutableStateOf<TaskViewModel.Potion?>(null) }
    var feedbackMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Helper functions to detail trade-off mechanics as a visual pop-up
    fun getPotionEffects(potionId: String): Pair<String, String> {
        return Pair("Gives nice boosts to help you finish your tasks.", "No bad side effects.")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Cosmos Shop",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge,
                    color = CosmosTextPrimary
                )
                
                Text(
                    text = "Spend your XP points here to unlock fun character outfits and background themes!",
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
                    .heightIn(max = 440.dp)
            ) {
                // Header displaying remaining XP as essence
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CosmosSurface, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Points:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmosTextSecondary
                    )
                    Text(
                        text = "${userLevel.currentXp} XP",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ElectroPurple,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (feedbackMessage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ElectroPurple.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = feedbackMessage,
                            color = CosmosTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                when (activeTab) {
                    0 -> {
                        // Marketplace Content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "👉 Click any name or info icon to see pros, cons, and side effects.",
                                fontSize = 9.sp,
                                color = ElectroPink,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )

                            viewModel.apothecaryStorePotions.forEach { potion ->
                                val currentPrice = viewModel.getPotionPrice(potion.id, potion.priceXp)
                                val isAffordable = userLevel.currentXp >= currentPrice
                                val potionColor = Color(android.graphics.Color.parseColor(potion.hexColor))

                                // Calculate the multiplier if inflated
                                val isInflated = currentPrice > potion.priceXp
                                val inflationFactorStr = if (isInflated) {
                                    val factor = currentPrice.toFloat() / potion.priceXp.toFloat()
                                    String.format(java.util.Locale.US, "%.1fx Price Penalty", factor)
                                } else null

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            BorderStroke(1.dp, potionColor.copy(alpha = 0.4f)),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(potionColor.copy(alpha = 0.15f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(potion.iconEmoji, fontSize = 20.sp)
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.clickable { selectedDetailPotion = potion }
                                            ) {
                                                Text(
                                                    text = potion.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = ElectroPurple
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.Info,
                                                    contentDescription = "Trade-off Info",
                                                    tint = ElectroPurple.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                            Text(
                                                text = potion.description,
                                                fontSize = 10.sp,
                                                color = CosmosTextSecondary
                                            )
                                            if (inflationFactorStr != null) {
                                                Text(
                                                    text = "⚠️ $inflationFactorStr (price went up because you bought this)",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ElectroPink
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                if (viewModel.buyPotion(potion)) {
                                                    feedbackMessage = "Successfully bought: ${potion.name}! Price increased for the next purchase."
                                                } else {
                                                    feedbackMessage = "Not enough points (XP)."
                                                }
                                            },
                                            enabled = isAffordable,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = potionColor,
                                                disabledContainerColor = CosmosSurfaceLight
                                            ),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(
                                                text = formatXpPrice(currentPrice),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isAffordable) Color.Black else CosmosTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // Cosmetics Contents (Skins and Themes)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🎭 Character Outfits",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CosmosTextPrimary,
                                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                            )

                            val skinsList = viewModel.cosmeticStoreItems.filter { it.type == "Skin" }
                            skinsList.forEach { item ->
                                val isUnlocked = viewModel.isCosmeticUnlocked(item.id)
                                val isEquipped = equippedSkinId == item.keyName

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                if (isEquipped) ElectroPink else if (isUnlocked) ElectroPurple.copy(alpha = 0.5f) else CosmosSurfaceLight
                                            ),
                                            RoundedCornerShape(8.dp)
                                        ),
                                    colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp, horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .background(if (isEquipped) ElectroPurple.copy(alpha = 0.2f) else CosmosSurfaceLight, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(item.iconEmoji, fontSize = 20.sp)
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                FlowRow(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = item.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = CosmosTextPrimary
                                                    )
                                                    Text(
                                                        text = item.aesthetic,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = ElectroPink,
                                                        modifier = Modifier
                                                            .background(ElectroPink.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = item.description,
                                                    fontSize = 10.sp,
                                                    color = CosmosTextSecondary
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        if (isEquipped) {
                                            Button(
                                                onClick = {},
                                                enabled = false,
                                                colors = ButtonDefaults.buttonColors(
                                                    disabledContainerColor = ElectroPurple.copy(alpha = 0.15f),
                                                    disabledContentColor = ElectroPurple
                                                ),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("EQUIPPED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElectroPurple)
                                            }
                                        } else if (isUnlocked) {
                                            Button(
                                                onClick = {
                                                    viewModel.equipSkin(item.keyName)
                                                    feedbackMessage = "Equipped Character: ${item.name}!"
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                                                contentPadding = PaddingValues(horizontal = 10.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("EQUIP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        } else {
                                            val isAffordable = userLevel.currentXp >= item.priceXp
                                            Button(
                                                onClick = {
                                                    if (viewModel.unlockCosmetic(item.id, item.priceXp)) {
                                                        feedbackMessage = "Unlocked Outfit: ${item.name}!"
                                                    } else {
                                                        feedbackMessage = "Insufficient Mind Essence (XP)."
                                                    }
                                                },
                                                enabled = isAffordable,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = ElectroPink,
                                                    disabledContainerColor = CosmosSurfaceLight
                                                ),
                                                contentPadding = PaddingValues(horizontal = 10.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(formatXpPrice(item.priceXp), fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (isAffordable) Color.Black else CosmosTextSecondary)
                                            }
                                        }
                                    }
                                }
                            }

                            Divider(color = CosmosSurfaceLight, modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                text = "🎨 Background Themes",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CosmosTextPrimary,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )

                            val themesList = viewModel.cosmeticStoreItems.filter { it.type == "Theme" }
                            themesList.forEach { item ->
                                val isUnlocked = viewModel.isCosmeticUnlocked(item.id)
                                val isEquipped = equippedThemeId == item.keyName

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                if (isEquipped) ElectroPink else if (isUnlocked) ElectroPurple.copy(alpha = 0.5f) else CosmosSurfaceLight
                                            ),
                                            RoundedCornerShape(8.dp)
                                        ),
                                    colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp, horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .background(if (isEquipped) ElectroPurple.copy(alpha = 0.2f) else CosmosSurfaceLight, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(item.iconEmoji, fontSize = 20.sp)
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                FlowRow(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = item.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = CosmosTextPrimary
                                                    )
                                                    Text(
                                                        text = item.aesthetic,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = ElectroPink,
                                                        modifier = Modifier
                                                            .background(ElectroPink.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = item.description,
                                                    fontSize = 10.sp,
                                                    color = CosmosTextSecondary
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        if (isEquipped) {
                                            Button(
                                                onClick = {},
                                                enabled = false,
                                                colors = ButtonDefaults.buttonColors(
                                                    disabledContainerColor = ElectroPurple.copy(alpha = 0.15f),
                                                    disabledContentColor = ElectroPurple
                                                ),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ElectroPurple)
                                            }
                                        } else if (isUnlocked) {
                                            Button(
                                                onClick = {
                                                    viewModel.equipTheme(item.keyName)
                                                    feedbackMessage = "Theme configuration updated: ${item.name}!"
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                                                contentPadding = PaddingValues(horizontal = 10.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("APPLY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        } else {
                                            val isAffordable = userLevel.currentXp >= item.priceXp
                                            Button(
                                                onClick = {
                                                    if (viewModel.unlockCosmetic(item.id, item.priceXp)) {
                                                        feedbackMessage = "Unlocked Theme: ${item.name}!"
                                                    } else {
                                                        feedbackMessage = "Insufficient Mind Essence (XP)."
                                                    }
                                                },
                                                enabled = isAffordable,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = ElectroPink,
                                                    disabledContainerColor = CosmosSurfaceLight
                                                ),
                                                contentPadding = PaddingValues(horizontal = 10.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(formatXpPrice(item.priceXp), fontSize = 10.sp, fontWeight = FontWeight.Black, color = if (isAffordable) Color.Black else CosmosTextSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // Vault Tab Content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            // Action overlays for item consumption
                            if (showAmnesiaAction) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CosmosSurfaceLight, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Delete a task safely",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CosmosTextPrimary
                                    )
                                    Text(
                                        text = "Choose a task to delete safely:",
                                        fontSize = 10.sp,
                                        color = CosmosTextSecondary
                                    )

                                    val activeTasksList = allTasks.filter { !it.isCompleted }

                                    if (activeTasksList.isEmpty()) {
                                        Text(
                                            "No active tasks found.",
                                            color = CosmosTextSecondary,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                        )
                                    } else {
                                        var dropdownExpanded by remember { mutableStateOf(false) }
                                        Box {
                                            Button(
                                                onClick = { dropdownExpanded = true },
                                                colors = ButtonDefaults.buttonColors(containerColor = CosmosSurface)
                                            ) {
                                                Text(
                                                    text = selectedAmnesiaTask?.title ?: "Select Task",
                                                    fontSize = 11.sp,
                                                    color = CosmosTextPrimary
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = dropdownExpanded,
                                                onDismissRequest = { dropdownExpanded = false },
                                                modifier = Modifier.background(CosmosSurface)
                                            ) {
                                                activeTasksList.forEach { t ->
                                                    DropdownMenuItem(
                                                        text = { Text(t.title, color = CosmosTextPrimary, fontSize = 11.sp) },
                                                        onClick = {
                                                            selectedAmnesiaTask = t
                                                            dropdownExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = amnesiaReflectionText,
                                        onValueChange = { amnesiaReflectionText = it },
                                        placeholder = { Text("What did you learn from pausing this task?", fontSize = 11.sp) },
                                        textStyle = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.fillMaxWidth().height(70.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = CosmosTextPrimary,
                                            unfocusedTextColor = CosmosTextSecondary
                                        )
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        TextButton(
                                            onClick = {
                                                showAmnesiaAction = false
                                                selectedAmnesiaTask = null
                                                amnesiaReflectionText = ""
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Cancel", fontSize = 11.sp, color = CosmosTextSecondary)
                                        }

                                        Button(
                                            onClick = {
                                                val task = selectedAmnesiaTask
                                                if (task != null) {
                                                    // Log reflection note
                                                    viewModel.saveDailyNote(
                                                        "Reflection: ${task.title}",
                                                        amnesiaReflectionText.ifEmpty { "Abandoned guilt-free under draft amnesia." }
                                                    )
                                                    // Consume amnesia potion
                                                    val draft = viewModel.apothecaryStorePotions.first { it.id == "amnesia_draft" }
                                                    if (viewModel.consumePotion(draft)) {
                                                        // Safely delete without penalty
                                                        viewModel.deleteTask(task)
                                                        feedbackMessage = "Purged '${task.title}' guilt-free!"
                                                    }
                                                }
                                                showAmnesiaAction = false
                                                selectedAmnesiaTask = null
                                                amnesiaReflectionText = ""
                                            },
                                            enabled = selectedAmnesiaTask != null,
                                            colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                                            modifier = Modifier.weight(1.5f)
                                        ) {
                                            Text("Drink and Erase Task", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            } else if (showPhoenixAction) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CosmosSurfaceLight, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Save broken streak",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CosmosTextPrimary
                                    )
                                    Text(
                                        text = "Choose a habit to save its broken streak:",
                                        fontSize = 10.sp,
                                        color = CosmosTextSecondary
                                    )

                                    val streakTasks = allTasks.filter { it.isRecurring && it.streak == 0 }

                                    if (streakTasks.isEmpty()) {
                                        Text(
                                            "No broken habits found to restore.",
                                            color = CosmosTextSecondary,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                        )
                                    } else {
                                        var dropdownExpanded by remember { mutableStateOf(false) }
                                        Box {
                                            Button(
                                                onClick = { dropdownExpanded = true },
                                                colors = ButtonDefaults.buttonColors(containerColor = CosmosSurface)
                                            ) {
                                                Text(
                                                    text = selectedPhoenixTask?.title ?: "Select Habit",
                                                    fontSize = 11.sp,
                                                    color = CosmosTextPrimary
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = dropdownExpanded,
                                                onDismissRequest = { dropdownExpanded = false },
                                                modifier = Modifier.background(CosmosSurface)
                                            ) {
                                                streakTasks.forEach { t ->
                                                    DropdownMenuItem(
                                                        text = { Text(t.title, color = CosmosTextPrimary, fontSize = 11.sp) },
                                                        onClick = {
                                                            selectedPhoenixTask = t
                                                            dropdownExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        TextButton(
                                            onClick = {
                                                showPhoenixAction = false
                                                selectedPhoenixTask = null
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Cancel", fontSize = 11.sp, color = CosmosTextSecondary)
                                        }

                                        Button(
                                            onClick = {
                                                val t = selectedPhoenixTask
                                                if (t != null) {
                                                    // Resurrect streak
                                                    viewModel.updateTask(t.copy(streak = 5)) // Resurrect streak count to 5
                                                    val tear = viewModel.apothecaryStorePotions.first { it.id == "phoenix_tear" }
                                                    if (viewModel.consumePotion(tear)) {
                                                        feedbackMessage = "Streak of '${t.title}' resurrected to 5!"
                                                    }
                                                }
                                                showPhoenixAction = false
                                                selectedPhoenixTask = null
                                            },
                                            enabled = selectedPhoenixTask != null,
                                            colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                                            modifier = Modifier.weight(1.5f)
                                        ) {
                                            Text("Apply Phoenix Tear", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            } else {
                                // Standard inventory view
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Active Effects List
                                    val currentlyActive = activePotionEffects.filter { it.value > System.currentTimeMillis() }
                                    if (currentlyActive.isNotEmpty()) {
                                        Text(
                                            text = "🌌 Active Effects",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = ElectroPurple
                                        )

                                        currentlyActive.forEach { (id, expiry) ->
                                            val potion = viewModel.apothecaryStorePotions.find { it.id == id }
                                            if (potion != null) {
                                                val hours = ((expiry - System.currentTimeMillis()) / 3600000L).toInt()
                                                val minutes = (((expiry - System.currentTimeMillis()) % 3600000L) / 60000).toInt()
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(
                                                            Color(android.graphics.Color.parseColor(potion.hexColor)).copy(alpha = 0.1f),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(10.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(potion.iconEmoji)
                                                        Text(potion.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CosmosTextPrimary)
                                                    }
                                                    Text(
                                                        text = "Remaining: ${hours}h ${minutes}m",
                                                        fontSize = 10.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = CosmosTextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Text(
                                        text = "🍶 My Potions",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = CosmosTextSecondary
                                    )

                                    var emptyVault = true
                                    viewModel.apothecaryStorePotions.forEach { potion ->
                                        val ownedCount = potionCounts[potion.id] ?: 0
                                        if (ownedCount > 0) {
                                            emptyVault = false
                                            val potionColor = Color(android.graphics.Color.parseColor(potion.hexColor))

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(CosmosSurface, RoundedCornerShape(12.dp))
                                                    .border(BorderStroke(1.dp, CosmosSurfaceLight), RoundedCornerShape(12.dp))
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .background(potionColor.copy(alpha = 0.15f), CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(potion.iconEmoji, fontSize = 18.sp)
                                                    }

                                                    Column {
                                                        Text(potion.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CosmosTextPrimary)
                                                        Text("Quantity: $ownedCount", fontSize = 10.sp, color = CosmosTextSecondary)
                                                    }
                                                }

                                                Button(
                                                    onClick = {
                                                        if (potion.id == "amnesia_draft") {
                                                            showAmnesiaAction = true
                                                        } else if (potion.id == "phoenix_tear") {
                                                            showPhoenixAction = true
                                                        } else {
                                                            if (viewModel.consumePotion(potion)) {
                                                                feedbackMessage = "Used: ${potion.name}! This potion will cost a bit more next time."
                                                            }
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.height(30.dp)
                                                ) {
                                                    Text("Drink", fontSize = 10.sp, color = Color.White)
                                                }
                                            }
                                        }
                                    }

                                    if (emptyVault) {
                                        Text(
                                            text = "You have no potions right now. Buy some from the Shop first!",
                                            color = CosmosTextSecondary,
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CLOSE SHOP", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CosmosBackground,
        shape = RoundedCornerShape(20.dp)
    )

    // Modal Pop-up for Potion Details and side-effects (Trade-off dialog)
    val currentDetailPotion = selectedDetailPotion
    if (currentDetailPotion != null) {
        val effects = getPotionEffects(currentDetailPotion.id)
        
        AlertDialog(
            onDismissRequest = { selectedDetailPotion = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(ElectroPurple.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(currentDetailPotion.iconEmoji, fontSize = 18.sp)
                    }
                    Text(
                        text = currentDetailPotion.name,
                        fontWeight = FontWeight.Black,
                        color = CosmosTextPrimary,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Strength Booster",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "WHAT IT DOES:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4CAF50)
                            )
                        }
                        Text(
                            text = effects.first,
                            fontSize = 12.sp,
                            color = CosmosTextPrimary,
                            lineHeight = 15.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Tradeoff Danger",
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "SIDE EFFECTS:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFF44336)
                            )
                        }
                        Text(
                            text = effects.second,
                            fontSize = 12.sp,
                            color = CosmosTextPrimary,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Note: The price increases every time you buy or use this potion.",
                        fontSize = 9.sp,
                        color = CosmosTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedDetailPotion = null },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("UNDERSTOOD", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            containerColor = CosmosSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

fun formatXpPrice(price: Int): String {
    return if (price >= 10000) {
        val kValue = price / 1000f
        String.format(java.util.Locale.US, "%.0fk XP", kValue)
    } else if (price >= 1000) {
        val kValue = price / 1000f
        if (price % 1000 == 0) {
            String.format(java.util.Locale.US, "%.0fk XP", kValue)
        } else {
            String.format(java.util.Locale.US, "%.1fk XP", kValue)
        }
    } else {
        "$price XP"
    }
}
