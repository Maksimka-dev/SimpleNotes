package com.epam.training.simplenotes.model

import android.util.Log
import com.epam.training.simplenotes.DatabaseConstants.NOTES_COLLECTION_NAME
import com.epam.training.simplenotes.DatabaseConstants.USERS_COLLECTION_NAME
import com.epam.training.simplenotes.entity.DatabaseNote
import com.epam.training.simplenotes.entity.VisibleNote
import com.epam.training.simplenotes.mapper.DatabaseToVisibleNoteMapper
import com.epam.training.simplenotes.mapper.VisibleToDatabaseNoteMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

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
        visibleNote.imageBitmap?.let {
            getSavedImageUrl(visibleNote.id) { imageUrl ->
                deleteImage(imageUrl)
            }
        }

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

    private fun getSavedImageUrl(
        noteId: String,
        onSuccess: (String?) -> Unit
    ) {
        auth.currentUser?.let { user ->
            db.collection(USERS_COLLECTION_NAME)
                .document(user.uid)
                .collection(NOTES_COLLECTION_NAME)
                .document(noteId)
                .get()
                .addOnSuccessListener {
                    it.toObject(DatabaseNote::class.java)?.let { databaseNote ->
                        onSuccess(databaseNote.imageUrl)
                        Log.d("DELETING", databaseNote.imageUrl)
                    }
                }
        }
    }

    private fun deleteImage(
        imageUrl: String?
    ) {
//        var result = false

        imageUrl?.let {
            storage.reference.child(imageUrl).delete()
                .addOnSuccessListener {
                    Log.d("DELETING", "image deleted from storage")
//                    result = true
                }.addOnFailureListener {
//                    result = false
                    Log.d("DELETING", "failed to delete image from storage")
                }
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

        return "NOTE_${System.currentTimeMillis()}"
    }

    override fun deleteNote(
        noteId: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        getSavedImageUrl(noteId) { savedImageUrl ->
            deleteImage(savedImageUrl)
        }

        auth.currentUser?.let { user ->
            db.collection(USERS_COLLECTION_NAME)
                .document(user.uid)
                .collection(NOTES_COLLECTION_NAME)
                .document(noteId)
                .delete()
                .addOnSuccessListener {
                    onSuccess()
                }
//                .get().addOnSuccessListener {
//                    it.toObject(DatabaseNote::class.java)?.let { databaseNote ->
//                        db.collection(USERS_COLLECTION_NAME)
//                            .document(user.uid)
//                            .collection(NOTES_COLLECTION_NAME)
//                            .document(noteId)
//                            .delete()
//                            .addOnSuccessListener {
//                                databaseNote.imageUrl?.let { imageUrl ->
//                                    storage.reference.child(imageUrl).delete()
//                                        .addOnSuccessListener {
//                                            onSuccess()
//                                        }.addOnFailureListener { exception ->
//                                            onError(exception)
//                                        }
//                                } ?: onSuccess()
//                            }.addOnFailureListener { exception ->
//                                onError(exception)
//                            }
//                    } ?: onSuccess()
//                }
                .addOnFailureListener { exception ->
                    onError(exception)
                }
        }
    }

}