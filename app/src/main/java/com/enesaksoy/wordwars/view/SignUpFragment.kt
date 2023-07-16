package com.enesaksoy.wordwars.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.enesaksoy.wordwars.R
import com.enesaksoy.wordwars.databinding.SignUpFragmentBinding
import com.enesaksoy.wordwars.util.Status
import com.enesaksoy.wordwars.viewmodel.SignViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment: Fragment(R.layout.sign_up_fragment) {
    private lateinit var binding: SignUpFragmentBinding
    private val signViewModel by viewModel<SignViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SignUpFragmentBinding.bind(view)
        binding.signUpBtn.setOnClickListener {
            if(!binding.passwordText.text.toString().equals(binding.rePasswordText.text.toString())){
                Toast.makeText(requireContext(),"passwords do not match.", Toast.LENGTH_SHORT).show()
            } else {
                signViewModel.signUp(
                    binding.emailText.text.toString(),
                    binding.passwordText.text.toString(),
                    binding.nameText.text.toString()
                )
            }
        }
        observeOn()
    }
    private fun observeOn(){
        signViewModel.authResult.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.status == Status.SUCCESS){
                    val action = SignUpFragmentDirections.actionSignUpFragmentToMatchingFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                }else if(it.status == Status.ERROR){
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}