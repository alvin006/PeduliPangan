package com.alvintio.pedulipangan.model

import com.google.firebase.firestore.FieldValue

data class Notification(
    val uid: String,
    val date: String,
    val message: String,
    val namaPemesan: String,
    val namaRestoran: String
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "uid" to uid,
            "date" to date,
            "message" to message,
            "namaPemesan" to namaPemesan,
            "namaRestoran" to namaRestoran
        )
    }
}
