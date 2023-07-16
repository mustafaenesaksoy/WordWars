package com.enesaksoy.wordwars.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.enesaksoy.wordwars.service.KeyAPI
import com.enesaksoy.wordwars.viewmodel.SignViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WordsFragmentFactory (
    private val auth: FirebaseAuth,
    ): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
       return  when(className){
           ModeFragment::class.java.name -> ModeFragment(auth)
           SignUpFragment::class.java.name -> SignUpFragment()
           SignInFragment::class.java.name -> SignInFragment()
           IntroFragment::class.java.name -> IntroFragment()
           MatchingFragment::class.java.name -> MatchingFragment(auth)
           OnGameFragment::class.java.name -> OnGameFragment()
           AccountFragment::class.java.name -> AccountFragment(auth)
           LevelFragment::class.java.name -> LevelFragment()
           OffGameFragment::class.java.name -> OffGameFragment()
           ResultFragment::class.java.name -> ResultFragment(auth)
           else -> return super.instantiate(classLoader, className)
       }
    }
}