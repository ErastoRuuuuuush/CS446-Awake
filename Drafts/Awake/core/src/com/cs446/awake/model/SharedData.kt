package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

// Todo: read from file
// the current Enemy in battle
public var enemy : Enemy? = null
// the current Player in battle
public var player: Player? = null

// the materials that the player collects
public var backPackMaterial : Array<MaterialCard> = Array<MaterialCard>()
// the weapons that player bring into dungeon
public var backPackItem : Array<ItemCard> = Array<ItemCard>()


// constants for dungeon moves
public val INVALIDMOVE = 0
public val EMPTY = 1
public val COLLECT = 2
public val BATTLE = 3
public val NEXTLEVEL = 4

// Info
public var monsterInfo : MonsterData = MonsterData(Array<Monster>(arrayOf(Monster(Array<String?>(arrayOf("skeleton1.png","skeleton2.png","skeleton3.png","skeleton2.png")), 1, "Enemy"))))
public var materialInfo : MaterialData = MaterialData(Array<MaterialCard>(arrayOf(MaterialCard("rock", "rock", "a rock"))))
