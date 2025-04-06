package tk.vhhg.rooms.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tk.vhhg.rooms.R

@Composable
fun DeletionRow(close: () -> Unit, modifier: Modifier = Modifier, onDelete: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.clickable {
        onDelete()
        close()
    }) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp, 48.dp)
        ) {
            Icon(
                Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error
            )
        }
        Spacer(Modifier.width(24.dp))
        Text(
            stringResource(R.string.delete_room), color = MaterialTheme.colorScheme.error
        )
    }
}