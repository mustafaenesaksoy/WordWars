package com.enesaksoy.wordwars.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.enesaksoy.wordwars.R
import com.enesaksoy.wordwars.databinding.MatchingFragmentBinding
import com.enesaksoy.wordwars.util.Status
import com.enesaksoy.wordwars.viewmodel.MatchingViewModel
import com.enesaksoy.wordwars.viewmodel.SignViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MatchingFragment (private val auth: FirebaseAuth): Fragment(R.layout.matching_fragment) {
    private lateinit var binding: MatchingFragmentBinding
    private val viewModel by viewModel<MatchingViewModel>()
    val delayInMillis = 2000L
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MatchingFragmentBinding.bind(view)
        viewModel.updateIsActive()
        binding.player1.text = auth.currentUser!!.displayName
        observeOn()
    }

    private fun observeOn(){

        viewModel.isactive.observe(viewLifecycleOwner, Observer {
            it?.let {
               viewModel.addActiveUser(it)
            }
        })

        viewModel.addactiveUser.observe(viewLifecycleOwner, Observer {
            it?.let { it ->
                if (it.status == Status.SUCCESS){
                    it.data?.let { number ->
                        if (number != 0 && number%2 == 0){
                            viewModel.navigateCompet(number)
                        }else if (number != 0 && number%2 != 0){
                            viewModel.getWord()
                            viewModel.number = number
                        }
                    }
                }else if (it.status == Status.ERROR){
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.makeMatch.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.status == Status.SUCCESS){
                    it.data?.let { it ->
                        if (it.first){
                            binding.player2.text = it.second
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.startText.visibility = View.VISIBLE
                            viewModel.updateIsActive()

                            Handler(Looper.getMainLooper()).postDelayed({
                                val action = MatchingFragmentDirections.actionMatchingFragmentToOnGameFragment(viewModel.selectedWord!!, true)
                                Navigation.findNavController(requireView()).navigate(action)
                                binding.startText.visibility = View.INVISIBLE
                            }, delayInMillis)
                        }else{
                            viewModel.getWord()
                        }
                    }
                }else if (it.status == Status.ERROR){
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.navigateCompet.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.status == Status.SUCCESS){
                    it.data?.let {
                        if (!it.first.equals("")) {
                            binding.player2.text = it.second
                            viewModel.updateIsActive()
                            binding.startText.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.INVISIBLE

                            Handler(Looper.getMainLooper()).postDelayed({
                                val action =
                                    MatchingFragmentDirections.actionMatchingFragmentToOnGameFragment(
                                        it.first,
                                        false
                                    )
                                Navigation.findNavController(requireView()).navigate(action)
                                binding.startText.visibility = View.INVISIBLE
                            }, delayInMillis)
                        }
                    }
                }else if (it.status == Status.ERROR){
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.getword.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.status == Status.SUCCESS){
                    it.data?.let {
                        viewModel.makeMatch(it[0])
                    }
                }else if (it.status == Status.ERROR){
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}