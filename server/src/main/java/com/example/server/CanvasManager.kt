package com.example.server

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
        // 1. 맵에서 Room을 찾음
        val room = rooms[roomId] ?: return null // 방이 없으면 null 반환

        // 2. Room에 연결된 모든 사용자의 WebSocket 연결을 먼저 닫음
        room.closeAllConnections()

        // 3. 맵에서 Room을 제거
        val removedRoom = rooms.remove(roomId)
        if (removedRoom != null) {
            println("캔버스 삭제됨 $roomId")
        }
        return removedRoom
    }
}