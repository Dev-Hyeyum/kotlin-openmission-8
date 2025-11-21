package com.example.kotlin_openmission_8.model

enum class EventType(val label: String) {
    NONE("없음"),
    SHOW_TOAST("토스트 메시지 띄우기"),
    OPEN_LINK("웹 페이지 열기 (URL)"),
    SET_TEXT("다른 컴포넌트 텍스트 변경");
}