package com.example.server.models

import java.util.*

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

    // id가 일치하는 컴포넌트를 찾아 반환하는 함수, 못찾으면 null을 반환함
    fun getComponent(id: String): StateComponent? {
        return components.find { it.id == id }
    }
}