package com.example

import android.os.Bundle
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.*
import com.example.data.models.*
import com.example.ui.TaskViewModel
import com.example.ui.UserLevelInfo
import com.example.ui.FocusTimerScreen
import com.example.ui.ApothecaryStoreDialog
import com.example.ui.ReviewRitualDialog
import com.example.ui.TechnicalAnalyticsScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.snapshotFlow
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.text.selection.SelectionContainer

// --- Custom Theme Palette (Supporting distinct dynamic color palettes) ---
enum class AppThemePalette(val displayName: String) {
    MIDNIGHT_SYSTEM("Midnight System"),
    STARLIGHT_ORBIT("Starlight Orbit"),
    ELITE_ACADEMY("Elite Academy"),
    MECH_BLUEPRINT("Mech Blueprint"),
    TERRACOTTA_EARTH("Terracotta Earth"),
    NET_RUNNER("Retro Hacker Green"),
    LAB_DIVERTER("Steins Laboratory Orange"),
    DEEP_SPACE("Odyssey Ship Board"),
    BRASS_ALCHEMY("Clockwork Brass Gold"),
    NEO_GENESIS("Neo-Genesis Purple")
}

data class ThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceLight: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val tertiaryAccent: Color,
    val textColor: Color,
    val mutedTextColor: Color,
    val categoryWorkColor: Color,
    val categoryWorkBg: Color,
    val categoryPersonalColor: Color,
    val categoryPersonalBg: Color,
    val categoryEntertainmentColor: Color,
    val categoryEntertainmentBg: Color,
    val isDark: Boolean
)

object AppThemeManager {
    var currentTheme by mutableStateOf(AppThemePalette.MIDNIGHT_SYSTEM)
        private set

    val isLightTheme: Boolean
        get() = currentTheme == AppThemePalette.ELITE_ACADEMY || currentTheme == AppThemePalette.TERRACOTTA_EARTH

    val themeColors: ThemeColors
        get() = when (currentTheme) {
            AppThemePalette.MIDNIGHT_SYSTEM -> ThemeColors(
                background = Color(0xFF0F172A),
                surface = Color(0xFF1E293B),
                surfaceLight = Color(0x3394A3B8),
                primaryAccent = Color(0xFF3B82F6),
                secondaryAccent = Color(0xFF06B6D4),
                tertiaryAccent = Color(0xFF06B6D4),
                textColor = Color(0xFFF8FAFC),
                mutedTextColor = Color(0xFF94A3B8),
                categoryWorkColor = Color(0xFFD3E3FD),
                categoryWorkBg = Color(0xFF122C5C),
                categoryPersonalColor = Color(0xFFC2EFD4),
                categoryPersonalBg = Color(0xFF094C20),
                categoryEntertainmentColor = Color(0xFFFFD8E4),
                categoryEntertainmentBg = Color(0xFF58142A),
                isDark = true
            )
            AppThemePalette.STARLIGHT_ORBIT -> ThemeColors(
                background = Color(0xFF000000),
                surface = Color(0xFF121212),
                surfaceLight = Color(0x442D2D2D),
                primaryAccent = Color(0xFFA855F7),
                secondaryAccent = Color(0xFFE2E8F0),
                tertiaryAccent = Color(0xFFA855F7),
                textColor = Color(0xFFFFFFFF),
                mutedTextColor = Color(0xFFA1A1AA),
                categoryWorkColor = Color(0xFFE9D5FF),
                categoryWorkBg = Color(0xFF3B0764),
                categoryPersonalColor = Color(0xFFF1F5F9),
                categoryPersonalBg = Color(0xFF1E293B),
                categoryEntertainmentColor = Color(0xFFFAE8FF),
                categoryEntertainmentBg = Color(0xFF701A75),
                isDark = true
            )
            AppThemePalette.ELITE_ACADEMY -> ThemeColors(
                background = Color(0xFFF8FAFC),
                surface = Color(0xFFFFFFFF),
                surfaceLight = Color(0x1F0F172A),
                primaryAccent = Color(0xFFDC2626),
                secondaryAccent = Color(0xFF1E293B),
                tertiaryAccent = Color(0xFFDC2626),
                textColor = Color(0xFF0F172A),
                mutedTextColor = Color(0xFF64748B),
                categoryWorkColor = Color(0xFFFFE4E6),
                categoryWorkBg = Color(0xFF991B1B),
                categoryPersonalColor = Color(0xFFE2E8F0),
                categoryPersonalBg = Color(0xFF1E293B),
                categoryEntertainmentColor = Color(0xFFFCE7F3),
                categoryEntertainmentBg = Color(0xFF9D174D),
                isDark = false
            )
            AppThemePalette.MECH_BLUEPRINT -> ThemeColors(
                background = Color(0xFF0B132B),
                surface = Color(0xFF1C2541),
                surfaceLight = Color(0x335BC0BE),
                primaryAccent = Color(0xFFFF6B35),
                secondaryAccent = Color(0xFF42FD93),
                tertiaryAccent = Color(0xFFFF6B35),
                textColor = Color(0xFFF4F9F9),
                mutedTextColor = Color(0xFF8D99AE),
                categoryWorkColor = Color(0xFFFFEDD5),
                categoryWorkBg = Color(0xFF7C2D12),
                categoryPersonalColor = Color(0xFFD1FAE5),
                categoryPersonalBg = Color(0xFF064E3B),
                categoryEntertainmentColor = Color(0xFFE0F2FE),
                categoryEntertainmentBg = Color(0xFF0C4A6E),
                isDark = true
            )
            AppThemePalette.TERRACOTTA_EARTH -> ThemeColors(
                background = Color(0xFFF7F5F0),
                surface = Color(0xFFEFECE6),
                surfaceLight = Color(0x3378350F),
                primaryAccent = Color(0xFFB45309),
                secondaryAccent = Color(0xFF047857),
                tertiaryAccent = Color(0xFFB45309),
                textColor = Color(0xFF1C1917),
                mutedTextColor = Color(0xFF78716C),
                categoryWorkColor = Color(0xFFFEF3C7),
                categoryWorkBg = Color(0xFF78350F),
                categoryPersonalColor = Color(0xFFD1FAE5),
                categoryPersonalBg = Color(0xFF064E3B),
                categoryEntertainmentColor = Color(0xFFFEE2E2),
                categoryEntertainmentBg = Color(0xFF7F1D1D),
                isDark = false
            )
            AppThemePalette.NET_RUNNER -> ThemeColors(
                background = Color(0xFF031006),
                surface = Color(0xFF05200C),
                surfaceLight = Color(0x3300FF41),
                primaryAccent = Color(0xFF00FF41),
                secondaryAccent = Color(0xFF33CC33),
                tertiaryAccent = Color(0xFF00FF41),
                textColor = Color(0xFF00FF41),
                mutedTextColor = Color(0xFF008F11),
                categoryWorkColor = Color(0xFFD3E3FD),
                categoryWorkBg = Color(0xFF122C5C),
                categoryPersonalColor = Color(0xFFC2EFD4),
                categoryPersonalBg = Color(0xFF094C20),
                categoryEntertainmentColor = Color(0xFFFFD8E4),
                categoryEntertainmentBg = Color(0xFF58142A),
                isDark = true
            )
            AppThemePalette.LAB_DIVERTER -> ThemeColors(
                background = Color(0xFF0C0A09),
                surface = Color(0xFF1C1917),
                surfaceLight = Color(0x33EA580C),
                primaryAccent = Color(0xFFF97316),
                secondaryAccent = Color(0xFFEA580C),
                tertiaryAccent = Color(0xFFF97316),
                textColor = Color(0xFFF2F2F2),
                mutedTextColor = Color(0xFF78716C),
                categoryWorkColor = Color(0xFFFEF3C7),
                categoryWorkBg = Color(0xFF78350F),
                categoryPersonalColor = Color(0xFFD1FAE5),
                categoryPersonalBg = Color(0xFF064E3B),
                categoryEntertainmentColor = Color(0xFFFEE2E2),
                categoryEntertainmentBg = Color(0xFF7F1D1D),
                isDark = true
            )
            AppThemePalette.DEEP_SPACE -> ThemeColors(
                background = Color(0xFF050505),
                surface = Color(0xFF141414),
                surfaceLight = Color(0x33DC2626),
                primaryAccent = Color(0xFFEF4444),
                secondaryAccent = Color(0xFFB91C1C),
                tertiaryAccent = Color(0xFFEF4444),
                textColor = Color(0xFFECECEC),
                mutedTextColor = Color(0xFF6B7280),
                categoryWorkColor = Color(0xFFFFFFD5),
                categoryWorkBg = Color(0xFF7C2D12),
                categoryPersonalColor = Color(0xFFD1FAE5),
                categoryPersonalBg = Color(0xFF064E3B),
                categoryEntertainmentColor = Color(0xFFE0F2FE),
                categoryEntertainmentBg = Color(0xFF0C4A6E),
                isDark = true
            )
            AppThemePalette.BRASS_ALCHEMY -> ThemeColors(
                background = Color(0xFF0F1E36),
                surface = Color(0xFF1B2E4F),
                surfaceLight = Color(0x33EAB308),
                primaryAccent = Color(0xFFFACC15),
                secondaryAccent = Color(0xFFCA8A04),
                tertiaryAccent = Color(0xFFFACC15),
                textColor = Color(0xFFF1F5F9),
                mutedTextColor = Color(0xFF94A3B8),
                categoryWorkColor = Color(0xFFFFEDD5),
                categoryWorkBg = Color(0xFF7C2D12),
                categoryPersonalColor = Color(0xFFD1FAE5),
                categoryPersonalBg = Color(0xFF064E3B),
                categoryEntertainmentColor = Color(0xFFE0F2FE),
                categoryEntertainmentBg = Color(0xFF0C4A6E),
                isDark = true
            )
            AppThemePalette.NEO_GENESIS -> ThemeColors(
                background = Color(0xFF1E0A2D),
                surface = Color(0xFF2C1042),
                surfaceLight = Color(0x33F97316),
                primaryAccent = Color(0xFFD946EF),
                secondaryAccent = Color(0xFFF97316),
                tertiaryAccent = Color(0xFFD946EF),
                textColor = Color(0xFFFDF4FF),
                mutedTextColor = Color(0xFFA21CAF),
                categoryWorkColor = Color(0xFFE9D5FF),
                categoryWorkBg = Color(0xFF3B0764),
                categoryPersonalColor = Color(0xFFF1F5F9),
                categoryPersonalBg = Color(0xFF1E293B),
                categoryEntertainmentColor = Color(0xFFFAE8FF),
                categoryEntertainmentBg = Color(0xFF701A75),
                isDark = true
            )
        }

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences("local_storage", Context.MODE_PRIVATE)
        val savedThemeStr = prefs.getString("selected_theme", AppThemePalette.MIDNIGHT_SYSTEM.name)
        currentTheme = try {
            AppThemePalette.valueOf(savedThemeStr ?: AppThemePalette.MIDNIGHT_SYSTEM.name)
        } catch (e: Exception) {
            AppThemePalette.MIDNIGHT_SYSTEM
        }
    }

    fun updateTheme(context: Context, theme: AppThemePalette) {
        currentTheme = theme
        val prefs = context.getSharedPreferences("local_storage", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_theme", theme.name).apply()
    }
}

val CosmosBackground: Color
    get() = AppThemeManager.themeColors.background

val CosmosSurface: Color
    get() = AppThemeManager.themeColors.surface

val CosmosSurfaceLight: Color
    get() = AppThemeManager.themeColors.surfaceLight

val ElectroPurple: Color
    get() = AppThemeManager.themeColors.primaryAccent

val ElectroPink: Color
    get() = AppThemeManager.themeColors.secondaryAccent

val NeonCyan: Color
    get() = AppThemeManager.themeColors.tertiaryAccent

val CategoryWorkColor: Color
    get() = AppThemeManager.themeColors.categoryWorkColor

val CategoryWorkBg: Color
    get() = AppThemeManager.themeColors.categoryWorkBg

val CategoryPersonalColor: Color
    get() = AppThemeManager.themeColors.categoryPersonalColor

val CategoryPersonalBg: Color
    get() = AppThemeManager.themeColors.categoryPersonalBg

val CategoryEntertainmentColor: Color
    get() = AppThemeManager.themeColors.categoryEntertainmentColor

val CategoryEntertainmentBg: Color
    get() = AppThemeManager.themeColors.categoryEntertainmentBg

val CosmosTextPrimary: Color
    get() = AppThemeManager.themeColors.textColor

val CosmosTextSecondary: Color
    get() = AppThemeManager.themeColors.mutedTextColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppThemeManager.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            CosmosTheme {
                var showSplash by remember { mutableStateOf(true) }

                val lifecycle = this@MainActivity.lifecycle
                DisposableEffect(lifecycle) {
                    var isFirstTransition = true
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_STOP) {
                            isFirstTransition = false
                        } else if (event == Lifecycle.Event.ON_RESUME) {
                            if (!isFirstTransition) {
                                showSplash = true
                            }
                        }
                    }
                    lifecycle.addObserver(observer)
                    onDispose {
                        lifecycle.removeObserver(observer)
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    MainAppScreen()
                    if (showSplash) {
                        SplashAppScreen(onTimeout = { showSplash = false })
                    }
                }
            }
        }
    }
}

@Composable
fun SplashAppScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmosBackground)
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Conquer",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 54.sp,
                    letterSpacing = 2.sp
                ),
                color = CosmosTextPrimary,
                modifier = Modifier.testTag("splash_app_title")
            )
            Text(
                text = "Reach your peak",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                ),
                color = CosmosTextSecondary,
                modifier = Modifier.testTag("splash_app_tagline")
            )
        }
    }
}

@Composable
fun CosmosTheme(content: @Composable () -> Unit) {
    val currentThemeColors = AppThemeManager.themeColors
    val isLight = !currentThemeColors.isDark

    val themeColors = if (isLight) {
        lightColorScheme(
            primary = ElectroPurple,
            secondary = ElectroPink,
            tertiary = NeonCyan,
            background = CosmosBackground,
            surface = CosmosSurface,
            onBackground = currentThemeColors.textColor,
            onSurface = currentThemeColors.textColor
        )
    } else {
        darkColorScheme(
            primary = ElectroPurple,
            secondary = ElectroPink,
            tertiary = NeonCyan,
            background = CosmosBackground,
            surface = CosmosSurface,
            onBackground = currentThemeColors.textColor,
            onSurface = currentThemeColors.textColor
        )
    }

    MaterialTheme(
        colorScheme = themeColors,
        typography = com.example.ui.theme.Typography,
        content = content
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainAppScreen(viewModel: TaskViewModel = viewModel()) {
    var selectedTab by remember { mutableStateOf(0) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showApothecaryStore by remember { mutableStateOf(false) }
    var showWeeklyReviewModal by remember { mutableStateOf(false) }
    var showMonthlyReviewModal by remember { mutableStateOf(false) }
    var showReviewSelector by remember { mutableStateOf(false) }

    // Aesthetic level-up state listener
    var showLevelUpDialog by remember { mutableStateOf<TaskViewModel.LevelUpEvent?>(null) }

    val userLevelStateCheck by viewModel.userLevelState.collectAsState()
    val equippedSkinIdCheck by viewModel.equippedSkinId.collectAsState()

    LaunchedEffect(userLevelStateCheck.level, equippedSkinIdCheck) {
        val currentLevel = userLevelStateCheck.level
        val requiredLevel = when (equippedSkinIdCheck) {
            "neo_novice" -> 2
            "quantum_architect" -> 3
            "hyperion_voyager" -> 5
            "grandmaster_ai" -> 5
            else -> 0
        }
        if (equippedSkinIdCheck != "default" && currentLevel < requiredLevel) {
            viewModel.equipSkin("default")
        }
    }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.levelUpEvent.collect { event ->
            showLevelUpDialog = event
        }
    }

    LaunchedEffect(viewModel) {
        try {
            val reviewsList = viewModel.allReviewsFlow.first()
            val cal = Calendar.getInstance()
            
            // Weekly Check: Is today Sunday?
            val isSunday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
            if (isSunday) {
                val completedToday = reviewsList.any { it.reviewType == "Weekly" && isToday(it.createdAt) }
                if (!completedToday) {
                    showWeeklyReviewModal = true
                }
            }
            
            // Monthly Check: Is today the last day of the current month?
            val isLastDayOfMonth = cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            if (isLastDayOfMonth) {
                val completedToday = reviewsList.any { it.reviewType == "Monthly" && isToday(it.createdAt) }
                if (!completedToday) {
                    showMonthlyReviewModal = true
                }
            }
        } catch (e: Exception) {
            // Handle gracefully
        }
    }

    val isWide = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 800

    if (showSettingsDialog) {
        MainSettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showApothecaryStore) {
        ApothecaryStoreDialog(
            viewModel = viewModel,
            onDismiss = { showApothecaryStore = false }
        )
    }

    if (showWeeklyReviewModal) {
        ReviewRitualDialog(
            viewModel = viewModel,
            isMonthly = false,
            onDismiss = { showWeeklyReviewModal = false }
        )
    }

    if (showMonthlyReviewModal) {
        ReviewRitualDialog(
            viewModel = viewModel,
            isMonthly = true,
            onDismiss = { showMonthlyReviewModal = false }
        )
    }

    if (showReviewSelector) {
        AlertDialog(
            onDismissRequest = { showReviewSelector = false },
            title = {
                Text(
                    text = "Weekly and Monthly Reviews",
                    fontWeight = FontWeight.Black,
                    color = CosmosTextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Look back at your achievements and problems this week and month to grow!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CosmosTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            showReviewSelector = false
                            showWeeklyReviewModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("launch_weekly_review_btn")
                    ) {
                        Text("Begin Weekly Review", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            showReviewSelector = false
                            showMonthlyReviewModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("launch_monthly_review_btn")
                    ) {
                        Text("Begin Monthly Review", fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = { showReviewSelector = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("LATER", fontWeight = FontWeight.Bold, color = CosmosTextSecondary)
                    }
                }
            },
            containerColor = CosmosBackground,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().background(CosmosSurface)) {
                if (selectedTab == 0) {
                    MarqueeQuoteBar(quotes = spaceSciFiQuotes)
                }
                if (!isWide) {
                    NavigationBar(
                containerColor = CosmosSurface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectroPurple,
                        selectedTextColor = ElectroPurple,
                        unselectedIconColor = CosmosTextSecondary,
                        unselectedTextColor = CosmosTextSecondary,
                        indicatorColor = CosmosSurfaceLight
                    ),
                    modifier = Modifier.testTag("nav_dashboard_tab")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Habits") },
                    label = { Text("Habits", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectroPurple,
                        selectedTextColor = ElectroPurple,
                        unselectedIconColor = CosmosTextSecondary,
                        unselectedTextColor = CosmosTextSecondary,
                        indicatorColor = CosmosSurfaceLight
                    ),
                    modifier = Modifier.testTag("nav_habits_tab")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Focus Timer") },
                    label = { Text("Timer", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectroPurple,
                        selectedTextColor = ElectroPurple,
                        unselectedIconColor = CosmosTextSecondary,
                        unselectedTextColor = CosmosTextSecondary,
                        indicatorColor = CosmosSurfaceLight
                    ),
                    modifier = Modifier.testTag("nav_timer_tab")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Technical Stats") },
                    label = { Text("Analytics", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectroPurple,
                        selectedTextColor = ElectroPurple,
                        unselectedIconColor = CosmosTextSecondary,
                        unselectedTextColor = CosmosTextSecondary,
                        indicatorColor = CosmosSurfaceLight
                    ),
                    modifier = Modifier.testTag("nav_analytics_tab")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Time-Series Calendars") },
                    label = { Text("Calendars", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectroPurple,
                        selectedTextColor = ElectroPurple,
                        unselectedIconColor = CosmosTextSecondary,
                        unselectedTextColor = CosmosTextSecondary,
                        indicatorColor = CosmosSurfaceLight
                    ),
                    modifier = Modifier.testTag("nav_calendar_tab")
                )
                NavigationBarItem(
                    selected = selectedTab == 5,
                    onClick = { selectedTab = 5 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Priority Matrix") },
                    label = { Text("Matrix", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectroPurple,
                        selectedTextColor = ElectroPurple,
                        unselectedIconColor = CosmosTextSecondary,
                        unselectedTextColor = CosmosTextSecondary,
                        indicatorColor = CosmosSurfaceLight
                    ),
                    modifier = Modifier.testTag("nav_matrix_tab")
                )
            }
            }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CosmosBackground)
                    .padding(innerPadding)
            ) {
                // pane 1: left navigation sidebar
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(220.dp)
                        .background(CosmosSurface)
                        .border(BorderStroke(1.dp, CosmosSurfaceLight.copy(alpha = 0.5f)), RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "COSMIC MODULES",
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectroPink,
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val tabs = listOf(
                        Triple("Dashboard", Icons.Default.Home, 0),
                        Triple("Habits Hub", Icons.Default.Favorite, 1),
                        Triple("Focus Timer", Icons.Default.PlayArrow, 2),
                        Triple("Analytics", Icons.Default.Star, 3),
                        Triple("Calendars", Icons.Default.DateRange, 4),
                        Triple("Priority Matrix", Icons.Default.List, 5)
                    )

                    tabs.forEach { (label, icon, index) ->
                        val isSelected = selectedTab == index
                        Button(
                            onClick = { selectedTab = index },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) CosmosSurfaceLight else Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) NeonCyan else CosmosTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) CosmosTextPrimary else CosmosTextSecondary
                                )
                            }
                        }
                    }
                }

                // pane 2: central workspace content pane
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
            when (selectedTab) {
                0 -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onOpenAddTask = { showAddTaskDialog = true },
                        onEditTask = { taskToEdit = it },
                        onOpenReviewRitual = { showReviewSelector = true },
                        onOpenApothecaryStore = { showApothecaryStore = true },
                        onOpenSettings = { showSettingsDialog = true }
                    )
                }
                1 -> {
                    HabitsScreen(
                        viewModel = viewModel,
                        onEditHabit = { taskToEdit = it }
                    )
                }
                2 -> {
                    FocusTimerScreen(viewModel = viewModel)
                }
                3 -> {
                    TechnicalAnalyticsScreen(viewModel = viewModel)
                }
                4 -> {
                    CalendarAnalyticsScreen(viewModel = viewModel)
                }
                5 -> {
                    PriorityMatrixScreen(viewModel = viewModel)
                }
            }
                }

                // pane 3: right side panel displaying Avatar, level, and animated Progress Bar
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .background(CosmosSurface)
                        .border(BorderStroke(1.dp, CosmosSurfaceLight.copy(alpha = 0.5f)), RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val userLevel by viewModel.userLevelState.collectAsState()
                    val equippedSkinId by viewModel.equippedSkinId.collectAsState()
                    val unlockedSet by viewModel.unlockedRewardsIdsFlow.collectAsState()

                    Text(
                        text = "AVATAR MONITOR HUD",
                        fontWeight = FontWeight.Black,
                        color = ElectroPink,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp
                    )

                    AvatarProfileView(
                        equippedSkinId = equippedSkinId, 
                        unlockedSet = unlockedSet,
                        modifier = Modifier.size(120.dp)
                    )

                    val activeSkin = MilestoneSystem.unlockablesList.firstOrNull { it.id == equippedSkinId }
                    Text(
                        text = activeSkin?.name ?: "Cyber Silhouette(Default)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CosmosTextPrimary
                    )

                    Box(
                        modifier = Modifier
                            .background(ElectroPurple.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .border(1.dp, ElectroPurple, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "LEVEL ${userLevel.level}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                    }

                    // Animated Progress bar (Elastic Spring animation)
                    val animatedProgress by animateFloatAsState(
                        targetValue = userLevel.levelProgressPercent,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 140f)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EXP: ${userLevel.currentXp} / ${userLevel.nextLevelXp}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmosTextPrimary
                            )
                            Text(
                                text = "${(userLevel.levelProgressPercent * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }

                        // Neon Progress track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape)
                                .background(CosmosBackground)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(ElectroPurple, ElectroPink, NeonCyan)
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Milestones unlock state overview
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CosmosSurfaceLight.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Milestones Unlocked",
                            style = MaterialTheme.typography.labelSmall,
                            color = CosmosTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Unlocks", tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Text(
                                text = "${unlockedSet.size} of 10 items unlocked",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = CosmosTextPrimary
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CosmosBackground)
                    .padding(innerPadding)
            ) {
                 when (selectedTab) {
                    0 -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onOpenAddTask = { showAddTaskDialog = true },
                            onEditTask = { taskToEdit = it },
                            onOpenReviewRitual = { showReviewSelector = true },
                            onOpenApothecaryStore = { showApothecaryStore = true },
                            onOpenSettings = { showSettingsDialog = true }
                        )
                    }
                    1 -> {
                        HabitsScreen(
                            viewModel = viewModel,
                            onEditHabit = { taskToEdit = it }
                        )
                    }
                    2 -> {
                        FocusTimerScreen(viewModel = viewModel)
                    }
                    3 -> {
                        TechnicalAnalyticsScreen(viewModel = viewModel)
                    }
                    4 -> {
                        CalendarAnalyticsScreen(viewModel = viewModel)
                    }
                    5 -> {
                        PriorityMatrixScreen(viewModel = viewModel)
                    }
                }
            }
        }

            if (showAddTaskDialog) {
                AddTaskDialog(
                    viewModel = viewModel,
                    onDismiss = { showAddTaskDialog = false },
                    onConfirm = { title, category, isRecurring, xp, difficulty, assignedDateMillis, priorityQuadrant, freq ->
                        viewModel.addTask(title, category, isRecurring, xp, difficulty, assignedDateMillis, freq, priorityQuadrant)
                        showAddTaskDialog = false
                    }
                )
            }

            taskToEdit?.let { task ->
                EditTaskDialog(
                    task = task,
                    viewModel = viewModel,
                    onDismiss = { taskToEdit = null },
                    onConfirm = { updatedTask ->
                        viewModel.updateTask(updatedTask)
                        taskToEdit = null
                    }
                )
            }

            showLevelUpDialog?.let { event ->
                AlertDialog(
                    onDismissRequest = { showLevelUpDialog = null },
                    title = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "⚡ CRITICAL LEVEL UP ⚡",
                                style = MaterialTheme.typography.labelMedium,
                                color = ElectroPurple,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "HERO ASCENDED!",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = CosmosTextPrimary
                            )
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(ElectroPurple.copy(alpha = 0.1f), CircleShape)
                                    .border(2.dp, ElectroPurple, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("LEVEL", style = MaterialTheme.typography.labelSmall, color = CosmosTextSecondary)
                                    Text(
                                        "${event.newLevel}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = ElectroPurple
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                "Your digital avatar has progressed from Level ${event.oldLevel} to Level ${event.newLevel}!",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = CosmosTextPrimary
                            )
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF094C20))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "+${event.xpEarned} XP Multiplier Applied",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = Color(0xFFC2EFD4),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Text(
                                "🔒 Check unlocks tab to equip new skins!",
                                style = MaterialTheme.typography.bodySmall,
                                color = CosmosTextSecondary
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showLevelUpDialog = null },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CONTINUE QUESTING", fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = CosmosBackground,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // Floating button logic removed to prevent overlap in layout stack
    }
}

// ============================================
// DASHBOARD TAB: Task lists & character stats
// ============================================
// ============================================
// DASHBOARD TAB: Task lists & character stats
// ============================================

fun getOutfitEmoji(skinId: String): String? {
    if (skinId == "default" || skinId.isEmpty()) return null
    val milestone = com.example.data.models.MilestoneSystem.unlockablesList.firstOrNull { it.id == skinId }
    if (milestone != null) return milestone.iconEmoji
    
    return when (skinId) {
        "tokyo_runner" -> "🧑‍🎤"
        "ghost_protocol" -> "🥽"
        "chrono_weaver" -> "⌚"
        "mecha_pilot" -> "🤖"
        "star_marshal" -> "🚀"
        "neo_novice" -> "🧑‍🎤"
        "quantum_architect" -> "🥽"
        "mecha_overlord" -> "🤖"
        "glitch_runner" -> "🛹"
        "chronos_controller" -> "⌚"
        "hyperion_voyager" -> "🚀"
        "grandmaster_ai" -> "🌌"
        else -> null
    }
}

@Composable
fun AvatarProfileView(
    equippedSkinId: String, 
    unlockedSet: Set<String> = emptySet(), 
    modifier: Modifier = Modifier
) {
    val storeSkins = setOf("tokyo_runner", "ghost_protocol", "chrono_weaver", "mecha_pilot", "star_marshal")
    val isLocked = equippedSkinId != "default" && !storeSkins.contains(equippedSkinId) && (unlockedSet.isEmpty() || !unlockedSet.contains(equippedSkinId))
    val outfitEmoji = if (isLocked) null else getOutfitEmoji(equippedSkinId)

    BoxWithConstraints(
        modifier = modifier
            .background(if (isLocked) Color(0xFF151515) else ElectroPurple.copy(alpha = 0.05f), CircleShape)
            .border(
                2.dp, 
                if (isLocked) androidx.compose.ui.graphics.SolidColor(Color(0xFF424242)) else Brush.linearGradient(listOf(ElectroPurple, NeonCyan)), 
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        val containerWidth = maxWidth
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2 * 0.85f
            
            // Draw background halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (isLocked) {
                        listOf(Color(0xFF222222), Color(0xFF151515), Color.Transparent)
                    } else {
                        when (equippedSkinId) {
                            "neo_novice" -> listOf(Color(0xFF381E72), Color(0xFFEADDFF), Color.Transparent)
                            "quantum_architect" -> listOf(Color(0xFF006874), Color(0xFFC2EFD4), Color.Transparent)
                            "mecha_overlord" -> listOf(Color(0xFF8B0000), Color(0xFFFFD8E4), Color.Transparent)
                            "glitch_runner" -> listOf(Color(0xFFFF007F), Color(0xFFEADDFF), Color.Transparent)
                            "chronos_controller" -> listOf(Color(0xFFDAA520), Color(0xFFEADDFF), Color.Transparent)
                            "hyperion_voyager" -> listOf(Color(0xFF00BFFF), Color(0xFFD3E3FD), Color.Transparent)
                            "grandmaster_ai" -> listOf(Color(0xFF9400D3), Color(0xFFFFD700), Color.Transparent)
                            "tokyo_runner" -> listOf(Color(0xFF00FF41), Color(0xFF031006), Color.Transparent)
                            "ghost_protocol" -> listOf(Color(0xFFA855F7), Color(0xFF1E1E1E), Color.Transparent)
                            "chrono_weaver" -> listOf(Color(0xFFF97316), Color(0xFF0C0A09), Color.Transparent)
                            "mecha_pilot" -> listOf(Color(0xFF6B7280), Color(0xFFEF4444), Color.Transparent)
                            "star_marshal" -> listOf(Color(0xFF1E90FF), Color(0xFFFFD700), Color.Transparent)
                            else -> listOf(ElectroPurple.copy(alpha = 0.3f), Color.Transparent)
                        }
                    },
                    center = center,
                    radius = radius
                ),
                center = center,
                radius = radius
            )

            // ONLY draw the legacy Head/Shoulders vector shapes if we do NOT have a custom active outfit emoji
            if (outfitEmoji == null) {
                // Draw base head/shoulders or cybernetic accessories
                val headCenter = Offset(size.width / 2, size.height * 0.45f)
                val headRadius = size.width * 0.22f
                
                // Shoulders path
                val shoulderPath = Path().apply {
                    moveTo(size.width * 0.2f, size.height * 0.9f)
                    quadraticBezierTo(
                        size.width / 2, size.height * 0.65f,
                        size.width * 0.8f, size.height * 0.9f
                    )
                    close()
                }
                
                // Draw shoulders
                drawPath(
                    path = shoulderPath,
                    color = if (isLocked) {
                        Color(0xFF242424)
                    } else {
                        when (equippedSkinId) {
                            "neo_novice" -> Color(0xFF6750A4)
                            "quantum_architect" -> Color(0xFF381E72)
                            "mecha_overlord" -> Color(0xFF333333)
                            "glitch_runner" -> Color(0xFF1D1B20)
                            "chronos_controller" -> Color(0xFF8B4513)
                            "hyperion_voyager" -> Color(0xFFE0E0E0)
                            "grandmaster_ai" -> Color(0xFFFFD700)
                            "tokyo_runner" -> Color(0xFF00FF41)
                            "ghost_protocol" -> Color(0xFFA855F7)
                            "chrono_weaver" -> Color(0xFFF97316)
                            "mecha_pilot" -> Color(0xFFEF4444)
                            "star_marshal" -> Color(0xFF1E90FF)
                            else -> Color(0xFF9C27B0)
                        }
                    }
                )
                
                // Draw face / helmet
                drawCircle(
                    color = if (isLocked) {
                        Color(0xFF333333)
                    } else {
                        when (equippedSkinId) {
                            "mecha_overlord" -> Color(0xFF808080)
                            "hyperion_voyager" -> Color(0xFFF5F5F5)
                            "grandmaster_ai" -> Color(0xFFFFF8DC)
                            "mecha_pilot" -> Color(0xFF4B5563)
                            "star_marshal" -> Color(0xFF111827)
                            else -> Color(0xFFFFD1A9)
                        }
                    },
                    radius = headRadius,
                    center = headCenter
                )

                // Simple default dark glasses
                drawLine(
                    color = Color.Black,
                    start = Offset(headCenter.x - headRadius * 0.8f, headCenter.y - 2.dp.toPx()),
                    end = Offset(headCenter.x + headRadius * 0.8f, headCenter.y - 2.dp.toPx()),
                    strokeWidth = 6.dp.toPx()
                )
            }
        }

        // Render the activeOutfit's visual asset emoji centered!
        if (outfitEmoji != null) {
            val emojiSize = (containerWidth.value * 0.45f).coerceIn(14f, 48f).sp
            Text(
                text = outfitEmoji,
                fontSize = emojiSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (isLocked) {
            Box(
                modifier = Modifier
                    .size((containerWidth.value * 0.24f).coerceIn(12f, 24f).dp)
                    .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "🔒 Cyber Silhouette Locked",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size((containerWidth.value * 0.14f).coerceIn(8f, 14f).dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun GamificationJsonDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cosmic Engine Schema",
                    fontWeight = FontWeight.Black,
                    color = CosmosTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = CosmosTextPrimary)
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
                    .background(CosmosSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, CosmosSurfaceLight, RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SelectionContainer {
                    Text(
                        text = MilestoneSystem.GAMIFICATION_CONFIG_JSON,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = ElectroPurple
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CLOSE PAYLOAD")
            }
        },
        containerColor = CosmosBackground,
        shape = RoundedCornerShape(20.dp)
    )
}

fun getLogicalTodayString(): String {
    val cal = java.util.Calendar.getInstance()
    val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
    if (hour < 4) {
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
    }
    return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(cal.time)
}

@Composable
fun DashboardScreen(
    viewModel: TaskViewModel,
    onOpenAddTask: () -> Unit,
    onEditTask: (Task) -> Unit,
    onOpenReviewRitual: (() -> Unit)? = null,
    onOpenApothecaryStore: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null
) {
    val tasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
    val userLevel by viewModel.userLevelState.collectAsState()
    val filterCategory by viewModel.selectedCategoryFilter.collectAsState()
    val activeStreaks by viewModel.activeStreaksCount.collectAsState()
    val longestStreak by viewModel.maxStreak.collectAsState()
    val frogTaskId by viewModel.frogTaskId.collectAsState()
    val activePotionEffects by viewModel.activePotionEffects.collectAsState()
    val midnightOilTaskIdsByState by viewModel.midnightOilTaskIds.collectAsState()
    val activeOutfit by viewModel.activeOutfit.collectAsState()
    val unlockedSet by viewModel.unlockedRewardsIdsFlow.collectAsState()

    var logicalTodayString by remember { mutableStateOf(getLogicalTodayString()) }

    LaunchedEffect(Unit) {
        viewModel.checkAndResetHabits()
        while (true) {
            delay(10000) // check every 10 seconds for rollover
            val currentLogical = getLogicalTodayString()
            if (logicalTodayString != currentLogical) {
                logicalTodayString = currentLogical
                viewModel.refreshFrogTask()
            }
        }
    }

    var energyFilter by remember { mutableStateOf("All") }

    val tasksForToday = remember(tasks, logicalTodayString) {
        tasks.filter { task ->
            val taskAssignedStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(task.assignedDateMillis))
            taskAssignedStr == logicalTodayString
        }
    }

    val filteredTasks = remember(tasksForToday, filterCategory, energyFilter) {
        tasksForToday.filter { !it.isRecurring }.filter { task ->
            val passCategory = filterCategory == null || task.category == filterCategory
            val energy = getTaskEnergyLevel(task.title)
            val passEnergy = when (energyFilter) {
                "Low" -> energy == "Low Brainpower"
                "Steady" -> energy == "Medium/Steady"
                "High" -> energy == "High Focus"
                else -> true
            }
            passCategory && passEnergy
        }
    }

    // Sort tasks so the designated "Eat the Frog" (MIT) task sits at the very top of the list
    val sortedTasks = remember(filteredTasks, frogTaskId) {
        filteredTasks.sortedByDescending { it.id == frogTaskId }
    }

    val frogTask = remember(tasksForToday, frogTaskId) {
        tasksForToday.find { it.id == frogTaskId }
    }

    val ordinaryTasks = remember(sortedTasks, frogTaskId) {
        sortedTasks.filter { it.id != frogTaskId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Top Header Row: "Conquer" on left, three icon buttons on right ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Conquer",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black
                ),
                color = CosmosTextPrimary,
                modifier = Modifier.testTag("dashboard_app_title")
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- Weekly and Monthly Review Button ---
                IconButton(
                    onClick = { onOpenReviewRitual?.invoke() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(CosmosSurface, CircleShape)
                        .border(BorderStroke(1.dp, CosmosSurfaceLight), CircleShape)
                        .testTag("review_ritual_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Reviews",
                        tint = ElectroPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // --- Apothecary Store Button ---
                IconButton(
                    onClick = { onOpenApothecaryStore?.invoke() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(CosmosSurface, CircleShape)
                        .border(BorderStroke(1.dp, CosmosSurfaceLight), CircleShape)
                        .testTag("apothecary_store_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shop",
                        tint = ElectroPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // --- Global Settings Gear Icon ---
                IconButton(
                    onClick = { onOpenSettings?.invoke() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(CosmosSurface, CircleShape)
                        .border(BorderStroke(1.dp, CosmosSurfaceLight), CircleShape)
                        .testTag("global_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Cosmic Settings Menu",
                        tint = ElectroPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // --- Top Header Profile Avatar Touchpoint ---
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CosmosSurface)
                        .border(BorderStroke(1.dp, CosmosSurfaceLight), CircleShape)
                        .clickable { onOpenSettings?.invoke() }
                        .testTag("top_header_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarProfileView(
                        equippedSkinId = activeOutfit,
                        unlockedSet = unlockedSet,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // --- 1. GUIILD HERO LEVEL (Strictly top-most card displaying Level and XP) ---
        HeroLevelCard(
            levelInfo = userLevel,
            activeOutfit = activeOutfit,
            unlockedSet = unlockedSet
        )

        // --- Today's Frog Dedicated Section ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "🐸 Today's Frog",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CosmosTextPrimary,
                modifier = Modifier.testTag("frog_section_title")
            )
            
            if (frogTask != null) {
                TaskRow(
                    task = frogTask,
                    onCompletedToggle = { viewModel.toggleTaskCompletion(frogTask) },
                    onDelete = { viewModel.deleteTask(frogTask) },
                    onEdit = { onEditTask(frogTask) },
                    isFrog = true,
                    onFrogToggle = { viewModel.tagEatTheFrog(frogTask.id) },
                    activePotionEffects = activePotionEffects,
                    midnightOilTaskIds = midnightOilTaskIdsByState,
                    onApplyMidnightOil = { viewModel.applyMidnightOilToTask(frogTask.id) }
                )
            } else {
                val emptyStateBorderColor = CosmosTextSecondary.copy(alpha = 0.4f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val stroke = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                            )
                            drawRoundRect(
                                color = emptyStateBorderColor,
                                style = stroke,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                            )
                        }
                        .padding(20.dp)
                        .testTag("frog_empty_state_container"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "No frog selected for today.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CosmosTextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("frog_empty_state_title")
                        )
                        Text(
                            text = "Tap the frog icon on any task below to make it your main priority!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CosmosTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }

        // --- 2. ALL PRODUCTIVITY TASKS (Directly beneath Today's Frog) ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Filter Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CosmosTextSecondary
            )
            CategoryFiltersRow(
                tasks = tasks,
                selectedFilter = filterCategory,
                onSelected = { viewModel.setCategoryFilter(it) }
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Filter Energy Level",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CosmosTextSecondary
            )
            EnergyFiltersRow(
                selectedFilter = energyFilter,
                onSelected = { energyFilter = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (filterCategory != null) "Tasks: $filterCategory" else "All Productivity Tasks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CosmosTextPrimary
            )
            Button(
                onClick = onOpenAddTask,
                colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .testTag("add_task_trigger_button")
                    .heightIn(min = 48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create")
            }
        }

        // Checklist of Tasks (showing Eat the Frog highlighted task at the absolute top)
        if (ordinaryTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Empty",
                        tint = CosmosTextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No tasks yet.",
                        color = CosmosTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Click 'Create' to add your first quest!",
                        color = CosmosTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ordinaryTasks.forEach { task ->
                    TaskRow(
                        task = task,
                        onCompletedToggle = { viewModel.toggleTaskCompletion(task) },
                        onDelete = { viewModel.deleteTask(task) },
                        onEdit = { onEditTask(task) },
                        isFrog = false,
                        onFrogToggle = { viewModel.tagEatTheFrog(task.id) },
                        activePotionEffects = activePotionEffects,
                        midnightOilTaskIds = midnightOilTaskIdsByState,
                        onApplyMidnightOil = { viewModel.applyMidnightOilToTask(task.id) }
                    )
                }
            }
        }

        // --- Streaks Overview ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StreakStatsCard(
                title = "Active Streaks",
                value = "$activeStreaks Tasks",
                emoji = "⚡",
                modifier = Modifier.weight(1f)
            )
            StreakStatsCard(
                title = "Longest Streak",
                value = "$longestStreak Days",
                emoji = "🔥",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MainSettingsDialog(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentTheme = AppThemeManager.currentTheme
    
    // States for sub-menus
    var activeTab by remember { mutableStateOf(0) } // 0 = Themes, 1 = Profile, 2 = Reflection Archive
    var reviewFilter by remember { mutableStateOf("All") } // "All", "Weekly", "Monthly"
    var reviewToEdit by remember { mutableStateOf<Review?>(null) }
    val pastReviews by viewModel.allReviewsFlow.collectAsState(initial = emptyList())
    
    // User stats collected from ViewModel
    val equippedSkinId by viewModel.equippedSkinId.collectAsState()
    val unlockedSet by viewModel.unlockedRewardsIdsFlow.collectAsState()
    val userLevel by viewModel.userLevelState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Cosmic Hub Settings",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge,
                    color = CosmosTextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Navigation tabs for Settings sub-options
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sub-option: Themes selector
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (activeTab == 0) ElectroPurple else CosmosSurfaceLight)
                            .clickable { activeTab = 0 }
                            .padding(vertical = 10.dp)
                            .testTag("settings_themes_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Themes",
                                tint = if (activeTab == 0) Color.White else CosmosTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Themes",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 0) Color.White else CosmosTextSecondary
                            )
                        }
                    }

                    // Sub-option: Profile avatar & milestone achievements
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (activeTab == 1) ElectroPurple else CosmosSurfaceLight)
                            .clickable { activeTab = 1 }
                            .padding(vertical = 10.dp)
                            .testTag("settings_profile_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = if (activeTab == 1) Color.White else CosmosTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Profile",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 1) Color.White else CosmosTextSecondary
                            )
                        }
                    }

                    // Sub-option: Reflection Archive
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (activeTab == 2) ElectroPurple else CosmosSurfaceLight)
                            .clickable { activeTab = 2 }
                            .padding(vertical = 10.dp)
                            .testTag("settings_archive_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Reflection Archive",
                                tint = if (activeTab == 2) Color.White else CosmosTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Reflection Archive",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 2) Color.White else CosmosTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                if (activeTab == 0) {
                    // --- SUB-OPTION 1: THEMES SELECTION ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Select a visual frequency palette. Themes are managed safely via local storage and do not affect character milestones or quest histories.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CosmosTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        AppThemePalette.values().forEach { palette ->
                            val isSelected = currentTheme == palette
                            
                            Card(
                                onClick = {
                                    AppThemeManager.updateTheme(context, palette)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) ElectroPurple else CosmosSurfaceLight
                                        ),
                                        RoundedCornerShape(16.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = when (palette) {
                                        AppThemePalette.MIDNIGHT_SYSTEM -> Color(0xFF0F172A)
                                        AppThemePalette.STARLIGHT_ORBIT -> Color(0xFF000000)
                                        AppThemePalette.ELITE_ACADEMY -> Color(0xFFFFFFFF)
                                        AppThemePalette.MECH_BLUEPRINT -> Color(0xFF0B132B)
                                        AppThemePalette.TERRACOTTA_EARTH -> Color(0xFFF7F5F0)
                                        AppThemePalette.NET_RUNNER -> Color(0xFF031006)
                                        AppThemePalette.LAB_DIVERTER -> Color(0xFF0C0A09)
                                        AppThemePalette.DEEP_SPACE -> Color(0xFF050505)
                                        AppThemePalette.BRASS_ALCHEMY -> Color(0xFF0F1E36)
                                        AppThemePalette.NEO_GENESIS -> Color(0xFF1E0A2D)
                                    }
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = palette.displayName,
                                            fontWeight = FontWeight.Bold,
                                            color = when (palette) {
                                                AppThemePalette.ELITE_ACADEMY -> Color(0xFF0F172A)
                                                AppThemePalette.TERRACOTTA_EARTH -> Color(0xFF1C1917)
                                                else -> Color(0xFFF8FAFC)
                                            },
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = when (palette) {
                                                AppThemePalette.MIDNIGHT_SYSTEM -> "Deep slate blues + vivid cyan accents"
                                                AppThemePalette.STARLIGHT_ORBIT -> "AMOLED blacks + neon purple/silver accents"
                                                AppThemePalette.ELITE_ACADEMY -> "Crisp crimson accents + sharp light style"
                                                AppThemePalette.MECH_BLUEPRINT -> "Indigo blueprints & drafting-orange grid"
                                                AppThemePalette.TERRACOTTA_EARTH -> "Eye-safe warm beige clay organic hues"
                                                AppThemePalette.NET_RUNNER -> "Neo-Tokyo matrix hacker green console theme"
                                                AppThemePalette.LAB_DIVERTER -> "Monochrome slate with orange vacuum tube signals"
                                                AppThemePalette.DEEP_SPACE -> "Total cosmic void with emergency red alerts"
                                                AppThemePalette.BRASS_ALCHEMY -> "Clockwork brass gold & library blue textures"
                                                AppThemePalette.NEO_GENESIS -> "Evangelion anime mecha orange & purple"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = when (palette) {
                                                AppThemePalette.ELITE_ACADEMY -> Color(0xFF64748B)
                                                AppThemePalette.TERRACOTTA_EARTH -> Color(0xFF78716C)
                                                else -> Color(0xFF94A3B8)
                                            }
                                        )
                                    }
                                    
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Dot 1: Primary Accent
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(
                                                    when (palette) {
                                                        AppThemePalette.MIDNIGHT_SYSTEM -> Color(0xFF3B82F6)
                                                        AppThemePalette.STARLIGHT_ORBIT -> Color(0xFFA855F7)
                                                        AppThemePalette.ELITE_ACADEMY -> Color(0xFFDC2626)
                                                        AppThemePalette.MECH_BLUEPRINT -> Color(0xFFFF6B35)
                                                        AppThemePalette.TERRACOTTA_EARTH -> Color(0xFFB45309)
                                                        AppThemePalette.NET_RUNNER -> Color(0xFF00FF41)
                                                        AppThemePalette.LAB_DIVERTER -> Color(0xFFF97316)
                                                        AppThemePalette.DEEP_SPACE -> Color(0xFFEF4444)
                                                        AppThemePalette.BRASS_ALCHEMY -> Color(0xFFFACC15)
                                                        AppThemePalette.NEO_GENESIS -> Color(0xFFD946EF)
                                                    },
                                                    CircleShape
                                                )
                                        )
                                        // Dot 2: Secondary Accent
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(
                                                    when (palette) {
                                                        AppThemePalette.MIDNIGHT_SYSTEM -> Color(0xFF06B6D4)
                                                        AppThemePalette.STARLIGHT_ORBIT -> Color(0xFFE2E8F0)
                                                        AppThemePalette.ELITE_ACADEMY -> Color(0xFF1E293B)
                                                        AppThemePalette.MECH_BLUEPRINT -> Color(0xFF42FD93)
                                                        AppThemePalette.TERRACOTTA_EARTH -> Color(0xFF047857)
                                                        AppThemePalette.NET_RUNNER -> Color(0xFF33CC33)
                                                        AppThemePalette.LAB_DIVERTER -> Color(0xFFEA580C)
                                                        AppThemePalette.DEEP_SPACE -> Color(0xFFB91C1C)
                                                        AppThemePalette.BRASS_ALCHEMY -> Color(0xFFCA8A04)
                                                        AppThemePalette.NEO_GENESIS -> Color(0xFFF97316)
                                                    },
                                                    CircleShape
                                                )
                                        )
                                        
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected Theme",
                                                tint = when (palette) {
                                                    AppThemePalette.ELITE_ACADEMY -> Color(0xFFDC2626)
                                                    AppThemePalette.TERRACOTTA_EARTH -> Color(0xFFB45309)
                                                    else -> Color.White
                                                },
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (activeTab == 1) {
                    // --- SUB-OPTION 2: PROFILE & MILESTONES ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // User Profile summary row
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CosmosSurfaceLight.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, CosmosSurfaceLight),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AvatarProfileView(
                                    equippedSkinId = equippedSkinId,
                                    unlockedSet = unlockedSet,
                                    modifier = Modifier.size(76.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    val activeSkin = MilestoneSystem.unlockablesList.firstOrNull { it.id == equippedSkinId }
                                    val displayProfileTitle = if (equippedSkinId != "default" && activeSkin != null) {
                                        activeSkin.name
                                    } else {
                                        "Hero Champion"
                                    }
                                    Text(
                                        text = displayProfileTitle,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = CosmosTextPrimary
                                    )
                                    Text(
                                        text = "Level ${userLevel.level} Hero (${userLevel.currentXp} XP)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElectroPink,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = activeSkin?.description ?: "Default base simulation avatar.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CosmosTextSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Unlocked Milestones (${unlockedSet.size} of 10)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CosmosTextPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        // Vertical progress of Milestones
                        MilestoneSystem.unlockablesList.forEach { reward ->
                            val isUnlocked = unlockedSet.contains(reward.id)
                            val isEquipped = equippedSkinId == reward.id

                            val levelRequired = when (reward.id) {
                                "neo_novice" -> 2
                                "quantum_architect" -> 3
                                "hyperion_voyager" -> 5
                                "grandmaster_ai" -> 5
                                else -> 0
                            }
                            val displayCondition = if (levelRequired > 0) {
                                val xpTarget = Math.pow((levelRequired - 1.0) / 0.05, 2.0).toInt()
                                if (reward.id == "grandmaster_ai") {
                                    "Complete 10 Work tasks & Reach Level 5 (${xpTarget} XP)"
                                } else {
                                    "Reach Level $levelRequired (${xpTarget} XP)"
                                }
                            } else {
                                reward.unlockConditionDesc
                            }
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (isEquipped) ElectroPurple else if (isUnlocked) ElectroPink.copy(alpha = 0.5f) else CosmosSurfaceLight
                                        ),
                                        RoundedCornerShape(12.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isEquipped) CosmosSurfaceLight else if (isUnlocked) CosmosSurface else CosmosSurface.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(
                                                    if (isUnlocked) ElectroPurple.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(reward.iconEmoji, fontSize = 16.sp)
                                        }
                                        
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (isUnlocked) Color(0x3306B6D4) else Color(0x3394A3B8)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = reward.type.uppercase(Locale.ROOT),
                                                fontSize = 6.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUnlocked) Color(0xFF06B6D4) else CosmosTextSecondary
                                            )
                                        }
                                    }
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = reward.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isUnlocked) CosmosTextPrimary else CosmosTextSecondary
                                        )
                                        Text(
                                            text = reward.description,
                                            fontSize = 9.sp,
                                            lineHeight = 11.sp,
                                            color = if (isUnlocked) CosmosTextSecondary else CosmosTextSecondary.copy(alpha = 0.6f)
                                        )
                                        Text(
                                            text = "Requires: " + displayCondition,
                                            fontSize = 8.sp,
                                            color = if (isUnlocked) Color(0xFF06B6D4) else Color(0xFFEF4444),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (reward.type == "Skin" && isUnlocked) {
                                        Button(
                                            onClick = { viewModel.equipSkin(reward.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isEquipped) ElectroPurple else CosmosSurfaceLight
                                            ),
                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .wrapContentWidth()
                                                .height(28.dp)
                                        ) {
                                            Text(
                                                text = if (isEquipped) "EQUIPPED" else "EQUIP",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isEquipped) Color.White else ElectroPurple
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .width(60.dp)
                                                .height(28.dp)
                                                .background(
                                                    if (isUnlocked) Color(0x1A06B6D4) else Color.Transparent,
                                                    RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (isUnlocked) "UNLOCKED" else "LOCKED",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUnlocked) Color(0xFF06B6D4) else CosmosTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // --- SUB-OPTION 3: REFLECTION ARCHIVE ---
                    val filteredReviews = remember(pastReviews, reviewFilter) {
                        if (reviewFilter == "All") {
                            pastReviews
                        } else {
                            pastReviews.filter { it.reviewType == reviewFilter }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Reflection Archive",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CosmosTextPrimary
                        )
                        Text(
                            text = "A modern cyber-chronicle of your historical Weekly and Monthly Review cycles.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CosmosTextSecondary
                        )

                        // Filters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("All", "Weekly", "Monthly").forEach { filterType ->
                                val isFilterSelected = reviewFilter == filterType
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isFilterSelected) ElectroPurple else CosmosSurfaceLight)
                                        .clickable { reviewFilter = filterType }
                                        .padding(vertical = 8.dp)
                                        .testTag("filter_review_${filterType.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = filterType,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFilterSelected) Color.White else CosmosTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Review cards list
                        if (filteredReviews.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No past reflections found under this filter.",
                                    color = CosmosTextSecondary,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredReviews.size) { index ->
                                    val review = filteredReviews[index]
                                    val dateStr = remember(review.createdAt) {
                                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(review.createdAt))
                                    }
                                    
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("review_archive_card_${review.id}"),
                                        colors = CardDefaults.cardColors(containerColor = CosmosSurfaceLight.copy(alpha = 0.5f)),
                                        border = BorderStroke(1.dp, CosmosSurfaceLight),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${review.reviewType} Review",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 12.sp,
                                                    color = ElectroPurple
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = dateStr,
                                                        fontSize = 9.sp,
                                                        color = CosmosTextSecondary
                                                    )
                                                    IconButton(
                                                        onClick = { reviewToEdit = review },
                                                        modifier = Modifier.size(24.dp).testTag("edit_review_button_${review.id}")
                                                    ) {
                                                        Text("✏️", fontSize = 12.sp)
                                                    }
                                                }
                                            }
                                            
                                            HorizontalDivider(
                                                color = CosmosSurfaceLight.copy(alpha = 0.3f),
                                                thickness = 1.dp
                                            )
                                            
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    text = if (review.reviewType == "Monthly") "What went well this month?" else "What went well this week?",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NeonCyan
                                                )
                                                Text(
                                                    text = review.wentWell.ifBlank { "No achievements written." },
                                                    fontSize = 11.sp,
                                                    color = CosmosTextPrimary
                                                )
                                            }
                                            
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(
                                                    text = "What distracted me?",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ElectroPink
                                                )
                                                Text(
                                                    text = review.distractions.ifBlank { "No distractions written." },
                                                    fontSize = 11.sp,
                                                    color = CosmosTextPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", fontWeight = FontWeight.Black, color = ElectroPurple)
            }
        },
        containerColor = CosmosSurface,
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 6.dp
    )

    if (reviewToEdit != null) {
        val review = reviewToEdit!!
        EditReviewDialog(
            review = review,
            onDismiss = { reviewToEdit = null },
            onSave = { updatedWentWell, updatedDistractions ->
                viewModel.updateReview(
                    id = review.id,
                    reviewType = review.reviewType,
                    createdAt = review.createdAt,
                    wentWell = updatedWentWell,
                    distractions = updatedDistractions
                )
                reviewToEdit = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReviewDialog(
    review: Review,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var step1Text by remember { mutableStateOf(review.wentWell) }
    var step2Text by remember { mutableStateOf(review.distractions) }

    val prompt1 = if (review.reviewType == "Monthly") "What went well this month?" else "What went well this week?"
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
                    text = "Edit ${review.reviewType} Reflection",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleLarge,
                    color = CosmosTextPrimary,
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
                            .testTag("edit_review_input_well"),
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
                            .testTag("edit_review_input_distracted"),
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
                    onSave(step1Text, step2Text)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("edit_review_save_btn")
            ) {
                Text("SAVE", fontWeight = FontWeight.Black, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("edit_review_cancel_btn")
            ) {
                Text("CANCEL", fontWeight = FontWeight.Bold, color = CosmosTextSecondary)
            }
        },
        containerColor = CosmosSurface,
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 6.dp
    )
}

fun isToday(timestampMillis: Long): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestampMillis }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun HeroLevelCard(
    levelInfo: UserLevelInfo,
    activeOutfit: String = "default",
    unlockedSet: Set<String> = emptySet()
) {
    val activeSkin = com.example.data.models.MilestoneSystem.unlockablesList.firstOrNull { it.id == activeOutfit }
    val displayHeroTitle = if (activeOutfit != "default" && activeSkin != null) {
        activeSkin.name
    } else {
        "Hero Champion"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        colors = CardDefaults.cardColors(containerColor = CosmosSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Guild hero level",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        ),
                        color = ElectroPurple
                    )
                    Text(
                        text = displayHeroTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = CosmosTextPrimary
                    )
                }
                
                // Displays the user's Dynamic Profile Avatar with currently equipped character outfit!
                Box(
                    modifier = Modifier.size(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarProfileView(
                        equippedSkinId = activeOutfit,
                        unlockedSet = unlockedSet,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Small overlay at the bottom end displaying the numeric Level super cleanly
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(ElectroPurple, RoundedCornerShape(6.dp))
                            .border(1.dp, CosmosSurface, RoundedCornerShape(6.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Lv ${levelInfo.level}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 8.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Row: A progress bar layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${levelInfo.currentXp} XP",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = CosmosTextPrimary
                )
                LinearProgressIndicator(
                    progress = levelInfo.levelProgressPercent.coerceIn(0f, 1f),
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(CircleShape),
                    color = ElectroPurple,
                    trackColor = CosmosSurfaceLight
                )
                Text(
                    text = "${levelInfo.nextLevelXp} XP target",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CosmosTextSecondary
                )
            }
        }
    }
}

@Composable
fun StreakStatsCard(
    title: String,
    value: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CosmosSurface),
        border = BorderStroke(1.dp, CosmosSurfaceLight),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(CosmosSurfaceLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = CosmosTextSecondary, style = MaterialTheme.typography.labelMedium)
                Text(text = value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = CosmosTextPrimary)
            }
        }
    }
}

@Composable
fun EnergyFiltersRow(
    selectedFilter: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val energyLevels = listOf("All", "Low", "Steady", "High")
        val labels = listOf("🌌 All", "🟢 Low", "🔵 Steady", "🔴 High")

        energyLevels.forEach { lvl ->
            val isSelected = selectedFilter == lvl
            val activeColor = when (lvl) {
                "Low" -> Color(0xFF064E3B)
                "Steady" -> Color(0xFF1E3A8A)
                "High" -> Color(0xFF450A0A)
                else -> ElectroPurple
            }
            val activeTextColor = when (lvl) {
                "Low" -> Color(0xFF6EE7B7)
                "Steady" -> Color(0xFF93C5FD)
                "High" -> Color(0xFFFCA5A5)
                else -> Color.White
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) activeColor else CosmosSurface)
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isSelected) activeColor else CosmosSurfaceLight
                        ),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelected(lvl) }
                    .padding(vertical = 8.dp)
                    .testTag("energy_filter_${lvl.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                val label = labels[energyLevels.indexOf(lvl)]
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) activeTextColor else CosmosTextSecondary
                )
            }
        }
    }
}

@Composable
fun CategoryFiltersRow(
    tasks: List<Task>,
    selectedFilter: String?,
    onSelected: (String?) -> Unit
) {
    val defaultCategories = listOf("Work", "Personal", "Others")
    val taskCategories = tasks.map { it.category }.filter { it.isNotBlank() }
    val dynamicCategories = (defaultCategories + taskCategories)
        .map { it.trim() }
        .distinct()

    val categories = listOf(null) + dynamicCategories

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { cat ->
            val isSelected = selectedFilter == cat
            val label = when (cat) {
                null -> "💎 All"
                "Work" -> "💼 Work"
                "Personal" -> "🧘 Personal"
                "Others" -> "🧭 Others"
                else -> "🏷️ $cat"
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) ElectroPurple else CosmosSurface)
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isSelected) ElectroPurple else CosmosSurfaceLight
                        ),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelected(cat) }
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .testTag("category_filter_${cat ?: "all"}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else CosmosTextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

data class XpParticle(
    val id: Int,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val r: Float,
    val alpha: Float,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskRow(
    task: Task,
    onCompletedToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    isFrog: Boolean = false,
    onFrogToggle: (() -> Unit)? = null,
    activePotionEffects: Map<String, Long> = emptyMap(),
    midnightOilTaskIds: Set<Int> = emptySet(),
    onApplyMidnightOil: (() -> Unit)? = null
) {
    var isClickAnimating by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isClickAnimating) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        finishedListener = { isClickAnimating = false }
    )

    val isOverclockActive = false
    val isFreezeActive = false
    val isSpartansVowActive = false
    val isMidnightOiled = false
    val isGreyedOut = false

    val cardBorder = if (isFrog) {
        BorderStroke(2.dp, Brush.linearGradient(listOf(Color(0xFF4ADE80), Color(0xFF22C55E))))
    } else if (task.isCompleted) {
        BorderStroke(1.dp, CosmosSurfaceLight.copy(alpha = 0.3f))
    } else if (isMidnightOiled) {
        BorderStroke(1.5.dp, Color(0xFFEF4444))
    } else if (isOverclockActive) {
        BorderStroke(1.5.dp, Color(0xFFF59E0B))
    } else if (isFreezeActive) {
        BorderStroke(1.5.dp, Color(0xFF60A5FA))
    } else {
        BorderStroke(
            1.dp,
            when (task.category) {
                "Work" -> CategoryWorkBg
                "Personal" -> CategoryPersonalBg
                else -> CategoryEntertainmentBg
            }
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .graphicsLayer(scaleX = scale, scaleY = scale, alpha = if (isGreyedOut) 0.5f else 1f)
                .fillMaxWidth()
                .testTag("task_item_card_${task.id}"),
            colors = CardDefaults.cardColors(
                containerColor = if (isFrog) {
                    Color(0xFF0F2D1F)
                } else if (task.isCompleted) {
                    CosmosSurface.copy(alpha = 0.7f)
                } else {
                    CosmosSurfaceLight.copy(alpha = 0.3f)
                }
            ),
            border = cardBorder,
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top row: contains the selection checkbox, title/deadline column, and Frog icon in top-right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Far Left Checkbox
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isGreyedOut) Color(0xFF450A0A)
                                    else if (task.isCompleted) ElectroPurple
                                    else Color.Transparent
                                )
                                .border(
                                    BorderStroke(
                                        2.dp,
                                        if (isGreyedOut) Color(0xFFEF4444)
                                        else if (task.isCompleted) ElectroPurple
                                        else Color(0xFFFAF6FE).copy(alpha = 0.7f)
                                    ),
                                    CircleShape
                                )
                                .clickable(enabled = !isGreyedOut) {
                                    if (!task.isCompleted) {
                                        isClickAnimating = true
                                    }
                                    onCompletedToggle()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isGreyedOut) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Locked by Spartan's Vow",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(12.dp)
                                )
                            } else if (task.isCompleted) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Middle-Left: The Task Title & Deadline info
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = getTaskDisplayName(task.title),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = if (task.isCompleted) CosmosTextSecondary else CosmosTextPrimary,
                                    modifier = Modifier.weight(1f, fill = false)
                                )

                                if (isGreyedOut) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF7F1D1D))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                            .testTag("spartans_vow_locked_badge")
                                    ) {
                                        Text(
                                            text = "🛡️ LOCKED BY VOW",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFCA5A5)
                                        )
                                    }
                                }

                                if (isFrog) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF065F46))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                            .testTag("eat_the_frog_badge")
                                    ) {
                                        Text(
                                            text = "🐸 FROG",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF34D399)
                                        )
                                    }
                                }
                            }

                            val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
                            val deadlineText = "🗓️ Due: ${dateFormat.format(Date(task.assignedDateMillis))}"

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isMidnightOiled) {
                                    Text(
                                        text = "🛢️ $deadlineText",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444)
                                    )
                                    Text(
                                        text = "(0 XP)",
                                        fontSize = 8.sp,
                                        color = Color(0xFFEF4444),
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = deadlineText,
                                        fontSize = 9.sp,
                                        color = CosmosTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Frog Toggle Button moved to Top-Right
                    if (onFrogToggle != null) {
                        IconButton(
                            onClick = onFrogToggle,
                            enabled = !isGreyedOut,
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    if (isFrog) Color(0xFF065F46).copy(alpha = 0.2f) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .testTag("frog_toggle_button_${task.id}")
                        ) {
                            Text(
                                text = "🐸",
                                fontSize = 14.sp,
                                modifier = Modifier.graphicsLayer(alpha = if (isFrog && !isGreyedOut) 1f else 0.35f)
                            )
                        }
                    }
                }

                // Bottom Row using FlowRow for responsive wrapping on smaller screens
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Left: Badge indicators and XP Reward
                    FlowRow(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Category pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when (task.category) {
                                        "Work" -> CategoryWorkBg
                                        "Personal" -> CategoryPersonalBg
                                        else -> CategoryEntertainmentBg
                                    }
                                )
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.category,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (task.category) {
                                    "Work" -> CategoryWorkColor
                                    "Personal" -> CategoryPersonalColor
                                    else -> CategoryEntertainmentColor
                                }
                            )
                        }

                        // Energy Pill
                        val energy = getTaskEnergyLevel(task.title)
                        val (energyBg, energyColor, energyText) = when (energy) {
                            "High Focus" -> Triple(Color(0xFF450A0A), Color(0xFFFCA5A5), "🔴 High")
                            "Low Brainpower" -> Triple(Color(0xFF064E3B), Color(0xFF6EE7B7), "🟢 Low")
                            else -> Triple(Color(0xFF1E3A8A), Color(0xFF93C5FD), "🔵 Med")
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(energyBg)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = energyText,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = energyColor
                            )
                        }

                        // XP Reward
                        val calculatedXp = remember(task) {
                            val categoryFactor = when (task.category.lowercase(Locale.ROOT)) {
                                "work" -> 1.2f
                                "personal" -> 1.0f
                                "entertainment" -> 0.8f
                                else -> 1.0f
                            }
                            val difficultyFactor = when (task.difficulty.lowercase(Locale.ROOT)) {
                                "easy" -> 1.0f
                                "medium" -> 1.5f
                                "hard" -> 2.0f
                                "mythic" -> 3.0f
                                else -> 1.5f
                            }
                            val streakBonus = 1.0f + (if (task.isRecurring) minOf(task.streak, 10) * 0.05f else 0.0f)
                            Math.round(task.xp * difficultyFactor * categoryFactor * streakBonus)
                        }

                        var displayXp = calculatedXp
                        var xpText = "+${displayXp} XP"
                        var xpColor = NeonCyan

                        if (task.usedMidnightOil) {
                            xpText = "0 XP"
                            xpColor = Color(0xFF991B1B)
                        } else if (isMidnightOiled) {
                            xpText = "0 XP"
                            xpColor = Color(0xFFEF4444)
                        } else {
                            if (isFreezeActive) {
                                displayXp = Math.round(displayXp / 2f)
                                xpText = "+${displayXp} XP (0.5x)"
                            }
                            if (isOverclockActive) {
                                displayXp = displayXp * 2
                                xpText = "+${displayXp} XP (2x)"
                                xpColor = Color(0xFFF59E0B)
                            } else if (isSpartansVowActive && task.category.lowercase(Locale.ROOT) == "work") {
                                displayXp = Math.round(displayXp * 1.5f)
                                xpText = "+${displayXp} XP (1.5x)"
                                xpColor = Color(0xFFEC4899)
                            }
                        }

                        Text(
                            text = xpText,
                            fontWeight = FontWeight.Bold,
                            color = xpColor,
                            fontSize = 10.sp
                        )
                    }

                    // Right: Actions (Edit, Delete)
                    Row(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (isGreyedOut) {
                            Text(
                                text = "Locked by Spartan's Vow",
                                color = Color(0xFFEF4444),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Locked by Spartan's Vow",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        IconButton(
                            onClick = onEdit,
                            enabled = !isGreyedOut,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Quest",
                                tint = if (isGreyedOut) CosmosTextSecondary.copy(alpha = 0.5f) else CosmosTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            enabled = !isGreyedOut,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Quest",
                                tint = if (isGreyedOut) CosmosTextSecondary.copy(alpha = 0.5f) else CosmosTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// CALENDAR TAB: Time-Series Queries and Charts
// ============================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarAnalyticsScreen(viewModel: TaskViewModel) {
    val rangeType by viewModel.calendarRangeType.collectAsState()
    val targetDateMillis by viewModel.targetDateMillis.collectAsState()
    val metricsResponse by viewModel.calendarResponse.collectAsState()
    val selectedCalendarDateMillis by viewModel.selectedCalendarDateMillis.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val cal = Calendar.getInstance().apply { timeInMillis = targetDateMillis }
    val format = when (rangeType) {
        CalendarRangeType.DAILY -> SimpleDateFormat("EEEE, d MMM yyyy", Locale.getDefault())
        CalendarRangeType.WEEKLY -> SimpleDateFormat("'Week' w, yyyy", Locale.getDefault())
        CalendarRangeType.MONTHLY -> SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        CalendarRangeType.YEARLY -> SimpleDateFormat("yyyy", Locale.getDefault())
    }
    val dateLabel = format.format(cal.time)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Time-Series Calendars",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = CosmosTextPrimary
        )
        Text(
            text = "Track your daily completion history and periodic achievements.",
            color = CosmosTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        // --- Range Selector row ---
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            border = BorderStroke(1.dp, CosmosSurfaceLight),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CalendarRangeType.values().forEach { range ->
                    val isSelected = rangeType == range
                    Button(
                        onClick = { viewModel.setCalendarRangeType(range) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) ElectroPurple else Color.Transparent
                        ),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = range.name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else CosmosTextSecondary
                        )
                    }
                }
            }
        }

        // --- Date Navigation ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    cal.add(
                        when (rangeType) {
                            CalendarRangeType.DAILY -> Calendar.DAY_OF_YEAR
                            CalendarRangeType.WEEKLY -> Calendar.WEEK_OF_YEAR
                            CalendarRangeType.MONTHLY -> Calendar.MONTH
                            CalendarRangeType.YEARLY -> Calendar.YEAR
                        },
                        -1
                    )
                    viewModel.setTargetDate(cal.timeInMillis)
                }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Range", tint = Color.White)
            }

            Text(
                text = dateLabel,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = ElectroPurple,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    cal.add(
                        when (rangeType) {
                            CalendarRangeType.DAILY -> Calendar.DAY_OF_YEAR
                            CalendarRangeType.WEEKLY -> Calendar.WEEK_OF_YEAR
                            CalendarRangeType.MONTHLY -> Calendar.MONTH
                            CalendarRangeType.YEARLY -> Calendar.YEAR
                        },
                        1
                    )
                    viewModel.setTargetDate(cal.timeInMillis)
                }
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Range", tint = Color.White)
            }
        }

        // Helper function for day matching
        val isSameDay = { t1: Long, t2: Long ->
            val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
            val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
            c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
        }

        // --- Interactive Calendar Date Selector Grid/Row ---
        when (rangeType) {
            CalendarRangeType.DAILY -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                    border = BorderStroke(1.dp, ElectroPurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                ) {
                    Box(modifier = Modifier.padding(14.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "🗓️ Selected Day: " + SimpleDateFormat("EEEE, d MMM yyyy", Locale.getDefault()).format(Date(selectedCalendarDateMillis)),
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            CalendarRangeType.WEEKLY -> {
                val weekStartCal = Calendar.getInstance().apply {
                    timeInMillis = targetDateMillis
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    val offset = get(Calendar.DAY_OF_WEEK) - firstDayOfWeek
                    add(Calendar.DAY_OF_YEAR, -if (offset < 0) offset + 7 else offset)
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SELECT SPECIFIC DATE BELOW TO FILTER RECORDS",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmosTextSecondary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        List(7) { idx ->
                            val currentMillis = weekStartCal.timeInMillis
                            val isSelected = isSameDay(currentMillis, selectedCalendarDateMillis)
                            
                            val dayNum = weekStartCal.get(Calendar.DAY_OF_MONTH)
                            val dayName = SimpleDateFormat("E", Locale.getDefault()).format(weekStartCal.time)
                            
                            Card(
                                onClick = { viewModel.setSelectedCalendarDate(currentMillis) },
                                colors = CardDefaults.cardColors(containerColor = if (isSelected) ElectroPurple else CosmosSurface),
                                border = BorderStroke(1.dp, if (isSelected) ElectroPurple else CosmosSurfaceLight),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.width(56.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(dayName, fontSize = 11.sp, color = if (isSelected) Color.White else CosmosTextSecondary, fontWeight = FontWeight.Bold)
                                    Text(dayNum.toString(), fontSize = 14.sp, color = if (isSelected) Color.White else CosmosTextPrimary, fontWeight = FontWeight.Black)
                                }
                            }
                            weekStartCal.add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }
                }
            }
            CalendarRangeType.MONTHLY -> {
                val monthStartCal = Calendar.getInstance().apply {
                    timeInMillis = targetDateMillis
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val maxDays = monthStartCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val currentMonthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(monthStartCal.time)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SELECT DAY IN $currentMonthName",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmosTextSecondary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxItemsInEachRow = 7,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        List(maxDays) { idx ->
                            val currentMillis = monthStartCal.timeInMillis
                            val isSelected = isSameDay(currentMillis, selectedCalendarDateMillis)
                            val dayNum = idx + 1

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ElectroPurple else CosmosSurface)
                                    .border(BorderStroke(1.dp, if (isSelected) ElectroPurple else CosmosSurfaceLight), RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setSelectedCalendarDate(currentMillis) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) Color.White else CosmosTextPrimary
                                )
                            }
                            monthStartCal.add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }
                }
            }
            CalendarRangeType.YEARLY -> {
                val yearStartCal = Calendar.getInstance().apply {
                    timeInMillis = targetDateMillis
                    set(Calendar.MONTH, 0)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SELECT MONTH OF YEAR",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmosTextSecondary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        List(12) { idx ->
                            val currentMillis = yearStartCal.timeInMillis
                            val isSelected = isSameDay(currentMillis, selectedCalendarDateMillis)
                            val monthLabel = SimpleDateFormat("MMM", Locale.getDefault()).format(yearStartCal.time)
                            
                            Card(
                                onClick = { viewModel.setSelectedCalendarDate(currentMillis) },
                                colors = CardDefaults.cardColors(containerColor = if (isSelected) ElectroPurple else CosmosSurface),
                                border = BorderStroke(1.dp, if (isSelected) ElectroPurple else CosmosSurfaceLight),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(monthLabel, fontSize = 12.sp, color = if (isSelected) Color.White else CosmosTextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                            yearStartCal.add(Calendar.MONTH, 1)
                        }
                    }
                }
            }
        }

        // --- Custom Daily Special Note Editor (Journaling entry) ---
        val noteDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDateStr = noteDateFormat.format(Date(selectedCalendarDateMillis))
        val readableDateStr = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date(selectedCalendarDateMillis))
        
        val allNotes by viewModel.allDailyNotesFlow.collectAsState(initial = emptyList())
        val notesMap = allNotes.associate { it.dateStr to it.note }
        val savedNoteText = notesMap[selectedDateStr] ?: ""

        val context = androidx.compose.ui.platform.LocalContext.current
        var isSavedByClick by remember(selectedDateStr) { mutableStateOf(false) }

        var tempNoteText by remember(selectedDateStr) { mutableStateOf("") }
        LaunchedEffect(selectedDateStr, savedNoteText) {
            tempNoteText = savedNoteText
            isSavedByClick = false
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            border = BorderStroke(1.dp, CosmosSurfaceLight),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "📝 SPECIAL DAILY NOTE",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectroPink,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Journal thoughts or objectives for $readableDateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = CosmosTextSecondary
                )

                OutlinedTextField(
                    value = tempNoteText,
                    onValueChange = {
                        tempNoteText = it
                        isSavedByClick = false
                    },
                    placeholder = { Text("Write a special note, strategy or journal entry for this date...", color = CosmosTextSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectroPurple,
                        unfocusedBorderColor = CosmosSurfaceLight,
                        focusedLabelColor = ElectroPurple,
                        unfocusedLabelColor = CosmosTextSecondary
                    )
                )

                Button(
                    onClick = {
                        viewModel.saveDailyNote(selectedDateStr, tempNoteText)
                        isSavedByClick = true
                        android.widget.Toast.makeText(context, "Note Saved Successfully!", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSavedByClick) Color(0xFF10B981) else ElectroPurple
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save Note", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSavedByClick) "Saved ✔️" else "Save Note",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- Selective Progress logs checklist for selected date ---
        val tasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
        val completions by viewModel.completionsFlow.collectAsState(initial = emptyList())

        val dayRecords = tasks.filter { task ->
            if (task.isRecurring) {
                task.createdAt <= selectedCalendarDateMillis
            } else {
                isSameDay(task.assignedDateMillis, selectedCalendarDateMillis)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            border = BorderStroke(1.dp, CosmosSurfaceLight),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "⚔️ DAILY ACHIEVEMENTS & QUESTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Completions and scheduled tasks for this date.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CosmosTextSecondary
                )

                if (dayRecords.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No quests scheduled or enqueued for this date.",
                            color = CosmosTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    dayRecords.forEach { task ->
                        val calendarDateFormattedStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selectedCalendarDateMillis))
                        val isCompletedOnThisDay = if (task.isRecurring) {
                            task.history.contains(calendarDateFormattedStr)
                        } else {
                            completions.any { comp ->
                                comp.taskId == task.id && isSameDay(comp.completedAt, selectedCalendarDateMillis)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CosmosBackground.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = getTaskDisplayName(task.title),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CosmosTextPrimary
                                )
                                Text(
                                    text = if (task.isRecurring) "🔄 Habit (${task.frequencyDaysOfWeek} days/week)" else "🎯 One-Time Quest",
                                    fontSize = 11.sp,
                                    color = CosmosTextSecondary
                                )
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.toggleTaskCompletionOnDay(task, selectedCalendarDateMillis)
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isCompletedOnThisDay) ElectroPurple.copy(alpha = 0.2f) else CosmosSurfaceLight)
                                    .border(1.5.dp, if (isCompletedOnThisDay) ElectroPurple else Color.Gray, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isCompletedOnThisDay) Icons.Default.Check else Icons.Default.Add,
                                    contentDescription = "Day completion status toggle",
                                    tint = if (isCompletedOnThisDay) ElectroPurple else CosmosTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Dynamic Report Metrics Card ---
        metricsResponse?.let { resp ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                border = BorderStroke(1.dp, CosmosSurfaceLight),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "${resp.rangeType} HEALTH INDEX REPORT",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectroPurple,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Quest Progress Rate", color = CosmosTextSecondary, fontSize = 11.sp)
                            Text(
                                "${resp.completedTasks} of ${resp.totalTasks} Done",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = CosmosTextPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("XP Achieved", color = CosmosTextSecondary, fontSize = 11.sp)
                            Text(
                                "+${resp.totalXpEarned} XP",
                                fontWeight = FontWeight.Bold,
                                color = ElectroPurple,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = (resp.completionRate / 100f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = ElectroPurple,
                        trackColor = CosmosSurfaceLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Completion Index: ${"%.1f".format(resp.completionRate)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmosTextSecondary,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            // --- Custom Canvas Graphic (Time-Series chart) ---
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                border = BorderStroke(1.dp, CosmosSurfaceLight),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HISTOGRAM CHRONOLOGY",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmosTextSecondary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    TimeHistogramCanvas(timeSeries = resp.timeSeriesData)
                }
            }

            // --- Categories Distribution Breakdown ---
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                border = BorderStroke(1.dp, CosmosSurfaceLight),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PROGRESS BY CATEGORY",
                        style = MaterialTheme.typography.labelSmall,
                        color = CosmosTextSecondary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val dist = resp.categoryDistribution
                    val grandTotal = dist.work + dist.personal + dist.entertainment
                    
                    CategoryMetricRow(label = "💼 Work Activities", completed = dist.work, total = grandTotal, color = CategoryWorkColor)
                    CategoryMetricRow(label = "🧘 Personal Tasks", completed = dist.personal, total = grandTotal, color = CategoryPersonalColor)
                    CategoryMetricRow(label = "🧭 Other Tasks", completed = dist.entertainment, total = grandTotal, color = CategoryEntertainmentColor)
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ElectroPurple)
        }
    }
}

@Composable
fun TimeHistogramCanvas(timeSeries: List<CalendarTimeSeriesItem>) {
    if (timeSeries.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No graph parameters logged.", color = Color(0xFF49454F))
        }
        return
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        val width = size.width
        val height = size.height

        val stepX = if (timeSeries.size > 1) {
            width / (timeSeries.size - 1)
        } else {
            width
        }

        val maxVal = maxOf(1, timeSeries.maxOfOrNull { it.completedCount } ?: 1)
        
        // Draw gridlines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = height * i / gridLines
            drawLine(
                color = Color(0xFFCAC4D0).copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        val points = mutableListOf<Offset>()
        for (idx in timeSeries.indices) {
            val item = timeSeries[idx]
            val x = idx * stepX
            val y = height - (item.completedCount.toFloat() / maxVal.toFloat() * height)
            points.add(Offset(x, y))
        }

        // Draw Area brush
        if (points.isNotEmpty()) {
            val areaPath = Path().apply {
                moveTo(0f, height)
                for (idx in points.indices) {
                    val p = points[idx]
                    lineTo(p.x, p.y)
                }
                lineTo(width, height)
                close()
            }
            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(ElectroPurple.copy(alpha = 0.4f), Color.Transparent),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw connecting line
            val linePath = Path().apply {
                val first = points.first()
                moveTo(first.x, first.y)
                for (idx in 1 until points.size) {
                    val p = points[idx]
                    lineTo(p.x, p.y)
                }
            }
            drawPath(
                path = linePath,
                color = ElectroPurple,
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw Dot highlights
            for (idx in points.indices) {
                val p = points[idx]
                val item = timeSeries[idx]
                drawCircle(
                    color = CosmosBackground,
                    radius = 6.dp.toPx(),
                    center = p
                )
                drawCircle(
                    color = if (item.completedCount > 0) ElectroPurple else Color(0xFFCAC4D0),
                    radius = 4.dp.toPx(),
                    center = p
                )
            }
        }
    }

    // Horizontal Labels
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        timeSeries.forEach { item ->
            Text(
                text = item.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF49454F),
                modifier = Modifier.widthIn(max = 48.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CategoryMetricRow(
    label: String,
    completed: Int,
    total: Int,
    color: Color
) {
    val fraction = if (total > 0) completed.toFloat() / total.toFloat() else 0f
    
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1B20))
            Text(
                "$completed reps",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = fraction.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = CosmosSurfaceLight
        )
    }
}

// ============================================
// PRIORITY MATRIX TAB: Eisenhower Urgency/Importance Grid
// ============================================
@Composable
fun PriorityMatrixScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
    val activeTasks = remember(tasks) { tasks.filter { !it.isCompleted } }

    var selectedTaskForReassign by remember { mutableStateOf<com.example.data.Task?>(null) }
    var quadrantToAddIn by remember { mutableStateOf<String?>(null) }

    val q1 = activeTasks.filter { it.priorityQuadrant == "Urgent & Important" }
    val q2 = activeTasks.filter { it.priorityQuadrant == "Not Urgent & Important" }
    val q3 = activeTasks.filter { it.priorityQuadrant == "Urgent & Unimportant" }
    val q4 = activeTasks.filter { it.priorityQuadrant == "Not Urgent & Unimportant" }

    // Nested dialog for adding task directly to a quadrant
    if (quadrantToAddIn != null) {
        AddTaskDialog(
            viewModel = viewModel,
            onDismiss = { quadrantToAddIn = null },
            onConfirm = { title, category, isRecurring, xp, difficulty, assignedDateMillis, _, freq ->
                viewModel.addTask(title, category, isRecurring, xp, difficulty, assignedDateMillis, freq, quadrantToAddIn!!)
                quadrantToAddIn = null
            }
        )
    }

    // Reassignment picker bottom dialog
    if (selectedTaskForReassign != null) {
        val task = selectedTaskForReassign!!
        AlertDialog(
            onDismissRequest = { selectedTaskForReassign = null },
            title = {
                Text(
                    text = "Assign Quadrant",
                    fontWeight = FontWeight.Bold,
                    color = CosmosTextPrimary
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Change priority of \"${getTaskDisplayName(task.title)}\":",
                        color = CosmosTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val options = listOf(
                        Triple("Urgent & Important", "🔴 DO FIRST (Urgent & Important)", ElectroPink),
                        Triple("Not Urgent & Important", "🟣 SCHEDULE (Important, Not Urgent)", ElectroPurple),
                        Triple("Urgent & Unimportant", "🔵 DELEGATE (Urgent, Not Important)", NeonCyan),
                        Triple("Not Urgent & Unimportant", "🌿 ELIMINATE (Neither urgent/important)", Color(0xFF4ADE80))
                    )

                    options.forEach { (quadKey, label, color) ->
                        Button(
                            onClick = {
                                viewModel.updateTaskQuadrant(task, quadKey)
                                selectedTaskForReassign = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmosSurfaceLight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .border(BorderStroke(1.dp, color.copy(alpha = 0.5f)), RoundedCornerShape(10.dp))
                        ) {
                            Text(text = label, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedTaskForReassign = null }) {
                    Text("CANCEL", color = CosmosTextSecondary)
                }
            },
            containerColor = CosmosSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Headline
        Column {
            Text(
                text = "PRIORITY MATRIX",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = CosmosTextPrimary
            )
            Text(
                text = "Dwight Eisenhower's urgency matrix to structure focus, optimize cycles, and accelerate milestones.",
                color = CosmosTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 2x2 grid layout
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Row 1: Q1 and Q2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    QuadrantCard(
                        title = "Urgent & Important",
                        subtitle = "Do immediately",
                        colorAccent = ElectroPink,
                        tasks = q1,
                        onAddTask = { quadrantToAddIn = "Urgent & Important" },
                        onSelectTask = { selectedTaskForReassign = it }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    QuadrantCard(
                        title = "Not Urgent & Important",
                        subtitle = "Schedule date",
                        colorAccent = ElectroPurple,
                        tasks = q2,
                        onAddTask = { quadrantToAddIn = "Not Urgent & Important" },
                        onSelectTask = { selectedTaskForReassign = it }
                    )
                }
            }

            // Row 2: Q3 and Q4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    QuadrantCard(
                        title = "Urgent & Unimportant",
                        subtitle = "Delegate choice",
                        colorAccent = NeonCyan,
                        tasks = q3,
                        onAddTask = { quadrantToAddIn = "Urgent & Unimportant" },
                        onSelectTask = { selectedTaskForReassign = it }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    QuadrantCard(
                        title = "Not Urgent & Unimportant",
                        subtitle = "Eliminate/Others",
                        colorAccent = Color(0xFF4ADE80),
                        tasks = q4,
                        onAddTask = { quadrantToAddIn = "Not Urgent & Unimportant" },
                        onSelectTask = { selectedTaskForReassign = it }
                    )
                }
            }
        }
    }
}

@Composable
fun QuadrantCard(
    title: String,
    subtitle: String,
    colorAccent: Color,
    tasks: List<com.example.data.Task>,
    onAddTask: () -> Unit,
    onSelectTask: (com.example.data.Task) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CosmosSurface),
        border = BorderStroke(1.dp, CosmosSurfaceLight),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = colorAccent,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = subtitle,
                            fontSize = 9.sp,
                            color = CosmosTextSecondary
                        )
                    }
                    
                    IconButton(
                        onClick = onAddTask,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task to Quadrant",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                HorizontalDivider(color = CosmosSurfaceLight.copy(alpha = 0.5f))

                // Tasks list
                if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No quests",
                            color = Color(0xFF475569),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        tasks.forEach { item ->
                            Card(
                                onClick = { onSelectTask(item) },
                                colors = CardDefaults.cardColors(containerColor = CosmosSurfaceLight.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(colorAccent)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = getTaskDisplayName(item.title),
                                        color = CosmosTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Create AddTask Dialog ---
@Composable
fun AddAddTaskOptionItem(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) ElectroPurple else CosmosSurface)
            .border(
                BorderStroke(
                    1.dp,
                    if (isSelected) ElectroPurple else CosmosSurfaceLight
                ),
                RoundedCornerShape(8.dp)
            )
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else CosmosTextSecondary
        )
    }
}

@Composable
fun AddTaskDialog(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Boolean, Int, String, Long, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Work") }
    var isRecurring by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf("Medium") }
    var selectedEnergy by remember { mutableStateOf("Medium/Steady") }
    val xpEarned = when (selectedDifficulty) {
        "Easy" -> 10
        "Medium" -> 30
        "Hard" -> 50
        "Mythic" -> 100
        else -> 30
    }
    var assignedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedQuadrant by remember { mutableStateOf("Not Urgent & Unimportant") }
    var frequencyDaysOfWeek by remember { mutableStateOf(3) } // Default habit frequency 3 days/week

    val allCategories by viewModel.allCategoriesFlow.collectAsState()
    var showCreateCategoryDialog by remember { mutableStateOf(false) }

    if (showCreateCategoryDialog) {
        var newCatName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateCategoryDialog = false },
            title = { Text("New Custom Category", fontWeight = FontWeight.Bold, color = CosmosTextPrimary) },
            text = {
                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    label = { Text("Category Name") },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectroPurple,
                        unfocusedBorderColor = CosmosSurfaceLight,
                        focusedLabelColor = ElectroPurple,
                        unfocusedLabelColor = CosmosTextSecondary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = newCatName.trim()
                        if (trimmed.isNotEmpty()) {
                            viewModel.addCustomCategory(trimmed)
                            selectedCategory = trimmed
                        }
                        showCreateCategoryDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ElectroPurple)
                ) {
                    Text("CREATE", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCategoryDialog = false }) {
                    Text("CANCEL", color = CosmosTextSecondary)
                }
            },
            containerColor = CosmosSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Create Mythic Quest",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFF8FAFC)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Quest Title", fontSize = 11.sp) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_task_title_input"),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, lineHeight = 20.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectroPurple,
                        unfocusedBorderColor = CosmosSurfaceLight,
                        focusedLabelColor = ElectroPurple,
                        unfocusedLabelColor = CosmosTextSecondary
                    )
                )

                // Category selection row
                Column {
                    Text("Quest Classification", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        allCategories.forEach { category ->
                            AddAddTaskOptionItem(
                                label = when (category) {
                                    "Work" -> "💼 Work"
                                    "Personal" -> "🧘 Personal"
                                    "Others" -> "🧭 Others"
                                    else -> "⭐ $category"
                                },
                                isSelected = selectedCategory == category,
                                onSelect = { selectedCategory = category }
                            )
                        }

                        // ➕ Add Custom Category button
                        Card(
                            onClick = { showCreateCategoryDialog = true },
                            colors = CardDefaults.cardColors(containerColor = CosmosSurfaceLight.copy(alpha = 0.4f)),
                            border = BorderStroke(1.dp, CosmosSurfaceLight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxHeight().padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "New Category", modifier = Modifier.size(14.dp), tint = ElectroPink)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectroPink)
                            }
                        }
                    }
                }

                // Priority Matrix Quadrant Selection
                Column {
                    Text("Urgency & Importance Matrix", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    var expandedQuad by remember { mutableStateOf(false) }
                    Card(
                        onClick = { expandedQuad = true },
                        colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                        border = BorderStroke(1.dp, CosmosSurfaceLight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedQuadrant,
                                color = CosmosTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = ElectroPurple, modifier = Modifier.size(18.dp))
                        }
                    }
                    
                    DropdownMenu(
                        expanded = expandedQuad,
                        onDismissRequest = { expandedQuad = false },
                        modifier = Modifier.background(CosmosSurface).border(1.dp, CosmosSurfaceLight)
                    ) {
                        listOf(
                            "Urgent & Important",
                            "Not Urgent & Important",
                            "Urgent & Unimportant",
                            "Not Urgent & Unimportant"
                        ).forEach { quadrant ->
                            DropdownMenuItem(
                                text = { Text(quadrant, color = CosmosTextPrimary, fontWeight = FontWeight.Medium) },
                                onClick = {
                                    selectedQuadrant = quadrant
                                    expandedQuad = false
                                }
                            )
                        }
                    }
                }

                // Difficulty
                Column {
                    Text("Quest Difficulty", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Easy", "Medium", "Hard", "Mythic").forEach { itDiff ->
                            val isSel = selectedDifficulty == itDiff
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ElectroPurple else CosmosSurface)
                                    .border(
                                        BorderStroke(1.dp, if (isSel) ElectroPurple else CosmosSurfaceLight),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDifficulty = itDiff }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = itDiff,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else CosmosTextSecondary
                                )
                            }
                        }
                    }
                }

                // Energy Level selector
                Column {
                    Text("Energy Level required", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Low Brainpower", "Medium/Steady", "High Focus").forEach { level ->
                            val isSel = selectedEnergy == level
                            val (badgeBg, badgeColor, badgeText) = when (level) {
                                "High Focus" -> Triple(if (isSel) Color(0xFF450A0A) else CosmosSurface, if (isSel) Color(0xFFFCA5A5) else CosmosTextSecondary, "🔴 High Focus")
                                "Low Brainpower" -> Triple(if (isSel) Color(0xFF064E3B) else CosmosSurface, if (isSel) Color(0xFF6EE7B7) else CosmosTextSecondary, "🟢 Low")
                                else -> Triple(if (isSel) Color(0xFF1E3A8A) else CosmosSurface, if (isSel) Color(0xFF93C5FD) else CosmosTextSecondary, "🔵 Steady")
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeBg)
                                    .border(
                                        BorderStroke(1.dp, if (isSel) badgeBg else CosmosSurfaceLight),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedEnergy = level }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = badgeText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            }
                        }
                    }
                }

                // Type Option
                Column {
                    Text("Repetition Type", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AddAddTaskOptionItem(
                            label = "🎯 One-Time Quest",
                            isSelected = !isRecurring,
                            onSelect = { isRecurring = false },
                            modifier = Modifier.weight(1f)
                        )
                        AddAddTaskOptionItem(
                            label = "⚡ Repeat Daily",
                            isSelected = isRecurring,
                            onSelect = { isRecurring = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Area with Date assignment, Datepicker button
                if (!isRecurring) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val dateFormat = SimpleDateFormat("EEEE, d MMM yyyy", Locale.getDefault())
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Assign target date", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val calendar = Calendar.getInstance().apply { timeInMillis = assignedDateMillis }
                                    android.app.DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            val sel = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, year)
                                                set(Calendar.MONTH, month)
                                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                                set(Calendar.HOUR_OF_DAY, 0)
                                                set(Calendar.MONTH, month)
                                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                                set(Calendar.HOUR_OF_DAY, 0)
                                                set(Calendar.MINUTE, 0)
                                                set(Calendar.SECOND, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }
                                            assignedDateMillis = sel.timeInMillis
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmosSurfaceLight),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pick Date", tint = NeonCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Select Date", color = CosmosTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Text(
                                text = dateFormat.format(java.util.Date(assignedDateMillis)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = ElectroPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    // Habit frequency selector if isRecurring is true
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Habit Frequency", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                                Text("$frequencyDaysOfWeek days/week", fontWeight = FontWeight.Bold, color = ElectroPink, fontSize = 12.sp)
                            }
                            Slider(
                                value = frequencyDaysOfWeek.toFloat(),
                                onValueChange = { frequencyDaysOfWeek = it.toInt() },
                                valueRange = 1f..7f,
                                steps = 5,
                                colors = SliderDefaults.colors(
                                    thumbColor = ElectroPink,
                                    activeTrackColor = ElectroPink,
                                    inactiveTrackColor = CosmosSurfaceLight
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = CosmosTextSecondary)
                ) {
                    Text("CANCEL", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                TextButton(
                    onClick = { if (title.isNotBlank()) onConfirm(title.trim() + "|||" + selectedEnergy, selectedCategory, isRecurring, xpEarned, selectedDifficulty, assignedDateMillis, selectedQuadrant, frequencyDaysOfWeek) },
                    colors = ButtonDefaults.textButtonColors(contentColor = ElectroPurple),
                    modifier = Modifier.testTag("add_task_dialog_confirm")
                ) {
                    Text("ADD QUEST", fontWeight = FontWeight.Black)
                }
            }
        },
        dismissButton = null,
        containerColor = CosmosSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun EditTaskDialog(
    task: Task,
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    var title by remember { mutableStateOf(getTaskDisplayName(task.title)) }
    var selectedCategory by remember { mutableStateOf(task.category) }
    var isRecurring by remember { mutableStateOf(task.isRecurring) }
    var selectedDifficulty by remember { mutableStateOf(task.difficulty) }
    var selectedEnergy by remember { mutableStateOf(getTaskEnergyLevel(task.title)) }
    val xpEarned = when (selectedDifficulty) {
        "Easy" -> 10
        "Medium" -> 30
        "Hard" -> 50
        "Mythic" -> 100
        else -> 30
    }
    var assignedDateMillis by remember { mutableStateOf(task.assignedDateMillis) }
    var selectedQuadrant by remember { mutableStateOf(task.priorityQuadrant) }
    var frequencyDaysOfWeek by remember { mutableStateOf(task.frequencyDaysOfWeek) }

    val allCategories by viewModel.allCategoriesFlow.collectAsState()
    var showCreateCategoryDialog by remember { mutableStateOf(false) }

    if (showCreateCategoryDialog) {
        var newCatName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateCategoryDialog = false },
            title = { Text("New Custom Category", fontWeight = FontWeight.Bold, color = CosmosTextPrimary) },
            text = {
                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    label = { Text("Category Name") },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectroPurple,
                        unfocusedBorderColor = CosmosSurfaceLight,
                        focusedLabelColor = ElectroPurple,
                        unfocusedLabelColor = CosmosTextSecondary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = newCatName.trim()
                        if (trimmed.isNotEmpty()) {
                            viewModel.addCustomCategory(trimmed)
                            selectedCategory = trimmed
                        }
                        showCreateCategoryDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ElectroPurple)
                ) {
                    Text("CREATE", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCategoryDialog = false }) {
                    Text("CANCEL", color = CosmosTextSecondary)
                }
            },
            containerColor = CosmosSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isRecurring) "Edit Recurring Ritual" else "Edit Mythic Quest",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFF8FAFC)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", fontSize = 11.sp) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, lineHeight = 20.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectroPurple,
                        unfocusedBorderColor = CosmosSurfaceLight,
                        focusedLabelColor = ElectroPurple,
                        unfocusedLabelColor = CosmosTextSecondary
                    )
                )

                // Category selection row
                Column {
                    Text("Classification", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        allCategories.forEach { category ->
                            AddAddTaskOptionItem(
                                label = when (category) {
                                    "Work" -> "💼 Work"
                                    "Personal" -> "🧘 Personal"
                                    "Others" -> "🧭 Others"
                                    else -> "⭐ $category"
                                },
                                isSelected = selectedCategory == category,
                                onSelect = { selectedCategory = category }
                            )
                        }

                        // ➕ Add Custom Category button
                        Card(
                            onClick = { showCreateCategoryDialog = true },
                            colors = CardDefaults.cardColors(containerColor = CosmosSurfaceLight.copy(alpha = 0.4f)),
                            border = BorderStroke(1.dp, CosmosSurfaceLight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxHeight().padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "New Category", modifier = Modifier.size(14.dp), tint = ElectroPink)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectroPink)
                            }
                        }
                    }
                }

                // If not recurring, show scheduled quadrant selection & date picker
                if (!isRecurring) {
                    Column {
                        Text("Urgency & Importance Matrix", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        var expandedQuad by remember { mutableStateOf(false) }
                        Card(
                            onClick = { expandedQuad = true },
                            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                            border = BorderStroke(1.dp, CosmosSurfaceLight),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedQuadrant,
                                    color = CosmosTextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = ElectroPurple, modifier = Modifier.size(18.dp))
                            }
                        }
                        
                        DropdownMenu(
                            expanded = expandedQuad,
                            onDismissRequest = { expandedQuad = false },
                            modifier = Modifier.background(CosmosSurface).border(1.dp, CosmosSurfaceLight)
                        ) {
                            listOf(
                                "Urgent & Important",
                                "Not Urgent & Important",
                                "Urgent & Unimportant",
                                "Not Urgent & Unimportant"
                            ).forEach { quadrant ->
                                DropdownMenuItem(
                                    text = { Text(quadrant, color = CosmosTextPrimary, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        selectedQuadrant = quadrant
                                        expandedQuad = false
                                    }
                                )
                            }
                        }
                    }

                    val context = androidx.compose.ui.platform.LocalContext.current
                    val dateFormat = SimpleDateFormat("EEEE, d MMM yyyy", Locale.getDefault())
                    // Integrated container for the Scheduled Date component
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Scheduled Date", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val calendar = Calendar.getInstance().apply { timeInMillis = assignedDateMillis }
                                    android.app.DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            val sel = Calendar.getInstance().apply {
                                                set(Calendar.YEAR, year)
                                                set(Calendar.MONTH, month)
                                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                                set(Calendar.HOUR_OF_DAY, 0)
                                                set(Calendar.MINUTE, 0)
                                                set(Calendar.SECOND, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }
                                            assignedDateMillis = sel.timeInMillis
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmosSurfaceLight),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pick Date", tint = NeonCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Select Date", color = CosmosTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Text(
                                text = dateFormat.format(java.util.Date(assignedDateMillis)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = ElectroPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // If recurring, show weekly frequency
                if (isRecurring) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ritual Frequency", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                            Text("$frequencyDaysOfWeek days/week", fontWeight = FontWeight.Bold, color = ElectroPink, fontSize = 12.sp)
                        }
                        Slider(
                            value = frequencyDaysOfWeek.toFloat(),
                            onValueChange = { frequencyDaysOfWeek = it.toInt() },
                            valueRange = 1f..7f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectroPink,
                                activeTrackColor = ElectroPink,
                                inactiveTrackColor = CosmosSurfaceLight
                            )
                        )
                    }
                }

                // Difficulty
                Column {
                    Text("Quest Difficulty", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Easy", "Medium", "Hard", "Mythic").forEach { itDiff ->
                            val isSel = selectedDifficulty == itDiff
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ElectroPurple else CosmosSurface)
                                    .border(
                                        BorderStroke(1.dp, if (isSel) ElectroPurple else CosmosSurfaceLight),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDifficulty = itDiff }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = itDiff,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else CosmosTextSecondary
                                )
                            }
                        }
                    }
                }

                // Energy Level selector
                Column {
                    Text("Energy Level required", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Low Brainpower", "Medium/Steady", "High Focus").forEach { level ->
                            val isSel = selectedEnergy == level
                            val (badgeBg, badgeColor, badgeText) = when (level) {
                                "High Focus" -> Triple(if (isSel) Color(0xFF450A0A) else CosmosSurface, if (isSel) Color(0xFFFCA5A5) else CosmosTextSecondary, "🔴 High Focus")
                                "Low Brainpower" -> Triple(if (isSel) Color(0xFF064E3B) else CosmosSurface, if (isSel) Color(0xFF6EE7B7) else CosmosTextSecondary, "🟢 Low")
                                else -> Triple(if (isSel) Color(0xFF1E3A8A) else CosmosSurface, if (isSel) Color(0xFF93C5FD) else CosmosTextSecondary, "🔵 Steady")
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeBg)
                                    .border(
                                        BorderStroke(1.dp, if (isSel) badgeBg else CosmosSurfaceLight),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedEnergy = level }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = badgeText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = CosmosTextSecondary)
                ) {
                    Text("CANCEL", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                TextButton(
                    onClick = {
                        if (title.isNotBlank()) {
                            val updatedTask = task.copy(
                                title = title.trim() + "|||" + selectedEnergy,
                                category = selectedCategory,
                                difficulty = selectedDifficulty,
                                xp = xpEarned,
                                assignedDateMillis = assignedDateMillis,
                                priorityQuadrant = selectedQuadrant,
                                frequencyDaysOfWeek = frequencyDaysOfWeek
                            )
                            onConfirm(updatedTask)
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ElectroPurple)
                ) {
                    Text("SAVE CHANGES", fontWeight = FontWeight.Black)
                }
            }
        },
        dismissButton = null,
        containerColor = CosmosSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun HabitsScreen(
    viewModel: TaskViewModel,
    onEditHabit: (Task) -> Unit
) {
    val tasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
    val habits = tasks.filter { it.isRecurring }
    var showCreateHabitDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Block ---
        Text(
            text = "Dedicated Habits Hub",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = CosmosTextPrimary
        )
        Text(
            text = "Form long-term neural pathways. Build custom rituals with weekly frequency goals to scale your XP indices.",
            color = CosmosTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        // --- Stats card for habits ---
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            border = BorderStroke(1.dp, CosmosSurfaceLight),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Enrolled Habits", color = CosmosTextSecondary, fontSize = 12.sp)
                    Text("${habits.size} Active Protocols", fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyLarge, color = CosmosTextPrimary)
                }
                Button(
                    onClick = { showCreateHabitDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectroPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Enlist Habit")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Enlist Habit")
                }
            }
        }

        // --- Habits Checklist List ---
        if (habits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Empty Habits", tint = CosmosTextSecondary, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No recurring protocols locked in yet.", color = CosmosTextPrimary, fontWeight = FontWeight.Bold)
                    Text("Tap 'Enlist Habit' above to pledge your first automated milestone!", color = CosmosTextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                habits.forEach { habit ->
                    val isCompletedToday = habit.isCompleted
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmosSurface),
                        border = BorderStroke(1.dp, CosmosSurfaceLight),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .graphicsLayer(alpha = if (isCompletedToday) 0.6f else 1f)
                            ) {
                                Text(
                                    text = getTaskDisplayName(habit.title),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        textDecoration = if (isCompletedToday) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = CosmosTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Classification Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CosmosSurfaceLight)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = habit.category,
                                            fontSize = 9.sp,
                                            color = ElectroPink,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Energy Pill
                                    val energy = getTaskEnergyLevel(habit.title)
                                    val (energyBg, energyColor, energyText) = when (energy) {
                                        "High Focus" -> Triple(Color(0xFF450A0A), Color(0xFFFCA5A5), "🔴 High Focus")
                                        "Low Brainpower" -> Triple(Color(0xFF064E3B), Color(0xFF6EE7B7), "🟢 Low")
                                        else -> Triple(Color(0xFF1E3A8A), Color(0xFF93C5FD), "🔵 Steady")
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(energyBg)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = energyText,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = energyColor
                                        )
                                    }
                                    
                                    // Frequency Indicator
                                    Text(
                                        text = "🔄 ${habit.frequencyDaysOfWeek} days/week",
                                        fontSize = 11.sp,
                                        color = NeonCyan,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    if (habit.streak > 0) {
                                        Text(
                                            text = "🔥 ${habit.streak} day streak",
                                            fontSize = 11.sp,
                                            color = ElectroPurple,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Toggle complete today checkbox button
                                IconButton(
                                    onClick = { viewModel.toggleTaskCompletion(habit) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (isCompletedToday) ElectroPurple.copy(alpha = 0.2f) else CosmosSurfaceLight)
                                        .border(2.dp, if (isCompletedToday) ElectroPurple else Color.Gray.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isCompletedToday) Icons.Default.Check else Icons.Default.Add,
                                        contentDescription = "Toggle Complete",
                                        tint = if (isCompletedToday) ElectroPurple else CosmosTextPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onEditHabit(habit) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Habit", tint = CosmosTextSecondary, modifier = Modifier.size(20.dp))
                                }

                                IconButton(
                                    onClick = { viewModel.deleteTask(habit) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Habit", tint = Color.Red.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateHabitDialog) {
        CreateHabitDialog(
            viewModel = viewModel,
            onDismiss = { showCreateHabitDialog = false },
            onConfirm = { title, category, frequency, xp, difficulty ->
                viewModel.addTask(
                    title = title,
                    category = category,
                    isRecurring = true,
                    xp = xp,
                    difficulty = difficulty,
                    frequencyDaysOfWeek = frequency
                )
                showCreateHabitDialog = false
            }
        )
    }
}

@Composable
fun CreateHabitDialog(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Int, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Work") }
    var selectedFrequency by remember { mutableStateOf(5) } // Default 5 days/week
    var selectedDifficulty by remember { mutableStateOf("Medium") }
    var selectedEnergy by remember { mutableStateOf("Medium/Steady") }
    val xpEarned = when (selectedDifficulty) {
        "Easy" -> 10
        "Medium" -> 30
        "Hard" -> 50
        "Mythic" -> 100
        else -> 30
    }

    val allCategories by viewModel.allCategoriesFlow.collectAsState()
    var showCreateCategoryDialog by remember { mutableStateOf(false) }

    if (showCreateCategoryDialog) {
        var newCatName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateCategoryDialog = false },
            title = { Text("New Custom Category", fontWeight = FontWeight.Bold, color = CosmosTextPrimary) },
            text = {
                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    label = { Text("Category Name") },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectroPurple,
                        unfocusedBorderColor = CosmosSurfaceLight,
                        focusedLabelColor = ElectroPurple,
                        unfocusedLabelColor = CosmosTextSecondary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = newCatName.trim()
                        if (trimmed.isNotEmpty()) {
                            viewModel.addCustomCategory(trimmed)
                            selectedCategory = trimmed
                        }
                        showCreateCategoryDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ElectroPurple)
                ) {
                    Text("CREATE", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCategoryDialog = false }) {
                    Text("CANCEL", color = CosmosTextSecondary)
                }
            },
            containerColor = CosmosSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Pledge Habit Routine",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFF8FAFC)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Protocol Title (e.g. Read 15 pages)") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectroPurple,
                        unfocusedBorderColor = CosmosSurfaceLight,
                        focusedLabelColor = ElectroPurple,
                        unfocusedLabelColor = CosmosTextSecondary
                    )
                )

                // Category selection row
                Column {
                    Text("Routine Classification", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        allCategories.forEach { category ->
                            AddAddTaskOptionItem(
                                label = when (category) {
                                    "Work" -> "💼 Work"
                                    "Personal" -> "🧘 Personal"
                                    "Others" -> "🧭 Others"
                                    else -> "⭐ $category"
                                },
                                isSelected = selectedCategory == category,
                                onSelect = { selectedCategory = category }
                            )
                        }

                        // ➕ Add Custom Category button
                        Card(
                            onClick = { showCreateCategoryDialog = true },
                            colors = CardDefaults.cardColors(containerColor = CosmosSurfaceLight.copy(alpha = 0.4f)),
                            border = BorderStroke(1.dp, CosmosSurfaceLight.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add custom category", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                                Text("New", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                // Frequency Row Button Selector
                Column {
                    Text("Target Frequency (${selectedFrequency} days/week)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (1..7).forEach { num ->
                            val isSel = selectedFrequency == num
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ElectroPurple else CosmosSurface)
                                    .border(
                                        BorderStroke(1.dp, if (isSel) ElectroPurple else CosmosSurfaceLight),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedFrequency = num }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = num.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else CosmosTextSecondary
                                )
                            }
                        }
                    }
                }

                // Difficulty
                Column {
                    Text("Routine Difficulty", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Easy", "Medium", "Hard", "Mythic").forEach { itDiff ->
                            val isSel = selectedDifficulty == itDiff
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ElectroPurple else CosmosSurface)
                                    .border(
                                        BorderStroke(1.dp, if (isSel) ElectroPurple else CosmosSurfaceLight),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDifficulty = itDiff }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = itDiff,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else CosmosTextSecondary
                                )
                            }
                        }
                    }
                }

                // Energy Level selector
                Column {
                    Text("Energy Level required", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CosmosTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Low Brainpower", "Medium/Steady", "High Focus").forEach { level ->
                            val isSel = selectedEnergy == level
                            val (badgeBg, badgeColor, badgeText) = when (level) {
                                "High Focus" -> Triple(if (isSel) Color(0xFF450A0A) else CosmosSurface, if (isSel) Color(0xFFFCA5A5) else CosmosTextSecondary, "🔴 High Focus")
                                "Low Brainpower" -> Triple(if (isSel) Color(0xFF064E3B) else CosmosSurface, if (isSel) Color(0xFF6EE7B7) else CosmosTextSecondary, "🟢 Low")
                                else -> Triple(if (isSel) Color(0xFF1E3A8A) else CosmosSurface, if (isSel) Color(0xFF93C5FD) else CosmosTextSecondary, "🔵 Steady")
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeBg)
                                    .border(
                                        BorderStroke(1.dp, if (isSel) badgeBg else CosmosSurfaceLight),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedEnergy = level }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = badgeText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            }
                        }
                    }
                }


            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title.trim() + "|||" + selectedEnergy, selectedCategory, selectedFrequency, xpEarned, selectedDifficulty) },
                colors = ButtonDefaults.textButtonColors(contentColor = ElectroPurple)
            ) {
                Text("ADD HABIT", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = CosmosTextSecondary)
            ) {
                Text("CANCEL")
            }
        },
        containerColor = CosmosSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

val spaceSciFiQuotes = listOf(
    "Every gear matters.",
    "Survive by doing the math.",
    "Structures stand because the foundation is true.",
    "Genius is just calculated effort.",
    "Look at the stars, but build on the ground.",
    "Flow like water, manage the current.",
    "Silence the noise, find the signal.",
    "Focus is a mechanical advantage.",
    "Adapt to the environment.",
    "The blueprint is in the details.",
    "Master the basics, control the complex.",
    "A strong mind designs a strong reality.",
    "Logic is your sharpest tool.",
    "Progress is built one block at a time.",
    "Time is the only currency.",
    "Observe, analyze, execute.",
    "The hardest problems require the simplest mechanics.",
    "Design with purpose, execute with precision.",
    "Momentum is built through consistency.",
    "True efficiency looks effortless."
)

fun getDailyQuotes(database: List<String>): List<String> {
    val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val seed = dateString.hashCode().toLong()
    val random = Random(seed)
    
    val available = database.toMutableList()
    val result = mutableListOf<String>()
    repeat(5) {
        if (available.isNotEmpty()) {
            val index = random.nextInt(available.size)
            result.add(available.removeAt(index))
        }
    }
    return result
}

fun getTaskDisplayName(fullTitle: String): String {
    return if (fullTitle.contains("|||")) fullTitle.substringBefore("|||") else fullTitle
}

fun getTaskEnergyLevel(fullTitle: String): String {
    return if (fullTitle.contains("|||")) fullTitle.substringAfter("|||") else "Medium/Steady"
}

@Composable
fun MarqueeQuoteBar(
    quotes: List<String> = spaceSciFiQuotes,
    modifier: Modifier = Modifier
) {
    val dailyQuotes = remember(quotes) {
        getDailyQuotes(quotes)
    }

    var currentQuoteIndex by remember { mutableStateOf(0) }
    val currentQuote = if (dailyQuotes.isNotEmpty()) dailyQuotes[currentQuoteIndex] else ""

    var containerWidth by remember { mutableStateOf(0f) }
    val quoteTranslationX = remember { Animatable(0f) }

    LaunchedEffect(dailyQuotes) {
        if (dailyQuotes.isEmpty()) return@LaunchedEffect

        while (true) {
            val element = if (currentQuoteIndex in dailyQuotes.indices) dailyQuotes[currentQuoteIndex] else ""
            if (element.isEmpty()) {
                delay(1000)
                continue
            }

            // Wait until containerWidth is greater than 0
            if (containerWidth == 0f) {
                snapshotFlow { containerWidth }.first { it > 0f }
            }

            val width = containerWidth
            // 1. Initial position: completely off-screen to the right
            quoteTranslationX.snapTo(width)
            
            // 2. Slide into center (0f)
            quoteTranslationX.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
            
            // 3. Pause stationary in the center for exactly 5 seconds (5000ms)
            delay(5000)
            
            // 4. Slide out to the left (-width)
            quoteTranslationX.animateTo(
                targetValue = -width,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
            
            // 5. Trigger next quote
            currentQuoteIndex = (currentQuoteIndex + 1) % dailyQuotes.size
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(34.dp)
            .background(CosmosSurface)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(ElectroPurple.copy(alpha = 0.3f), Color.Transparent)
                ),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .onGloballyPositioned { coordinates ->
                containerWidth = coordinates.size.width.toFloat()
            }
            .testTag("quote_bar_container"),
        contentAlignment = Alignment.Center
    ) {
        if (currentQuote.isNotEmpty()) {
            Text(
                text = currentQuote,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = ElectroPink,
                    letterSpacing = 1.2.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = quoteTranslationX.value
                    }
                    .wrapContentWidth(unbounded = true)
            )
        }
    }
}

