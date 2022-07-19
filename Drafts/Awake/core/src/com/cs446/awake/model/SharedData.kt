package com.cs446.awake.model

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Array
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


// information about the player progress
// the current Enemy in battle
public var enemy : Enemy? = null
// the current Player in battle
public var player: Player? = null
// the deck of player
public var deck : Deck = Deck()

// the storage of a player in village
public var storage: CardData = CardData(mutableListOf())
// the dungeon level that the player is at
public var dungeonLevel : Int = 1
// the HP of player
public var HP : Int = 10
// the energy that the player has at beginning of battle
public var energy : Int = 5

// the materials that the player collects in dungeon
public var backPackMaterial : CardData = CardData(mutableListOf())
// the weapons that player bring into dungeon
public var backPackItem : CardData = CardData(mutableListOf())

fun dumpJson (){// create json from class
    var json = Gson().toJson(storage, CardData::class.java)
    var handle = Gdx.files.local("storage")
    handle.writeString(json, false)

    json = Gson().toJson(dungeonLevel, Int::class.java)
    handle = Gdx.files.local("dungeonLevel")
    handle.writeString(json, false)

    json = Gson().toJson(HP, Int::class.java)
    handle = Gdx.files.local("HP")
    handle.writeString(json, false)

    json = Gson().toJson(energy, Int::class.java)
    handle = Gdx.files.local("energy")
    handle.writeString(json, false)
}

fun readJson (){
    storage = CardData(kotlin.collections.mutableListOf())
    var handle = Gdx.files.local("storage")
    var json = handle.readString()
    var loaded = Gson().fromJson(json, CardData::class.java)
    // change each card to either MaterialCard or ItemCard
    for (card in loaded.getStored()){
        val loadedItem = itemInfo.find(card.cardName)
        if (loadedItem != null){
            loadedItem.count = card.count
            storage.add(loadedItem)
            continue
        }
        val loadedMaterial = materialInfo.find(card.cardName)
        if (loadedMaterial != null){
            loadedMaterial.count = card.count
            storage.add(loadedMaterial)
            continue
        }
        println("find unrecognized card: " + card.cardName)
        storage.add(card)
    }

    handle = Gdx.files.local("dungeonLevel")
    json = handle.readString()
    dungeonLevel = Gson().fromJson(json, Int::class.java)

    handle = Gdx.files.local("HP")
    json = handle.readString()
    HP = Gson().fromJson(json, Int::class.java)

    handle = Gdx.files.local("energy")
    json = handle.readString()
    energy = Gson().fromJson(json, Int::class.java)

    println("restored")
}

// constants for dungeon moves
public const val INVALIDMOVE = 0
public const val EMPTY = 1
public const val COLLECT = 2
public const val BATTLE = 3
public const val NEXTLEVEL = 4

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

