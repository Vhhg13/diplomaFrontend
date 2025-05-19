package tk.vhhg.im.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import tk.vhhg.im.data.ImRepository
import javax.inject.Inject

@HiltViewModel
class ImViewModel @Inject constructor(private val imRepo: ImRepository) : ViewModel() {
    companion object {
        val EMPTY_MODEL = ImModel()
        val EMPTY_UI_STATE = EMPTY_MODEL.toUiState()
    }

    private val _uiState = MutableStateFlow<List<ImModelUiState>>(emptyList())
    val uiState: StateFlow<List<ImModelUiState>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val models = imRepo.getIms().map { Json.decodeFromString<ImModel>(it) }
            _uiState.value = models.map { it.toUiState() }
        }
    }

    fun onEvent(event: ImEvent) {
        when (event) {
            ImEvent.AddNewImEvent -> addNewIm()
            is ImEvent.SubmitImEvent -> submitIm(event)
            is ImEvent.DeleteImEvent -> deleteIm(event.index)
        }
    }

    private fun submitIm(event: ImEvent.SubmitImEvent) {
        viewModelScope.launch {
            val updatedList = _uiState.value.toMutableList()
            updatedList[event.index] = event.modelUiState
            _uiState.value = updatedList
            val str = Json.encodeToString(event.modelUiState.toImModel())
            Log.d("deb", event.modelUiState.toImModel().toString())
            Log.d("deb", str)
            imRepo.putIm(str)
        }
    }

    private fun addNewIm() {
        val updatedList = _uiState.value.toMutableList()
        updatedList.add(EMPTY_UI_STATE)
        _uiState.value = updatedList
    }

    private fun deleteIm(index: Int) {
        viewModelScope.launch {
            val currentList = _uiState.value
            val id = currentList[index].id.toLongOrNull() ?: return@launch
            imRepo.deleteIm(id.toInt())
            val updatedList = currentList.toMutableList()
            updatedList.removeAt(index)
            _uiState.value = updatedList
        }
    }
}
