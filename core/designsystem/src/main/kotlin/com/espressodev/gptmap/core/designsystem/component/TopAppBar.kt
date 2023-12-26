package com.espressodev.gptmap.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GmTopAppBar(@StringRes title: Int, onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(id = title))
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
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = GmIcons.FavouriteOutlined, contentDescription = null)
        }
    )
}