package com.oBBo.svebro.ui.preduel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oBBo.svebro.R
import com.oBBo.svebro.databinding.FragmentPreDuelBinding
import com.oBBo.svebro.model.Leader
import com.oBBo.svebro.ui.duel.DuelViewModel
import com.oBBo.svebro.ui.duel.DuelViewModelFactory
import java.io.File


class PreDuelFragment : Fragment(R.layout.fragment_pre_duel) {

    private lateinit var viewModel: DuelViewModel
    private var _binding: FragmentPreDuelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity(), DuelViewModelFactory(requireContext())).get(
            DuelViewModel::class.java)
        _binding = FragmentPreDuelBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners(view)
        binding.opponentClassBtn1.alpha = 1.0f
    }

    private fun showCharacterSelectionPopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_leader_selection, null)
        val displayMetrics = resources.displayMetrics
        val height = (displayMetrics.heightPixels * 0.8).toInt()
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, height, true)
        val leaderRecyclerView = popupView.findViewById<RecyclerView>(R.id.leaderRecyclerView)
        leaderRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val leaderAdapter = LeaderAdapter(emptyList(), { selectedLeader ->
            popupWindow.dismiss()
            viewModel.selectedLeader.value = selectedLeader
        }) { leaderToDelete ->
            deleteLeaderFromDatabase(leaderToDelete)
        }

        leaderRecyclerView.adapter = leaderAdapter

        viewModel.filteredLeaders.observe(viewLifecycleOwner) { leaders ->
            if (leaders != null && leaders.isNotEmpty()) {
                leaderAdapter.updateLeaders(leaders)
            }
        }

        val anchorView = binding.opponentClassSelectionSpace
        popupWindow.showAsDropDown(anchorView, 0, -anchorView.height) // Position the popup relative to anchorView
    }

    private fun deleteLeaderFromDatabase(leader: Leader) {
        viewModel.deleteLeader(leader)
        val leaderDir = File(requireContext().filesDir, leader.id.toString())
        deleteLeaderFolder(leaderDir)
    }

    private fun deleteLeaderFolder(leaderFolder: File) {
        if (leaderFolder.exists() && leaderFolder.isDirectory) {
            Log.d("d", "Deleting folder"+leaderFolder.absolutePath)
            val files = leaderFolder.listFiles()
            if (files != null) {
                for (file in files) {
                    Log.d("d", "Deleting"+file.name)
                    file.delete()
                }
            }
            leaderFolder.delete()
        }
    }

    private fun setupListeners(view:View) {
        binding.floatingAddLeaderBtn.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_preDuelFragment_to_DIYFragment)
        }
        binding.userClassBtn1.setOnClickListener{
            viewModel.getFilteredLeader(0)
            showCharacterSelectionPopup()
        }
        binding.userClassBtn2.setOnClickListener{
            viewModel.getFilteredLeader(1)
            showCharacterSelectionPopup()
        }
        binding.userClassBtn3.setOnClickListener{
            viewModel.getFilteredLeader(2)
            showCharacterSelectionPopup()
        }
        binding.userClassBtn4.setOnClickListener{
            viewModel.getFilteredLeader(3)
            showCharacterSelectionPopup()
        }
        binding.userClassBtn5.setOnClickListener{
            viewModel.getFilteredLeader(4)
            showCharacterSelectionPopup()
        }
        binding.userClassBtn6.setOnClickListener{
            viewModel.getFilteredLeader(5)
            showCharacterSelectionPopup()
        }
        binding.userClassBtn7.setOnClickListener{
            viewModel.getFilteredLeader(6)
            showCharacterSelectionPopup()
        }
        viewModel.opponentClassSelectionState.observe(viewLifecycleOwner) { alphaList ->
            binding.opponentClassBtn1.alpha = alphaList[0]
            binding.opponentClassBtn2.alpha = alphaList[1]
            binding.opponentClassBtn3.alpha = alphaList[2]
            binding.opponentClassBtn4.alpha = alphaList[3]
            binding.opponentClassBtn5.alpha = alphaList[4]
            binding.opponentClassBtn6.alpha = alphaList[5]
            binding.opponentClassBtn7.alpha = alphaList[6]
        }
        viewModel.playerClassSelectionState.observe(viewLifecycleOwner) { alphaList ->
            binding.userClassBtn1.alpha = alphaList[0]
            binding.userClassBtn2.alpha = alphaList[1]
            binding.userClassBtn3.alpha = alphaList[2]
            binding.userClassBtn4.alpha = alphaList[3]
            binding.userClassBtn5.alpha = alphaList[4]
            binding.userClassBtn6.alpha = alphaList[5]
            binding.userClassBtn7.alpha = alphaList[6]
        }
        viewModel.selectedLeader.observe(viewLifecycleOwner) {
            viewModel.initSoundMapFromLeader()
            viewModel.onSelectPlayerClass(it.classType)
        }
        binding.toDuelBtn.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_preDuelFragment_to_duelFragment)
            viewModel.restartDuel()
        }
    }

}