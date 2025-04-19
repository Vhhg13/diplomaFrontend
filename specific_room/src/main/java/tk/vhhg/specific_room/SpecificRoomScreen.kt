package tk.vhhg.specific_room

import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import tk.vhhg.data.dto.Device
import tk.vhhg.data.dto.DeviceType
import tk.vhhg.knob.CelsiusKnob
import tk.vhhg.theme.HvacAppTheme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

private val LocalSdf = compositionLocalOf { SimpleDateFormat("hh:mm a", Locale.ROOT) }

@Composable
fun SpecificRoomScreen(
    roomId: Long,
    navigateToDevice: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpecificRoomViewModel =
        hiltViewModel<SpecificRoomViewModel, SpecificRoomViewModel.Factory> { it.create(roomId) },
) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState.isLoading) {
        LoadingLayout(modifier)
    } else {
        SpecificRoomLayout(uiState, viewModel::onEvent, navigateToDevice, modifier)
    }
}

@Preview
@Composable
private fun ScreenPreview() {
    HvacAppTheme {
        Surface {
            SpecificRoomLayout(UiState(
                isLoading = false,
                currentTemp = 18F,
                targetTemp = 23F,
                deadline = System.currentTimeMillis() + 3600000,
                //deadline = null,
                devices = listOf(
                    Device(1, "Конд", DeviceType.COND, 1, null, "", maxPower = 100F),
                    Device(1, "Грей", DeviceType.HEAT, 1, null, "", maxPower = 10F),
                    Device(1, "Темпо", DeviceType.TEMP, 1, null, "", maxPower = 5F)
                ),
                scriptCode = "",
            ), {}, {})
        }
    }
}

@Composable
fun SpecificRoomLayout(
    uiState: UiState,
    onEvent: (UiEvent) -> Unit,
    navigateToDevice: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) { onEvent(UiEvent.UpdateDataEvent) }
    Column(modifier.verticalScroll(rememberScrollState())) {
        if (uiState.currentTemp == null) {
            CelsiusKnob(0F, {}, modifier, 0F) { _, _ ->
                Text(stringResource(R.string.no_thermostat))
            }
        } else {
            CelsiusKnob(
                current = uiState.currentTemp,
                target = uiState.targetTemp ?: uiState.currentTemp,
                setTargetPosition = { celsius ->
                    onEvent(UiEvent.SetTargetTemp(celsius))
                }
            ) { c, t ->
                Column {
                    Text("$c °C", fontSize = 50.sp)
                    AnimatedVisibility(t != c) {
                        if (t < c) {
                            Text(stringResource(R.string.cooling_to, t))
                        } else if (c < t) {
                            Text(stringResource(R.string.heating_to, t))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(Modifier.padding(horizontal = 26.dp))
        uiState.currentTemp?.let {
            HeatingCoolingComponent(
                uiState.currentTemp,
                uiState.targetTemp,
                uiState.deadline,
                onEvent
            )
        }
        DevicesAndScripts(
            script = uiState.scriptCode,
            devices = uiState.devices,
            onEvent = onEvent,
            navigateToDevice = navigateToDevice
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.DevicesAndScripts(
    script: String,
    devices: List<Device>,
    onEvent: (UiEvent) -> Unit,
    navigateToDevice: (Long) -> Unit,
) {
    var areDevicesOpen by remember { mutableStateOf(true) }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .clickable { areDevicesOpen = !areDevicesOpen }
    ) {
        val icon =
            with(Icons.Default) { if (areDevicesOpen) KeyboardArrowUp else KeyboardArrowDown }
        Icon(icon, null)
        Spacer(Modifier.width(16.dp))
        Text(stringResource(R.string.devices_and_scripts), style = MaterialTheme.typography.titleLarge)
    }
    val haptics = LocalHapticFeedback.current
    AnimatedVisibility(areDevicesOpen) {
        Column {
            for(device in devices) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 26.dp)
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                //onEvent(UiEvent.OpenDeviceEvent(device.id))
                                navigateToDevice(device.id)
                            },
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onEvent(UiEvent.DeleteEvent(device.id))
                            },
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (device.type) {
                        DeviceType.COND -> R.drawable.baseline_ac_unit_24
                        DeviceType.TEMP -> R.drawable.baseline_device_thermostat_24
                        DeviceType.HEAT -> R.drawable.heat
                    }
                    Icon(painterResource(icon), null, Modifier.padding(16.dp))
                    Text(device.name)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .padding(horizontal = 26.dp)
                .fillMaxWidth()
                .clickable {
                    //onEvent(UiEvent.AddNewDeviceEvent)
                    navigateToDevice(Device.NONEXISTENT_DEVICE_ID)
                }) {
                Icon(Icons.Default.Add, null, Modifier.padding(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.add_device), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
//            var code by remember { mutableStateOf(script) }
//            Card(Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 32.dp, vertical = 16.dp), RoundedCornerShape(16.dp)) {
//                BasicTextField(
//                    value = code,
//                    onValueChange = { code = it },
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth(),
//                    textStyle = TextStyle(
//                        color = MaterialTheme.colorScheme.onSurface,
//                        fontFamily = FontFamily(Font(R.font.jb_mono))
//                    )
//                )
//            }
//            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//                Button(onClick = {
//                    onEvent(UiEvent.SaveScriptEvent(code))
//                }, enabled = code != script) {
//                    Text(stringResource(R.string.save))
//                }
//            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatingCoolingComponent(
    currentTemp: Float,
    targetTemp: Float?,
    deadline: Long?,
    onEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isTimeInputShown by remember { mutableStateOf(false) }
    val currentTime = Instant.now().atZone(ZoneId.of("Europe/Moscow"))// TODO("remember {}") ?
    val timeInputState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = false
    )
    if (isTimeInputShown) TimeInputDialog(
        timeInputState,
        onOkRequest = {
            onEvent(UiEvent.SetDeadlineEvent(todayAt(timeInputState.hour, timeInputState.minute)))
            isTimeInputShown = false
        },
        onDismissRequest = {
            isTimeInputShown = false
        }
    )
//    AnimatedVisibility(visible = targetTemp?.let { it != currentTemp } ?: false) {
        Column(modifier.fillMaxWidth()) {
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
            ) {
                val strRes =
                    if (currentTemp > (targetTemp ?: 0F)) R.string.specify_cool_deadline
                    else R.string.specify_heat_deadline
                Text(stringResource(strRes), style = MaterialTheme.typography.titleLarge)
                Switch(checked = deadline != null, onCheckedChange = {
                    if (it) isTimeInputShown = true
                    else onEvent(UiEvent.SetDeadlineEvent(null))
                })
            }
            Spacer(Modifier.height(16.dp))
            val sdf = LocalSdf.current
            OutlinedTextField(
                readOnly = true,
                leadingIcon = {
                    Icon(painterResource(R.drawable.baseline_access_time_24), null)
                },
                enabled = deadline != null,
                value = sdf.format(deadline ?: System.currentTimeMillis()),
                onValueChange = {},
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button({ onEvent(UiEvent.SaveRegimeEvent) }) {
                    Text(stringResource(R.string.save))
                }
                Spacer(Modifier.width(32.dp))
                Button(onClick = { onEvent(UiEvent.ClearEvent) }, enabled = deadline != null || targetTemp != null) {
                    Text(stringResource(R.string.cancel))
                }
            }
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(Modifier.padding(horizontal = 26.dp))
        }
//    }
}

@Composable
fun LoadingLayout(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) { CircularProgressIndicator() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputDialog(timeInputState: TimePickerState, onOkRequest: () -> Unit, onDismissRequest: () -> Unit, modifier: Modifier = Modifier) {
    Dialog(onDismissRequest) {
        Card(modifier, RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(24.dp)) {
                Text(stringResource(R.string.enter_time), style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(20.dp))
                TimeInput(timeInputState, modifier = Modifier.fillMaxWidth())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.cancel)) }
                    TextButton(onClick = onOkRequest) { Text(stringResource(R.string.OK)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TimeInputDialogPreview() {
    val currentTime = Instant.now().atZone(ZoneId.of("Europe/Moscow"))// TODO("remember {}")
    val timeInputState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = false
    )
    HvacAppTheme { TimeInputDialog(timeInputState, {}, {}) }
}

private fun todayAt(hour: Int, minute: Int): Long {
    return Instant.now().atZone(ZoneId.of("Europe/Moscow")).truncatedTo(ChronoUnit.DAYS)
        .plusHours(hour.toLong())
        .plusMinutes(minute.toLong())
        .toEpochSecond()*1000
}