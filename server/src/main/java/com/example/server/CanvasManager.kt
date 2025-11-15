package com.example.server

import java.util.*
import java.util.concurrent.ConcurrentHashMap

// Room 인스턴스를 생성하고 맵으로 관리하는 싱글톤 객체
object CanvasManager {
    // 멀티쓰레드 환경에 특화된 HashMap
    private val rooms = ConcurrentHashMap<String, Room>()

    fun createCanvas(): String {
        val roomId = UUID.randomUUID().toString().take(6) // 6자리의 랜덤 아이디
        val newRoom = Room()
        rooms[roomId] = newRoom
        println("새 캔버스 생성됨 $roomId")
        return roomId
    }

    fun getRoom(roomId: String): Room? {
        return rooms[roomId]
    }
}