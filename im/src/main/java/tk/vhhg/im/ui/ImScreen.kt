package tk.vhhg.im.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ImScreen(modifier: Modifier = Modifier, viewModel: ImViewModel = hiltViewModel<ImViewModel>()) {
    val uiState by viewModel.uiState.collectAsState()
    ImLayout(uiState, viewModel::onEvent, modifier = modifier)
}
