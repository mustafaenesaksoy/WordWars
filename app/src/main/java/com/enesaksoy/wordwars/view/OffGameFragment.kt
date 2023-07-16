package com.enesaksoy.wordwars.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.enesaksoy.wordwars.R
import com.enesaksoy.wordwars.adapter.GameAdapter
import com.enesaksoy.wordwars.databinding.GameFragmentBinding
import com.enesaksoy.wordwars.model.Answer
import com.enesaksoy.wordwars.util.Status
import com.enesaksoy.wordwars.viewmodel.GameViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class OffGameFragment: Fragment(R.layout.game_fragment){
    private lateinit var binding: GameFragmentBinding
    private var level: String = ""
    private val viewModel by viewModel<GameViewModel>()
    private val adapter by inject<GameAdapter>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GameFragmentBinding.bind(view)


        binding.gameRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.gameRecyclerView.adapter = adapter

        arguments?.let {
            level = OffGameFragmentArgs.fromBundle(it).level
        }

        binding.backButton.setOnClickListener {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Are you sure?")
            alert.setMessage("are you sure you want to go out?")
            alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val action = OffGameFragmentDirections.actionGameFragmentToModeFragment()
                Navigation.findNavController(requireView()).navigate(action)
            })
            alert.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
            })
            alert.show()
        }
        viewModel.getWord()
        observeOn()
    }


    private fun observeOn() {
        viewModel.timer.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.progressBar.progress = it.toInt()
            }
        })

        viewModel.getWord.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.status == Status.SUCCESS) {
                    it.data?.let { key ->
                        binding.keywordText.text = "Words that come to mind when talking about '${key[0]}'"
                        viewModel.getWordList(it.data[0])
                    }
                } else if (it.status == Status.ERROR) {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.getwordList.observe(viewLifecycleOwner, Observer {
            if (it.status == Status.SUCCESS) {
                it.data?.let {
                    viewModel.responseConverttoList(it.response[0])
                }
            } else if (it.status == Status.ERROR) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        })


        viewModel.isFinished.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
                    val action = OffGameFragmentDirections.actionGameFragmentToResultFragment("lose")
                    Navigation.findNavController(requireView()).navigate(action)
                    viewModel.stopTimer()
                    adapter.cleanWordList()
                }else if (!it){
                    val action = OffGameFragmentDirections.actionGameFragmentToResultFragment("won")
                    Navigation.findNavController(requireView()).navigate(action)
                    viewModel.stopTimer()
                    adapter.cleanWordList()
                }
            }
        })

        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == 1){
                    viewModel.startTimer(30000)
                    binding.wordText.hint = "Enter the word"
                    binding.wordText.addTextChangedListener {
                        if (it.isNullOrEmpty()){
                            binding.sendBtn.setBackgroundResource(R.drawable.with_btn_background)
                            binding.sendBtn.isClickable = false
                            binding.wordText.hint ="Enter the word"
                        }else{
                            binding.wordText.hint = "Enter the word"
                            binding.sendBtn.isClickable = !it.equals("")
                            binding.sendBtn.setBackgroundResource(R.drawable.other_btn_background)
                            binding.sendBtn.setOnClickListener {view ->
                                val getWord = viewModel.checkListforItem(it.toString())
                                if (getWord.equals("")){
                                    val answer = Answer(it.toString(), false, 1)
                                    adapter!!.addWordtoList(answer, requireContext())
                                    adapter!!.notifyDataSetChanged()
                                }else {
                                    val answer = Answer(getWord, true, 1)
                                    adapter!!.addWordtoList(answer, requireContext())
                                    adapter!!.notifyDataSetChanged()
                                    binding.wordText.setText("")
                                    viewModel.stopTimer()
                                    viewModel.changeOrder()
                                }
                            }
                        }
                    }
                }else{
                    viewModel.startTimer(30000)
                    binding.sendBtn.isClickable = false
                    binding.wordText.isEnabled = false
                    binding.wordText.hint = "Wait for the your opponent's answer"
                    binding.sendBtn.setBackgroundResource(R.drawable.with_btn_background)
                    val offAnswer = viewModel.offlineAnswer(level)
                    viewModel.checkListforItem(offAnswer)
                    if (!offAnswer.equals("")){
                        val answer = Answer(offAnswer,true,2)
                        adapter!!.addWordtoList(answer, requireContext())
                        adapter!!.notifyDataSetChanged()
                        viewModel.stopTimer()
                        viewModel.changeOrder()
                        binding.wordText.isEnabled = true
                    }
                }
            }
        })
    }
}