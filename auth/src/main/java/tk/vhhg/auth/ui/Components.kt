package tk.vhhg.auth.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import tk.vhhg.auth.R

@Composable
internal fun LoginTextField(
    kbAction: ImeAction?,
    state: MutableState<String>,
    label: String,
    onEvent: (UiEvent) -> Unit,
    errorText: String?,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        singleLine = true,
        keyboardActions = KeyboardActions {
            kbAction?.let { defaultKeyboardAction(it) }
            focusManager.moveFocus(FocusDirection.Down)
        },
        isError = errorText != null,
        label = { Text(label) },
        supportingText = { errorText?.let { Text(it) } },
        value = state.value,
        onValueChange = {
            state.value = it
            onEvent(UiEvent.FieldsChangedEvent)
        },
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginSegmentedButton(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier) {
        val isLoggingIn = uiState.isLoggingIn
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(0, 2),
            selected = isLoggingIn,
            onClick = { onEvent(UiEvent.SwitchEvent) },
        ) {
            Text(
                text = stringResource(R.string.log_in),
                style = MaterialTheme.typography.labelLarge
            )
        }
        SegmentedButton(
            shape = SegmentedButtonDefaults.itemShape(1, 2),
            selected = !isLoggingIn,
            onClick = { onEvent(UiEvent.SwitchEvent) },
        ) {
            Text(
                text = stringResource(R.string.register),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

