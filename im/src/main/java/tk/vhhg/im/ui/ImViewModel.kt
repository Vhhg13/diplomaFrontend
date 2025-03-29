package tk.vhhg.im.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import tk.vhhg.im.data.ImRepository
import javax.inject.Inject

@HiltViewModel
class ImViewModel @Inject constructor(private val imRepo: ImRepository) : ViewModel() {
    companion object {
        const val EMPTY_JSON = """
{
 "id": ,
 "heaters": "",
 "coolers": "",
 "volume": ,
 "out": ,
 "k": 1,
 "thermostat": ""
}"""
    }

    val list = mutableStateListOf<String>()

    init {
        viewModelScope.launch {
            list.addAll(imRepo.getIms().map {
                it.replace(",", ",\n ")
                    .replace("}", "\n}")
                    .replace("{", "{\n ")
            })
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
            list[event.index] = event.json
            imRepo.putIm(event.json)
        }
    }

    private fun addNewIm() {
        list.add(EMPTY_JSON)
    }

    private fun deleteIm(index: Int) {
        viewModelScope.launch {
            val o = Json.decodeFromString<JsonObject>(list[index])["id"]!!.jsonPrimitive.int
            imRepo.deleteIm(o)
            list.removeRange(index, index + 1)
        }
    }
}