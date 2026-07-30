package com.taskflow.data.model

/**
 * 업무/일정 분류 카테고리. 각 항목은 표시용 라벨과 대표 색상(ARGB)을 가진다.
 */
enum class Category(val label: String, val colorArgb: Long) {
    WORK("업무", 0xFF5A67F2),
    PERSONAL("개인", 0xFF00C2A8),
    STUDY("공부", 0xFF9B5DE5),
    HEALTH("건강", 0xFFFF7A59),
    OTHER("기타", 0xFF64748B);

    companion object {
        fun fromNameOrDefault(name: String?): Category =
            entries.firstOrNull { it.name == name } ?: WORK
    }
}
