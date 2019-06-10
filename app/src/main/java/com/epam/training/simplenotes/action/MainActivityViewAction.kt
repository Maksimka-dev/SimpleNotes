package com.epam.training.simplenotes.action

sealed class MainActivityViewAction {
    object GoToNewNoteDetails: MainActivityViewAction()
    data class GoToExistingNoteDetails(
        val noteId: String
    ): MainActivityViewAction()
}