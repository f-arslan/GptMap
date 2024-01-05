package com.espressodev.gptmap.feature.forgot_password

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.NO_SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_MAX_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.DefaultTextField
import com.espressodev.gptmap.core.designsystem.component.GmCircularIndicator
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun ForgotPasswordRoute(
    clearAndNavigateLogin: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsStateWithLifecycle()
    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
    if (loadingState is LoadingState.Loading)
        GmCircularIndicator()
    ForgotPasswordScreen(
        email,
        viewModel::onEmailChange,
        onResetPasswordClick = { viewModel.sendPasswordResetEmail { clearAndNavigateLogin() } },
        backToLoginClick = { clearAndNavigateLogin() }
    )
}

@Composable
fun ForgotPasswordScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onResetPasswordClick: () -> Unit,
    backToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(HIGH_PADDING),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FingerprintIcon()
            Spacer(Modifier.height(HIGH_PADDING))
            Text(
                stringResource(AppText.forgot_password),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(MEDIUM_PADDING))
            Text(stringResource(AppText.forgot_password_instruction), textAlign = TextAlign.Center)
            Spacer(Modifier.height(HIGH_PADDING))
            DefaultTextField(
                email,
                leadingIcon = GmIcons.EmailOutlined,
                label = AppText.email,
                onValueChange = onEmailChange
            )
            Spacer(Modifier.height(HIGH_PADDING))
            Button(onClick = onResetPasswordClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(AppText.reset_password))
            }
            TextButton(onClick = backToLoginClick, modifier = Modifier.imePadding()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        GmIcons.ArrowBackOutlined,
                        contentDescription = stringResource(AppText.back_arrow),
                        modifier = Modifier.size(HIGH_PADDING)
                    )
                    Text(
                        stringResource(AppText.back_login),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun FingerprintIcon() {
    Surface(
        shadowElevation = VERY_SMALL_PADDING,
        modifier = Modifier.size(VERY_MAX_PADDING),
        shape = RoundedCornerShape(MEDIUM_HIGH_PADDING),
        border = BorderStroke(
            NO_SMALL_PADDING,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    ) {
        Icon(
            imageVector = GmIcons.FingerPrintFilled,
            contentDescription = stringResource(AppText.forgot_password),
            modifier = Modifier
                .padding(MEDIUM_PADDING)
                .rotate(15f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPreview() {
    ForgotPasswordScreen(
        email = "chandra.bradley@example.com",
        onEmailChange = {},
        onResetPasswordClick = {},
        backToLoginClick = {}
    )
}
