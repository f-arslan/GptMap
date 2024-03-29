package com.espressodev.gptmap.feature.profile

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmTonalIconButton
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.LetterInCircle
import com.espressodev.gptmap.core.designsystem.component.LottieAnimationPlaceholder
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.firebase.User
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileRoute(
    popUp: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToInfo: () -> Unit,
    navigateToDelete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            GmTopAppBar(
                text = TextType.Res(AppText.profile_top_bar_header),
                icon = IconType.Vector(GmIcons.AccountCircleOutlined),
                onBackClick = popUp
            )
        },
        modifier = modifier
    ) {
        when (val result = user) {
            is Response.Failure -> LottieAnimationPlaceholder(AppRaw.confused_man_404)
            Response.Loading -> {}
            is Response.Success -> ProfileScreen(
                user = result.data,
                onInfoClick = navigateToInfo,
                onLogOutClick = { viewModel.onLogoutClick(navigateToLogin) },
                onDeleteClick = navigateToDelete,
                modifier = Modifier.padding(it)
            )
        }
    }
}

@Composable
private fun ProfileScreen(
    user: User,
    onInfoClick: () -> Unit,
    onLogOutClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LetterInCircle(letter = user.fullName.first(), modifier = Modifier.size(120.dp))
        Text(text = user.fullName, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        ProfileItem(GmIcons.InfoOutlined, AppText.info, onInfoClick)
        ProfileItem(GmIcons.DeleteOutlined, AppText.delete_profile_title, onDeleteClick)
        ProfileItem(GmIcons.LogoutOutlined, AppText.logout, onLogOutClick)
    }
}

@Composable
private fun ProfileItem(
    icon: ImageVector,
    @StringRes textId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GmTonalIconButton(icon = icon)
            Text(text = stringResource(id = textId), style = MaterialTheme.typography.titleMedium)
        }
        Icon(
            imageVector = GmIcons.NavigateNextDefault,
            contentDescription = null,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        user = User(fullName = "Fatih Arslan"),
        onInfoClick = {},
        onLogOutClick = {},
        {}
    )
}
