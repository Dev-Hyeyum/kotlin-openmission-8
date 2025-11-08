package com.example.server.model

object ComponentRepository {
    val components = mutableListOf(
        Component(action = ComponentAction.Create, type = ComponentType.Text, text = "Hello World!"),
        Component(action = ComponentAction.Create, type = ComponentType.Text, text = "Hello Android!"),
        Component(action = ComponentAction.Create, type = ComponentType.Text, text = "Hello Kotlin!"),
        Component(action = ComponentAction.Create, type = ComponentType.Text, text = "Hello Python!")
    )

    fun getAll(): List<Component> = components

    fun addComponents(component: Component) {
        components.add(component)
    }

    fun deleteComponent(id: String): Boolean {
        return components.removeIf{ it.id == id}
    }
}