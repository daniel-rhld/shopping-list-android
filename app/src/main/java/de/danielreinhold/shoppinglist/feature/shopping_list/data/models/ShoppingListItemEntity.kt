package de.danielreinhold.shoppinglist.feature.shopping_list.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.danielreinhold.shoppinglist.feature.shopping_list.domain.models.ShoppingListItem

@Entity(tableName = "shopping_list_items")
data class ShoppingListItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "amount")
    val amount: Int
)

fun ShoppingListItemEntity.mapToDomainModel() = ShoppingListItem(
    id = id,
    name = name,
    amount = amount
)