/*
 * Apex Launcher 
 * Ultra-Premium High-Quality Dashboard (Mobile Landscape)
 */

package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CompositionLocalProvider(
                LocalUriHandler provides object : UriHandler {
                    override fun openUri(uri: String) {
                        onOpenLink(uri)
                    }
                }
            ) {
                ApexDashboardContent(
                    modifier = Modifier.weight(6f),
                    isVisible = isVisible,
                    onLaunchGame = onLaunchGame
                )
            }

            val toAccountManageScreen: () -> Unit = { backStackViewModel.mainScreen.navigateTo(NormalNavKey.AccountManager(FirstLoginMenu.NONE)) }
            val toVersionManageScreen: () -> Unit = { backStackViewModel.mainScreen.removeAndNavigateTo(remove = NestedNavKey.VersionSettings::class, screenKey = NormalNavKey.VersionsManager) }
            val toVersionSettingsScreen: () -> Unit = { VersionsManager.currentVersion.value?.let { navigateToVersions(it) } }

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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // 🔥 PREMIUM BANNER WITH IMAGE OVERLAY & GLOW
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF2E1065), Color(0xFF0F0518))))
            ) {
                // Background Pattern/Image placeholder (You can replace R.drawable.ic_launcher_background with an actual cool gaming image)
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background), 
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().alpha(0.2f)
                )
                
                // Dark Gradient to make text pop
                Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xCC050508), Color.Transparent))))

                Column(
                    modifier = Modifier.padding(20.dp).align(Alignment.CenterStart),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("WELCOME BACK,", color = Color(0xFFA78BFA), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                    Text(account?.username ?: "Guest", color = Color.White, fontWeight = FontWeight.Black, fontSize = 32.sp)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { onLaunchGame(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .background(Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))))
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Play Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            // 🔥 PREMIUM GLASSMORPHISM STATS CARDS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
        modifier = modifier.height(72.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151520).copy(alpha = 0.8f)), // Slight transparency
        border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.3f)), // Neon Outline
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(Brush.linearGradient(listOf(Color(0xFF3B1D75), Color(0xFF1E103C))), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = Color(0xFFD8B4FE), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color(0xFFAAAAAA), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 1)
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101018).copy(alpha = 0.9f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)), // Subtle glass border
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔥 Avatar with Radial Glow
            Box(
                modifier = Modifier.size(80.dp).background(Brush.radialGradient(listOf(Color(0xFF6D28D9).copy(alpha = 0.5f), Color.Transparent))),
                contentAlignment = Alignment.Center
            ) {
                AccountAvatar(account = account, avatarSize = 64.dp, onClick = toAccountManageScreen)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = account?.username ?: "Guest", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape)) // Changed to Green for 'Online/Ready' vibe
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ready", color = Color(0xFFAAAAAA), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔥 Version Selector Box (Premium Outline)
            var showList by remember { mutableStateOf(false) }
            var versionManagerRow by remember { mutableStateOf<LayoutCoordinates?>(null) }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E28), RoundedCornerShape(14.dp))
                    .clickable { showList = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .onGloballyPositioned { versionManagerRow = it },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Selected Profile", color = Color(0xFF8B5CF6), fontSize = 10.sp, fontWeight = FontWeight.Bold) // Neon subtitle
                    Text(version?.getVersionName() ?: "No Version", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = toVersionSettingsScreen, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            val menuAnchor = versionManagerRow
            val menuAnchorBounds = menuAnchor?.boundsInParent()
            val menuAnchorX = menuAnchorBounds?.left ?: 0f
            val menuAnchorHeight = menuAnchorBounds?.height ?: 0f

            DropdownMenu(
                expanded = showList && menuAnchor != null,
                onDismissRequest = { showList = false },
                modifier = Modifier.width(200.dp).background(Color(0xFF1E1E28)),
                offset = DpOffset(x = with(LocalDensity.current) { menuAnchorX.toDp() }, y = with(LocalDensity.current) { (-menuAnchorHeight).toDp() } - 8.dp)
            ) {
                VersionsManager.versions.forEach { version0 ->
                    DropdownMenuItem(
                        text = { Row(verticalAlignment = Alignment.CenterVertically) { CommonVersionInfoLayout(modifier = Modifier.weight(1f), version = version0, iconSize = 24.dp) } },
                        onClick = { if (version != version0) VersionsManager.saveVersion(version0); showList = false }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 🔥 Quick Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                QuickActionButton(icon = Icons.Rounded.Create, label = "Edit", onClick = toVersionSettingsScreen)
                QuickActionButton(icon = Icons.Rounded.DateRange, label = "Versions", onClick = toVersionManageScreen)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔥 MASSIVE GRADIENT LAUNCH BUTTON
            Button(
                onClick = { onLaunchGame(null) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Launch", tint = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Launch Game", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier.size(42.dp).background(Color(0xFF1E1E28), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = Color(0xFFAAAAAA), fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
