package tk.vhhg.rooms.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tk.vhhg.rooms.R

@Composable
fun CancelSaveRow(close: () -> Unit, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(horizontalArrangement = Arrangement.End, modifier = modifier.fillMaxWidth()) {
        TextButton(close) { Text(stringResource(R.string.cancel)) }
        FilledTonalButton(onClick) { Text(stringResource(R.string.save)) }
    }
}