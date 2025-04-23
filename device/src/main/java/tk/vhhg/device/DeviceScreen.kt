package tk.vhhg.device

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tk.vhhg.data.dto.Device
import tk.vhhg.data.dto.DeviceType
import tk.vhhg.theme.HvacAppTheme

@Composable
fun DeviceScreen(
    deviceId: Long,
    roomId: Long,
    roomName: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeviceScreenViewModel =
        hiltViewModel<DeviceScreenViewModel, DeviceScreenViewModel.Factory>{
            it.create(roomId, deviceId, roomName)
        },
) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState.isLoading) {
        LoadingLayout()
    } else {
        DeviceLayout(uiState, viewModel::onEvent, navigateBack, modifier)
    }
}

@Composable
fun DeviceLayout(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(dimensionResource(R.dimen.wattage_top_padding)))
        Text(
            stringResource(R.string.watts, uiState.currentWattage.toInt()),
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )

        AnimatedVisibility(uiState.maxPower.toFloatOrNull() != null) {
            uiState.maxPower.toFloatOrNull()?.let {
                Slider(
                    valueRange = 0F..it,
                    modifier = Modifier.padding(32.dp),
                    value = uiState.currentWattage,
                    onValueChange = { wattage ->
                        onEvent(UiEvent.SetWattageEvent(wattage))
                    }
                )
            }
        }


        OutlinedTextField(
            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth(),
            value = uiState.roomName,
            onValueChange = {},
            label = { Text(stringResource(R.string.room)) },
            enabled = false
        )

        Spacer(Modifier.height(32.dp))


        Row(
            Modifier.padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val deviceType = uiState.device.type
            val deviceIcon = when (deviceType){
                DeviceType.COND -> painterResource(R.drawable.baseline_ac_unit_24)
                DeviceType.TEMP -> painterResource(R.drawable.baseline_device_thermostat_24)
                DeviceType.HEAT -> painterResource(R.drawable.heat)
            }
            Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Icon(deviceIcon, null, Modifier.padding(top = 8.dp).clickable {
                    onEvent(UiEvent.SwitchDeviceTypeEvent)
                })
            }
            Spacer(Modifier.width(16.dp))
            OutlinedTextField(
                singleLine = true,
                keyboardActions = KeyboardActions { defaultKeyboardAction(ImeAction.Next) },
                modifier = Modifier.fillMaxWidth(),
                value = uiState.device.name,
                onValueChange = { onEvent(UiEvent.OnDeviceNameChangedEvent(it)) },
                label = { Text(stringResource(R.string.device_name)) }
            )
        }
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            singleLine = true,
            modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth(),
            keyboardActions = KeyboardActions { defaultKeyboardAction(ImeAction.Next) },
            value = uiState.maxPower,
            onValueChange = { onEvent(UiEvent.ChangeMaxPowerEvent(it)) },
            label = { Text(stringResource(R.string.max_power)) },
            enabled = uiState.device.type != DeviceType.TEMP
        )


        Spacer(Modifier.height(32.dp))
        Row(
            Modifier.padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var topicFieldSelection by remember { mutableStateOf(TextRange.Zero) }
            OutlinedTextField(
                keyboardActions = KeyboardActions { defaultKeyboardAction(ImeAction.Done) },
                modifier = Modifier.weight(1F),
                value = TextFieldValue(uiState.device.topic, topicFieldSelection),
                onValueChange = {
                    topicFieldSelection = it.selection
                    onEvent(UiEvent.OnTopicChangedEvent(it.text))
                },
                label = { Text(stringResource(R.string.device_topic)) }
            )
            Spacer(Modifier.width(16.dp))
            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    topicFieldSelection = TextRange(uiState.device.topic.length+1)
                    onEvent(UiEvent.OnTopicChangedEvent(uiState.device.topic + "/"))
                }
            ) { Text("/") }
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = {
            onEvent(UiEvent.OnSaveDeviceEvent)
            navigateBack()
        }) {
            Icon(Icons.Default.Check, null)
            Text(stringResource(R.string.save))
        }

        val onSurface = MaterialTheme.colorScheme.onSurface
//        Canvas(Modifier
//            .padding(32.dp)
//            .fillMaxWidth()
//            .height(dimensionResource(R.dimen.placeholder_graph_height))) {
//            drawLine(onSurface, Offset.Zero, Offset(0F, size.height), strokeWidth = 5F)
//            drawLine(onSurface, Offset(0F, size.height), Offset(size.width, size.height), strokeWidth = 5F)
//            drawLine(onSurface, Offset(50F, size.height-50), Offset(size.height-50F, 50F), strokeWidth = 5F)
//        }
    }
}

@Composable
fun LoadingLayout(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
private fun DeviceScreenPreview() {
    HvacAppTheme {
        Surface {
            DeviceLayout(
                modifier = Modifier.fillMaxSize(),
                uiState = UiState(
                    device = Device(
                        id = 0,
                        name = "Device name",
                        type = DeviceType.COND,
                        roomId = 0,
                        historicData = emptyList(),
                        topic = "Device topic",
                        maxPower = 500F
                    ),
                    currentWattage = 500F,
                    isLoading = false,
                    roomName = "Room name",
                ), onEvent = {_ ->}, navigateBack = {}
            )
        }
    }
}