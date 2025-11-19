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
        // resources/static 폴더를 호스팅하는 설정 추가
        // http://localhost:8080/test.html 로 접속
        staticResources("/", "static")
        staticFiles("/uploads", uploadRoot)

        get("/test.html") {
            // 1. URL 파라미터에서 'room' ID를 가져옵니다.
            val roomId = call.parameters["room"]
            if (roomId == null) {
                call.respond(HttpStatusCode.BadRequest, "Error: Room ID가 URL에 없습니다.")
                return@get
            }

            // 2. CanvasManager에 'room' ID가 실제로 존재하는지 확인합니다.
            val room = CanvasManager.getRoom(roomId)

            if (room != null) {
                // 3. (성공) 방이 존재하면 -> static 폴더의 test.html 파일을 전송합니다.
                call.respondFile(File("resources/static/test.html"))
            } else {
                // 4. (실패) 방이 삭제되었거나 없으면 -> 404 Not Found 에러를 전송합니다.
                call.respond(HttpStatusCode.NotFound, "Error: 삭제되었거나 존재하지 않는 캔버스입니다.")
            }
        }

        get("/rooms") {
            val roomIds = CanvasManager.getAllRoomIds()
            call.respond(roomIds)
        }

        post("/create-canvas") {
            val roomId = CanvasManager.createCanvas() // 새 방 만들기

            // TODO: url 부분을 실제 서버 IP로 변경하기
            // (로컬 테스트 시: "http://localhost:8080")
            val serverBaseUrl = "http://localhost:8080"

            // 'app'에게 "이 링크를 사용자에게 전달해"라고 URL 응답
            call.respond(
                mapOf(
                    "roomId" to roomId,
                    // roomId 가 포함된 URL
                    "url" to "$serverBaseUrl/test.html?room=$roomId"
                )
            )
        }

        delete("/canvas/{roomId}") {
            val roomId = call.parameters["roomId"]
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("status" to "Error", "message" to "Room ID가 없습니다.")
                )

            // CanvasManager의 삭제 함수 호출 (suspend 함수)
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

            // 1. ✨ [추가/수정] 룸 ID에 해당하는 서브 폴더 경로 생성
            val roomSubDir = File(uploadRoot, roomId)
            if (!roomSubDir.exists()) {
                roomSubDir.mkdirs() // 폴더가 없으면 생성
            }

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    // 2. ✨ [수정] 파일명을 'thumbnail.png'로 고정하여 룸 폴더 안에 저장
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

            // TODO: 반드시 실제 서버 IP로 변경해야 합니다!
            val serverBaseUrl = "http://localhost:8080"

            val multipart = call.receiveMultipart()
            var uploadedFileName: String? = null // uploads/{roomId}/uuid.jpg 형태

            // 1. ✨ [추가/수정] 룸 ID에 해당하는 서브 폴더 경로 생성
            val roomSubDir = File(uploadRoot, roomId)
            if (!roomSubDir.exists()) {
                roomSubDir.mkdirs() // 폴더가 없으면 생성
            }

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val fileName = UUID.randomUUID().toString() + ".jpg"
                    // 2. ✨ [수정] 이미지를 룸 ID 서브 폴더 안에 저장
                    val file = File(roomSubDir, fileName)

                    // 3. ✨ [핵심] URL에 포함될 파일명(폴더 포함)
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
                // 4. ✨ [수정] 응답 URL에 'uploads' 경로를 포함
                val imageUrl = "$serverBaseUrl/uploads/$uploadedFileName"
                call.respond(mapOf("imageUrl" to imageUrl))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Image file not found in request."))
            }
        }
    }
}
