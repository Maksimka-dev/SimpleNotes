package com.epam.training.simplenotes.entity

import android.graphics.Bitmap
import java.util.*

/**
 * Represents note information to display for user.
 */
class VisibleNote(
    var id: String = "",
    var title: String = "",
    var text: String = "",
    var date: Calendar = Calendar.getInstance(),
    var imageBitmap: Bitmap? = null
)