package com.espressodev.gptmap.feature.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.Constants.PRIVACY_POLICY
import com.espressodev.gptmap.core.designsystem.Constants.PRIVACY_POLICY_DESC
import com.espressodev.gptmap.core.designsystem.Constants.PRIVACY_POLICY_LINK
import com.espressodev.gptmap.core.designsystem.Constants.TERMS_CONDITIONS
import com.espressodev.gptmap.core.designsystem.Constants.TERMS_CONDITIONS_LINK
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.AppWrapper
import com.espressodev.gptmap.core.designsystem.component.DayHeader
import com.espressodev.gptmap.core.designsystem.component.DefaultButton
import com.espressodev.gptmap.core.designsystem.component.DefaultTextField
import com.espressodev.gptmap.core.designsystem.component.ExtFloActionButton
import com.espressodev.gptmap.core.designsystem.component.GmAlertDialog
import com.espressodev.gptmap.core.designsystem.component.GmProgressIndicator
import com.espressodev.gptmap.core.designsystem.component.HeaderWrapper
import com.espressodev.gptmap.core.designsystem.component.HyperlinkText
import com.espressodev.gptmap.core.designsystem.component.PasswordTextField
import com.espressodev.gptmap.core.model.LoadingState
import kotlinx.collections.immutable.persistentMapOf
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
internal fun RegisterRoute(
    clearAndNavigateLogin: () -> Unit,
    clearAndNavigateMap: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterScreenViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        uiState.loadingState is LoadingState.Loading -> {
            GmProgressIndicator()
        }

        uiState.verificationAlertState is LoadingState.Loading -> {
            GmAlertDialog(
                icon = GmIcons.MarkEmailUnreadOutlined,
                title = AppText.email_confirmation_title,
                onConfirm = { viewModel.handleVerificationAndNavigate { clearAndNavigateLogin() } },
                onDismiss = {
                    viewModel.onEvent(
                        RegisterEvent.OnVerificationAlertStateChanged(
                            LoadingState.Idle,
                        ),
                    )
                },
                text = {
                    Text(
                        stringResource(AppText.email_confirmation_body),
                        textAlign = TextAlign.Center,
                    )
                },
            )
        }
    }

    RegisterScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onAlreadyHaveAccountClicked = clearAndNavigateLogin,
        modifier = modifier,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RegisterScreen(
    uiState: RegisterUiState,
    onEvent: (RegisterEvent) -> Unit,
    onAlreadyHaveAccountClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (
        fullNameFocusRequester,
        emailFocusRequester,
        passwordFocusRequester,
        confirmPasswordFocusRequester,
    ) = FocusRequester.createRefs()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    AppWrapper(modifier = modifier) {
        RegisterHeader()
        TextFieldSection(
            uiState = uiState,
            onEvent = onEvent,
            fullNameFocusRequester = fullNameFocusRequester,
            emailFocusRequester = emailFocusRequester,
            passwordFocusRequester = passwordFocusRequester,
            confirmPasswordFocusRequester = confirmPasswordFocusRequester,
        )
        HyperlinkText(
            fullText = PRIVACY_POLICY_DESC,
            hyperLinks = persistentMapOf(
                PRIVACY_POLICY.lowercase() to PRIVACY_POLICY_LINK,
                TERMS_CONDITIONS.lowercase() to TERMS_CONDITIONS_LINK,
            ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        )
        DefaultButton(
            text = AppText.register,
            onClick = {
                keyboardController?.hide()
                onEvent(RegisterEvent.OnRegisterClicked)
            },
            modifier = Modifier.fillMaxWidth(),
        )
        DayHeader(
            dayString = stringResource(AppText.or),
            style = MaterialTheme.typography.titleMedium,
            height = 24.dp,
        )
        ExtFloActionButton(
            icon = AppDrawable.google,
            label = AppText.continue_google,
            onClick = { onEvent(RegisterEvent.OnGoogleClicked(context)) },
        )
        Spacer(Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Text(
                text = stringResource(id = AppText.already_have_account),
                style = MaterialTheme.typography.titleMedium,
            )
            TextButton(onClick = onAlreadyHaveAccountClicked) {
                Text(
                    text = stringResource(id = AppText.login),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun TextFieldSection(
    uiState: RegisterUiState,
    onEvent: (RegisterEvent) -> Unit,
    fullNameFocusRequester: FocusRequester,
    emailFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester,
    confirmPasswordFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DefaultTextField(
            value = uiState.fullName,
            label = AppText.full_name,
            leadingIcon = GmIcons.FaceOutlined,
            onValueChange = { onEvent(RegisterEvent.OnFullNameChanged(it)) },
            modifier = Modifier.focusRequester(fullNameFocusRequester),
            keyboardActions = KeyboardActions(onNext = { emailFocusRequester.requestFocus() }),
        )
        DefaultTextField(
            value = uiState.email,
            label = AppText.email,
            leadingIcon = GmIcons.EmailOutlined,
            onValueChange = { onEvent(RegisterEvent.OnEmailChanged(it)) },
            modifier = Modifier.focusRequester(emailFocusRequester),
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { passwordFocusRequester.requestFocus() }),
        )
        PasswordTextField(
            value = uiState.password,
            icon = GmIcons.LockOutlined,
            label = AppText.password,
            onValueChange = { onEvent(RegisterEvent.OnPasswordChanged(it)) },
            modifier = Modifier.focusRequester(passwordFocusRequester),
            keyboardActions = KeyboardActions(onNext = { confirmPasswordFocusRequester.requestFocus() }),
        )
        PasswordTextField(
            value = uiState.confirmPassword,
            icon = GmIcons.LockOutlined,
            label = AppText.repeat_password,
            onValueChange = { onEvent(RegisterEvent.OnConfirmPasswordChanged(it)) },
            modifier = Modifier.focusRequester(confirmPasswordFocusRequester),
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions =
            KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onEvent(RegisterEvent.OnRegisterClicked)
                },
            ),
        )
    }
}

@Composable
private fun RegisterHeader(modifier: Modifier = Modifier) {
    HeaderWrapper(modifier = modifier) {
        Text(
            text = stringResource(AppText.register_header),
            style = MaterialTheme.typography.displaySmall,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun RegisterPreview() {
    RegisterScreen(
        uiState =
        RegisterUiState(
            fullName = "Beatriz Wells",
            email = "whitney.brewer@example.com",
            password = "sale",
            confirmPassword = "mandamus",
            verificationAlertState = LoadingState.Loading,
            loadingState = LoadingState.Loading,
        ),
        onEvent = {},
        onAlreadyHaveAccountClicked = {},
    )
}
