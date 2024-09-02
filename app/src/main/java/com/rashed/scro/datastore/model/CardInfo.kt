package com.rashed.scro.datastore.model


import kotlinx.serialization.Serializable

@Serializable
data class CardInfo(
    val cards: List<Card> = List(4) { Card() }
) {
    @Serializable
    data class Card(
        val value: String = "",
        val isSuspended: Boolean = false
    )
}
