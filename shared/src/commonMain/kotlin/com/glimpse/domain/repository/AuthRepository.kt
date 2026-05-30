package com.glimpse.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signInWithGoogle()
    suspend fun checkSession(): Boolean
    fun observeAuthState(): Flow<Boolean>
    suspend fun signOut()
}