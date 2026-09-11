package com.local.amjbc.model

class NavDrawerItem {
    var title: String? = null
    var icon: Int = 0
    var count: String = "0"
    var isCounterVisible: Boolean = true

    constructor()

    constructor(title: String?, icon: Int) {
        this.title = title
        this.icon = icon
    }

    constructor(title: String?, icon: Int, isCounterVisible: Boolean, count: String) {
        this.title = title
        this.icon = icon
        this.isCounterVisible = isCounterVisible
        this.count = count
    }

    fun getCounterVisibility(): Boolean {
        return isCounterVisible
    }

    fun setCounterVisibility(isCounterVisible: Boolean) {
        this.isCounterVisible = isCounterVisible
    }
}
