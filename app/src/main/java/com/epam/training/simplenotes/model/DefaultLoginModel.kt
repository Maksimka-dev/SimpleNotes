package com.epam.training.simplenotes.model

import com.epam.training.simplenotes.DatabaseConstants.DEFAULT_USER_NAME
import com.epam.training.simplenotes.DatabaseConstants.EMAIL_FIELD_NAME
import com.epam.training.simplenotes.DatabaseConstants.NOTES_COLLECTION_NAME
import com.epam.training.simplenotes.DatabaseConstants.USERS_COLLECTION_NAME
import com.epam.training.simplenotes.entity.DatabaseNote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DefaultLoginModel(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : LoginModel {

    override fun isUserAuthenticated(): Boolean {

        return auth.currentUser != null
    }

    override fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    onError()
                }
            }
    }

    override fun registerAccount(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    createUserDataInDB(email, onSuccess, onError)
//                    onSuccess()
                } else {
                    onError()
                }
            }
    }

    private fun createUserDataInDB(
        email: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val newUser = HashMap<String, Any>()
        newUser[EMAIL_FIELD_NAME] = email

        db.collection(USERS_COLLECTION_NAME)
            .document(auth.currentUser?.uid ?: DEFAULT_USER_NAME)
            .set(newUser)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val firstNoteRef = db.collection(USERS_COLLECTION_NAME)
                        .document(auth.currentUser?.uid ?: DEFAULT_USER_NAME)
                        .collection(NOTES_COLLECTION_NAME)
                        .document()

                    val firstNoteData = DatabaseNote.createHelloNote(firstNoteRef.id)

                    firstNoteRef
                        .set(firstNoteData)
                        .addOnCompleteListener { itt ->
                            if (itt.isSuccessful) {
                                onSuccess()
//                                Log.d("CHECK_UID", auth.currentUser?.uid)
                            } else {
                                onError()
//                                Log.d("CHECK_FAILED", itt.exception?.localizedMessage)
                            }
                        }
                } else {
                    onError()
//                    Log.d("CHECK_FAILED", it.exception?.localizedMessage)
                }
            }

    }
}
