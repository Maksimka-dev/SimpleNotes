package com.epam.training.simplenotes.view

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epam.training.simplenotes.R
import com.epam.training.simplenotes.entity.VisibleNote

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

    //ViewHolder
    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val noteView = view.findViewById<NoteView>(R.id.note_view)

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

    interface NoteListener {
        fun onNoteClicked(position: Int, note: VisibleNote, view: View)
    }
}