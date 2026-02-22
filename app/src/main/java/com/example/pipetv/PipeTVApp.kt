package com.example.pipetv

import android.app.Application
import com.example.pipetv.network.InvidiousRepository

class PipeTVApp : Application() {
    // Single instance, initialized only when first accessed
    lateinit var repository: InvidiousRepository

    override fun onCreate() {
        super.onCreate()
        repository = InvidiousRepository()
    }
}
