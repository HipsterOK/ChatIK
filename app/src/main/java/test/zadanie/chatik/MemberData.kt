package test.zadanie.chatik

class MemberData {
    var name: String? = null
        private set
    var color: String? = null
        private set

    constructor(name: String?, color: String?) {
        this.name = name
        this.color = color
    }

    // Add an empty constructor so we can later parse JSON into MemberData using Jackson
    constructor() {}
}