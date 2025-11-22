package com.example.server.plugins

import com.example.server.CanvasManager
import com.example.server.models.Component
import io.ktor.server.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.UUID

fun Application.configureRouting() {

    val uploadRoot = File("uploads")
    if (!uploadRoot.exists()) {
        uploadRoot.mkdirs()
    }

    routing {
        get("/ping") {
            call.respondText("PONG! Server is Alive!")
        }
        staticResources("/", "static")
        staticFiles("/uploads", uploadRoot)


        // 방 목록 조회
        get("/rooms") {
            val roomIds = CanvasManager.getAllRoomIds()
            call.respond(roomIds)
        }

        post("/create-canvas") {
            val roomId = CanvasManager.createCanvas() // 새 방 만들기

            // 서버의 현재 주소 자동 감지
            val serverBaseUrl = "${call.request.local.scheme}://${call.request.local.serverHost}:${call.request.local.serverPort}"

            call.respond(
                mapOf(
                    "roomId" to roomId,
                    // 생성된 URL 반환
                    "url" to "$serverBaseUrl/test.html?room=$roomId"
                )
            )
        }

        // 방 삭제
        delete("/canvas/{roomId}") {
            val roomId = call.parameters["roomId"]
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("status" to "Error", "message" to "Room ID가 없습니다.")
                )

            val removedRoom = CanvasManager.deleteCanvas(roomId)

            if (removedRoom != null) {
                call.respond(HttpStatusCode.OK, mapOf("status" to "OK", "message" to "Room $roomId 삭제됨"))
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("status" to "Error", "message" to "Room을 찾을 수 없습니다.")
                )
            }
        }

        post("/command/{roomId}") {
            //  URL에서 roomId를 가져옵니다.
            val roomId = call.parameters["roomId"]
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("status" to "Error", "message" to "Room ID가 없습니다.")
                )

            //  캔버스 매니저에게 해당 roomId의 room을 요청
            val room = CanvasManager.getRoom(roomId)
                ?: return@post call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("status" to "Error", "message" to "Room을 찾을 수 없습니다.")
                )

            // 해당 room의 broadcastCommand를 호출
            try {
                val command = call.receive<Component>()
                room.broadcastCommand(command)
                call.respond(mapOf("status" to "OK"))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("status" to "Error", "message" to e.message)
                )
            }
        }

        post("/upload-thumbnail/{roomId}") {
            val roomId = call.parameters["roomId"] ?: return@post call.respond(HttpStatusCode.BadRequest)

            val multipart = call.receiveMultipart()

            // 룸 ID에 해당하는 서브 폴더 경로 생성
            val roomSubDir = File(uploadRoot, roomId)
            if (!roomSubDir.exists()) {
                roomSubDir.mkdirs() // 폴더가 없으면 생성
            }

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    // 파일명을 'thumbnail.png'로 고정하여 룸 폴더 안에 저장
                    val fileName = "thumbnail.png"
                    val file = File(roomSubDir, fileName)

                    part.streamProvider().use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                part.dispose()
            }
            call.respond(HttpStatusCode.OK)
        }

        post("/upload-image/{roomId}") {
            val roomId = call.parameters["roomId"] ?: return@post call.respond(HttpStatusCode.BadRequest)

            val serverBaseUrl = "${call.request.local.scheme}://${call.request.local.serverHost}:${call.request.local.serverPort}"

            val multipart = call.receiveMultipart()
            var uploadedFileName: String? = null // uploads/{roomId}/uuid.jpg 형태

            // 룸 ID에 해당하는 서브 폴더 경로 생성
            val roomSubDir = File(uploadRoot, roomId)
            if (!roomSubDir.exists()) {
                roomSubDir.mkdirs() // 폴더가 없으면 생성
            }

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val fileName = UUID.randomUUID().toString() + ".jpg"
                    // 이미지를 룸 ID 서브 폴더 안에 저장
                    val file = File(roomSubDir, fileName)

                    // URL에 포함될 파일명(폴더 포함)
                    uploadedFileName = "$roomId/$fileName"

                    part.streamProvider().use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                part.dispose()
            }

            if (uploadedFileName != null) {
                // 응답 URL에 'uploads' 경로를 포함
                val imageUrl = "$serverBaseUrl/uploads/$uploadedFileName"
                call.respond(mapOf("imageUrl" to imageUrl))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Image file not found in request."))
            }
        }
    }
}