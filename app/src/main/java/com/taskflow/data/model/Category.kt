package com.taskflow.data.model

/**
 * 업무/일정 분류 카테고리. 각 항목은 표시용 라벨과 대표 색상(ARGB)을 가진다.
 */
enum class Category(val label: String, val colorArgb: Long) {
    WORK("업무", 0xFF4C6FFF),
    PERSONAL("개인", 0xFF00BFA6),
    STUDY("공부", 0xFFAB47BC),
    HEALTH("건강", 0xFFFF7043),
    OTHER("기타", 0xFF78909C);

    companion object {
        fun fromNameOrDefault(name: String?): Category =
            entries.firstOrNull { it.name == name } ?: WORK
    }
}
