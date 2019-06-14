package com.epam.training.simplenotes.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.UiThread
import com.epam.training.simplenotes.action.DialogViewAction
import com.epam.training.simplenotes.action.EditingNoteState
import com.epam.training.simplenotes.action.NoteFillViewAction
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.model.NoteDetailsModel

/**
 * ViewModel for NoteDetailsActivity and its content.
 */
class NoteDetailsViewModel(
    private val noteModel: NoteDetailsModel
) : ViewModel() {

    private val _dialogViewAction = MutableLiveData<DialogViewAction>()
    val dialogViewAction: LiveData<DialogViewAction>
        get() = _dialogViewAction

    private val _editingNoteState = MutableLiveData<EditingNoteState>()
    val editingNoteState: LiveData<EditingNoteState>
        get() = _editingNoteState

    private val _noteFillViewAction = MutableLiveData<NoteFillViewAction>()
    val noteFillViewAction: LiveData<NoteFillViewAction>
        get() = _noteFillViewAction

    private var currentId: String? = null

    /**
     * Provides existing note id or generates new one for new note.
     */
    fun getNoteId(): String {
        currentId?.let {
            return it
        } ?: run {
            val id = noteModel.newNoteId()
            currentId = id

            return id
        }
    }

    /**
     * Calls to model to save note to database and notifies NoteDetailsActivity
     * about start and end of operation for displaying loading dialog via LiveData.
     */
    @UiThread
    fun saveNote(visibleNote: VisibleNote) {
        _dialogViewAction.value = DialogViewAction.Show
        _editingNoteState.value = EditingNoteState.Start

        noteModel.saveNote(
            visibleNote,
            {
                _editingNoteState.postValue(EditingNoteState.Success)
                _dialogViewAction.postValue(DialogViewAction.Hide)
            },
            {
                handleError(it)
            }
        )
    }

    private fun handleError(throwable: Throwable) {
        _dialogViewAction.postValue(DialogViewAction.Hide)
        _editingNoteState.postValue(EditingNoteState.Error)
    }

    /**
     * Calls to model to load note's info from database and notifies NoteDetailsActivity
     * about start and end of operation for displaying loading dialog via LiveData.
     */
    @UiThread
    fun loadNote(id: String) {
        _editingNoteState.value = EditingNoteState.Start
        _dialogViewAction.value = DialogViewAction.Show

        currentId = id

        noteModel.downloadNote(
            id,
            {
                _noteFillViewAction.postValue(NoteFillViewAction.FillNote(it))
                _dialogViewAction.postValue(DialogViewAction.Hide)
            },
            {
                handleError(it)
            }
        )
    }

    /**
     * Calls to model to delete existing note from database and notifies NoteDetailsActivity
     * about start and end of operation for displaying loading dialog via LiveData.
     */
    fun onDeleteButtonClicked() {
        _editingNoteState.value = EditingNoteState.Start
        _dialogViewAction.value = DialogViewAction.Show

        currentId?.let {
            noteModel.deleteNote(
                it,
                {
                    _editingNoteState.postValue(EditingNoteState.Success)
                    _dialogViewAction.postValue(DialogViewAction.Hide)
                },
                { exception ->
                    handleError(exception)
                }
            )
        }
    }

}