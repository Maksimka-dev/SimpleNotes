package com.epam.training.simplenotes.action

sealed class DialogViewAction {
    object Show: DialogViewAction()
    object Hide: DialogViewAction()
}