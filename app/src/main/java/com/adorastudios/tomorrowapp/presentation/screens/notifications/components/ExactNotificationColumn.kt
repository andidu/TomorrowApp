package com.adorastudios.tomorrowapp.presentation.screens.notifications.components

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adorastudios.tomorrowapp.R
import com.adorastudios.tomorrowapp.domain.getTime
import com.adorastudios.tomorrowapp.presentation.screens.notifications.exactNotifications.SixList

@Composable
fun ColumnScope.ExactNotificationColumn(
    exactTimeList: SixList,
    exactAllowed: Boolean,
    onDelete: (Int) -> Unit,
    onOpenTimeDialog: (Int) -> Unit,
) {
    val context = LocalContext.current

    if (!exactAllowed) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.notificationsScreen_giveExactPermissionExplanation),
            textAlign = TextAlign.Center,
        )
        TextButton(
            onClick = {
                context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
            ),
        ) {
            Text(
                text = stringResource(id = R.string.notificationsScreen_giveExactPermission),
                textAlign = TextAlign.Center,
            )
        }
    }
    Text(
        text = stringResource(id = R.string.notificationsScreen_notificationsExact),
        textAlign = TextAlign.Center,
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        exactTimeList.forEachIndexed { index: Int, time: Int? ->
            AnimatedContent(
                targetState = time,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "time$index",
            ) { state ->
                when (state) {
                    null -> {
                        FilledTonalButton(
                            modifier = Modifier.padding(vertical = 8.dp),
                            onClick = {
                                onOpenTimeDialog(index)
                            },
                        ) {
                            Text(text = stringResource(id = R.string.notificationsScreen_addNotificationTime))
                        }
                    }

                    else -> {
                        val timeString = remember(time, exactAllowed) {
                            if (exactAllowed) {
                                state.getTime()
                            } else {
                                state.getTime() + " - " + (state + 60).getTime()
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = timeString,
                            )
                            IconButton(onClick = {
                                onDelete(index)
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteForever,
                                    contentDescription = stringResource(id = R.string.contentDescription_delete),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
