package tk.vhhg.auth.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import tk.vhhg.theme.HvacAppTheme

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
private fun LoginPreview() {
    HvacAppTheme {
        Surface {
            LoginLayout(UiState(isLoggingIn = true), {}, Modifier.fillMaxSize())
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
private fun RegisterPreview() {
    HvacAppTheme {
        Surface {
            LoginLayout(UiState(isLoggingIn = false), {}, Modifier.fillMaxSize())
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
private fun WrongCredentialsPreview() {
    HvacAppTheme {
        Surface {
            LoginLayout(
                UiState(isLoggingIn = true, error = UiError.WRONG_CREDENTIALS),
                {},
                Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
private fun NoMatchPreview() {
    HvacAppTheme {
        Surface {
            LoginLayout(
                UiState(isLoggingIn = false, error = UiError.PASSWORDS_DO_NOT_MATCH),
                {},
                Modifier.fillMaxSize()
            )
        }
    }
}