package com.epam.training.simplenotes.model

import android.util.Log
import com.epam.training.simplenotes.DatabaseConstants.DEFAULT_USER_NAME
import com.epam.training.simplenotes.DatabaseConstants.NOTES_COLLECTION_NAME
import com.epam.training.simplenotes.DatabaseConstants.USERS_COLLECTION_NAME
import com.epam.training.simplenotes.entity.DatabaseNote
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.mapper.DatabaseToVisibleNoteMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DefaultMainModel(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val mapper: DatabaseToVisibleNoteMapper
) : MainModel {

    override fun loadUserNotes(
        onSuccess: (MutableList<VisibleNote>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val userNotes = mutableListOf<VisibleNote>()
        val curUserId = auth.currentUser?.uid

        db.collection(USERS_COLLECTION_NAME)
            .document(curUserId ?: DEFAULT_USER_NAME)
            .collection(NOTES_COLLECTION_NAME)
            .get()
            .addOnSuccessListener { notes ->
                for (note in notes) {
                    val databaseNote = note.toObject(DatabaseNote::class.java)

                    mapper.map(
                        databaseNote,
                        curUserId ?: DEFAULT_USER_NAME,
                        {
                            userNotes.add(it)
                            Log.d("SimpleNotesLog", logMessage(databaseNote, it))

                            if (userNotes.size == notes.size()) {
                                onSuccess(userNotes)
                            }
                        },
                        {
                            onError(it)
                            Log.e("DefaultMainModel", it.message)
                        }
                    )
                }
            }
            .addOnFailureListener {
                onError(it)
            }
    }

    private fun logMessage(databaseNote: DatabaseNote, visibleNote: VisibleNote): String? {

        return "Note \"${visibleNote.title}\" has image URL = ${databaseNote.imageUrl != null}, " +
                "has bitmap = ${visibleNote.imageBitmap != null}"
    }

    override fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        onComplete()
    }
}