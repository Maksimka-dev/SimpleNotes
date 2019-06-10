package com.epam.training.simplenotes.entity

import android.graphics.Bitmap
import android.net.Uri
import java.util.*

class VisibleNote(
    var id: String = "",
    var title: String = "",
    var text: String = "",
    var date: Calendar = Calendar.getInstance(),
    var imageBitmap: Bitmap? = null
)