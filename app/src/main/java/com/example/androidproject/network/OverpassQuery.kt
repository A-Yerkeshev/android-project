package com.example.androidproject.network

object OverpassQuery {
    fun buildQuery(radius: Int, lat: Double, lon: Double): String {
        return "[out:json];" +
                "(node(around:$radius,$lat,$lon)[tourism~\"attraction|monument|museum|artwork|viewpoint\"];" +
                "node(around:$radius,$lat,$lon)[\"historic\"];);" +
                "out body;"
    }
}