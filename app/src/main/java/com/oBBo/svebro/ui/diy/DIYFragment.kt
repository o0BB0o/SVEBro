package com.oBBo.svebro.ui.diy

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.oBBo.svebro.databinding.FragmentDiyBinding
import com.oBBo.svebro.model.Leader
import com.oBBo.svebro.model.LeaderDao
import com.oBBo.svebro.model.LeaderDatabase
import com.oBBo.svebro.model.LeaderRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DIYFragment : Fragment() {

    private lateinit var viewModel: DIYViewModel
    private var _binding: FragmentDiyBinding? = null
    private val binding get() = _binding!!
    private lateinit var leaderDao: LeaderDao
    private lateinit var leaderRepository: LeaderRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val db = LeaderDatabase.getDatabase(requireContext())
        leaderDao = db.LeaderDao()
        leaderRepository = LeaderRepository(leaderDao)
        viewModel = ViewModelProvider(this).get(DIYViewModel::class.java)
        _binding = FragmentDiyBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectZipBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/zip"
            }
            zipFilePickerLauncher.launch(intent)
        }

        binding.diyProceedBtn.setOnClickListener {
            if(!validateZipFile(viewModel.zipRealUri.value)){
                binding.diyStatusText.setTextColor(Color.RED)
                viewModel.statusText.postValue("Please check your zip file")
                viewModel.isStatusTextViewVisible.postValue(true)
            }
            else if(viewModel.leaderNameInput.value.isNullOrEmpty()) {
                binding.diyStatusText.setTextColor(Color.RED)
                viewModel.statusText.postValue("Please check your leader name")
                viewModel.isStatusTextViewVisible.postValue(true)
            }
            else{
                viewModel.isProcessingZip.value = true
                binding.diyStatusText.setTextColor(Color.WHITE)
                viewModel.isStatusTextViewVisible.postValue(true)
                viewModel.statusText.postValue("Please Wait...")
                val leader = Leader(name = viewModel.leaderNameInput.value!!, classType = viewModel.selectedClass.value!!)
                GlobalScope.launch {
                    try {
                        val leaderId = insertLeader(leader)
                        processZipFile(viewModel.zipRealUri.value!!, leaderId)
                    } catch (e: Exception) {
                        binding.diyStatusText.setTextColor(Color.RED)
                        viewModel.statusText.postValue("Failed to add leader")
                    } finally {
                        viewModel.isProcessingZip.postValue(false)
                    }
                }
            }
        }

        binding.diyFinishBtn.setOnClickListener{
            Navigation.findNavController(view).popBackStack()
        }
    }

    private val zipFilePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val zipUri = result.data?.data
            if (zipUri != null) {
                viewModel.zipRealUri.value = zipUri
                val cursor = context?.contentResolver?.query(zipUri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val zipFileName = it.getString(displayNameIndex)
                        viewModel.zipFileName.value = zipFileName
                    }
                }
            } else {
                Toast.makeText(context, "No zip file selected", Toast.LENGTH_SHORT)
            }
        }
    }

    fun validateZipFile(zipUri: Uri?): Boolean {
        if(zipUri==null){
            return false
        }
        val inputStream = context?.contentResolver?.openInputStream(zipUri)
        val zipInputStream = ZipInputStream(inputStream)
        var imageCount = 0
        var audioCount = 0
        var fileCount = 0

        var zipEntry: ZipEntry? = zipInputStream.nextEntry
        while (zipEntry != null) {
            val fileName = zipEntry.name
            if (!zipEntry.isDirectory) {
                when {
                    fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") -> {
                        imageCount++
                    }
                    fileName.endsWith(".mp3") || fileName.endsWith(".wav") -> {
                        audioCount++
                    }
                }
            }
            fileCount++
            zipEntry = zipInputStream.nextEntry
        }
        zipInputStream.closeEntry()
        zipInputStream.close()

        if (imageCount == 1 && audioCount == 10 && fileCount == 11) {
            return true
        } else {
            return false
        }
    }

    fun processZipFile(zipUri: Uri, leaderId: Long) {
        val inputStream = requireContext().contentResolver.openInputStream(zipUri)
        val zipInputStream = ZipInputStream(inputStream)

        val leaderDir = File(requireContext().filesDir, leaderId.toString())
        leaderDir.mkdirs()

        val extractedFiles = mutableListOf<String>()

        var zipEntry: ZipEntry? = zipInputStream.nextEntry
        while (zipEntry != null) {
            val fileName = zipEntry.name
            if (!zipEntry.isDirectory) {
                val file = saveFileToLeaderDirectory(zipInputStream, leaderDir, fileName)
                extractedFiles.add(file.absolutePath)
            }
            zipEntry = zipInputStream.nextEntry
        }
        zipInputStream.closeEntry()
        zipInputStream.close()

        validateAndUpdateLeaderFiles(extractedFiles, leaderId)
    }

    fun saveFileToLeaderDirectory(zipInputStream: ZipInputStream, leaderDir: File, fileName: String): File {
        val outputFile = File(leaderDir, fileName)
        val outputStream = FileOutputStream(outputFile)
        zipInputStream.copyTo(outputStream)
        outputStream.close()

        return outputFile
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun validateAndUpdateLeaderFiles(filePaths: List<String>, leaderId: Long) {
        val imageFiles = filePaths.filter { it.endsWith(".png") || it.endsWith(".jpg") || it.endsWith(".jpeg") }
        val audioFiles = filePaths.filter { it.endsWith(".mp3") || it.endsWith(".wav") }

        GlobalScope.launch {
            val leader = leaderDao.getLeaderById(leaderId)!!
            if(leader==null){
                //TODO Change !! above and handle Error if leader not found
            }
            leader.bgPath = imageFiles[0]
            leader.emote1Path = audioFiles[0]
            leader.emote2Path = audioFiles[1]
            leader.emote3Path = audioFiles[2]
            leader.emote4Path = audioFiles[3]
            leader.emote5Path = audioFiles[4]
            leader.emote6Path = audioFiles[5]
            leader.emote7Path = audioFiles[6]
            leader.soundFanfare = audioFiles[7]
            leader.soundTurnStart = audioFiles[8]
            leader.soundTurnEnd = audioFiles[9]

            leaderRepository.updateCharacter(leader)

            //TODO Update status text
            binding.diyStatusText.setTextColor(Color.GREEN)
            viewModel.statusText.postValue("Leader added!")
            viewModel.isLoadSuccess.postValue(true)
        }
    }


    suspend fun insertLeader(leader: Leader): Long {
        return leaderRepository.insertCharacter(leader)
    }

}