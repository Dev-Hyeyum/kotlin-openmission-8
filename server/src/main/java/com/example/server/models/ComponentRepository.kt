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

    // id가 일치하는 컴포넌트를 찾아 반환하는 함수, 못찾으면 null을 반환함
    fun getComponent(id: String): StateComponent? {
        return components.find { it.id == id }
    }

    // id를 기준으로 기존 컴포넌트를 찾고, updatedState로 교체
    fun updateComponent(updatedState: StateComponent) {
        val index = components.indexOfFirst  {it.id == updatedState.id}

        if (index != -1) {
            // 항목을 찾은 경우
            components[index] = updatedState
        } else {
            // 항목이 없는경우
            // 일단은 경고만 띄우지만 그대로 새로운 컴포넌트를 추가하는 것도 고려가능할 것 같습니다.
            // addComponent(updatedState)

            println("[Warning] Update: ID($updatedState.id})와 일치하는 컴포넌트가 없습니다.")
        }
    }
}