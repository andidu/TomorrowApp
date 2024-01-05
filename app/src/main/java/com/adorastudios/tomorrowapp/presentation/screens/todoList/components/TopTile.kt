@file:OptIn(ExperimentalAnimationApi::class)

package com.adorastudios.tomorrowapp.presentation.screens.todoList.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.ViewHeadline
import androidx.compose.material.icons.rounded.Window
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adorastudios.tomorrowapp.R
import com.adorastudios.tomorrowapp.domain.settings.Preferences
import com.adorastudios.tomorrowapp.presentation.screens.todoList.ListViewType
import com.adorastudios.tomorrowapp.ui.theme.AppTheme
import com.adorastudios.tomorrowapp.ui.theme.topShape

@Composable
fun TopTile(
    topTileOpen: Boolean,
    preferences: Preferences,
    onOpen: (Boolean) -> Unit,
    onOptionSelected: (ListViewType) -> Unit,
    onShowOverdueInTodaySelected: (Boolean) -> Unit,
    onMoveDoneToPastSelected: (Boolean) -> Unit,
) {
    val transition = updateTransition(
        targetState = topTileOpen,
        label = "topTileOpen",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = topShape,
            )
            .clip(topShape)
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.paddings.appBarHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.size(48.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.todoListScreen_title),
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge,
            )
            IconButton(
                onClick = {
                    onOpen(!transition.currentState)
                },
            ) {
                transition.AnimatedContent {
                    when (it) {
                        true -> {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropUp,
                                contentDescription = stringResource(id = R.string.contentDescription_closeSettings),
                            )
                        }

                        false -> {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = stringResource(id = R.string.contentDescription_openSettings),
                            )
                        }
                    }
                }
            }
        }
        transition.AnimatedVisibility(
            visible = { it },
            enter = fadeIn() + expandVertically(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    DefaultImageRadioButton(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Rounded.ViewHeadline,
                        iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentDescription = stringResource(id = R.string.contentDescription_titleOnly),
                        selected = preferences.listViewType == ListViewType.TitleOnly,
                        onSelect = { onOptionSelected(ListViewType.TitleOnly) },
                    )
                    DefaultImageRadioButton(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Rounded.TableRows,
                        iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentDescription = stringResource(id = R.string.contentDescription_titleAndContent),
                        selected = preferences.listViewType == ListViewType.TitleAndContent,
                        onSelect = { onOptionSelected(ListViewType.TitleAndContent) },
                    )
                    DefaultImageRadioButton(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Rounded.Window,
                        iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentDescription = stringResource(id = R.string.contentDescription_largeGrid),
                        selected = preferences.listViewType == ListViewType.LargeGrid,
                        onSelect = { onOptionSelected(ListViewType.LargeGrid) },
                    )
                    DefaultImageRadioButton(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Rounded.GridOn,
                        iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        contentDescription = stringResource(id = R.string.contentDescription_smallGrid),
                        selected = preferences.listViewType == ListViewType.SmallGrid,
                        onSelect = { onOptionSelected(ListViewType.SmallGrid) },
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = preferences.showOverdueInToday,
                        onCheckedChange = onShowOverdueInTodaySelected,
                    )
                    Text(text = stringResource(id = R.string.todoListScreen_showOverdueInToday))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = preferences.moveDoneToPast,
                        onCheckedChange = onMoveDoneToPastSelected,
                    )
                    Text(text = stringResource(id = R.string.todoListScreen_moveDoneToPast))
                }
            }
        }
    }
}
