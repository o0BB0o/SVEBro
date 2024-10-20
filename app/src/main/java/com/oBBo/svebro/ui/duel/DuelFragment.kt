package com.oBBo.svebro.ui.duel

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import com.bumptech.glide.Glide
import com.oBBo.svebro.R
import com.oBBo.svebro.databinding.FragmentDuelBinding

class DuelFragment : Fragment(R.layout.fragment_duel) {

    private lateinit var viewModel: DuelViewModel
    private var _binding: FragmentDuelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity(), DuelViewModelFactory(requireContext())).get(DuelViewModel::class.java)
        _binding = FragmentDuelBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.opponentHpHolder.setBackgroundResource(viewModel.opponentClassBackgroundR[viewModel.opponentClass.value!!])
        binding.playerHpHolder.setBackgroundResource(viewModel.selectedClassBackgroundR[viewModel.selectedLeader.value!!.classType])
        binding.diceRoll.setOnClickListener{ showDiceRollPopup() }
        Glide.with(this)
            .load(viewModel.selectedLeader.value!!.bgPath)
            .into(binding.leader)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isCurrentPlayerTurn.observe(viewLifecycleOwner) { isTurn ->
            updateTurnBtn(isTurn)
        }
        viewModel.isEmoteTabOpen.observe(viewLifecycleOwner) { isOpen ->
            updateEmoteTab(isOpen)
        }
    }

    private fun updateEmoteTab(isOpen: Boolean){
        if(isOpen) {
            binding.emoteOpen.setImageResource(R.drawable.src_assets_emote_btn_emote_menu_l_on)
        }
        else{
            binding.emoteOpen.setImageResource(R.drawable.src_assets_emote_btn_emote_01_on)
        }
    }

    private fun updateTurnBtn(isTurn: Boolean) {
        if(isTurn) {
            binding.turnStartEnd.setBackgroundResource(R.drawable.src_assets_battle_btn_turn_end_off)
            binding.turnStartEnd.text = getText(R.string.turn_end)
            binding.turnStartEnd.setOnClickListener(){
                viewModel.endTurn()
            }
        }
        else{
            binding.turnStartEnd.setBackgroundResource(R.drawable.src_assets_battle_btn_turn_start_off)
            binding.turnStartEnd.text = getText(R.string.my_turn)
            binding.turnStartEnd.setOnClickListener(){
                viewModel.startTurn()
            }
        }
    }

    private fun showDiceRollPopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_dice_rolling, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)

        val diceImageView = popupView.findViewById<ImageView>(R.id.diceImageView)

        var rolling = true
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                if (rolling) {
                    val result = (1..6).random()
                    diceImageView.setImageResource(getDiceImageResource(result))
                    handler.postDelayed(this, 50)
                }
            }
        }

        handler.post(runnable)

        // Stop rolling after a few seconds
        handler.postDelayed({
            rolling = false
            val finalResult = (1..6).random()
            diceImageView.setImageResource(getDiceImageResource(finalResult))
        }, 500)

        popupView.setOnClickListener {
            popupWindow.dismiss()
        }

        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
    }

    private fun getDiceImageResource(result: Int): Int {
        return when (result) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            else -> R.drawable.dice_1 // Default
        }
    }
}