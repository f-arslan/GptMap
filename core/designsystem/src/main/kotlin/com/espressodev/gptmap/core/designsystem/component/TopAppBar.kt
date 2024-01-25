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
    selectedItemsCount: Int = 0,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = { AppBarTitle(text, editText, isInEditMode) },
        navigationIcon = { AppBarNavigationIcon(isInEditMode, onBackClick, onCancelClick) },
        actions = {
            AppBarActions(
                isInEditMode,
                selectedItemsCount,
                onEditClick,
                onDeleteClick,
                icon
            )
        }
    )
}

@Composable
private fun AppBarTitle(text: TextType, editText: String, isInEditMode: Boolean) {
    val titleText = when (text) {
        is TextType.Res -> stringResource(id = text.textId)
        is TextType.Text -> text.text
    }

    if (isInEditMode) {
        Text(text = editText, maxLines = 1, overflow = TextOverflow.Ellipsis)
    } else {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = titleText,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AppBarNavigationIcon(
    isInEditMode: Boolean,
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    if (isInEditMode) {
        IconButton(onClick = onCancelClick) {
            Icon(
                imageVector = GmIcons.CancelOutlined,
                contentDescription = stringResource(id = AppText.cancel)
            )
        }
    } else {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = GmIcons.ArrowBackOutlined,
                contentDescription = stringResource(id = AppText.back_arrow)
            )
        }
    }
}

@Composable
private fun AppBarActions(
    isInEditMode: Boolean,
    selectedItemsCount: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    icon: IconType
) {
    if (isInEditMode) {
        if (selectedItemsCount == 1) {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = GmIcons.EditDefault,
                    contentDescription = stringResource(id = AppText.edit)
                )
            }
        }
        if (selectedItemsCount > 0) {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = GmIcons.DeleteOutlined,
                    contentDescription = stringResource(id = AppText.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    } else {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
private fun TopAppBarPreview() {
    GptmapTheme {
        GmTopAppBar(
            text = TextType.Res(AppText.add_favourite),
            icon = IconType.Vector(GmIcons.AccountCircleOutlined),
            onBackClick = { },
            isInEditMode = true,
        )
    }
}
