package com.enesaksoy.wordwars.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.enesaksoy.wordwars.R
import com.enesaksoy.wordwars.databinding.IntroFragmentBinding

class IntroFragment: Fragment(R.layout.intro_fragment) {
    private lateinit var binding : IntroFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = IntroFragmentBinding.bind(view)
        binding.signInBtn.setOnClickListener {
            val action = IntroFragmentDirections.actionIntoFragmentToSignInFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.signUpBtn.setOnClickListener {
            val action = IntroFragmentDirections.actionIntoFragmentToSignUpFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
    }
}