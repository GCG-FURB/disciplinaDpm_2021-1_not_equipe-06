package com.furb.br.nathan.tourguider.objects

import kotlinx.serialization.Serializable

@Serializable
data class Route(val places: List<Place>) {
}