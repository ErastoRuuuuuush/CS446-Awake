package com.cs446.awake.model

import com.badlogic.gdx.utils.Array
import java.io.FileReader
//import com.google.gson.Gson


// information about the player progress
// Todo: read from file
// the current Enemy in battle
public var enemy : Enemy? = null
// the current Player in battle
public var player: Player? = null
// the deck of player
public var deck : Deck = Deck()
// the dungeon level that the player is at
public var dungeonLevel : Int = 1
// the storage of a player in village
public var storage: CardData = CardData(mutableListOf())
// the materials that the player collects in dungeon
public var backPackMaterial : CardData = CardData(mutableListOf())
// the weapons that player bring into dungeon
public var backPackItem : CardData = CardData(mutableListOf())

// create json from class
var map = mapOf("storage" to storage, "dungeonLevel" to dungeonLevel)
//val json = gson.toJson(map)

// constants for dungeon moves
public val INVALIDMOVE = 0
public val EMPTY = 1
public val COLLECT = 2
public val BATTLE = 3
public val NEXTLEVEL = 4

// Info
// ActionCards
val stickStrike : ActionCard = ActionCard("strike", "skeleton1.png",
    "deals 5 damage cost 1 energy", 1, 0, -5, Array<State>(), 5)

// Items
val stick : ItemCard = ItemCard("stick", "skeleton1.png", "a simple weapon",
    10, actionCards = Deck(Array<ActionCard>(arrayOf(stickStrike))))
public var itemInfo: CardData = CardData(mutableListOf(stick))

// materials
val stone : MaterialCard = MaterialCard("stone","skeleton1.png", "a stone", earth = 5, metal = 2, water = -2)
val log : MaterialCard = MaterialCard("log", "skeleton1.png", "a log", wood = 5, fire = 2, earth = -2)
public var materialInfo : CardData = CardData(mutableListOf(stone, log))

public var monsterInfo : MonsterData = MonsterData(mutableListOf((Monster(Array<String?>(arrayOf("skeleton1.png","skeleton2.png","skeleton3.png","skeleton2.png")), 1, "Enemy"))))

