package com.layrin.smsapp.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.layrin.smsapp.R
import com.layrin.smsapp.databinding.FragmentStartBinding
import com.layrin.smsapp.repository.DaoRepository
import com.layrin.smsapp.repository.ProviderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min
import kotlin.math.roundToInt

class StartFragment : Fragment() {
    companion object {
        const val KEY_INIT = "init_organized"
    }

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    private lateinit var preference: SharedPreferences

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            ProviderRepository(requireContext()),
            DaoRepository(),
            requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preference = PreferenceManager.getDefaultSharedPreferences(requireActivity())

        if (preference.getBoolean(KEY_INIT, false)) {
            findNavController().navigate(R.id.action_startFragment_to_mainFragment)
            return
        }

        binding.btnNext.setOnClickListener {
            startOrganize()
        }

    }

    private fun startOrganize() {
        val smsManager = viewModel.providerRepository.smsManager

        var progress = 0f

        smsManager.onProgressListener = {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                progress = it
                if (it > 0) {
                    if (binding.progressBar.isIndeterminate) {
                        binding.progressBar.isIndeterminate = false
                        smsManager.onStatusChangeListener(1)
                    }
                    binding.tvProgress.text = getString(R.string.progress_percent, it.roundToInt())
                    binding.progressBar.progress = it.roundToInt()
                }
            }
        }

        smsManager.onStatusChangeListener = {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                if (it == 2) binding.tvEta.visibility = View.INVISIBLE
            }
        }
        smsManager.onStatusChangeListener(0)

        var preTimer: CountDownTimer? = null
        var minTime = Long.MAX_VALUE
        smsManager.onEtaChangeListener = {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                preTimer?.cancel()
                preTimer = object : CountDownTimer(it, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        minTime = min(minTime, millisUntilFinished)
                        val second = (minTime / 1000) % 60
                        val minute = (minTime / 1000) / 60
                        if ((0 < progress) && (progress < 100)) {
                            binding.tvEta.text = when {
                                minute > 0 -> minute.toString()
                                second > 9 -> second.toString()
                                second > 3 -> second.toString()
                                else -> "Lagi dikit bro"
                            }
                        } else binding.tvEta.text = ""
                    }

                    override fun onFinish() {}
                }.start()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            smsManager.apply {
                getMessages()
                getLabels()
            }
            withContext(Dispatchers.Main) {
                findNavController().navigate(R.id.action_startFragment_to_mainFragment)
                preference.edit().putBoolean(KEY_INIT, true).apply()
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}