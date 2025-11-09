package com.example.server

import com.example.server.model.Component
import com.example.server.model.ComponentRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/components") {
            val components = ComponentRepository.getAll()
            call.respond(components)
            return@get
        }

        post("/components") {
            try {
                val newComponent = call.receive<Component>()
                ComponentRepository.addComponents(newComponent)
                call.respond(HttpStatusCode.Created, "Component added successfully")
                return@post
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid component format")
            }
        }

        delete("/{id}") {
            try{
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                if (ComponentRepository.deleteComponent(id)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "${e.message}")
            }
        }

    }
}
