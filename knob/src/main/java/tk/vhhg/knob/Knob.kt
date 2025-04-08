package tk.vhhg.knob

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tk.vhhg.theme.HvacAppTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sin

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun KnobPreview(modifier: Modifier = Modifier) {
    HvacAppTheme {
        Surface {
            TKnob(
                depth = 30F,
                strokeWidth = 5F,
                step = 2,
                targetPosition = 20,
                currentPosition = 50,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                isTickSpecial = { it % 45 == 0 },
                setTargetPosition = {}
            ) { from, to ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("28 °C", fontSize = 50.sp)
                    Text("Охлаждение до 20 °C")
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun KnobAppIcon(modifier: Modifier = Modifier) {
    HvacAppTheme {
        Surface {
            TKnob(
                depth = 50F,
                strokeWidth = 20F,
                step = 30,
                targetPosition = 0,
                currentPosition = 0,
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxWidth(),
                isTickSpecial = { it % 45 == 0 },
                setTargetPosition = {}
            ) { from, to ->
                Text("42", fontSize = 100.sp)
            }
        }
    }
}


@Composable
fun TKnob(
    depth: Float,
    strokeWidth: Float,
    targetPosition: Int,
    setTargetPosition: (Int) -> Unit,
    modifier: Modifier = Modifier,
    currentPosition: Int = targetPosition,
    isTickSpecial: (Int) -> Boolean = { false },
    step: Int = 3,
    normalTickColor: Color = MaterialTheme.colorScheme.onSurface,
    specialTickColor: Color = MaterialTheme.colorScheme.errorContainer,
    pointerColor: Color = MaterialTheme.colorScheme.onSurface,
    heatingColor: Color = Color.Red,
    coolingColor: Color = Color.Blue,
    content: @Composable (Int, Int) -> Unit
) {
    BoxWithConstraints(contentAlignment = Alignment.Center, modifier = modifier.aspectRatio(1F)) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        Canvas(Modifier
            .background(Color.Transparent)
            .fillMaxSize().pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val pos = event.changes.first().position
                        val alpha = (
                                atan((pos.x - width/2.0)/(height/2.0 - pos.y)) +
                                        when {
                                            pos.x < width/2.0 && pos.y < height/2.0 -> 2*PI
                                            pos.y > height/2.0 -> PI
                                            else -> 0.0
                                        }
                                )*180/ PI
                        Log.d("alpha", alpha.toString())
                        setTargetPosition(alpha.toInt())
                        event.changes.forEach { it.consume() }
                    }
                }
            }) {
            val R = size.width / 2F
            val r = R - depth
            (0..360 step step).forEach { deg ->
                val alpha = deg * PI.toFloat() / 180F
                val sin = sin(alpha)
                val cos = cos(alpha)
                drawLine(
                    start = Offset(R + r * sin, R - r * cos),
                    end = Offset(R + R * sin, R - R * cos),
                    color = if (isTickSpecial(deg)) specialTickColor else normalTickColor,
                    strokeWidth = strokeWidth
                )
            }
            val startOffset = Offset(R, 2 * depth)
            val pivot = Offset(R, R)
            rotate(targetPosition.toFloat(), pivot = pivot) {
                drawLine(
                    start = startOffset,
                    end = startOffset + Offset(depth, depth),
                    color = pointerColor,
                    strokeWidth = strokeWidth
                )
                drawLine(
                    start = startOffset,
                    end = startOffset + Offset(-depth, depth),
                    color = pointerColor,
                    strokeWidth = strokeWidth
                )
            }
            val startPointerRadians = currentPosition*PI.toFloat()/180F
            drawLine(
                start = Offset(R + r * sin(startPointerRadians), R - r * cos(startPointerRadians)),
                end = Offset(R + R * sin(startPointerRadians), R - R * cos(startPointerRadians)),
                color = if (isTickSpecial(currentPosition)) specialTickColor else normalTickColor,
                strokeWidth = strokeWidth
            )
            drawArc(
                color = if (targetPosition > currentPosition) heatingColor else coolingColor,
                startAngle = currentPosition-90F,
                sweepAngle = targetPosition-currentPosition.toFloat(),
                useCenter = false,
                alpha = 0.8F,
                topLeft = Offset(depth/2, depth/2),
                size = Size(r+R, r+R),
                style = Stroke(width = depth, cap = StrokeCap.Butt)
            )
        }
        content(currentPosition, targetPosition)
    }
}

@Composable
fun CelsiusKnob(current: Float, setTargetPosition: (Float) -> Unit, modifier: Modifier = Modifier, target: Float = current, minValue: Float = 0F, maxValue: Float = 42F, content: @Composable (Float, Float) -> Unit) {
    fun position(temp: Float): Int = (((temp-minValue)/maxValue) * 360).roundToInt()
    TKnob(
        depth = 30F,
        strokeWidth = 5F,
        step = 2,
        targetPosition = position(target),
        currentPosition = position(current),
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        isTickSpecial = { it % 45 == 0 },
        setTargetPosition = { deg ->
            setTargetPosition(
                round((deg.toFloat()/360F*maxValue + minValue)*10)/10F
            )
        }
    ) { _, _ ->
        content(current, target)
    }
}

@Preview
@Composable
fun CelsiusPreview(modifier: Modifier = Modifier) {
    HvacAppTheme {
        Surface {
            CelsiusKnob(
                current = 18F,
                target = 28F,
                setTargetPosition = {}
            ) { curr, target ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$curr °C", fontSize = 50.sp)
                    Text("Охлаждение до $target °C")
                }
            }
        }
    }
}