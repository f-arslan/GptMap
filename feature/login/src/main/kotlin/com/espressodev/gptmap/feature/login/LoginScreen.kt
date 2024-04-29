package com.espressodev.gptmap.feature.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.component.AppWrapper
import com.espressodev.gptmap.core.designsystem.component.DayHeader
import com.espressodev.gptmap.core.designsystem.component.DefaultButton
import com.espressodev.gptmap.core.designsystem.component.DefaultTextField
import com.espressodev.gptmap.core.designsystem.component.ExtFloActionButton
import com.espressodev.gptmap.core.designsystem.component.GmProgressIndicator
import com.espressodev.gptmap.core.designsystem.component.HeaderWrapper
import com.espressodev.gptmap.core.designsystem.component.PasswordTextField
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
internal fun LoginRoute(
    navigateToMap: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onEvent by rememberUpdatedState(
        newValue = { event: LoginEvent -> viewModel.onEvent(event) }
    )

    val navigationState by viewModel.navigationState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = navigationState) {
        fun performNavigation(action: () -> Unit) {
            action()
            viewModel.resetNavigation()
        }
        when (navigationState) {
            is NavigationState.NavigateToMap -> performNavigation(navigateToMap)
            is NavigationState.NavigateToRegister -> performNavigation(navigateToRegister)
            is NavigationState.NavigateToForgotPassword ->
                performNavigation(navigateToForgotPassword)
            NavigationState.None -> Unit
        }
    }

    if (uiState.loadingState is LoadingState.Loading) GmProgressIndicator()

    LoginScreen(
        uiState = uiState,
        onEvent = { event -> onEvent(event) },
    )

}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AppWrapper(modifier) {
        LoginHeader()
        DefaultTextField(
            value = uiState.email,
            label = AppText.email,
            leadingIcon = GmIcons.EmailOutlined,
            onValueChange = { onEvent(LoginEvent.OnEmailChanged(it)) }
        )
        Column {
            PasswordTextField(
                value = uiState.password,
                label = AppText.password,
                icon = GmIcons.LockOutlined,
                onValueChange = { onEvent(LoginEvent.OnPasswordChanged(it)) }
            )
            TextButton(
                onClick = { onEvent(LoginEvent.OnForgotPasswordClicked) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(AppText.forgot_password))
            }
        }
        DefaultButton(
            text = AppText.login,
            onClick = { onEvent(LoginEvent.OnLoginClicked) },
            Modifier.fillMaxWidth()
        )
        DayHeader(
            stringResource(AppText.or),
            style = MaterialTheme.typography.titleMedium,
            height = 24.dp
        )
        val context = LocalContext.current
        ExtFloActionButton(
            IconType.Bitmap(AppDrawable.google),
            label = AppText.continue_google,
            onClick = { onEvent(LoginEvent.OnGoogleClicked(context)) }
        )
        Spacer(Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = stringResource(id = AppText.not_a_member),
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = { onEvent(LoginEvent.OnNotMemberClicked) }) {
                Text(
                    text = stringResource(id = AppText.register_now),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun LoginHeader(modifier: Modifier = Modifier) {
    HeaderWrapper(modifier = modifier) {
        Text(
            text = stringResource(AppText.login_header),
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            text = stringResource(AppText.login_sub_header),
            style = MaterialTheme.typography.titleMedium,
            minLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(LoginUiState("Fatih"), {})
}
