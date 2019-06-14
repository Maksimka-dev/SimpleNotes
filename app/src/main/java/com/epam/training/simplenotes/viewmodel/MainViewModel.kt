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

/**
 * ViewModel for MainActivity and its content.
 */
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

    /**
     * Calls to model to get a list of all saved user notes and notifies RecyclerView in MainActivity
     * about data changes via LiveData.
     */
    @UiThread
    fun loadUserNotes() {
        _refreshingAction.value = RefreshingViewAction.Start
        mainModel.loadUserNotes(
            { items ->
                _refreshingAction.postValue(RefreshingViewAction.Stop)
                _recyclerViewAction.postValue(RecyclerViewAction.UpdateItems(items))
            },
            {
                _refreshingAction.postValue(RefreshingViewAction.Stop)
            }
        )
    }

    /**
     * Calls to model to sign out from application.
     */
    fun signOut() {
        mainModel.signOut {
            _signOutResult.postValue(true)
        }
    }

    /**
     * Allows MainActivity to create new note and redirect user to DetailsActivity.
     */
    fun onFloatingButtonClicked() {
        _activityViewAction.value = MainActivityViewAction.GoToNewNoteDetails
    }

    /**
     * Allows MainActivity to redirect user to DetailsActivity and open selected note.
     *
     * @param note represents the note to edit.
     */
    fun onNoteClicked(note: VisibleNote) {
        _activityViewAction.value = MainActivityViewAction.GoToExistingNoteDetails(note.id)
    }
}