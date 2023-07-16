package com.enesaksoy.wordwars.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.enesaksoy.wordwars.R
import com.enesaksoy.wordwars.databinding.ModeFragmentBinding
import com.enesaksoy.wordwars.service.KeyAPI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ModeFragment (private val auth: FirebaseAuth): Fragment(R.layout.mode_fragment) {
    private lateinit var binding : ModeFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ModeFragmentBinding.bind(view)
        val modeList = resources.getStringArray(R.array.modeList)

        if (binding.spinner != null){
            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item,modeList)
            binding.spinner.adapter = adapter
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(auth.currentUser == null && modeList[position].equals("Online")){
                        binding.warningText.visibility = View.VISIBLE
                    }else{
                        binding.warningText.visibility = View.INVISIBLE
                    }
                    binding.nextButton.setOnClickListener {
                        if(modeList[position].equals("Offline")){
                            val action = ModeFragmentDirections.actionModeFragmentToLevelFragment()
                            Navigation.findNavController(requireView()).navigate(action)
                        }else{
                            if(auth.currentUser == null){
                                val action = ModeFragmentDirections.actionModeFragmentToIntoFragment()
                                Navigation.findNavController(requireView()).navigate(action)
                            }else{
                                val action = ModeFragmentDirections.actionModeFragmentToMatchingFragment()
                                Navigation.findNavController(requireView()).navigate(action)
                            }
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?){}
            }
        }

        binding.accountBtn.setOnClickListener {
            if (auth.currentUser != null){
                val action = ModeFragmentDirections.actionModeFragmentToAccountFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }else{
                Snackbar.make(requireView(), "you need to login first", Snackbar.LENGTH_INDEFINITE).setAction("login", View.OnClickListener {
                    val action = ModeFragmentDirections.actionModeFragmentToIntoFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                }).show()
            }
        }
    }
}