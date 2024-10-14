package com.oBBo.svebro.ui.duel

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.oBBo.svebro.R
import com.oBBo.svebro.model.Leader
import com.oBBo.svebro.model.LeaderDatabase
import com.oBBo.svebro.model.LeaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DuelViewModel(context: Context) : ViewModel() {
    private val db = LeaderDatabase.getDatabase(context)
    private val leaderDao = db.LeaderDao()
    private val leaderRepository = LeaderRepository(leaderDao)

    // State for the Duel
    private val _currentPlayerMaxPP = MutableLiveData<Int>()
    val currentPlayerMaxPP: LiveData<Int> get() = _currentPlayerMaxPP

    private val _currentPlayerHP = MutableLiveData<Int>()
    val currentPlayerHP: LiveData<Int> get() = _currentPlayerHP

    private val _opponentPlayerHP = MutableLiveData<Int>()
    val opponentPlayerHP: LiveData<Int> get() = _opponentPlayerHP

    private val _currentPlayerRemainingPP = MutableLiveData<Int>()
    val currentPlayerRemainingPP: LiveData<Int> get() = _currentPlayerRemainingPP

    private val _isCurrentPlayerTurn = MutableLiveData<Boolean>()
    val isCurrentPlayerTurn: LiveData<Boolean> get() = _isCurrentPlayerTurn

    private val _isEmoteTabOpen = MutableLiveData<Boolean>()
    val isEmoteTabOpen: LiveData<Boolean> get() = _isEmoteTabOpen

    private val _PPStates = MutableLiveData<MutableList<PPState>>(MutableList(10) { PPState.NOT_ACTIVATED })
    val PPStates: LiveData<MutableList<PPState>> get() = _PPStates

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build())
        .build()

    private val soundMap = mapOf(
        "fanfare" to soundPool.load(context, R.raw.ya_fanfare, 1),
        "turn_end" to soundPool.load(context, R.raw.ya_turn_end, 1),
        "turn_start" to soundPool.load(context, R.raw.ya_turn_start, 1),
        "emote1" to soundPool.load(context, R.raw.ya1, 1),
        "emote2" to soundPool.load(context, R.raw.ya2, 1),
        "emote3" to soundPool.load(context, R.raw.ya3, 1),
        "emote4" to soundPool.load(context, R.raw.ya4, 1),
        "emote5" to soundPool.load(context, R.raw.ya5, 1),
        "emote6" to soundPool.load(context, R.raw.ya6, 1),
        "emote7" to soundPool.load(context, R.raw.ya7, 1)
    )

    val opponentClass = MutableLiveData<Int>().apply { value = 5 }
    val opponentClassSelectionState: MutableLiveData<MutableList<Float>> = MutableLiveData(
        mutableListOf(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 1.0f)
    )
    var opponentClassBackgroundR = listOf(R.drawable.src_assets_cards_role_role_spirit_bottom_r,
        R.drawable.src_assets_cards_role_role_royal_bottom_r, R.drawable.src_assets_cards_role_role_master_bottom_r,
        R.drawable.src_assets_cards_role_role_nightmare_bottom_r, R.drawable.src_assets_cards_role_role_bishop_bottom_r,
        R.drawable.src_assets_cards_role_role_neutrality_bottom_r)


    var filteredLeaders: MutableLiveData<List<Leader>> = MutableLiveData()
    val playerClassSelectionState: MutableLiveData<MutableList<Float>> = MutableLiveData(
        mutableListOf(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f)
    )
    val selectedLeader = MutableLiveData<Leader>()
    var selectedClassBackgroundR = listOf(R.drawable.src_assets_cards_role_role_spirit_bottom,
        R.drawable.src_assets_cards_role_role_royal_bottom, R.drawable.src_assets_cards_role_role_master_bottom,
        R.drawable.src_assets_cards_role_role_nightmare_bottom, R.drawable.src_assets_cards_role_role_bishop_bottom,
        R.drawable.src_assets_cards_role_role_neutrality_bottom)

    init {
        // Initialize default values
        _currentPlayerMaxPP.value = 0
        _currentPlayerHP.value = 20
        _opponentPlayerHP.value = 20
        _currentPlayerRemainingPP.value = 0
        _isCurrentPlayerTurn.value = false
        _isEmoteTabOpen.value = false
        _PPStates.value = MutableList(10) { PPState.NOT_ACTIVATED }
        // TODO delete if structure changed
//        soundPool.setOnLoadCompleteListener { soundPool, soundId, status ->
//            if (status == 0 && soundId == 1) {
//                playSound("fanfare")
//            }
//        }
    }

    fun restartDuel() {
        // Reset into default values
        _currentPlayerMaxPP.value = 0
        _currentPlayerHP.value = 20
        _opponentPlayerHP.value = 20
        _currentPlayerRemainingPP.value = 0
        _isCurrentPlayerTurn.value = false
        _isEmoteTabOpen.value = false
        _PPStates.value = MutableList(10) { PPState.NOT_ACTIVATED }
        playSound("fanfare")
    }

    //TODO replace with Database
//    fun initSounds(context: Context) {
//        val audioAttributes = AudioAttributes.Builder()
//            .setUsage(AudioAttributes.USAGE_MEDIA)
//            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//            .build()
//
//        soundPool = SoundPool.Builder()
//            .setMaxStreams(10)
//            .setAudioAttributes(audioAttributes)
//            .build()
//
//        soundMap = mapOf(
//            "fanfare" to soundPool.load(context, R.raw.ya_fanfare, 1),
//            "turn_end" to soundPool.load(context, R.raw.ya_turn_end, 1),
//            "turn_start" to soundPool.load(context, R.raw.ya_turn_start, 1),
//            "emote1" to soundPool.load(context, R.raw.ya1, 1),
//            "emote2" to soundPool.load(context, R.raw.ya2, 1),
//            "emote3" to soundPool.load(context, R.raw.ya3, 1),
//            "emote4" to soundPool.load(context, R.raw.ya4, 1),
//            "emote5" to soundPool.load(context, R.raw.ya5, 1),
//            "emote6" to soundPool.load(context, R.raw.ya6, 1),
//            "emote7" to soundPool.load(context, R.raw.ya7, 1)
//        )
//    }

    fun startTurn(){
        toggleTurn()
        maxPPIncrease()
        _currentPlayerRemainingPP.value = _currentPlayerMaxPP.value
        updatePPAll()
        playSound("turn_start")
    }

    fun endTurn(){
        toggleTurn()
        playSound("turn_end")
    }

    fun maxPPIncrease(){
        val newMaxPP = (_currentPlayerMaxPP.value ?: 0) + 1
        _currentPlayerMaxPP.value = newMaxPP.coerceAtMost(10)
        setPlaypointState(_currentPlayerMaxPP.value!! - 1, PPState.ACTIVATED_NOT_USABLE)
    }

    fun maxPPDecrease(){
        val newMaxPP = (_currentPlayerMaxPP.value ?: 0) - 1
        _currentPlayerMaxPP.value = newMaxPP.coerceAtLeast(0)

        if ((_currentPlayerRemainingPP.value ?: 0) > (_currentPlayerMaxPP.value ?: 0)) {
            _currentPlayerRemainingPP.value = _currentPlayerMaxPP.value
        }
        setPlaypointState(_currentPlayerMaxPP.value!!, PPState.NOT_ACTIVATED)
    }

    fun remainingPPIncrease(){
        val newCurrPP = (_currentPlayerRemainingPP.value ?: 0) + 1
        if (newCurrPP > (_currentPlayerMaxPP.value ?: 0)) {
            _currentPlayerRemainingPP.value = _currentPlayerMaxPP.value
        }
        else {
            _currentPlayerRemainingPP.value = newCurrPP
            setPlaypointState(_currentPlayerRemainingPP.value!! - 1, PPState.ACTIVATED_USABLE)
            // Log.d("TEST", PPStates.value.toString())
        }
    }

    fun remainingPPDecrease(){
        val newCurrPP = (_currentPlayerRemainingPP.value ?: 0) - 1
        _currentPlayerRemainingPP.value = newCurrPP.coerceAtLeast(0)
        if((PPStates.value?.get(_currentPlayerRemainingPP.value!!)
                ?: PPState.NOT_ACTIVATED) == PPState.ACTIVATED_USABLE
        ){
            setPlaypointState(_currentPlayerRemainingPP.value!!, PPState.ACTIVATED_NOT_USABLE)
            // Log.d("TEST", PPStates.value.toString())
        }
    }

    fun currPlayerHPIncrease(){
        val newHP = (_currentPlayerHP.value ?: 0) + 1
        _currentPlayerHP.value = newHP.coerceAtLeast(0)
    }

    fun currPlayerHPDecrease(){
        val newHP = (_currentPlayerHP.value ?: 0) - 1
        _currentPlayerHP.value = newHP.coerceAtLeast(0)
    }

    fun opponentHPIncrease(){
        val newHP = (_opponentPlayerHP.value ?: 0) + 1
        _opponentPlayerHP.value = newHP.coerceAtLeast(0)
    }

    fun opponentHPDecrease(){
        val newHP = (_opponentPlayerHP.value ?: 0) - 1
        _opponentPlayerHP.value = newHP.coerceAtLeast(0)
    }

    fun toggleTurn() {
        _isCurrentPlayerTurn.value = !(_isCurrentPlayerTurn.value ?: false)
    }

    fun toggleEmoteTab() {
        _isEmoteTabOpen.value = !(_isEmoteTabOpen.value ?: false)
    }

    fun updatePPAll() {
        for (index in 0 until 10) {
            val state = when {
                index + 1 > _currentPlayerMaxPP.value!! -> PPState.NOT_ACTIVATED
                index + 1 <= _currentPlayerRemainingPP.value!! -> PPState.ACTIVATED_USABLE
                else -> PPState.ACTIVATED_NOT_USABLE
            }
            setPlaypointState(index, state)
        }
    }

    fun setPlaypointState(index: Int, state: PPState) {
        _PPStates.value?.let {
            if (index in it.indices) {
                it[index] = state
                _PPStates.postValue(it)
            }
        }
    }

    fun playSound(soundKey: String) {
        soundMap[soundKey]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun emotePress(soundKey: String) {
        playSound(soundKey)
        toggleEmoteTab()
    }

    fun onSelectOpponentClass(selection: Int) {
        val currentList = opponentClassSelectionState.value ?: return
        currentList.replaceAll { if (it == 1.0f) 0.5f else it }
        currentList[selection] = 1.0f
        opponentClassSelectionState.postValue(currentList)
        opponentClass.postValue(selection)
        Log.d("debug", opponentClass.value.toString())
    }

    fun onSelectPlayerClass(selection: Int) {
        val currentList = playerClassSelectionState.value ?: return
        currentList.replaceAll { if (it == 1.0f) 0.5f else it }
        currentList[selection] = 1.0f
        playerClassSelectionState.postValue(currentList)
    }

    fun getFilteredLeader(inputClass: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val tempLeaders = leaderRepository.getCharacterByClass(inputClass)
            filteredLeaders.postValue(tempLeaders)
        }
    }
}

enum class PPState {
    NOT_ACTIVATED,
    ACTIVATED_USABLE,
    ACTIVATED_NOT_USABLE
}