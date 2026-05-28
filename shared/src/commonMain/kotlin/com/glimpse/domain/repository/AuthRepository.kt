package com.glimpse.domain.repository

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signInWithGoogle()
    suspend fun checkSession(): Boolean
    suspend fun signOut()
}