package com.enesaksoy.wordwars.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.enesaksoy.wordwars.R
import com.enesaksoy.wordwars.databinding.ResultFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ResultFragment(private val auth: FirebaseAuth): Fragment(R.layout.result_fragment) {
    private lateinit var binding: ResultFragmentBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ResultFragmentBinding.bind(view)

        arguments?.let {
            val isWon = ResultFragmentArgs.fromBundle(it).isWon
            if (isWon.equals("won")){
                binding.resultTextView.text = "You Won"
                binding.resultTextView.setTextColor(Color.parseColor("#FF9800"))
                binding.resultImageView.animate().apply {
                    duration = 2000
                    rotationYBy(360f)
                }.start()
            }else if (isWon.equals("lose")){
                binding.resultTextView.text = "You Lose!"
                binding.resultTextView.setTextColor(Color.parseColor("#c42b1c"))
                binding.resultImageView.setImageResource(R.drawable.baseline_sentiment_very_dissatisfied_24)
                binding.resultImageView.animate().apply {
                    duration = 2000
                    rotationYBy(360f)
                }.start()
            }
        }

        binding.againBtn.setOnClickListener {
            val action = ResultFragmentDirections.actionResultFragmentToModeFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        binding.accountBtn.setOnClickListener {
            if (auth.currentUser != null) {
                val action = ResultFragmentDirections.actionResultFragmentToAccountFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }else{
                Snackbar.make(requireView(), "you need to login first", Snackbar.LENGTH_INDEFINITE).setAction("login", View.OnClickListener {
                    val action = ResultFragmentDirections.actionResultFragmentToIntoFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                }).show()
            }
        }
    }
}