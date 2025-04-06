package tk.vhhg.rooms.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction

@Composable
fun TextFieldWithState(
    @StringRes label: Int,
    state: MutableState<String>,
    supportingText: String?,
    onChange: () -> Unit,
    modifier: Modifier = Modifier,
    kbAction: ImeAction? = null,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(singleLine = true,
        value = state.value,
        modifier = modifier,
        supportingText = { supportingText?.let { Text(it) } },
        label = { Text(stringResource(label)) },
        onValueChange = {
            onChange()
            state.value = it
        },
        keyboardActions = KeyboardActions {
            kbAction?.let { defaultKeyboardAction(it) }
            focusManager.moveFocus(FocusDirection.Down)
        })
}