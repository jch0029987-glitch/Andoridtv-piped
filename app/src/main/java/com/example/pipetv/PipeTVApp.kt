package com.example.pipetv

import android.app.Application
import com.example.pipetv.network.InvidiousRepository

class PipeTVApp : Application() {
    // This allows the screens to access the repository via the 'app' parameter
    val repository = InvidiousRepository()
}
