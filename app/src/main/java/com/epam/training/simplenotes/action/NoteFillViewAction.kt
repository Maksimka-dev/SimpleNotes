package com.epam.training.simplenotes.action

import com.epam.training.simplenotes.entity.VisibleNote

sealed class NoteFillViewAction {
    data class FillNote(
        val note: VisibleNote
    ): NoteFillViewAction()
}