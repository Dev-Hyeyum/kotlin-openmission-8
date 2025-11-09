package com.example.server.models

object ComponentRepository {
    val components = mutableListOf(
        Component(ComponentAction.Create, ComponentType.Text, "Hello World!"),
        Component(ComponentAction.Create, ComponentType.Text, "Hello Android!"),
        Component(ComponentAction.Create, ComponentType.Text, "Hello Kotlin!"),
        Component(ComponentAction.Create, ComponentType.Text, "Hello Python!")
    )

    fun getAll(): List<Component> = components

    fun addComponents(component: Component) {
        components.add(component)
    }
}