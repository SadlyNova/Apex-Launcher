/*
 * Apex Launcher 
 * Mobile Optimized Dashboard
 */

package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompositionLocalProvider(
                LocalUriHandler provides object : UriHandler {
                    override fun openUri(uri: String) {
                        onOpenLink(uri)
                    }
                }
            ) {
                // 🔥 Left Content Area (Slightly larger weight for mobile)
                ApexDashboardContent(
                    modifier = Modifier.weight(6f),
                    isVisible = isVisible,
                    onLaunchGame = onLaunchGame
                )
            }

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

            // 🔥 Right Profile Panel
            ApexRightProfilePanel(
                isVisible = isVisible,
                modifier = Modifier.weight(4f).fillMaxHeight(),
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
        contentPadding = PaddingValues(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // 🔥 SCALED DOWN WELCOME BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp) // Reduced from 240dp
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF2E1065), Color(0xFF0F0518))))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterStart),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("WELCOME BACK,", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(account?.username ?: "Guest", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { onLaunchGame(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Play", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            // 🔥 SCALED DOWN STATS ROW
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ApexStatCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.DateRange, title = "Profiles", value = "$versionsCount")
                ApexStatCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.Build, title = "Mods", value = "48")
                ApexStatCard(modifier = Modifier.weight(1f), icon = Icons.Rounded.Settings, title = "Servers", value = "6")
            }
        }
    }
}

@Composable
fun ApexStatCard(modifier: Modifier, icon: ImageVector, title: String, value: String) {
    Card(
        modifier = modifier.height(64.dp), // Reduced height
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151520)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp).background(Color(0xFF2E1065), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF8B5CF6), modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, color = Color(0xFFAAAAAA), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
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
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔥 Scaled Avatar & Info
            Box(
                modifier = Modifier.size(60.dp).background(Brush.radialGradient(listOf(Color(0xFF4C1D95), Color.Transparent))),
                contentAlignment = Alignment.Center
            ) {
                AccountAvatar(account = account, avatarSize = 48.dp, onClick = toAccountManageScreen)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = account?.username ?: "Guest", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                Box(modifier = Modifier.size(6.dp).background(Color(0xFF666666), CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Offline", color = Color(0xFFAAAAAA), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔥 Version Selector Box
            var showList by remember { mutableStateOf(false) }
            var versionManagerRow by remember { mutableStateOf<LayoutCoordinates?>(null) }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E28), RoundedCornerShape(12.dp))
                    .clickable { showList = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .onGloballyPositioned { versionManagerRow = it },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Profile", color = Color(0xFFAAAAAA), fontSize = 10.sp)
                    Text(version?.getVersionName() ?: "No Version", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = toVersionSettingsScreen, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            val menuAnchor = versionManagerRow
            val menuAnchorBounds = menuAnchor?.boundsInParent()
            val menuAnchorX = menuAnchorBounds?.left ?: 0f
            val menuAnchorHeight = menuAnchorBounds?.height ?: 0f

            DropdownMenu(
                expanded = showList && menuAnchor != null,
                onDismissRequest = { showList = false },
                modifier = Modifier.width(180.dp).background(Color(0xFF1E1E28)),
                offset = DpOffset(
                    x = with(LocalDensity.current) { menuAnchorX.toDp() },
                    y = with(LocalDensity.current) { (-menuAnchorHeight).toDp() } - 8.dp
                )
            ) {
                VersionsManager.versions.forEach { version0 ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CommonVersionInfoLayout(modifier = Modifier.weight(1f), version = version0, iconSize = 24.dp)
                            }
                        },
                        onClick = {
                            if (version != version0) VersionsManager.saveVersion(version0)
                            showList = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 🔥 Scaled Quick Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                QuickActionButton(icon = Icons.Rounded.Create, label = "Edit", onClick = toVersionSettingsScreen)
                QuickActionButton(icon = Icons.Rounded.DateRange, label = "Versions", onClick = toVersionManageScreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔥 Corrected Launch Button
            Button(
                onClick = { onLaunchGame(null) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Launch", tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Launch", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier.size(36.dp).background(Color(0xFF1E1E28), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color(0xFFAAAAAA), fontSize = 10.sp)
    }
}
