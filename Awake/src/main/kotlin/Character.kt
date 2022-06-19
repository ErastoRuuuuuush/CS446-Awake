open class Character() {
    var name = ""
    var health = 0
    var hand : MutableList<Card> = mutableListOf()
    var deck : MutableList<Card> = mutableListOf()
    var state : MutableList<State> = mutableListOf()
    var board = Board()
    var energy = 0
    var strength = 0

    open fun useCard(i: Int, target: Character) {
        this.hand[i].use(target) // use card from hand, card should call target.isDead()
        this.hand.removeAt(i)
    }

    open fun drawCard(d: Deck) {
        this.hand = d.draw()
    }

    open fun updateState(s: State) {
        this.state.add(s)
    }

    open fun updateHealth(h: Int) {
        this.health += h
    }

    open fun endRound() {
        for (s in this.state) {
            s.apply(this)  // apply state to character
        }
        this.isDead()
        this.hand.clear()
    }

    open fun isDead(): Boolean {
        var res = false
        if (health <= 0) {
            this.board.nptify()
            res = true
        }
        return res
    }
}