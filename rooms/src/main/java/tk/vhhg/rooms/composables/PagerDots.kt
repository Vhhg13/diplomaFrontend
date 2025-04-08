package tk.vhhg.rooms.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PagerDots(textColor: Color, currentPage: Int, pageCount: Int, modifier: Modifier = Modifier) {
    if (pageCount == 1) return
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        val lineHeight = MaterialTheme.typography.displaySmall.lineHeight.value
        Spacer(Modifier.height(lineHeight.dp))
        Spacer(Modifier.height(lineHeight.dp))
        Spacer(Modifier.height(lineHeight.dp))
        Canvas(Modifier.size(height = lineHeight.dp, width = (pageCount * lineHeight/2).dp)) {
            repeat(pageCount) { page ->
                drawCircle(
                    color = textColor,
                    center = Offset(size.width.div(pageCount*2) + page * size.width.div(pageCount), size.height / 2),
                    radius = if (page == currentPage) lineHeight/3 else lineHeight/5
                )
            }
        }
    }
}