/*
 * Apex Launcher (Custom UI Framework)
 */

package com.movtery.zalithlauncher.ui.screens.main

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.movtery.zalithlauncher.BuildKeys
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.coroutine.Task
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.base.applyFullscreen
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.CardTitleLayout
import com.movtery.zalithlauncher.ui.screens.BackStackNavKey
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.AccountManageScreen
import com.movtery.zalithlauncher.ui.screens.content.DownloadScreen
import com.movtery.zalithlauncher.ui.screens.content.FileSelectorScreen
import com.movtery.zalithlauncher.ui.screens.content.HomePageEditorScreen
import com.movtery.zalithlauncher.ui.screens.content.LauncherScreen
import com.movtery.zalithlauncher.ui.screens.content.LicenseScreen
import com.movtery.zalithlauncher.ui.screens.content.LogViewScreen
import com.movtery.zalithlauncher.ui.screens.content.MultiplayerScreen
import com.movtery.zalithlauncher.ui.screens.content.SettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionExportScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionSettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionsManageScreen
import com.movtery.zalithlauncher.ui.screens.content.WebViewScreen
import com.movtery.zalithlauncher.ui.screens.content.navigateToDownload
import com.movtery.zalithlauncher.ui.screens.navigateTo
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.ui.theme.backgroundColor
import com.movtery.zalithlauncher.ui.theme.cardColor
import com.movtery.zalithlauncher.ui.theme.onBackgroundColor
import com.movtery.zalithlauncher.ui.theme.onCardColor
import com.movtery.zalithlauncher.utils.animation.getAnimateTween
import com.movtery.zalithlauncher.utils.file.formatFileSize
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.LocalBackgroundViewModel
import com.movtery.zalithlauncher.viewmodel.ModpackImportViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import com.movtery.zalithlauncher.viewmodel.sendKeepScreen

@Composable
fun MainScreen(
    screenBackStackModel: ScreenBackStackViewModel,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val tasks by TaskSystem.tasksFlow.collectAsStateWithLifecycle()

    LaunchedEffect(tasks) {
        if (tasks.isEmpty()) {
            eventViewModel.sendKeepScreen(false)
        } else {
            eventViewModel.sendKeepScreen(true)
        }
    }

    val isTaskMenuExpanded = AllSettings.launcherTaskMenuExpanded.state

    fun changeTasksExpandedState() {
        AllSettings.launcherTaskMenuExpanded.save(!isTaskMenuExpanded)
    }

    val toMainScreen: () -> Unit = {
        screenBackStackModel.mainScreen.clearWith(NormalNavKey.LauncherMain)
    }

    val mainScreenKey = screenBackStackModel.mainScreen.currentKey
    val inLauncherScreen = mainScreenKey == null || mainScreenKey is NormalNavKey.LauncherMain

    // 🔥 Apex Deep Space Background
    val apexBgColor = Color(0xFF050508) 

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = apexBgColor,
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .applyFullscreen(AllSettings.launcherFullScreen.state)
                .fillMaxSize()
        ) {
            // 🔥 APEX LEFT SIDEBAR
            ApexSidebar(
                currentKey = mainScreenKey,
                toMainScreen = toMainScreen,
                toProfiles = {
                    screenBackStackModel.mainScreen.removeAndNavigateTo(
                        removes = screenBackStackModel.clearBeforeNavKeys,
                        screenKey = NormalNavKey.VersionsManager
                    )
                },
                toServers = {
                    screenBackStackModel.mainScreen.removeAndNavigateTo(
                        removes = screenBackStackModel.clearBeforeNavKeys,
                        screenKey = NormalNavKey.Multiplayer
                    )
                },
                toSettings = {
                    screenBackStackModel.mainScreen.removeAndNavigateTo(
                        removes = screenBackStackModel.clearBeforeNavKeys,
                        screenKey = screenBackStackModel.settingsScreen
                    )
                }
            )

            // Content Area (Right Side)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color(0xFF0A0A0F), shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp))
                    .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp))
            ) {
                // Top Right Utility Bar
                ApexTopRightBar(
                    inLauncherScreen = inLauncherScreen,
                    taskRunning = tasks.isEmpty(),
                    isTasksExpanded = isTaskMenuExpanded,
                    onScreenBack = { screenBackStackModel.mainScreen.backStack.removeFirstOrNull() },
                    toDownloadScreen = { screenBackStackModel.navigateToDownload() },
                    changeExpandedState = { changeTasksExpandedState() }
                )

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    NavigationUI(
                        modifier = Modifier.fillMaxSize(),
                        screenBackStackModel = screenBackStackModel,
                        toMainScreen = toMainScreen,
                        eventViewModel = eventViewModel,
                        modpackImportViewModel = modpackImportViewModel,
                        submitError = submitError
                    )

                    TaskMenu(
                        tasks = tasks,
                        isExpanded = isTaskMenuExpanded,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.4f)
                            .align(Alignment.CenterEnd)
                            .padding(all = 16.dp)
                    ) {
                        changeTasksExpandedState()
                    }
                }
            }
        }
    }
}

@Composable
fun ApexSidebar(
    currentKey: TitledNavKey?,
    toMainScreen: () -> Unit,
    toProfiles: () -> Unit,
    toServers: () -> Unit,
    toSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        // Logo
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 32.dp, start = 8.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground), // Fallback, we'll customize later
                contentDescription = "Logo",
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "ApexLauncher", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
        }

        // Menu Items
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SidebarItem(
                icon = Icons.Rounded.Home,
                label = "Home",
                isSelected = currentKey == null || currentKey is NormalNavKey.LauncherMain,
                onClick = toMainScreen
            )
            SidebarItem(
                icon = Icons.Rounded.List,
                label = "Profiles",
                isSelected = currentKey is NormalNavKey.VersionsManager,
                onClick = toProfiles
            )
            SidebarItem(
                icon = Icons.Rounded.Build,
                label = "Mods",
                isSelected = false,
                onClick = {} // Placeholder for future
            )
            SidebarItem(
                icon = Icons.Rounded.ShoppingCart,
                label = "Resource Packs",
                isSelected = false,
                onClick = {} // Placeholder
            )
            SidebarItem(
                icon = Icons.Rounded.Place,
                label = "Worlds",
                isSelected = false,
                onClick = {} // Placeholder
            )
            SidebarItem(
                icon = Icons.Rounded.Person,
                label = "Servers",
                isSelected = currentKey is NormalNavKey.Multiplayer,
                onClick = toServers
            )
            SidebarItem(
                icon = Icons.Rounded.Settings,
                label = "Settings",
                isSelected = currentKey is NestedNavKey.Settings,
                onClick = toSettings
            )
        }

        // Apex Unleashed Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(listOf(Color(0xFF1E103C), Color(0xFF0F0518))),
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp).align(Alignment.BottomStart)) {
                Text(text = "APEX", fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color.White)
                Text(text = "UNLEASH", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF8B5CF6))
                Text(text = "THE ULTIMATE", fontSize = 10.sp, color = Color(0xFFAAAAAA))
                Text(text = "EXPERIENCE", fontSize = 10.sp, color = Color(0xFFAAAAAA))
            }
        }
    }
}

@Composable
fun SidebarItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) Color(0xFF8B5CF6) else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color(0xFFAAAAAA)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ApexTopRightBar(
    inLauncherScreen: Boolean,
    taskRunning: Boolean,
    isTasksExpanded: Boolean,
    onScreenBack: () -> Unit,
    toDownloadScreen: () -> Unit,
    changeExpandedState: () -> Unit,
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button (If not in Home)
        AnimatedVisibility(visible = !inLauncherScreen) {
            IconButton(
                onClick = { backDispatcher?.onBackPressed() ?: onScreenBack() },
                modifier = Modifier.background(Color(0xFF1E1E28), shape = RoundedCornerShape(12.dp))
            ) {
                Icon(painter = painterResource(R.drawable.ic_arrow_back), contentDescription = "Back", tint = Color.White)
            }
        }
        if (inLauncherScreen) Spacer(Modifier.width(1.dp))

        // Right side utilities (Downloads, Tasks)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AnimatedVisibility(visible = !(isTasksExpanded || taskRunning)) {
                IconButton(
                    onClick = changeExpandedState,
                    modifier = Modifier.background(Color(0xFF1E1E28), shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(painter = painterResource(R.drawable.ic_assignment_filled), contentDescription = "Tasks", tint = Color(0xFF8B5CF6))
                }
            }
            IconButton(
                onClick = toDownloadScreen,
                modifier = Modifier.background(Color(0xFF1E1E28), shape = RoundedCornerShape(12.dp))
            ) {
                Icon(painter = painterResource(R.drawable.ic_download_2_filled), contentDescription = "Downloads", tint = Color.White)
            }
        }
    }
}

// ==========================================
// CORE NAVIGATION AND TASK UI (UNCHANGED)
// ==========================================

@Composable
private fun NavigationUI(
    modifier: Modifier = Modifier,
    screenBackStackModel: ScreenBackStackViewModel,
    toMainScreen: () -> Unit,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val backStack = screenBackStackModel.mainScreen.backStack
    val currentKey = backStack.lastOrNull()

    LaunchedEffect(currentKey) {
        screenBackStackModel.mainScreen.currentKey = currentKey
    }

    if (backStack.isNotEmpty()) {
        val navigateToVersions: (Version) -> Unit = { version ->
            screenBackStackModel.mainScreen.navigateTo(
                screenKey = NestedNavKey.VersionSettings(version),
                useClassEquality = true
            )
        }
        val navigateToExport: (Version) -> Unit = { version ->
            screenBackStackModel.mainScreen.removeAndNavigateTo(
                remove = NestedNavKey.VersionSettings::class,
                screenKey = NestedNavKey.VersionExport(version),
                useClassEquality = true
            )
        }

        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = { onBack(backStack) },
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(),
            entryProvider = entryProvider {
                entry<NormalNavKey.LauncherMain> {
                    LauncherScreen(
                        backStackViewModel = screenBackStackModel,
                        navigateToVersions = navigateToVersions,
                        onLaunchGame = { version -> eventViewModel.sendEvent(EventViewModel.Event.Launch.Game(version)) },
                        onOpenLink = { eventViewModel.sendEvent(EventViewModel.Event.OpenLink(it)) },
                        onHomePageEvent = { event -> eventViewModel.sendEvent(EventViewModel.Event.HomePage.Event(event)) }
                    )
                }
                entry<NestedNavKey.Settings> { key ->
                    SettingsScreen(key = key, backStackViewModel = screenBackStackModel, openLicenseScreen = { raw -> backStack.navigateTo(NormalNavKey.License(raw)) }, eventViewModel = eventViewModel, submitError = submitError)
                }
                entry<NormalNavKey.License> { key -> LicenseScreen(key = key, backStackViewModel = screenBackStackModel) }
                entry<NormalNavKey.AccountManager> { key -> AccountManageScreen(key = key, backStackViewModel = screenBackStackModel, backToMainScreen = toMainScreen, openLink = { url -> eventViewModel.sendEvent(EventViewModel.Event.OpenLink(url)) }, submitError = submitError) }
                entry<NormalNavKey.WebScreen> { key -> WebViewScreen(key = key, backStackViewModel = screenBackStackModel, eventViewModel = eventViewModel) }
                entry<NormalNavKey.VersionsManager> { VersionsManageScreen(backScreenViewModel = screenBackStackModel, navigateToVersions = navigateToVersions, navigateToExport = navigateToExport, eventViewModel = eventViewModel, submitError = submitError) }
                entry<NormalNavKey.FileSelector> { key -> FileSelectorScreen(key = key, backScreenViewModel = screenBackStackModel) { backStack.removeLastOrNull() } }
                entry<NestedNavKey.VersionSettings> { key -> VersionSettingsScreen(key = key, backScreenViewModel = screenBackStackModel, backToMainScreen = toMainScreen, onExportModpack = { navigateToExport(key.version) }, eventViewModel = eventViewModel, submitError = submitError) }
                entry<NestedNavKey.VersionExport> { key -> VersionExportScreen(key = key, backScreenViewModel = screenBackStackModel, eventViewModel = eventViewModel, backToMainScreen = toMainScreen) }
                entry<NestedNavKey.Download> { key -> DownloadScreen(key = key, backScreenViewModel = screenBackStackModel, eventViewModel = eventViewModel, modpackImportViewModel = modpackImportViewModel, submitError = submitError) }
                entry<NormalNavKey.Multiplayer> { MultiplayerScreen(backScreenViewModel = screenBackStackModel, eventViewModel = eventViewModel) }
                entry<NormalNavKey.HomePageEditor> { HomePageEditorScreen(backStackViewModel = screenBackStackModel) }
                entry<NormalNavKey.LogView> { key -> LogViewScreen(key = key, backStackViewModel = screenBackStackModel) }
            }
        )
    } else {
        Box(modifier)
    }
}

@Composable
private fun TaskMenu(tasks: List<Task>, isExpanded: Boolean, modifier: Modifier = Modifier, changeExpandedState: () -> Unit = {}) {
    val show = isExpanded && tasks.isNotEmpty()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    AnimatedVisibility(
        modifier = modifier,
        enter = slideInHorizontally(initialOffsetX = { if (isRtl) it else -it }, animationSpec = getAnimateTween()) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { if (isRtl) it else -it }, animationSpec = getAnimateTween()) + fadeOut(),
        visible = show
    ) {
        BackgroundCard(
            modifier = Modifier.fillMaxSize().padding(all = 6.dp),
            influencedByBackground = false,
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = backgroundColor(), contentColor = onBackgroundColor()),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            Column {
                CardTitleLayout(blur = 0) {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 8.dp, bottom = 4.dp)) {
                        IconButton(modifier = Modifier.size(28.dp).align(Alignment.CenterStart), onClick = changeExpandedState) {
                            Icon(modifier = Modifier.size(28.dp), painter = painterResource(R.drawable.ic_arrow_left_rounded), contentDescription = stringResource(R.string.generic_collapse))
                        }
                        Text(modifier = Modifier.align(Alignment.Center), text = stringResource(R.string.main_task_menu))
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxHeight().weight(1f), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                    items(tasks) { task ->
                        TaskItem(taskProgress = task.currentProgress, taskMessageRes = task.currentMessageRes, taskMessageArgs = task.currentMessageArgs, taskRateBytesPerSec = task.currentRateBytesPerSec, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) { TaskSystem.cancelTask(task.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(taskProgress: Float, taskMessageRes: Int?, taskMessageArgs: Array<out Any>?, taskRateBytesPerSec: Long, modifier: Modifier = Modifier, shape: Shape = MaterialTheme.shapes.large, color: Color = cardColor(false), contentColor: Color = onCardColor(), onCancelClick: () -> Unit = {}) {
    Surface(modifier = modifier, shape = shape, color = color, contentColor = contentColor) {
        Row(modifier = Modifier.padding(all = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(modifier = Modifier.size(24.dp).align(Alignment.CenterVertically), onClick = onCancelClick) {
                Icon(modifier = Modifier.size(20.dp), painter = painterResource(R.drawable.ic_close), contentDescription = stringResource(R.string.generic_cancel))
            }
            Column(modifier = Modifier.weight(1f).align(Alignment.CenterVertically)) {
                taskMessageRes?.let { messageRes -> Text(text = if (taskMessageArgs != null) { stringResource(messageRes, *taskMessageArgs) } else { stringResource(messageRes) }, style = MaterialTheme.typography.labelMedium) }
                if (taskProgress < 0) { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) } else { LinearProgressIndicator(progress = { taskProgress }, modifier = Modifier.fillMaxWidth()) }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    taskProgress.takeIf { it >= 0f }?.let { progress -> Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium) }
                    taskRateBytesPerSec.takeIf { it >= 0L }?.let { bytes -> val text = remember(bytes) { "${formatFileSize(bytes)}/s" }; Text(text = text, style = MaterialTheme.typography.labelMedium) }
                }
            }
        }
    }
}
