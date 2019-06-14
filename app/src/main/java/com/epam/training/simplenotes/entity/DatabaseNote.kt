package com.epam.training.simplenotes.entity

/**
 * Represents note information in database.
 */
data class DatabaseNote(
    var id: String = "",
    var title: String = "",
    var text: String = "",
//    var date: String? = null,
    var imageUrl: String? = null
) {
    companion object {
        fun createHelloNote(wantedId: String): DatabaseNote {

            return DatabaseNote(
                wantedId,
                "First note",
                "Hello! This is your very first note in Simple Notes application!"
//                Calendar.getInstance()
            )
        }
    }
}