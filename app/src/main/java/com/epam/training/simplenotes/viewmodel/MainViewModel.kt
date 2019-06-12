package com.epam.training.simplenotes.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.UiThread
import android.util.Log
import com.epam.training.simplenotes.action.MainActivityViewAction
import com.epam.training.simplenotes.action.RecyclerViewAction
import com.epam.training.simplenotes.action.RefreshingViewAction
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.model.MainModel
import com.epam.training.simplenotes.util.SingleLiveEvent

class MainViewModel(private val mainModel: MainModel) : ViewModel() {

    private val _recyclerViewAction = MutableLiveData<RecyclerViewAction>()
    val recyclerViewAction: LiveData<RecyclerViewAction>
        get() = _recyclerViewAction

    private val _signOutResult = MutableLiveData<Boolean>()
    val signOutResult: LiveData<Boolean>
        get() = _signOutResult

    private val _activityViewAction = SingleLiveEvent<MainActivityViewAction>()
    val activityViewAction: LiveData<MainActivityViewAction>
        get() = _activityViewAction

    private val _refreshingAction = MutableLiveData<RefreshingViewAction>()
    val refreshingAction: LiveData<RefreshingViewAction>
        get() = _refreshingAction


    @UiThread
    fun loadUserNotes() {
        _refreshingAction.value = RefreshingViewAction.Start
        mainModel.loadUserNotes(
            { items ->
                _refreshingAction.postValue(RefreshingViewAction.Stop)
                _recyclerViewAction.postValue(RecyclerViewAction.UpdateItems(items))
            },
            {
                Log.e("MainViewModel", it.message)
            }
        )
    }

    fun signOut() {
        mainModel.signOut {
            _signOutResult.postValue(true)
        }
    }

    fun onFloatingButtonClicked() {
        _activityViewAction.value = MainActivityViewAction.GoToNewNoteDetails
    }

    fun onNoteClicked(note: VisibleNote) {
        _activityViewAction.value = MainActivityViewAction.GoToExistingNoteDetails(note.id)
    }
}