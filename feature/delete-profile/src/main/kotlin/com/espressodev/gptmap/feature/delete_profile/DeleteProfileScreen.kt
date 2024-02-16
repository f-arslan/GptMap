package com.espressodev.gptmap.feature.delete_profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmAlertDialog
import com.espressodev.gptmap.core.designsystem.component.GmProgressIndicator
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteProfileRoute(
    navigateToLogin: () -> Unit,
    popUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeleteProfileViewModel = hiltViewModel(),
) {
    val isDialogOpened by viewModel.isDialogOpened.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            GmTopAppBar(
                text = TextType.Res(AppText.delete_profile_title),
                icon = IconType.Vector(GmIcons.DeleteOutlined),
                onBackClick = popUp
            )
        },
        modifier = modifier
    ) {
        DeleteProfileScreen(
            modifier = Modifier.padding(it),
            onDeleteClick = viewModel::onDeleteClick
        )
    }

    if (isDialogOpened) {
        GmAlertDialog(
            title = AppText.delete_confirm,
            text = {
                Text(
                    text = stringResource(id = AppText.delete_confirm_sub),
                    textAlign = TextAlign.Justify
                )
            },
            onConfirm = {
                viewModel.onDismissDialog()
                viewModel.onConfirmDialog(
                    navigate = navigateToLogin,
                    popUp = popUp
                )
            },
            onDismiss = viewModel::onDismissDialog
        )
    }

    if (isLoading) GmProgressIndicator()
}

@Composable
fun DeleteProfileScreen(modifier: Modifier = Modifier, onDeleteClick: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        ProfileImage()
        ConfirmSubText()
        HappeningListText()
        Spacer(Modifier.height(16.dp))
        ImportantConsiderationsTitle()
        ImportantConsiderationsList()
        DeleteAccountButton(onDeleteClick)
    }
}

@Composable
private fun ColumnScope.ProfileImage() {
    Image(
        painter = painterResource(AppDrawable.delete_profile),
        contentDescription = stringResource(AppText.delete),
        modifier = Modifier
            .size(180.dp)
            .align(Alignment.CenterHorizontally),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun ConfirmSubText() {
    Text(
        text = stringResource(AppText.delete_confirm_sub),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Justify,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun HappeningListText() {
    Text(
        stringResource(AppText.after_deleting_happening_list),
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        letterSpacing = 0.75.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun ImportantConsiderationsTitle() {
    Text(
        stringResource(AppText.important_cons),
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun ImportantConsiderationsList() {
    Text(
        stringResource(AppText.important_cons_list),
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun DeleteAccountButton(onDeleteClick: () -> Unit) {
    OutlinedButton(
        onClick = onDeleteClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(stringResource(AppText.delete_my_account))
    }
}

@Composable
@Preview(showBackground = true, device = "id:pixel")
private fun DeleteProfilePreview() {
    DeleteProfileScreen(modifier = Modifier, onDeleteClick = {})
}
