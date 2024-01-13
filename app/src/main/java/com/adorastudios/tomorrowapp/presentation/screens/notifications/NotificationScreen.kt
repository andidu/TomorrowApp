@file:OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class,
)

package com.adorastudios.tomorrowapp.presentation.screens.notifications

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adorastudios.tomorrowapp.R
import com.adorastudios.tomorrowapp.presentation.components.Dialog
import com.adorastudios.tomorrowapp.presentation.screens.notifications.components.ExactNotificationColumn
import com.adorastudios.tomorrowapp.presentation.screens.notifications.components.NotificationOption
import com.adorastudios.tomorrowapp.presentation.screens.notifications.components.PeriodicNotificationColumn
import com.adorastudios.tomorrowapp.ui.theme.AppTheme
import com.adorastudios.tomorrowapp.ui.theme.topShape
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun NotificationScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val state = viewModel.state.value
    val notificationPermissionState =
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(notificationPermissionState.status.isGranted) {
        viewModel.onEvent(NotificationEvent.NotificationPermission(notificationPermissionState.status.isGranted))
    }

    var openTimeDialog: Int? by remember { mutableStateOf(null) }
    val timePickerState = rememberTimePickerState(14, 0)
    val context = LocalContext.current

    val notificationTypeSelected by remember(
        state.notificationTypeInSettings,
        state.notificationsPermitted,
    ) {
        derivedStateOf {
            if (state.notificationsPermitted) {
                state.notificationTypeInSettings
            } else {
                NotificationType.Disabled
            }
        }
    }
    val selectedOption = updateTransition(
        targetState = notificationTypeSelected,
        label = "notificationType",
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = topShape,
                    )
                    .clip(topShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .statusBarsPadding()
                    .height(AppTheme.paddings.appBarHeight),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.notificationsScreen_title),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .border(
                        width = 2.dp,
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    )
                    .padding(8.dp),
            ) {
                val color by selectedOption.animateColor(label = "") { notificationType ->
                    when (notificationType) {
                        NotificationType.Disabled -> MaterialTheme.colorScheme.errorContainer
                        is NotificationType.Enabled -> MaterialTheme.colorScheme.primaryContainer
                    }
                }
                val density = LocalDensity.current
                val height = remember(density) {
                    with(density) { 68.dp.toPx() }
                }
                val translationY by selectedOption.animateFloat(label = "") { notificationType ->
                    when (notificationType) {
                        NotificationType.Disabled -> 0f
                        NotificationType.Enabled.Exact -> height
                        NotificationType.Enabled.Periodic -> height * 2
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            this.translationY = translationY
                        }
                        .height(60.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(color),
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    NotificationOption(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                viewModel.onEvent(
                                    NotificationEvent.NotificationTypeChanged(NotificationType.Disabled),
                                )
                            }
                            .padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.notificationsScreen_none),
                        icon = Icons.Rounded.NotificationsOff,
                    )
                    NotificationOption(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                if (!state.notificationsPermitted) {
                                    notificationPermissionState.launchPermissionRequest()
                                }
                                viewModel.onEvent(
                                    NotificationEvent.NotificationTypeChanged(NotificationType.Enabled.Exact),
                                )
                            }
                            .padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.notificationsScreen_exact),
                        icon = Icons.Rounded.Notifications,
                    )
                    NotificationOption(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                if (!state.notificationsPermitted) {
                                    notificationPermissionState.launchPermissionRequest()
                                }
                                viewModel.onEvent(
                                    NotificationEvent.NotificationTypeChanged(NotificationType.Enabled.Periodic),
                                )
                            }
                            .padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.notificationsScreen_periodic),
                        icon = Icons.Rounded.NotificationsActive,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            selectedOption.AnimatedContent(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
            ) { targetState ->
                when (targetState) {
                    NotificationType.Disabled -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(id = R.string.notificationsScreen_notificationsAreDisabled),
                                textAlign = TextAlign.Center,
                            )
                            AnimatedVisibility(
                                visible = notificationTypeSelected == NotificationType.Disabled &&
                                    state.notificationTypeInSettings != NotificationType.Disabled,
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.notificationsScreen_notificationsAreDisabledMore),
                                        textAlign = TextAlign.Center,
                                    )
                                    FilledTonalButton(
                                        onClick = {
                                            try {
                                                val intent =
                                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                val uri =
                                                    Uri.fromParts(
                                                        "package",
                                                        context.packageName,
                                                        null,
                                                    )
                                                intent.data = uri
                                                context.startActivity(intent)
                                            } catch (_: Exception) {
                                            }
                                        },
                                    ) {
                                        Text(text = stringResource(id = R.string.notificationsScreen_goToSettings))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Rounded.OpenInNew,
                                            contentDescription = stringResource(id = R.string.notificationsScreen_goToSettings),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    NotificationType.Enabled.Exact -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            ExactNotificationColumn(
                                exactTimeList = state.exactTimeList,
                                exactAllowed = state.exactAllowed,
                                onDelete = { index ->
                                    viewModel.onEvent(
                                        NotificationEvent.NotificationExactChanged.DeleteExact(index),
                                    )
                                },
                                onOpenTimeDialog = { id ->
                                    openTimeDialog = id
                                },
                            )
                        }
                    }

                    NotificationType.Enabled.Periodic -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            PeriodicNotificationColumn(
                                repeatTimePeriodInHours = state.repeatTimePeriodInHours,
                                repeatAtNight = state.repeatAtNight,
                                onTimePeriodChanged = {
                                    viewModel.onEvent(
                                        NotificationEvent.NotificationPeriodicChanged.TimePeriod(it),
                                    )
                                },
                            ) {
                                viewModel.onEvent(
                                    NotificationEvent.NotificationPeriodicChanged.SendAtNight(it),
                                )
                            }
                        }
                    }
                }
            }
        }

        Dialog(
            open = openTimeDialog != null,
            onClose = {
                openTimeDialog = null
            },
        ) {
            TimePicker(state = timePickerState)
            Row(
                modifier = Modifier.align(Alignment.End).padding(0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(
                    onClick = {
                        openTimeDialog?.let { index ->
                            viewModel.onEvent(
                                NotificationEvent.NotificationExactChanged.AddExact(
                                    id = index,
                                    time = timePickerState.hour * 60 + timePickerState.minute,
                                ),
                            )
                        }

                        openTimeDialog = null
                    },
                ) {
                    Text(stringResource(id = android.R.string.ok))
                }
                TextButton(
                    onClick = {
                        openTimeDialog = null
                    },
                ) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        }
    }
}
