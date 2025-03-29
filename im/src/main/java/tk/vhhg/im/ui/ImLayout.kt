package tk.vhhg.im.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tk.vhhg.im.R
import tk.vhhg.theme.HvacAppTheme

@Composable
fun ImLayout(list: List<String>, onEvent: (ImEvent) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        itemsIndexed(list) { index, item ->
            val currentText = remember { mutableStateOf(item) }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()) {
                    BasicTextField(modifier = Modifier.weight(1f), value = currentText.value, onValueChange = {
                        currentText.value = it
                    }, textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = dimensionResource(R.dimen.json_size).value.sp, fontFamily = FontFamily(Font(R.font.ubuntu_mono))))
                    Column {
                        IconButton(
                            onClick = {
                                onEvent(ImEvent.SubmitImEvent(index, currentText.value))
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Submit Im"
                            )
                        }
                        IconButton(
                            onClick = {
                                onEvent(ImEvent.DeleteImEvent(index))
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Im"
                            )
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(8.dp))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { onEvent(ImEvent.AddNewImEvent) }) {
                    Text(stringResource(R.string.add_new))
                }
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
fun ImLayoutPreview() {
    HvacAppTheme {
        Scaffold {
            ImLayout(listOf(
                stringResource(R.string.json_example),
                stringResource(R.string.json_example),
            ), {}, modifier = Modifier.padding(it).fillMaxSize())
        }
    }
}