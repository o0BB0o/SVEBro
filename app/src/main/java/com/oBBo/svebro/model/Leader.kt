package com.oBBo.svebro.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leader")
data class Leader(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var name: String,
    var classType: Int,
    var bgPath: String? = null,
    var emote1Path: String? = null,
    var emote2Path: String? = null,
    var emote3Path: String? = null,
    var emote4Path: String? = null,
    var emote5Path: String? = null,
    var emote6Path: String? = null,
    var emote7Path: String? = null,
    var soundFanfare: String? = null,
    var soundTurnStart: String? = null,
    var soundTurnEnd: String? = null
)
