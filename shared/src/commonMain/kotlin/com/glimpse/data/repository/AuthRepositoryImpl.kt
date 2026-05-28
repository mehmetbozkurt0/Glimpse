package com.glimpse.data.repository

import com.glimpse.data.network.SupabaseClient
import com.glimpse.domain.repository.AuthRepository
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

class AuthRepositoryImpl : AuthRepository {

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            SupabaseClient.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkSession(): Boolean {
        return SupabaseClient.client.auth.currentSessionOrNull() != null
    }

    override suspend fun signOut() {
        try {
            SupabaseClient.client.auth.signOut()
        } catch (e: Exception) {
            println("GLIMPSE_HATA: Çıkış yapılırken hata oluştu -> ${e.message}")
        }
    }
}