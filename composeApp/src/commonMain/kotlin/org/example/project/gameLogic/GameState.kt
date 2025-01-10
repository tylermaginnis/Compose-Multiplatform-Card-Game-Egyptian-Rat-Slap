package org.example.project.gameLogic

data class GameState(
    val deck: MutableList<Card>,
    val players: MutableList<Player>,
    val currentPlayerIndex: Int,
    val pile: MutableList<Card>,
    val faceUp: Boolean = false,
    val winner: String? = null,
    val scores: Map<String, Int> = emptyMap(),
    val lastSlapType: SlapType? = null
)