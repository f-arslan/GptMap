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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.AppWrapper
import com.espressodev.gptmap.core.designsystem.component.DayHeader
import com.espressodev.gptmap.core.designsystem.component.DefaultButton
import com.espressodev.gptmap.core.designsystem.component.DefaultTextField
import com.espressodev.gptmap.core.designsystem.component.ExtFloActionButton
import com.espressodev.gptmap.core.designsystem.component.GmCircularIndicator
import com.espressodev.gptmap.core.designsystem.component.HeaderWrapper
import com.espressodev.gptmap.core.designsystem.component.PasswordTextField
import com.espressodev.gptmap.core.google_auth.composable.OneTapLauncher
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun LoginRoute(
    navigateToMap: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.loadingState is LoadingState.Loading) GmCircularIndicator()
    LoginScreen(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event, navigateToMap) },
        onNotMemberClick = navigateToRegister,
        onForgotPasswordClick = navigateToForgotPassword
    )
    OneTapLauncher(
        oneTapClient = viewModel.oneTapClient,
        oneTapSignInUpResponse = uiState.oneTapSignInResponse,
        singInUpWithGoogleResponse = uiState.signInWithGoogleResponse,
        signInWithGoogle = viewModel::signInWithGoogle,
        navigate = navigateToMap
    )
}


@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    onNotMemberClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    AppWrapper {
        LoginHeader()
        DefaultTextField(
            value = uiState.email,
            label = AppText.email,
            leadingIcon = GmIcons.EmailOutlined,
            onValueChange = { onEvent(LoginEvent.OnEmailChanged(it)) })
        Column {
            PasswordTextField(
                value = uiState.password,
                label = AppText.password,
                icon = GmIcons.LockOutlined,
                onValueChange = { onEvent(LoginEvent.OnPasswordChanged(it)) }
            )
            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    stringResource(AppText.forgot_password)
                )
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
            height = VERY_HIGH_PADDING
        )
        ExtFloActionButton(
            AppDrawable.google,
            label = AppText.continue_google,
            onClick = { onEvent(LoginEvent.OnGoogleClicked) }
        )
        Spacer(Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = HIGH_PADDING)
        ) {
            Text(
                text = stringResource(id = AppText.not_a_member),
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = onNotMemberClick) {
                Text(
                    text = stringResource(id = AppText.register_now),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun LoginHeader() {
    HeaderWrapper {
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
fun LoginScreenPreview() {
    LoginScreen(LoginUiState("Fatih"), {}, {}, {})
}