package com.example.androidproject.ui.components

import androidx.compose.foundation.layout.fillMaxSize


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import java.util.concurrent.TimeUnit

@Composable
fun ConfettiAnimation() {
    val party = remember {
        Party(
            emitter = Emitter(duration = 3, TimeUnit.SECONDS).max(100),
            position = Position.Relative(0.5, 0.5),
            spread = 360,
            shapes = listOf(Shape.Circle, Shape.Square),
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def)
        )
    }

    KonfettiView(
        parties = listOf(party),
        modifier = Modifier.fillMaxSize()
    )
}
