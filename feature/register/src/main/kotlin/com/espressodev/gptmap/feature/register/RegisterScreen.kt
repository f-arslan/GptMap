package com.espressodev.gptmap.feature.register

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
import com.espressodev.gptmap.core.designsystem.Constants.PRIVACY_POLICY
import com.espressodev.gptmap.core.designsystem.Constants.PRIVACY_POLICY_DESC
import com.espressodev.gptmap.core.designsystem.Constants.PRIVACY_POLICY_LINK
import com.espressodev.gptmap.core.designsystem.Constants.TERMS_CONDITIONS
import com.espressodev.gptmap.core.designsystem.Constants.TERMS_CONDITIONS_LINK
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.AppAlertDialog
import com.espressodev.gptmap.core.designsystem.component.AppWrapper
import com.espressodev.gptmap.core.designsystem.component.DayHeader
import com.espressodev.gptmap.core.designsystem.component.DefaultButton
import com.espressodev.gptmap.core.designsystem.component.DefaultTextField
import com.espressodev.gptmap.core.designsystem.component.ExtFloActionButton
import com.espressodev.gptmap.core.designsystem.component.GmCircularIndicator
import com.espressodev.gptmap.core.designsystem.component.HeaderWrapper
import com.espressodev.gptmap.core.designsystem.component.HyperlinkText
import com.espressodev.gptmap.core.designsystem.component.PasswordTextField
import com.espressodev.gptmap.core.google_auth.composable.OneTapLauncher
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.google.GoogleResponse
import kotlinx.collections.immutable.immutableMapOf
import kotlinx.collections.immutable.persistentMapOf
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun RegisterRoute(
    clearAndNavigateLogin: () -> Unit,
    clearAndNavigateMap: () -> Unit,
    viewModel: RegisterScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        uiState.loadingState is LoadingState.Loading -> {
            GmCircularIndicator()
        }

        uiState.verificationAlertState is LoadingState.Loading -> {
            AppAlertDialog(
                GmIcons.MarkEmailUnreadOutlined,
                AppText.email_confirmation_title,
                AppText.email_confirmation_body,
                onConfirm = { viewModel.handleVerificationAndNavigate { clearAndNavigateLogin() } },
                onDismiss = {
                    viewModel.onEvent(
                        RegisterEvent.OnVerificationAlertStateChanged(
                            LoadingState.Idle
                        )
                    )
                }
            )
        }
    }

    RegisterScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onAlreadyHaveAccountClicked = clearAndNavigateLogin
    )

    OneTapLauncher(
        oneTapClient = viewModel.oneTapClient,
        oneTapSignInUpResponse = uiState.oneTapSignUpResponse,
        singInUpWithGoogleResponse = uiState.signUpWithGoogleResponse,
        signInWithGoogle = viewModel::signUpWithGoogle,
        navigate = { clearAndNavigateMap() }
    )
}

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onEvent: (RegisterEvent) -> Unit,
    onAlreadyHaveAccountClicked: () -> Unit
) {
    AppWrapper {
        RegisterHeader()
        DefaultTextField(
            value = uiState.fullName,
            label = AppText.full_name,
            leadingIcon = GmIcons.FaceOutlined,
            onValueChange = { onEvent(RegisterEvent.OnFullNameChanged(it)) }
        )
        DefaultTextField(
            value = uiState.email,
            label = AppText.email,
            leadingIcon = GmIcons.EmailOutlined,
            onValueChange = { onEvent(RegisterEvent.OnEmailChanged(it)) }
        )
        PasswordTextField(
            value = uiState.password,
            icon = GmIcons.LockOutlined,
            label = AppText.password,
            onValueChange = { onEvent(RegisterEvent.OnPasswordChanged(it)) }
        )
        PasswordTextField(
            value = uiState.confirmPassword,
            icon = GmIcons.LockOutlined,
            label = AppText.repeat_password,
            onValueChange = { onEvent(RegisterEvent.OnConfirmPasswordChanged(it)) }
        )
        HyperlinkText(
            fullText = PRIVACY_POLICY_DESC, persistentMapOf(
                PRIVACY_POLICY.lowercase() to PRIVACY_POLICY_LINK,
                TERMS_CONDITIONS.lowercase() to TERMS_CONDITIONS_LINK
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
        )
        DefaultButton(
            text = AppText.register,
            onClick = { onEvent(RegisterEvent.OnRegisterClicked) },
            modifier = Modifier.fillMaxWidth()
        )
        DayHeader(
            dayString = stringResource(AppText.or),
            style = MaterialTheme.typography.titleMedium,
            height = VERY_HIGH_PADDING
        )
        ExtFloActionButton(
            icon = AppDrawable.google,
            label = AppText.continue_google,
            onClick = { onEvent(RegisterEvent.OnGoogleClicked) }
        )
        Spacer(Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = HIGH_PADDING)
        ) {
            Text(
                text = stringResource(id = AppText.already_have_account),
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = onAlreadyHaveAccountClicked) {
                Text(
                    text = stringResource(id = AppText.login),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun RegisterHeader() {
    HeaderWrapper {
        Text(
            text = stringResource(AppText.register_header),
            style = MaterialTheme.typography.displaySmall
        )
    }
}

@Composable
@Preview(showBackground = true)
fun RegisterPreview() {
    RegisterScreen(uiState = RegisterUiState(
        fullName = "Beatriz Wells",
        email = "whitney.brewer@example.com",
        password = "sale",
        confirmPassword = "mandamus",
        verificationAlertState = LoadingState.Loading,
        loadingState = LoadingState.Loading,
        oneTapSignUpResponse = GoogleResponse.Success(null),
        signUpWithGoogleResponse = GoogleResponse.Success(true)
    ), onEvent = {}, onAlreadyHaveAccountClicked = {})
}
