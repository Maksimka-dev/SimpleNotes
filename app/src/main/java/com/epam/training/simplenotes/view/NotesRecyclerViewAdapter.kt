package com.epam.training.simplenotes.view

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epam.training.simplenotes.R
import com.epam.training.simplenotes.entity.VisibleNote

/**
 * Adapter for RecyclerView in [MainActivity].
 */
class NotesRecyclerViewAdapter(
    var items: MutableList<VisibleNote>
) : RecyclerView.Adapter<NotesRecyclerViewAdapter.NoteViewHolder>() {

    var noteListener: NoteListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.note_card_view, parent, false)

        val holder = NoteViewHolder(cardView)

        cardView.setOnClickListener { view ->
            holder.adapterPosition.takeIf { it != NO_POSITION }?.let {
                noteListener?.onNoteClicked(it, items[it], view)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.display(items[position])
    }

    override fun getItemCount(): Int {

        return items.size
    }

    /**
     * ViewHolder for [NotesRecyclerViewAdapter].
     */
    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val noteView = view.findViewById<NoteView>(R.id.note_view)

        /**
         * Sets up visible data to NoteViewHolder.
         *
         * @param note represents the note to display.
         */
        fun display(note: VisibleNote) {
            noteView.setNoteTitle(note.title)
            noteView.setNoteText(note.text)
            noteView.setNoteDate(note.date.toString())
            note.imageBitmap?.let {
                noteView.imageVisibility = View.VISIBLE
                noteView.setNoteImage(it)
            } ?: run {
                noteView.imageVisibility = View.GONE
            }
        }
    }

    /**
     * Listener for RecyclerView items' events.
     */
    interface NoteListener {
        /**
         * Represents an action to perform, when RecyclerView item is clicked.
         *
         * @param position represents the position of clicked item in RecyclerView.
         * @param note represents the note, that this item of RecyclerView contains.
         * @param view represents view holder.
         */
        fun onNoteClicked(position: Int, note: VisibleNote, view: View)
    }
}