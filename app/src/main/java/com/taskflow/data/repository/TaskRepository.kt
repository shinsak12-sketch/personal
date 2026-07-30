package com.taskflow.data.repository

import com.taskflow.data.local.Task
import com.taskflow.data.local.TaskDao
import kotlinx.coroutines.flow.Flow

/**
 * 데이터 접근 단일 창구. 뷰모델은 DAO 대신 이 저장소만 사용한다.
 */
class TaskRepository(private val dao: TaskDao) {

    val tasks: Flow<List<Task>> = dao.observeAll()

    suspend fun getById(id: Long): Task? = dao.getById(id)

    suspend fun getRemindable(): List<Task> = dao.getRemindable()

    /** 새 항목은 삽입, 기존 항목(id 존재)은 대체. 저장된 행의 id 를 반환한다. */
    suspend fun save(task: Task): Long = dao.insert(task)

    suspend fun update(task: Task) = dao.update(task)

    suspend fun delete(task: Task) = dao.delete(task)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
