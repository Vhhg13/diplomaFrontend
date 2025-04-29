package tk.vhhg.rooms.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import tk.vhhg.data.dto.Room
import tk.vhhg.rooms.R
import tk.vhhg.theme.HvacAppTheme
import kotlin.random.Random

@Composable
fun CommonDialog(
    title: String,
    room: Room?,
    close: () -> Unit,
    onSave: (String, String, String) -> Boolean,
    onChange: () -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier,
    nameSupportingText: String? = null,
    volSupportingText: String? = null,
    colorSupportingText: String? = null,
    allowDeletion: Boolean
) {
    Dialog(onDismissRequest = close) {
        Card(modifier.background(Color.Transparent), RoundedCornerShape(28.dp)) {
            Column(Modifier.padding(24.dp)) {
                Text(title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(24.dp))

                val roomName = remember { mutableStateOf(room?.name ?: "") }
                TextFieldWithState(
                    R.string.name_of_room,
                    roomName,
                    nameSupportingText,
                    onChange
                )

                val roomVol = remember { mutableStateOf(room?.volume?.toString() ?: "") }
                TextFieldWithState(
                    R.string.vol_of_room,
                    roomVol,
                    volSupportingText,
                    onChange,
                    Modifier.padding(vertical = 16.dp)
                )

                val color = remember { mutableStateOf(room?.color ?: generateRandomSoftColorHex()) }
                Row(verticalAlignment = Alignment.Top) {
                    Canvas(
                        Modifier
                            .padding(2.dp)
                            .size(48.dp, 48.dp)
                            .clickable {
                                color.value = generateRandomSoftColorHex()
                            }
                    ) { drawCircle(colorFrom(color, room)) }
                    Spacer(Modifier.width(24.dp))
                    TextFieldWithState(
                        R.string.choose_color,
                        color,
                        colorSupportingText,
                        onChange,
                        kbAction = ImeAction.Done
                    )
                }

                if (allowDeletion) DeletionRow(close) { onDelete(room!!.id) }

                CancelSaveRow(close) {
                    if (onSave(roomName.value, roomVol.value, color.value)) close()
                }
            }
        }
    }
}

private fun colorFrom(color: MutableState<String>, room: Room?): Color =
    try {
        color.value.toColorInt()
    } catch (e: IllegalArgumentException) {
        room?.color?.toColorInt() ?: 0xFFFF0000.toColorInt()
    }.let {
        Color(it)
    }

@Preview
@Composable
fun AddRoomDialogPreview(modifier: Modifier = Modifier) {
    HvacAppTheme {
        CommonDialog(
            stringResource(R.string.add_room),
            null, {}, {_,_,_-> false}, {},
            {}, modifier,null, null,
            allowDeletion = false
        )
    }
}

@Preview
@Composable
fun EditRoomDialogPreview(modifier: Modifier = Modifier) {
    HvacAppTheme {
        CommonDialog(
            stringResource(R.string.change_parameters),
            null, {}, {_,_,_-> false}, {},
            {}, modifier, null, null,
            allowDeletion = true
        )
    }
}

private fun generateRandomSoftColorHex(): String {
    val hue = Random.nextFloat() * 360f
    val saturation = 0.15f + Random.nextFloat() * 0.5f
    val lightness = 0.55f + Random.nextFloat() * 0.4f
    val alpha = 255

    val colorInt = ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness))
    val argbColor = (alpha shl 24) or (colorInt and 0x00FFFFFF)
    return String.format("#%08X", argbColor)
}