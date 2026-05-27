package com.glimpse.data.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://jutayyajnxbfltmwzlbg.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imp1dGF5eWFqbnhiZmx0bXd6bGJnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk4MTY0NjksImV4cCI6MjA5NTM5MjQ2OX0.vzHOJ6E6_JAy7X_IxivK2fitxmC5erI1jtizObw4s3k"
    ) {
        install(Postgrest)
        install(Realtime)
        install(Auth)
    }
}