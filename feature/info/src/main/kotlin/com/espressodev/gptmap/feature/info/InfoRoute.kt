package com.espressodev.gptmap.feature.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.espressodev.gptmap.core.designsystem.Constants.GITHUB_DESC
import com.espressodev.gptmap.core.designsystem.Constants.GITHUB_LINK
import com.espressodev.gptmap.core.designsystem.Constants.PRIVACY_POLICY
import com.espressodev.gptmap.core.designsystem.Constants.PRIVACY_POLICY_LINK
import com.espressodev.gptmap.core.designsystem.Constants.PRIVACY_TERM_CONDITIONS
import com.espressodev.gptmap.core.designsystem.Constants.TERMS_CONDITIONS
import com.espressodev.gptmap.core.designsystem.Constants.TERMS_CONDITIONS_LINK
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.HyperlinkText
import com.espressodev.gptmap.core.designsystem.theme.Pacifico
import kotlinx.collections.immutable.toPersistentMap
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoRoute(popUp: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            GmTopAppBar(
                text = TextType.Res(AppText.learn_more),
                icon = IconType.Vector(GmIcons.InfoOutlined),
                onBackClick = popUp
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(AppDrawable.app_icon_inner),
                        contentDescription = stringResource(AppText.logo),
                        modifier = Modifier.size(96.dp),
                    )
                    Text(
                        text = stringResource(AppText.app_name),
                        style = MaterialTheme.typography.displayLarge,
                        fontFamily = Pacifico,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Text(
                    text = stringResource(AppText.info_app_slogan),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(AppText.info_app_description),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))
                HyperlinkText(
                    fullText = GITHUB_DESC,
                    mapOf(GITHUB_DESC to GITHUB_LINK).toPersistentMap()
                )
            }
            Spacer(Modifier)
            Spacer(Modifier)
            HyperlinkText(
                fullText = PRIVACY_TERM_CONDITIONS,
                hyperLinks = mapOf(
                    PRIVACY_POLICY to PRIVACY_POLICY_LINK,
                    TERMS_CONDITIONS to TERMS_CONDITIONS_LINK
                ).toPersistentMap()
            )
        }
    }
}
