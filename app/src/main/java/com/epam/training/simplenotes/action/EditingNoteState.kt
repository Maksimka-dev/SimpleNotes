package com.epam.training.simplenotes.action

sealed class EditingNoteState {
    object Start: EditingNoteState()
    object Success: EditingNoteState()
    object Error: EditingNoteState()
}