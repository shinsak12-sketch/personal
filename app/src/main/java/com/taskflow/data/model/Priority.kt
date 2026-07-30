package com.taskflow.data.model

/**
 * 업무 우선순위. weight 가 클수록 높은 우선순위이며 정렬에 사용된다.
 */
enum class Priority(val label: String, val weight: Int, val colorArgb: Long) {
    HIGH("높음", 3, 0xFFFF5A5F),
    MEDIUM("보통", 2, 0xFFFFA726),
    LOW("낮음", 1, 0xFF26C281);
}
