/*
 * Apex Launcher 
 * Epic Dashboard UI (LauncherScreen.kt)
 */

package com.movtery.zalithlauncher.ui.screens.content

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.AccountAvatar
import com.movtery.zalithlauncher.ui.screens.content.elements.CommonVersionInfoLayout
import com.movtery.zalithlauncher.ui.screens.main.custom_home.MarkdownBlock
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel

@Composable
fun LauncherScreen(
    backStackViewModel: ScreenBackStackViewModel,
    navigateToVersions: (Version) -> Unit,
    onLaunchGame: (Version?) -> Unit,
    onOpenLink: (String) -> Unit,
    onHomePageEvent: (MarkdownBlock.Button.Event) -> Unit,
) {
    BaseScreen(
        screenKey = NormalNavKey.LauncherMain,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) { isVisible ->
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CompositionLocalProvider(
                LocalUriHandler provides object : UriHandler {
                    override fun openUri(uri: String) {
                        onOpenLink(uri)
                    }
                }
            ) {
                // 🔥 LEFT CONTENT DASHBOARD
                ApexDashboardContent(
                    modifier = Modifier.weight(7f),
                    isVisible = isVisible,
                    onLaunchGame = onLaunchGame
                )
            }

            // Navigation Helpers
            val toAccountManageScreen: () -> Unit = {
                backStackViewModel.mainScreen.navigateTo(NormalNavKey.AccountManager(FirstLoginMenu.NONE))
            }
            val toVersionManageScreen: () -> Unit = {
                backStackViewModel.mainScreen.removeAndNavigateTo(
                    remove = NestedNavKey.VersionSettings::class,
                    screenKey = NormalNavKey.VersionsManager
                )
            }
            val toVersionSettingsScreen: () -> Unit = {
                VersionsManager.currentVersion.value?.let { navigateToVersions(it) }
            }

            // 🔥 RIGHT PROFILE PANEL
            ApexRightProfilePanel(
                isVisible = isVisible,
                modifier = Modifier.weight(3.5f).fillMaxHeight(),
                onLaunchGame = onLaunchGame,
                toAccountManageScreen = toAccountManageScreen,
                toVersionManageScreen = toVersionManageScreen,
                toVersionSettingsScreen = toVersionSettingsScreen
            )
        }
    }
}

@Composable
private fun ApexDashboardContent(
    isVisible: Boolean,
    onLaunchGame: (Version?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val yOffset by swapAnimateDpAsState(targetValue = (-40).dp, swapIn = isVisible)
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val versionsCount = VersionsManager.versions.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(x = 0, y = yOffset.roundToPx()) },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // 🔥 EPIC WELCOME BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF2E1065), Color(0xFF0F0518))))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp).align(Alignment.CenterStart),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("WELCOME BACK,", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(account?.username ?: "Guest", color = Color.White, fontWeight = FontWeight.Black, fontSize = 42.sp)
                    Text("Ready to continue your adventure?", color = Color(0xFFAAAAAA), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { onLaunchGame(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Play", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Play Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // 🔥 STATS ROW
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ApexStatCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.DateRange, title = "Profiles", value = "$versionsCount", subtitle = "Configured")
                ApexStatCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.Build, title = "Mods", value = "48", subtitle = "Installed")
                ApexStatCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.Settings, title = "Servers", value = "6", subtitle = "Saved")
            }
        }
    }
}

@Composable
fun ApexStatCard(modifier: Modifier, icon: ImageVector, title: String, value: String, subtitle: String) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151520)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFF2E1065), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF8B5CF6))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color(0xFFAAAAAA), fontSize = 12.sp)
                Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text(subtitle, color = Color(0xFF666666), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun ApexRightProfilePanel(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    toVersionSettingsScreen: () -> Unit
) {
    val xOffset by swapAnimateDpAsState(targetValue = 40.dp, swapIn = isVisible, isHorizontal = true)
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()

    Card(
        modifier = modifier.offset { IntOffset(x = xOffset.roundToPx(), y = 0) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101018)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔥 Avatar & Account Info
            Box(
                modifier = Modifier.size(100.dp).background(Brush.radialGradient(listOf(Color(0xFF4C1D95), Color.Transparent))),
                contentAlignment = Alignment.Center
            ) {
                AccountAvatar(account = account, avatarSize = 80.dp, onClick = toAccountManageScreen)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = account?.username ?: "Guest", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF666666), CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Offline", color = Color(0xFFAAAAAA), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🔥 Version Selector Box
            var showList by remember { mutableStateOf(false) }
            var versionManagerRow by remember { mutableStateOf<LayoutCoordinates?>(null) }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E28), RoundedCornerShape(16.dp))
                    .clickable { showList = true }
                    .padding(16.dp)
                    .onGloballyPositioned { versionManagerRow = it },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Selected Profile", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                    Text(version?.getVersionName() ?: "No Version", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = toVersionSettingsScreen) {
                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }

            // Version Dropdown Logic
            val menuAnchor = versionManagerRow
            val menuAnchorBounds = menuAnchor?.boundsInParent()
            val menuAnchorX = menuAnchorBounds?.left ?: 0f
            val menuAnchorHeight = menuAnchorBounds?.height ?: 0f

            DropdownMenu(
                expanded = showList && menuAnchor != null,
                onDismissRequest = { showList = false },
                modifier = Modifier.width(260.dp).background(Color(0xFF1E1E28)),
                offset = DpOffset(
                    x = with(LocalDensity.current) { menuAnchorX.toDp() },
                    y = with(LocalDensity.current) { (-menuAnchorHeight).toDp() } - 8.dp
                )
            ) {
                VersionsManager.versions.forEach { version0 ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CommonVersionInfoLayout(modifier = Modifier.weight(1f), version = version0, iconSize = 28.dp)
                            }
                        },
                        onClick = {
                            if (version != version0) VersionsManager.saveVersion(version0)
                            showList = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔥 MASSIVE LAUNCH BUTTON
            Button(
                onClick = { onLaunchGame(null) },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Launch", tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Launch", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            // 🔥 Quick Actions Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                QuickActionButton(icon = Icons.Rounded.Create, label = "Edit", onClick = toVersionSettingsScreen)
                QuickActionButton(icon = Icons.Rounded.DateRange, label = "Versions", onClick = toVersionManageScreen)
                QuickActionButton(icon = Icons.Rounded.Build, label = "Backup", onClick = {})
                QuickActionButton(icon = Icons.Rounded.MoreVert, label = "More", onClick = {})
            }
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFF1E1E28), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = Color(0xFFAAAAAA), fontSize = 12.sp)
    }
}
