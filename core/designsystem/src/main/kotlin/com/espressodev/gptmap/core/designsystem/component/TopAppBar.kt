package com.espressodev.gptmap.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GmTopAppBar(
    text: TextType,
    icon: IconType,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val value = when (text) {
        is TextType.Res -> stringResource(id = text.textId)
        is TextType.Text -> text.text
    }
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = value,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = GmIcons.ArrowBackOutlined,
                    contentDescription = stringResource(id = AppText.back_arrow)
                )
            }
        },
        actions = {
            when (icon) {
                is IconType.Bitmap -> {
                    Icon(
                        painter = painterResource(id = icon.painterId),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 12.dp),
                    )
                }

                is IconType.Vector -> {
                    Icon(
                        imageVector = icon.imageVector,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 12.dp),
                    )
                }
            }
        }
    )
}
