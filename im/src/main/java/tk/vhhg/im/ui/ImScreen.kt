package tk.vhhg.im.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ImScreen(modifier: Modifier = Modifier, viewModel: ImViewModel = hiltViewModel<ImViewModel>()) {
    val list = viewModel.list
    ImLayout(list, viewModel::onEvent, modifier = modifier)
}