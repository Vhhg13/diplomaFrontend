package tk.vhhg.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tk.vhhg.auth.R

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel<LoginViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LoginLayout(uiState, viewModel::onEvent, modifier)
}

@Composable
fun LoginLayout(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.End) {
        Spacer(Modifier.height(dimensionResource(R.dimen.login_padding)))
        Text(
            text = stringResource(R.string.login_or_register),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center
        )

        LoginSegmentedButton(
            uiState, onEvent, Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        val login = remember { mutableStateOf("") }
        LoginTextField(
            kbAction = null,
            errorText = if (uiState.error == UiError.USERNAME_TAKEN) stringResource(R.string.username_taken) else null,
            label = stringResource(R.string.login),
            state = login,
            onEvent = onEvent
        )

        val password = remember { mutableStateOf("") }
        LoginTextField(
            kbAction = if (uiState.isLoggingIn) ImeAction.Done else null,
            errorText = if (uiState.error == UiError.WRONG_CREDENTIALS) stringResource(R.string.wrong_creds) else null,
            label = stringResource(R.string.password),
            state = password,
            onEvent = onEvent
        )

        val password2 = remember { mutableStateOf("") }
        AnimatedVisibility(visible = !uiState.isLoggingIn) {
            LoginTextField(
                kbAction = ImeAction.Done,
                errorText = if (uiState.error == UiError.PASSWORDS_DO_NOT_MATCH) stringResource(R.string.passwords_do_not_match) else null,
                label = stringResource(R.string.repeat_password),
                state = password2,
                onEvent = onEvent
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(Modifier.padding(end = 16.dp))
        } else {
            FilledIconButton(onClick = {
                onEvent(UiEvent.SubmitEvent(login.value, password.value, password2.value))
            }, modifier = Modifier.padding(end = 16.dp)) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Submit")
            }
        }
    }
}