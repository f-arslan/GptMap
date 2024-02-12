package com.espressodev.gptmap.feature.favourite

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.espressodev.gptmap.core.designsystem.Constants.BOTTOM_BAR_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmAlertDialog
import com.espressodev.gptmap.core.designsystem.component.GmEditAlertDialog
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.LottieAnimationPlaceholder
import com.espressodev.gptmap.core.model.EditableItemUiEvent
import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteRoute(
    popUp: () -> Unit,
    navigateToMap: (String) -> Unit,
    viewModel: FavouriteViewModel = hiltViewModel(),
) {
    val favourites by viewModel.favourites.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onEvent by rememberUpdatedState(newValue = viewModel::onEvent)
    Scaffold(
        topBar = {
            GmTopAppBar(
                text = TextType.Res(AppText.favourite),
                icon = IconType.Vector(GmIcons.FavouriteFilled),
                onBackClick = popUp,
                selectedItemsCount = 1,
                editText = uiState.selectedItem.title,
                isInEditMode = uiState.isUiInEditMode,
                onEditClick = { onEvent(EditableItemUiEvent.OnEditClick) },
                onDeleteClick = { onEvent(EditableItemUiEvent.OnDeleteClick) },
                onCancelClick = { onEvent(EditableItemUiEvent.OnCancelClick) }
            )
        },
        modifier = Modifier.padding(bottom = BOTTOM_BAR_PADDING)
    ) {
        when (val result = favourites) {
            is Response.Success -> {
                if (result.data.isEmpty()) {
                    LottieAnimationPlaceholder(AppRaw.nothing_here_anim)
                } else {
                    FavouriteScreen(
                        onCardClick = navigateToMap,
                        favourites = result.data,
                        onLongClick = { favourite ->
                            onEvent(EditableItemUiEvent.OnLongClickToItem(favourite))
                        },
                        selectedId = uiState.selectedItem.favouriteId,
                        isUiInEditMode = uiState.isUiInEditMode,
                        modifier = Modifier.padding(it)
                    )
                }
            }

            is Response.Failure -> {
                LottieAnimationPlaceholder(AppRaw.confused_man_404)
            }

            Response.Loading -> {}
        }

    }

    BackHandler {
        if (uiState.isUiInEditMode) {
            onEvent(EditableItemUiEvent.Reset)
        } else {
            popUp()
        }
    }

    if (uiState.editDialogState) {
        GmEditAlertDialog(
            title = AppText.rename,
            textFieldLabel = AppText.screenshot_gallery_edit_dialog_text_field_placeholder,
            onConfirm = { onEvent(EditableItemUiEvent.OnEditDialogConfirm(it)) },
            onDismiss = { onEvent(EditableItemUiEvent.OnEditDialogDismiss) }
        )
    }

    if (uiState.deleteDialogState) {
        GmAlertDialog(
            title = AppText.screenshot_gallery_delete_dialog_title,
            onConfirm = { onEvent(EditableItemUiEvent.OnDeleteDialogConfirm) },
            onDismiss = { onEvent(EditableItemUiEvent.OnDeleteDialogDismiss) }
        )
    }
}

internal const val LazyColumnTestTag = "lazy_column_test_tag"

@Composable
fun FavouriteScreen(
    favourites: List<Favourite>,
    selectedId: String,
    onCardClick: (String) -> Unit,
    onLongClick: (Favourite) -> Unit,
    isUiInEditMode: Boolean,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag(LazyColumnTestTag),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp)
    ) {
        items(favourites, key = { favourite -> favourite.id }) { favourite ->
            FavouriteCard(
                favourite = favourite,
                onClick = {
                    if (isUiInEditMode) {
                        onLongClick(favourite)
                    } else {
                        onCardClick(favourite.favouriteId)
                    }
                },
                onLongClick = { onLongClick(favourite) },
                isSelected = favourite.favouriteId == selectedId,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavouriteCard(
    favourite: Favourite,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderStroke = if (isSelected) 3.dp else 0.dp
    val elevation = if (isSelected) 8.dp else 0.dp
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Surface(
        modifier = modifier
            .shadow(elevation)
            .border(borderStroke, borderColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = favourite.placeholderImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (favourite.title.isNotBlank()) {
                    InfoRow(
                        icon = GmIcons.TitleDefault,
                        text = favourite.title
                    )
                }
                InfoRow(
                    icon = GmIcons.LocationCityOutlined,
                    text = favourite.placeholderTitle
                )
                InfoRow(
                    icon = GmIcons.MyLocationOutlined,
                    text = favourite.placeholderCoordinates
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 16.dp,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    iconTint: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = iconTint
        )
        Text(
            text = text,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
