package com.epam.training.simplenotes.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.epam.training.simplenotes.R

class NoteView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val noteTitle: TextView by lazy {
        findViewById<TextView>(R.id.note_title)
    }
    private val noteText: TextView by lazy {
        findViewById<TextView>(R.id.note_text)
    }
    private val noteDate: TextView by lazy {
        findViewById<TextView>(R.id.note_date)
    }
    private val noteImage: ImageView by lazy {
        findViewById<ImageView>(R.id.note_image)
    }

    var imageVisibility: Int
        get() = noteImage.visibility
        set(value) {
            noteImage.visibility = value
        }

    init {
        View.inflate(context, R.layout.note_view, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NoteView)
        noteText.maxLines = attributes.getInteger(R.styleable.NoteView_noteTextMaxLines, 2)
        attributes.recycle()
    }

    fun setNoteTitle(title: String) {
        noteTitle.text = title
    }

    fun setNoteText(text: String) {
        noteText.text = text
    }

    fun setNoteDate(date: String) {
        noteDate.text = date
    }

    fun setNoteImage(bitmap: Bitmap) {
        noteImage.setImageBitmap(bitmap)
    }

    fun setNoteImage(drawableResId: Int) {
        val drawable: Drawable = resources.getDrawable(drawableResId, null)
        noteImage.setImageDrawable(drawable)
    }
}