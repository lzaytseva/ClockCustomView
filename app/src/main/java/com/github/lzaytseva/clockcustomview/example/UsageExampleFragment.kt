package com.github.lzaytseva.clockcustomview.example

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.lzaytseva.clockcustomview.databinding.FragmentUsageExampleBinding
import java.util.Calendar
import java.util.TimeZone

class UsageExampleFragment : Fragment() {
    private var _binding: FragmentUsageExampleBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsageExampleBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler.post(
            object : Runnable {
                override fun run() {
                    setTime()

                    handler.postDelayed(
                        this,
                        REFRESH_DELAY_MILLIS,
                    )
                }
            }
        )
    }

    private fun setTime() {
        val localTime = Calendar.getInstance()
        val tokyoTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
        val londonTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"))

        binding.localTime.text =
            String.format("%02d:%02d", localTime[Calendar.HOUR_OF_DAY], localTime[Calendar.MINUTE])
        binding.tokyoTime.text =
            String.format("%02d:%02d", tokyoTime[Calendar.HOUR_OF_DAY], localTime[Calendar.MINUTE])
        binding.londonTime.text =
            String.format("%02d:%02d", londonTime[Calendar.HOUR_OF_DAY], localTime[Calendar.MINUTE])
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val REFRESH_DELAY_MILLIS = 1000L
    }
}