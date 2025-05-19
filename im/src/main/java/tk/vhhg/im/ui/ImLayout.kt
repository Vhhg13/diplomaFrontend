package tk.vhhg.im.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import tk.vhhg.im.R
import tk.vhhg.theme.HvacAppTheme

private val UbuntuFontFamily = FontFamily(Font(R.font.ubuntu_mono))

@Serializable
data class ImModel @OptIn(ExperimentalSerializationApi::class) constructor(
    var id: Long = 0,
    var heaters: String = "",
    var coolers: String = "",
    var volume: Double = 0.0,
    var out: Double = 0.0,
    @EncodeDefault
    var k: Double = 0.01,
    var thermostat: String = ""
)

data class ImModelUiState(
    val id: String = "",
    val heaters: String = "",
    val coolers: String = "",
    val volume: String = "",
    val out: String = "",
    val k: String = "",
    val thermostat: String = ""
)

fun ImModel.toUiState(): ImModelUiState = ImModelUiState(
    id = id.toString(),
    heaters = heaters,
    coolers = coolers,
    volume = volume.toString(),
    out = out.toString(),
    k = k.toString(),
    thermostat = thermostat
)

fun ImModelUiState.toImModel(): ImModel = ImModel(
    id = id.toLongOrNull() ?: 0L,
    heaters = heaters,
    coolers = coolers,
    volume = volume.toDoubleOrNull() ?: 0.0,
    out = out.toDoubleOrNull() ?: 0.0,
    k = k.toDoubleOrNull() ?: 0.01,
    thermostat = thermostat
)

@Composable
fun ImLayout(
    list: List<ImModelUiState>,
    onEvent: (ImEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        itemsIndexed(list) { index, uiState ->
            var localUiState by remember { mutableStateOf(uiState) }
            Column {
                ImModelConfigTable(
                    uiState = localUiState,
                    onUiStateChange = { newUiState -> localUiState = newUiState },
                    modifier = Modifier.padding(8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    IconButton(
                        onClick = {
                            onEvent(ImEvent.SubmitImEvent(index, localUiState))
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
                HorizontalDivider(Modifier.padding(8.dp))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { onEvent(ImEvent.AddNewImEvent) }) {
                    Text("Add New", fontFamily = UbuntuFontFamily)
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
            ImLayout(
                listOf(
                    ImModelUiState(),
                ), {}, modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun ImModelConfigTable(
    uiState: ImModelUiState,
    onUiStateChange: (ImModelUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TableRow("id", uiState.id, onValueChange = { onUiStateChange(uiState.copy(id = it)) })
        HorizontalDivider()
        TableRow(
            "heaters",
            uiState.heaters,
            onValueChange = { onUiStateChange(uiState.copy(heaters = it)) })
        HorizontalDivider()
        TableRow(
            "coolers",
            uiState.coolers,
            onValueChange = { onUiStateChange(uiState.copy(coolers = it)) })
        HorizontalDivider()
        TableRow(
            "volume",
            uiState.volume,
            onValueChange = { onUiStateChange(uiState.copy(volume = it)) })
        HorizontalDivider()
        TableRow("out", uiState.out, onValueChange = { onUiStateChange(uiState.copy(out = it)) })
        HorizontalDivider()
        TableRow("k", uiState.k, onValueChange = { onUiStateChange(uiState.copy(k = it)) })
        HorizontalDivider()
        TableRow(
            "thermostat",
            uiState.thermostat,
            onValueChange = { onUiStateChange(uiState.copy(thermostat = it)) })
    }
}

@Composable
private fun TableRow(
    property: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = property,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = UbuntuFontFamily
            )
        )
        VerticalDivider(
            modifier = Modifier
                .width(1.dp)
                .height(42.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = UbuntuFontFamily
            )
        )
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray
) {
    Box(
        modifier
            .background(color = color)
    )
}
