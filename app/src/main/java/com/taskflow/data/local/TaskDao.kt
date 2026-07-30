package com.taskflow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /** 모든 할 일을 관찰한다. 세부 정렬(우선순위 등)은 뷰모델에서 처리한다. */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): Task?

    /** 알림이 켜져 있고 아직 완료되지 않은 항목(부팅 후 알림 재예약에 사용). */
    @Query("SELECT * FROM tasks WHERE reminderEnabled = 1 AND isCompleted = 0")
    suspend fun getRemindable(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)
}
