package org.example.project.gameLogic

open class Player(val name: String) {
    val hand: MutableList<Card> = mutableListOf()
    val score: Int
        get() = hand.size

    override fun toString(): String {
        return name
    }
}