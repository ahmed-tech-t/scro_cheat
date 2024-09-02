package com.rashed.scro

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rashed.scro.datastore.model.CardInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(private val dataStore: DataStore<CardInfo>) : ViewModel() {
    private var _state = MutableStateFlow(CardInfo())
    val state: StateFlow<CardInfo> = _state

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            dataStore.data.collect {
                _state.value = it
            }
        }
    }

    fun resetCards() {
        viewModelScope.launch {
            dataStore.updateData {
                it.copy(
                    cards = List(4) { CardInfo.Card() } // Reset to 4 default cards
                )
            }
        }
    }

    fun updateCard(index: Int, value: String) {
        _state.value = _state.value.copy(
            cards = _state.value.cards.toMutableList().apply {
                this[index] = this[index].copy(value = value)
            }
        )

        viewModelScope.launch {
            dataStore.updateData {
                it.copy(
                    cards = it.cards.toMutableList().apply {
                        this[index] = this[index].copy(value = value)
                    }
                )
            }
        }
    }

    fun updateCardStatus(index: Int, suspended: Boolean) {
        _state.value = _state.value.copy(
            cards = _state.value.cards.toMutableList().apply {
                this[index] = this[index].copy(isSuspended = suspended)
            }
        )

        viewModelScope.launch {
            dataStore.updateData {
                it.copy(
                    cards = it.cards.toMutableList().apply {
                        this[index] = this[index].copy(isSuspended = suspended)
                    }
                )
            }
        }
    }
    fun addEmptyCard() {
        _state.value = _state.value.copy(
            cards = _state.value.cards + CardInfo.Card()
        )

        viewModelScope.launch {
            dataStore.updateData {
                it.copy(
                    cards = it.cards + CardInfo.Card()
                )
            }
        }
    }
}
