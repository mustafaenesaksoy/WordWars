package com.enesaksoy.wordwars.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.enesaksoy.wordwars.R
import com.enesaksoy.wordwars.adapter.GameAdapter
import com.enesaksoy.wordwars.adapter.UserCompetAdapter
import com.enesaksoy.wordwars.databinding.AccountFragmentBinding
import com.enesaksoy.wordwars.util.Status
import com.enesaksoy.wordwars.viewmodel.AccountViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment(private val auth: FirebaseAuth): Fragment(R.layout.account_fragment) {
    private lateinit var binding: AccountFragmentBinding
    private val viewModel by viewModel<AccountViewModel>()
    private val adapter by inject<UserCompetAdapter>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AccountFragmentBinding.bind(view)
        binding.competitionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.competitionsRecyclerView.adapter = adapter
        binding.logOut.setOnClickListener {
            if (auth != null) {
                auth.signOut()
                val action = AccountFragmentDirections.actionAccountFragmentToModeFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }
        }
        auth.currentUser?.let {
            binding.userName.text = it.displayName
            viewModel.getUserCompet()
        }
        observeOn()
    }

    private fun observeOn(){
        viewModel.getUserCompet.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.status == Status.SUCCESS){
                    it.data?.let {
                        if (it.size > 1) {
                            adapter.competList = it
                            adapter.notifyDataSetChanged()
                        }
                    }
                }else if(it.status == Status.ERROR){
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}