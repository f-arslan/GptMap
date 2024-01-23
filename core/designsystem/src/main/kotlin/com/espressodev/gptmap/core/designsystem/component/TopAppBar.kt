package com.espressodev.gptmap.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GmTopAppBar(
    text: TextType,
    icon: IconType,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    editText: String = "",
    isInEditMode: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val value = when (text) {
        is TextType.Res -> stringResource(id = text.textId)
        is TextType.Text -> text.text
    }
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = {
            when (isInEditMode) {
                true -> Text(text = editText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                false -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = value,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        navigationIcon = {
            when (isInEditMode) {
                true -> {
                    IconButton(onClick = onCancelClick) {
                        Icon(
                            imageVector = GmIcons.CancelOutlined,
                            contentDescription = stringResource(id = AppText.cancel)
                        )
                    }
                }

                false -> {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = GmIcons.ArrowBackOutlined,
                            contentDescription = stringResource(id = AppText.back_arrow)
                        )
                    }
                }
            }
        },
        actions = {
            when (isInEditMode) {
                true -> {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = GmIcons.EditDefault,
                            contentDescription = stringResource(id = AppText.edit)
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = GmIcons.DeleteOutlined,
                            contentDescription = stringResource(id = AppText.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                false -> {
                    when (icon) {
                        is IconType.Bitmap -> {
                            Icon(
                                painter = painterResource(id = icon.painterId),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(24.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }

                        is IconType.Vector -> {
                            Icon(
                                imageVector = icon.imageVector,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(24.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun TopAppBarPreview() {
    GptmapTheme {
        GmTopAppBar(
            text = TextType.Res(AppText.add_favourite),
            icon = IconType.Vector(GmIcons.AccountCircleOutlined),
            onBackClick = { },
            isInEditMode = true,
        )
    }
}
