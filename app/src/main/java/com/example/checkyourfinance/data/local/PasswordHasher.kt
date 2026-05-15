package com.example.checkyourfinance.data.local

import java.security.MessageDigest

/**
 * Educational helper only: hashes a password with SHA-256 for local demo storage.
 * Production apps should use a dedicated password API (e.g. bcrypt via a server).
 */
object PasswordHasher {

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
