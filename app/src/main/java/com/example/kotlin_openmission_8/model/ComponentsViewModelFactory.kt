package com.example.kotlin_openmission_8.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.ktor.client.HttpClient

class ComponentsViewModelFactory(private val client: HttpClient) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(Components::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return Components(client) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}