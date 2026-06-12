/*
 * Apex Launcher - Mobile Optimized
 */

package com.movtery.zalithlauncher.ui.screens.main

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.movtery.zalithlauncher.ui.screens.content.*
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
        eventViewModel.sendKeepScreen(tasks.isNotEmpty())
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
            // 🔥 SCALED DOWN APEX LEFT SIDEBAR
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

            // Content Area
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color(0xFF0A0A0F), shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            ) {
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
                            .fillMaxWidth(0.5f)
                            .align(Alignment.CenterEnd)
                            .padding(12.dp)
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
            .width(180.dp) // 🔥 Reduced from 260.dp
            .fillMaxHeight()
            .padding(12.dp) // 🔥 Reduced padding
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                tint = Color(0xFF8B5CF6),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Apex", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SidebarItem(Icons.Rounded.Home, "Home", currentKey == null || currentKey is NormalNavKey.LauncherMain, toMainScreen)
            SidebarItem(Icons.Rounded.List, "Profiles", currentKey is NormalNavKey.VersionsManager, toProfiles)
            SidebarItem(Icons.Rounded.Person, "Servers", currentKey is NormalNavKey.Multiplayer, toServers)
            SidebarItem(Icons.Rounded.Settings, "Settings", currentKey is NestedNavKey.Settings, toSettings)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // 🔥 Smaller banner
                .background(Brush.verticalGradient(listOf(Color(0xFF1E103C), Color(0xFF0F0518))), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp).align(Alignment.BottomStart)) {
                Text(text = "APEX", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                Text(text = "UNLEASH", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF8B5CF6))
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
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, color = contentColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(visible = !inLauncherScreen) {
            IconButton(
                onClick = { backDispatcher?.onBackPressed() ?: onScreenBack() },
                modifier = Modifier.background(Color(0xFF1E1E28), shape = RoundedCornerShape(8.dp)).size(36.dp)
            ) {
                Icon(painter = painterResource(R.drawable.ic_arrow_back), contentDescription = "Back", tint = Color.White)
            }
        }
        if (inLauncherScreen) Spacer(Modifier.width(1.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnimatedVisibility(visible = !(isTasksExpanded || taskRunning)) {
                IconButton(
                    onClick = changeExpandedState,
                    modifier = Modifier.background(Color(0xFF1E1E28), shape = RoundedCornerShape(8.dp)).size(36.dp)
                ) {
                    Icon(painter = painterResource(R.drawable.ic_assignment_filled), contentDescription = "Tasks", tint = Color(0xFF8B5CF6))
                }
            }
            IconButton(
                onClick = toDownloadScreen,
                modifier = Modifier.background(Color(0xFF1E1E28), shape = RoundedCornerShape(8.dp)).size(36.dp)
            ) {
                Icon(painter = painterResource(R.drawable.ic_download_2_filled), contentDescription = "Downloads", tint = Color.White)
            }
        }
    }
}

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
            screenBackStackModel.mainScreen.navigateTo(NestedNavKey.VersionSettings(version), useClassEquality = true)
        }
        val navigateToExport: (Version) -> Unit = { version ->
            screenBackStackModel.mainScreen.removeAndNavigateTo(NestedNavKey.VersionSettings::class, NestedNavKey.VersionExport(version), useClassEquality = true)
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
                entry<NestedNavKey.Settings> { key -> SettingsScreen(key = key, backStackViewModel = screenBackStackModel, openLicenseScreen = { raw -> backStack.navigateTo(NormalNavKey.License(raw)) }, eventViewModel = eventViewModel, submitError = submitError) }
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
