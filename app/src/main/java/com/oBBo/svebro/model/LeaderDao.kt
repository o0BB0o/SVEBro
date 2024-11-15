package com.oBBo.svebro.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LeaderDao {
    @Insert
    suspend fun insertCharacter(leader: Leader): Long

    @Query("SELECT * FROM leader WHERE id = :id")
    suspend fun getLeaderById(id: Long): Leader?

    @Update
    suspend fun updateCharacter(leader: Leader)

    @Query("SELECT * FROM leader WHERE classType = :classType")
    suspend fun getCharacterByClass(classType: Int): List<Leader>

    @Delete
    suspend fun deleteLeader(leader: Leader)
}