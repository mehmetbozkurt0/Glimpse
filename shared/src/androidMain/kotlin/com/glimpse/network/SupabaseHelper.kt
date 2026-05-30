package com.glimpse

import android.content.Intent
import com.glimpse.data.network.SupabaseClient
import io.github.jan.supabase.gotrue.handleDeeplinks

fun handleSupabaseDeeplink(intent: Intent) {
    try {
        SupabaseClient.client.handleDeeplinks(intent)
    } catch (e: Exception) {
        println("GLIMPSE_HATA: Deep link işlenemedi -> ${e.message}")
    }
}