package com.example.vidme.presentation.fragment.singles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vidme.databinding.FragmentSinglesBinding

class SinglesFragment : Fragment() {
    private var _binding: FragmentSinglesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSinglesBinding.inflate(layoutInflater)

        return binding.root
    }
}