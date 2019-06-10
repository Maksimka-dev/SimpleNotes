package com.epam.training.simplenotes.action

import com.epam.training.simplenotes.entity.VisibleNote

sealed class RecyclerViewAction {
    data class UpdateItems(
        val items: MutableList<VisibleNote>
    ) : RecyclerViewAction()

}
