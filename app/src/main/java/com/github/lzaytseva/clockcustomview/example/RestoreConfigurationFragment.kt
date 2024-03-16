package com.github.lzaytseva.clockcustomview.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.lzaytseva.clockcustomview.R
import com.github.lzaytseva.clockcustomview.databinding.FragmentRestoreConfigurationBinding

class RestoreConfigurationFragment: Fragment() {
    private var _binding: FragmentRestoreConfigurationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestoreConfigurationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            binding.clock.frameColor = ContextCompat.getColor(requireContext(), R.color.dark_blue)
        }
    }
}