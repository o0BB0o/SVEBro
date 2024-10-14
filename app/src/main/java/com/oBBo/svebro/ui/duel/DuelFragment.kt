package com.oBBo.svebro.ui.duel

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding.opponentHpHolder.setBackgroundResource(viewModel.opponentClassBackgroundR[viewModel.opponentClass.value!!])
        binding.playerHpHolder.setBackgroundResource(viewModel.selectedClassBackgroundR[viewModel.selectedLeader.value!!.classType])
        Glide.with(this)
            .load(viewModel.selectedLeader.value!!.bgPath) // Your image URL here
            .into(binding.leader)
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
}