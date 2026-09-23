package com.unicofrance.uniexo.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.data.repositories.ContainerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(containerRepository: ContainerRepository, containerId: String?) :
    ViewModel() {
    private val _container = MutableStateFlow<Container?>(null)
    val container = _container.asStateFlow()

    init {
        viewModelScope.launch {
            containerRepository.getById(containerId ?: "").collect { container ->
                _container.value = container
            }
        }
    }
}