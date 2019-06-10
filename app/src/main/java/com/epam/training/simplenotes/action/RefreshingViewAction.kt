package com.epam.training.simplenotes.action

sealed class RefreshingViewAction {
    object Start: RefreshingViewAction()
    object Stop: RefreshingViewAction()
}