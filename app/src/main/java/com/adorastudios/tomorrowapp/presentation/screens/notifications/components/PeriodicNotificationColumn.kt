@file:OptIn(ExperimentalLayoutApi::class)

package com.adorastudios.tomorrowapp.presentation.screens.notifications.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adorastudios.tomorrowapp.R

@Composable
fun PeriodicNotificationColumn(
    repeatTimePeriodInHours: Int,
    repeatAtNight: Boolean,
    onTimePeriodChanged: (Int) -> Unit,
    onSendAtNightChanged: (Boolean) -> Unit,
) {
    Text(
        text = stringResource(id = R.string.notificationsScreen_notificationsPeriodic),
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.height(8.dp))
    val list = remember { listOf(1, 2, 3, 4, 6, 8) }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 3,
    ) {
        list.forEach { hour ->
            TextButton(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (hour == repeatTimePeriodInHours) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    containerColor = if (hour == repeatTimePeriodInHours) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Color.Transparent
                    },
                ),
                onClick = {
                    onTimePeriodChanged(hour)
                },
            ) {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.notificationsScreen_xHours,
                        count = hour,
                        hour,
                    ),
                )
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally,
        ),
    ) {
        Text(
            text = stringResource(id = R.string.notificationsScreen_sendAtNight),
        )
        Checkbox(
            checked = repeatAtNight,
            onCheckedChange = {
                onSendAtNightChanged(it)
            },
        )
    }
}
