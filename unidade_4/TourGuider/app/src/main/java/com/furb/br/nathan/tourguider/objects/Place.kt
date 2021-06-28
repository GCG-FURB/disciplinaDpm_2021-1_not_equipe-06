package com.furb.br.nathan.tourguider.objects

import android.graphics.Bitmap
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Place(val latitude: Double, val longitude: Double, var order: Int, var name:String = "", var description:String = "", @Transient var image: Bitmap? = null)