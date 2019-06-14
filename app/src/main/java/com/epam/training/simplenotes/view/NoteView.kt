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

/**
 * Custom view, that contains visible note information, such as note's title, text and attached image.
 */
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

    /**
     * Allows to set up [noteTitle] TextView text.
     */
    fun setNoteTitle(title: String) {
        noteTitle.text = title
    }

    /**
     * Allows to set up [noteText] TextView text.
     */
    fun setNoteText(text: String) {
        noteText.text = text
    }

    /**
     * Allows to set up [noteDate] TextView text.
     */
    fun setNoteDate(date: String) {
        noteDate.text = date
    }

    /**
     * Allows to set up [noteImage] ImageView.
     *
     * @param bitmap contains image, that will be shown in [noteImage] ImageView
     */
    fun setNoteImage(bitmap: Bitmap) {
        noteImage.setImageBitmap(bitmap)
    }

    /**
     * Allows to set up [noteImage] ImageView.
     *
     * @param drawableResId contains resource id of an image, that will be shown in [noteImage] ImageView
     */
    fun setNoteImage(drawableResId: Int) {
        val drawable: Drawable = resources.getDrawable(drawableResId, null)
        noteImage.setImageDrawable(drawable)
    }
}