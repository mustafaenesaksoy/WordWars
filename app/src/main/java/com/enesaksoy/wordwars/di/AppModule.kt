package com.enesaksoy.wordwars.di

import android.os.Handler
import androidx.fragment.app.FragmentFactory
import com.enesaksoy.wordwars.adapter.GameAdapter
import com.enesaksoy.wordwars.adapter.UserCompetAdapter
import com.enesaksoy.wordwars.repo.WordRepository
import com.enesaksoy.wordwars.repo.WordRepositoryImpl
import com.enesaksoy.wordwars.service.KeyAPI
import com.enesaksoy.wordwars.service.WordAPI
import com.enesaksoy.wordwars.util.KEY_BASE_URL
import com.enesaksoy.wordwars.util.WORD_BASE_URL
import com.enesaksoy.wordwars.view.WordsFragmentFactory
import com.enesaksoy.wordwars.viewmodel.AccountViewModel
import com.enesaksoy.wordwars.viewmodel.GameViewModel
import com.enesaksoy.wordwars.viewmodel.MatchingViewModel
import com.enesaksoy.wordwars.viewmodel.SignViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single {
        Retrofit.Builder()
            .baseUrl(KEY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(KeyAPI::class.java)
    }

    single {
        Retrofit.Builder()
            .baseUrl(WORD_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WordAPI::class.java)
    }
    single <WordRepository>{
        WordRepositoryImpl(get(), get(), get(), get())
    }
    single {
        FirebaseAuth.getInstance()
    }

    single {
        FirebaseFirestore.getInstance()
    }

    single <FragmentFactory>{
        WordsFragmentFactory(get())
    }

    single {
        GameAdapter()
    }

    single {
        UserCompetAdapter()
    }

    viewModel {
        SignViewModel(get())
    }

    viewModel {
        GameViewModel(get())
    }

    viewModel {
        MatchingViewModel(get())
    }

    viewModel {
        AccountViewModel(get())
    }

}