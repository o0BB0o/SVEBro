package com.oBBo.svebro.model

import androidx.lifecycle.LiveData

class LeaderRepository(private val leaderDao : LeaderDao) {
    suspend fun insertCharacter(leader: Leader) = leaderDao.insertCharacter(leader)

    suspend fun getLeaderById(id: Int) = leaderDao.getCharacterByClass(id)

    suspend fun updateCharacter(leader: Leader) = leaderDao.updateCharacter(leader)

    suspend fun getCharacterByClass(classType: Int): List<Leader> {
        return leaderDao.getCharacterByClass(classType)
    }

    suspend fun deleteLeader(leader: Leader) = leaderDao.deleteLeader(leader)
}