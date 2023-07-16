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

class OnGameFragment : Fragment(R.layout.game_fragment) {
    private lateinit var binding: GameFragmentBinding
    private val viewModel by viewModel<GameViewModel>()
    private val adapter by inject<GameAdapter>()
    private var isHome : Boolean? = null
    private var word : String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = GameFragmentBinding.bind(view)
        binding.gameRecyclerView.adapter = adapter
        binding.gameRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        arguments?.let {
            word = OnGameFragmentArgs.fromBundle(it).word
            isHome = OnGameFragmentArgs.fromBundle(it).isHome
            viewModel.isHome(isHome!!)
            viewModel.getWordList(word!!)
            binding.keywordText.text = "Words that come to mind when talking about '${word}'"
        }

        binding.backButton.setOnClickListener {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Are you sure?")
            alert.setMessage("are you sure you want to go out?")
            alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val action = OnGameFragmentDirections.actionOnGameFragmentToModeFragment()
                Navigation.findNavController(requireView()).navigate(action)
            })
            alert.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
            })
            alert.show()
        }

        observeOn()
    }

    private fun observeOn(){

        viewModel.timer.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.progressBar.progress = it.toInt()
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

        viewModel.isHome.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it){
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
                                    adapter.addWordtoList(answer, requireContext())
                                    adapter.notifyDataSetChanged()
                                    viewModel.addAnswerFStore(it.toString(), false, isHome!!)
                                }else {
                                    val answer = Answer(getWord, true, 1)
                                    adapter.addWordtoList(answer, requireContext())
                                    adapter.notifyDataSetChanged()
                                    binding.wordText.setText("")
                                    viewModel.stopTimer()
                                    viewModel.updateIsHome()
                                    viewModel.addAnswerFStore(getWord, true, isHome!!)
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
                    viewModel.getAnswerFStore(isHome!!)
                }
            }
        })

        viewModel.getAnswerFstore.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.status == Status.SUCCESS){
                    it.data?.let {
                        if (!it.equals("")){
                            val answer = it.split(" ")
                            if (answer[1].equals("true")){
                                val adapterAnswer = Answer(answer[0], true, 2)
                                adapter.addWordtoList(adapterAnswer, requireContext())
                                adapter.notifyDataSetChanged()
                                viewModel.stopTimer()
                                binding.wordText.isEnabled = true
                                viewModel.updateIsHome()
                                viewModel.deleteAnswerFStore(isHome!!)
                            }else if (answer[1].equals("false")){
                                val adapterAnswer = Answer(answer[0], false, 2)
                                adapter.addWordtoList(adapterAnswer, requireContext())
                                adapter.notifyDataSetChanged()
                                viewModel.deleteAnswerFStore(isHome!!)
                                viewModel.getAnswerFStore(isHome!!)
                            }

                        }else{
                            viewModel.getAnswerFStore(isHome!!)
                        }
                    }
                }else if (it.status == Status.ERROR){
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.isCheckWon.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (isHome!! && !it || !isHome!! && !it){
                    val action = OnGameFragmentDirections.actionOnGameFragmentToResultFragment("won")
                    Navigation.findNavController(requireView()).navigate(action)
                    viewModel.addCompettoUser(word!!, "won")
                    adapter.cleanWordList()
                }else{
                    val action = OnGameFragmentDirections.actionOnGameFragmentToResultFragment("lose")
                    Navigation.findNavController(requireView()).navigate(action)
                    viewModel.addCompettoUser(word!!, "lose")
                    adapter.cleanWordList()
                }
                if (isHome!!){
                    viewModel.deleteCompet()
                }
            }
        })
    }
}