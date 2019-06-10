package com.epam.training.simplenotes.koin

import com.epam.training.simplenotes.mapper.DatabaseToVisibleNoteMapper
import com.epam.training.simplenotes.mapper.VisibleToDatabaseNoteMapper
import com.epam.training.simplenotes.model.*
import com.epam.training.simplenotes.util.DefaultImageLoader
import com.epam.training.simplenotes.util.ImageLoader
import com.epam.training.simplenotes.viewmodel.LoginViewModel
import com.epam.training.simplenotes.viewmodel.MainViewModel
import com.epam.training.simplenotes.viewmodel.NoteDetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val auth: FirebaseAuth = FirebaseAuth.getInstance()

val firebaseModule = module {
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
}

val mapperModule = module {
    single<ImageLoader> { DefaultImageLoader(get()) }

    single { DatabaseToVisibleNoteMapper(get()) }
    single { VisibleToDatabaseNoteMapper(get()) }
}

val loginModule = module {
    //    single<Storage> { SharedPreferencesStorage(PreferenceManager.getDefaultSharedPreferences(androidContext())) }
    single<LoginModel> { DefaultLoginModel(auth, get()) }

    viewModel { LoginViewModel(get()) }
//    scope(named<LoginActivity>()) {
//        viewModel { LoginViewModel(get()) }
//    }
}

val mainModule = module {
    single<MainModel> { DefaultMainModel(auth, get(), get()) }
    viewModel { MainViewModel(get()) }

    single<NoteDetailsModel> { DefaultNoteDetailsModel(auth, get(), get(), get(), get()) }
    viewModel { NoteDetailsViewModel(get()) }
}