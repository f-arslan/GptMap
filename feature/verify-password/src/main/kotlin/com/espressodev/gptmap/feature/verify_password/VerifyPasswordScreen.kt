package com.espressodev.gptmap.feature.verify_password

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmProgressIndicator
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.PasswordTextField
import com.espressodev.gptmap.core.designsystem.util.keyboardAsState
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyPasswordRoute(
    popUp: () -> Unit,
    navigateToDelete: () -> Unit,
    viewModel: VerifyPasswordViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            GmTopAppBar(
                text = TextType.Res(AppText.verify_password_title),
                icon = IconType.Vector(GmIcons.PasswordFilled),
                onBackClick = popUp
            )
        }
    ) {
        VerifyPasswordScreen(
            modifier = Modifier.padding(it),
            uiState = uiState,
            onValueChange = viewModel::onPasswordChanged,
            onDone = { viewModel.onDone(navigateToDelete) }
        )
    }
    if (uiState.isLoading) GmProgressIndicator()
}


@Composable
fun VerifyPasswordScreen(
    modifier: Modifier,
    uiState: VerifyPasswordUiState,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isKeyboardOpened by keyboardAsState()
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(isKeyboardOpened) {
        if (isKeyboardOpened) {
            scrollState.animateScrollTo(Int.MAX_VALUE)
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(AppDrawable.password_theme),
            contentDescription = stringResource(AppText.password),
            modifier = Modifier.size(250.dp),
        )
        Text(
            stringResource(AppText.password_insurance),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordTextField(
            value = uiState.password,
            icon = GmIcons.LockOutlined,
            label = AppText.password,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            enabled = !uiState.isLoading,
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboard?.hide()
                    onDone()
                }
            ),
            modifier = Modifier.imePadding()
        )
    }
}

@Composable
@Preview(showBackground = true, device = "id:pixel")
fun VerifyPasswordPreview() {
    VerifyPasswordScreen(modifier = Modifier, uiState = VerifyPasswordUiState(
        password = "",
        isLoading = false
    ), onValueChange = {}, onDone = {})
}