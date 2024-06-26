package de.danielreinhold.shoppinglist.feature.shopping_list.domain.models

data class ShoppingListItem(
    val id: Int,
    val name: String,
    val amount: Int
)

val ShoppingListItemMock = ShoppingListItem(
    id = 1,
    name = "Beer",
    amount = 2
)