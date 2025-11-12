package com.example.server.models

import java.util.Collections

object ComponentRepository {
    private val components = Collections.synchronizedList(mutableListOf<StateComponent>())

    init {
        components.add(StateComponent("text_1", ComponentType.Text, "Hello World!"))
        components.add(StateComponent("text_2", ComponentType.Text, "Hello Android!"))
    }

    fun getAll(): List<StateComponent> = components

    fun addComponent(component: StateComponent) {
        components.add(component)
    }

    fun removeComponent(id: String) {
        components.removeIf { it.id == id }
    }
}