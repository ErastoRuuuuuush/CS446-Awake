package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

// information about the player pregress
// Todo: read from file
// the current Enemy in battle
public var enemy : Enemy? = null
// the current Player in battle
public var player: Player? = null
// the dungeon level that the player is at
public var dungeonLevel : Int = 1
// the storage of a player in village
public var storage: CardData = CardData(mutableListOf())
// the materials that the player collects in dungeon
public var backPackMaterial : CardData = CardData(mutableListOf())
// the weapons that player bring into dungeon
public var backPackItem : CardData = CardData(mutableListOf())


// constants for dungeon moves
public val INVALIDMOVE = 0
public val EMPTY = 1
public val COLLECT = 2
public val BATTLE = 3
public val NEXTLEVEL = 4

// Info
public var monsterInfo : MonsterData = MonsterData(mutableListOf((Monster(Array<String?>(arrayOf("skeleton1.png","skeleton2.png","skeleton3.png","skeleton2.png")), 1, "Enemy"))))
public var materialInfo : CardData = CardData(mutableListOf(MaterialCard("rock", "rock", "a rock")))
public var itemInfo: CardData = CardData(mutableListOf())
