package com.example.server

import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

// Room 인스턴스를 생성하고 맵으로 관리하는 싱글톤 객체
object CanvasManager {
    // 멀티쓰레드 환경에 특화된 HashMap
    private val rooms = ConcurrentHashMap<String, Room>()
    private val uploadRoot = File("uploads")

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
        val room = rooms[roomId] ?: return null

        // 1. 연결 종료
        room.closeAllConnections()

        // 2. 맵에서 방 제거
        val removedRoom = rooms.remove(roomId)

        if (removedRoom != null) {
            // ✨ 3. [핵심] 룸 ID 폴더 전체를 재귀적으로 삭제
            val roomUploadFolder = File(uploadRoot, roomId)

            if (roomUploadFolder.exists()) {
                // 폴더와 그 안의 모든 파일(썸네일, 컴포넌트 이미지)을 삭제
                roomUploadFolder.deleteRecursively()
                println("[$roomId] 캔버스 관련 폴더 전체 삭제 완료: ${roomUploadFolder.name}")
            } else {
                println("[$roomId] 삭제할 업로드 폴더가 없음")
            }

            println("캔버스 삭제됨 $roomId")
        }

        return removedRoom
    }
}