package com.enesaksoy.wordwars.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.enesaksoy.wordwars.R
import com.enesaksoy.wordwars.databinding.SignInFragmentBinding
import com.enesaksoy.wordwars.util.Status
import com.enesaksoy.wordwars.viewmodel.SignViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInFragment : Fragment(R.layout.sign_in_fragment) {
    private lateinit var binding : SignInFragmentBinding
    private val signViewModel by viewModel<SignViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SignInFragmentBinding.bind(view)
        binding.signInBtn.setOnClickListener {
            signViewModel.signIn(binding.emailText.text.toString(),binding.passwordText.text.toString())
        }
        observeOn()
    }

    private fun observeOn(){
        signViewModel.authResult.observe(viewLifecycleOwner, Observer {
            it?.let {
               if (it.status == Status.SUCCESS){
                   val action = SignInFragmentDirections.actionSignInFragmentToMatchingFragment()
                   Navigation.findNavController(requireView()).navigate(action)
               }else if(it.status == Status.ERROR){
                   Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
               }
            }
        })
    }
}