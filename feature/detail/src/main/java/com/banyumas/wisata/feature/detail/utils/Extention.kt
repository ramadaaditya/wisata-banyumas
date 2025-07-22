package com.banyumas.wisata.feature.detail.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun openGoogleMaps(context: Context, lat: Double?, long: Double?) {
    val uri = if (lat != null && long != null) {
        "geo:$lat,$long?q=$lat,$long".toUri()
    } else {
        "https://maps.google.com/".toUri()
    }
    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(mapIntent)
}


