package tk.vhhg.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import tk.vhhg.rooms.composables.CommonDialog
import tk.vhhg.rooms.composables.PagerDots
import tk.vhhg.rooms.composables.PlaceholderComponent

@Composable
fun RoomsScreen(
    roomDialog: MutableState<RoomDialogEnum?>,
    navigateToRoom: (Long, String) -> Unit,
    onChangeColor: (Color) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomsViewModel = hiltViewModel(),
    textColor: Color = MaterialTheme.colorScheme.onSurface,
){
    LaunchedEffect(Unit) { viewModel.getData() }
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState { uiState.pageCount }
    PagerDots(textColor, pagerState.settledPage, uiState.pageCount)
    HorizontalPager(pagerState, modifier
        .fillMaxSize()
        .background(Color.Transparent)) { page: Int ->
        val room = uiState.rooms.getOrNull(page) ?: return@HorizontalPager
        if (pagerState.settledPage == page) onChangeColor(Color(room.color.toColorInt()))
        Column(
            Modifier
                .fillMaxSize()
                .clickable { navigateToRoom(room.id, room.name) },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(room.name, color = textColor, style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
        }
    }

    if (uiState.pageCount == 0) PlaceholderComponent(uiState.isLoading, onChangeColor)

    if (roomDialog.value == RoomDialogEnum.EDIT && uiState.pageCount == 0) roomDialog.value = null
    roomDialog.value?.let { enumValue ->
        CommonDialog(
            title = stringResource(enumValue.str),
            room = if (enumValue == RoomDialogEnum.EDIT) uiState.rooms.getOrNull(pagerState.settledPage) else null,
            close = {
                roomDialog.value = null
                viewModel.onChange()
            },
            onSave = { name, vol, color ->
                when (enumValue) {
                    RoomDialogEnum.ADD -> viewModel.add(name, vol, color)
                    RoomDialogEnum.EDIT -> viewModel.update(pagerState.settledPage, name, vol, color)
                }
            },
            onChange = viewModel::onChange,
            onDelete = viewModel::onDelete,
            nameSupportingText = if (uiState.needsNameSupportingText) stringResource(R.string.name_not_blank) else null,
            volSupportingText = if (uiState.needsVolSupportingText) stringResource(R.string.invalid_vol) else null,
            colorSupportingText = if (uiState.needsColorSupportingText) stringResource(R.string.invalid_color) else null,
            allowDeletion = enumValue == RoomDialogEnum.EDIT
        )
    }
}