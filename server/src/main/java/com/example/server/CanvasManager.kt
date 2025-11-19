package com.example.server

import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

// Room 인스턴스를 생성하고 맵으로 관리하는 싱글톤 객체
object CanvasManager {
    // 멀티쓰레드 환경에 특화된 HashMap
    private val rooms = ConcurrentHashMap<String, Room>()

    fun createCanvas(): String {
        val roomId = UUID.randomUUID().toString().take(6) // 6자리의 랜덤 아이디
        val newRoom = Room(roomId)
        rooms[roomId] = newRoom
        println("새 캔버스 생성됨 $roomId")
        return roomId
    }

    fun getRoom(roomId: String): Room? {
        return rooms[roomId]
    }

    fun getAllRoomIds(): List<String> {
        return rooms.keys().toList()
    }

    suspend fun deleteCanvas(roomId: String): Room? {
        // 1. 방 찾기
        val room = rooms[roomId] ?: return null

        val uploadDir = File("uploads/thumbnails")

        // 2. 연결 종료
        room.closeAllConnections()

        // 3. 맵에서 방 제거
        val removedRoom = rooms.remove(roomId)

        // ✨ 4. [추가] 썸네일 파일 삭제 로직
        if (removedRoom != null) {
            try {
                // 파일 객체 생성 (uploads/thumbnails/{roomId}.png)
                val thumbnailFile = File(uploadDir, "$roomId.png")

                // 파일이 존재하면 삭제
                if (thumbnailFile.exists()) {
                    val isDeleted = thumbnailFile.delete()
                    if (isDeleted) {
                        println("[$roomId] 썸네일 이미지 삭제 완료")
                    } else {
                        println("[$roomId] 썸네일 삭제 실패 (파일은 존재함)")
                    }
                } else {
                    println("[$roomId] 삭제할 썸네일이 없음")
                }
            } catch (e: Exception) {
                println("[$roomId] 파일 삭제 중 오류 발생: ${e.message}")
            }

            println("캔버스 삭제됨 $roomId")
        }

        return removedRoom
    }
}