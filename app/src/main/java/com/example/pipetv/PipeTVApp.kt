package com.example.pipetv

import android.app.Application
import com.example.pipetv.network.InvidiousRepository

class PipeTVApp : Application() {
    // Lazy ensures it doesn't block the main thread during app launch
    val repository: InvidiousRepository by lazy { InvidiousRepository() }
}
