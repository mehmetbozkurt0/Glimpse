package com.glimpse.data.repository

import com.glimpse.data.network.SupabaseClient
import com.glimpse.domain.repository.AuthRepository
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl : AuthRepository {
    override suspend fun signInWithGoogle() {
        try {
            SupabaseClient.client.auth.signInWith(Google)
        } catch (e: Exception) {
            println("GLIMPSE_HATA: Çıkış yapılırken hata oluştu -> ${e.message}")
        }
    }

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

    override fun observeAuthState(): Flow<Boolean> {
        return SupabaseClient.client.auth.sessionStatus.map { status ->
            status is SessionStatus.Authenticated
        }
    }

    override suspend fun signOut() {
        try {
            SupabaseClient.client.auth.signOut()
        } catch (e: Exception) {
            println("GLIMPSE_HATA: Çıkış yapılırken hata oluştu -> ${e.message}")
        }
    }
}