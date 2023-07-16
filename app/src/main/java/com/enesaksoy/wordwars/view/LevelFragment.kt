package com.enesaksoy.wordwars.view

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.enesaksoy.wordwars.R
import com.enesaksoy.wordwars.databinding.LevelFragmentBinding

class LevelFragment : Fragment(R.layout.level_fragment) {
    private lateinit var binding: LevelFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LevelFragmentBinding.bind(view)

        val levelList = resources.getStringArray(R.array.levelList)
        if (binding.spinner != null){
            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item,levelList)
            binding.spinner.adapter = adapter
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    binding.nextButton.setOnClickListener {
                        val action = LevelFragmentDirections.actionLevelFragmentToGameFragment(levelList[position])
                        Navigation.findNavController(requireView()).navigate(action)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }
}