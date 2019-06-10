package com.epam.training.simplenotes.model

import com.epam.training.simplenotes.DatabaseConstants.NOTES_COLLECTION_NAME
import com.epam.training.simplenotes.DatabaseConstants.USERS_COLLECTION_NAME
import com.epam.training.simplenotes.entity.DatabaseNote
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.mapper.DatabaseToVisibleNoteMapper
import com.epam.training.simplenotes.mapper.VisibleToDatabaseNoteMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class DefaultNoteDetailsModel(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val toDatabaseNoteMapper: VisibleToDatabaseNoteMapper,
    private val toVisibleNoteMapper: DatabaseToVisibleNoteMapper
) : NoteDetailsModel {

    override fun saveNote(
        visibleNote: VisibleNote,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        auth.currentUser?.let {
            toDatabaseNoteMapper.map(
                visibleNote,
                it.uid,
                { databaseNote ->
                    db.collection(USERS_COLLECTION_NAME)
                        .document(it.uid)
                        .collection(NOTES_COLLECTION_NAME)
                        .document(databaseNote.id)
                        .set(databaseNote)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onError(exception)
                        }
                },
                { exception ->
                    onError(exception)
                }
            )
        }
    }

    override fun downloadNote(
        noteId: String,
        onSuccess: (VisibleNote) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        auth.currentUser?.let { user ->
            db.collection(USERS_COLLECTION_NAME)
                .document(user.uid)
                .collection(NOTES_COLLECTION_NAME)
                .document(noteId)
                .get()
                .addOnSuccessListener {
                    it.toObject(DatabaseNote::class.java)?.let { databaseNote ->
                        toVisibleNoteMapper.map(
                            databaseNote,
                            user.uid,
                            onSuccess,
                            onError
                        )
                    }
                }.addOnFailureListener {
                    onError(it)
                }
        }
    }

    override fun newNoteId(): String {
        Calendar.getInstance().let {

            return "NOTE_${it.timeInMillis}"
        }
    }

    override fun deleteNote(
        noteId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        auth.currentUser?.let { user ->
            db.collection(USERS_COLLECTION_NAME)
                .document(user.uid)
                .collection(NOTES_COLLECTION_NAME)
                .document(noteId)
                .get().addOnSuccessListener {
                    it.toObject(DatabaseNote::class.java)?.let { databaseNote ->
                        db.collection(USERS_COLLECTION_NAME)
                            .document(user.uid)
                            .collection(NOTES_COLLECTION_NAME)
                            .document(noteId)
                            .delete()
                            .addOnSuccessListener {
                                databaseNote.imageUrl?.let { imageUrl ->
                                    storage.reference.child(imageUrl).delete().addOnSuccessListener {
                                        onSuccess()
                                    }.addOnFailureListener { exception ->
                                        onError(exception)
                                    }
                                } ?: onSuccess()
                            }.addOnFailureListener { exception ->
                                onError(exception)
                            }
                    } ?: onSuccess()
                }.addOnFailureListener { exception ->
                    onError(exception)
                }
        }
    }

}