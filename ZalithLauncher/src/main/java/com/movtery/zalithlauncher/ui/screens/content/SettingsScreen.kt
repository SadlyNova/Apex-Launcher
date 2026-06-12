/*
 * Apex Launcher 2 - Settings (About section removed)
 */

package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.fadeEdge
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.CategoryIcon
import com.movtery.zalithlauncher.ui.screens.content.elements.CategoryItem
import com.movtery.zalithlauncher.ui.screens.content.settings.*
import com.movtery.zalithlauncher.ui.screens.navigateOnce
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel

@Composable
fun SettingsScreen(
    key: NestedNavKey.Settings,
    backStackViewModel: ScreenBackStackViewModel,
    openLicenseScreen: (raw: Int) -> Unit,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    BaseScreen(screenKey = key, currentKey = backStackViewModel.mainScreen.currentKey) { isVisible ->
        Row(modifier = Modifier.fillMaxSize()) {
            TabMenu(
                modifier = Modifier.fillMaxHeight(),
                isVisible = isVisible,
                settingsScreenKey = backStackViewModel.settingsScreen.currentKey,
                navigateTo = { settingKey -> key.backStack.navigateOnce(settingKey) }
            )
            NavigationUI(
                key = key,
                mainScreenKey = backStackViewModel.mainScreen.currentKey,
                settingsScreenKey = backStackViewModel.settingsScreen.currentKey,
                onCurrentKeyChange = { backStackViewModel.settingsScreen.currentKey = it },
                eventViewModel = eventViewModel,
                submitError = submitError,
                modifier = Modifier.fillMaxHeight()
            )
        }
    }
}

private val settingItems = listOf(
    CategoryItem(NormalNavKey.Settings.Renderer, { CategoryIcon(R.drawable.ic_video_settings, R.string.settings_tab_renderer) }, R.string.settings_tab_renderer),
    CategoryItem(NormalNavKey.Settings.Game, { CategoryIcon(R.drawable.ic_rocket_launch_filled, R.string.settings_tab_game) }, R.string.settings_tab_game),
    CategoryItem(NormalNavKey.Settings.Control, { CategoryIcon(R.drawable.ic_videogame_asset_outlined, R.string.settings_tab_control) }, R.string.settings_tab_control),
    CategoryItem(NormalNavKey.Settings.Gamepad, { CategoryIcon(R.drawable.ic_sports_esports_outlined, R.string.settings_tab_gamepad) }, R.string.settings_tab_gamepad),
    CategoryItem(NormalNavKey.Settings.Launcher, { CategoryIcon(R.drawable.ic_setting_launcher, R.string.settings_tab_launcher) }, R.string.settings_tab_launcher),
    CategoryItem(NormalNavKey.Settings.JavaManager, { CategoryIcon(R.drawable.ic_java, R.string.settings_tab_java_manage) }, R.string.settings_tab_java_manage, division = true),
    CategoryItem(NormalNavKey.Settings.ControlManager, { CategoryIcon(R.drawable.ic_videogame_asset_outlined, R.string.settings_tab_control_manage) }, R.string.settings_tab_control_manage)
    // AboutInfo removed[span_1](start_span)[span_1](end_span)
)

@Composable
private fun TabMenu(modifier: Modifier = Modifier, isVisible: Boolean, settingsScreenKey: TitledNavKey?, navigateTo: (TitledNavKey) -> Unit) {
    val xOffset by swapAnimateDpAsState(targetValue = (-40).dp, swapIn = isVisible, isHorizontal = true)
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.fadeEdge(scrollState).width(IntrinsicSize.Min).padding(start = 8.dp).offset { IntOffset(x = xOffset.roundToPx(), y = 0) }.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        settingItems.forEach { item ->
            if (item.division) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(0.4f).alpha(0.4f), color = MaterialTheme.colorScheme.onSurface)
            }
            NavigationRailItem(
                selected = settingsScreenKey == item.key,
                onClick = { navigateTo(item.key) },
                icon = { item.icon() },
                label = { Text(modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE), text = stringResource(item.textRes), maxLines = 1, style = MaterialTheme.typography.labelMedium) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun NavigationUI(key: NestedNavKey.Settings, mainScreenKey: TitledNavKey?, settingsScreenKey: TitledNavKey?, onCurrentKeyChange: (TitledNavKey?) -> Unit, eventViewModel: EventViewModel, submitError: (ErrorViewModel.ThrowableMessage) -> Unit, modifier: Modifier = Modifier) {
    val backStack = key.backStack
    LaunchedEffect(backStack.lastOrNull()) { onCurrentKeyChange(backStack.lastOrNull()) }

    if (backStack.isNotEmpty()) {
        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = { onBack(backStack) },
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(),
            entryProvider = entryProvider {
                entry<NormalNavKey.Settings.Renderer> { RendererSettingsScreen(key, settingsScreenKey, mainScreenKey, eventViewModel) }
                entry<NormalNavKey.Settings.Game> { GameSettingsScreen(key, settingsScreenKey, mainScreenKey, eventViewModel) }
                entry<NormalNavKey.Settings.Control> { ControlSettingsScreen(key, settingsScreenKey, mainScreenKey, eventViewModel, submitError) }
                entry<NormalNavKey.Settings.Gamepad> { GamepadSettingsScreen(key, settingsScreenKey, mainScreenKey) }
                entry<NormalNavKey.Settings.Launcher> { LauncherSettingsScreen(key, settingsScreenKey, mainScreenKey, eventViewModel, { }, submitError) }
                entry<NormalNavKey.Settings.JavaManager> { JavaManageScreen(key, settingsScreenKey, mainScreenKey, submitError) }
                entry<NormalNavKey.Settings.ControlManager> { ControlManageScreen(key, settingsScreenKey, mainScreenKey, eventViewModel, submitError) }
                // AboutInfoScreen entry removed[span_2](start_span)[span_2](end_span)
            }
        )
    } else {
        Box(modifier)
    }
}
