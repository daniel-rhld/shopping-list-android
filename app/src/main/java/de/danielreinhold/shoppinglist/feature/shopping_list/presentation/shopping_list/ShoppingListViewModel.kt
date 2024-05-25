package de.danielreinhold.shoppinglist.feature.shopping_list.presentation.shopping_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.danielreinhold.shoppinglist.feature.shopping_list.domain.use_cases.CreateShoppingListUseCase
import de.danielreinhold.shoppinglist.feature.shopping_list.domain.use_cases.DeleteShoppingListUseCase
import de.danielreinhold.shoppinglist.feature.shopping_list.domain.use_cases.GetShoppingListsUseCase
import de.danielreinhold.shoppinglist.feature.shopping_list.presentation.add_shopping_list.AddShoppingListUiEvent
import de.danielreinhold.shoppinglist.feature.shopping_list.presentation.add_shopping_list.AddShoppingListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val getShoppingListsUseCase: GetShoppingListsUseCase,
    private val createShoppingListUseCase: CreateShoppingListUseCase,
    private val deleteShoppingListUseCase: DeleteShoppingListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        value = ShoppingListUiState(
            shoppingLists = listOf(),
            addShoppingListDialogVisible = false,
            addShoppingListUiState = AddShoppingListUiState(
                shoppingListName = "",
                buttonSaveEnabled = false
            ),
            contextualShoppingList = null
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            uiState.collectLatest {
                _uiState.update { uiState ->
                    uiState.copy(
                        addShoppingListUiState = uiState.addShoppingListUiState.copy(
                            buttonSaveEnabled = uiState.addShoppingListUiState.shoppingListName.isNotBlank()
                        )
                    )
                }
            }
        }

        viewModelScope.launch {
            getShoppingListsUseCase.invoke().collectLatest { shoppingLists ->
                _uiState.update { uiState ->
                    uiState.copy(
                        shoppingLists = shoppingLists
                    )
                }
            }
        }
    }

    fun onUiEvent(event: ShoppingListUiEvent) {
        when (event) {
            is ShoppingListUiEvent.AddShoppingList -> {
               _uiState.update {
                   it.copy(
                       addShoppingListDialogVisible = true
                   )
               }
            }

            is ShoppingListUiEvent.CloseAddShoppingListDialog -> {
                _uiState.update { uiState ->
                    uiState.copy(
                        addShoppingListDialogVisible = false
                    )
                }
            }

            is ShoppingListUiEvent.ShowShoppingListContextMenu -> {
                _uiState.update {
                    it.copy(
                        contextualShoppingList = event.shoppingList
                    )
                }
            }

            is ShoppingListUiEvent.CloseShoppingListContextMenu -> {
                _uiState.update {
                    it.copy(
                        contextualShoppingList = null
                    )
                }
            }

            is ShoppingListUiEvent.EditShoppingList -> {

            }

            is ShoppingListUiEvent.DeleteShoppingList -> {
                viewModelScope.launch {
                    deleteShoppingListUseCase.invoke(event.shoppingList)

                    _uiState.update {
                        it.copy(
                            contextualShoppingList = null
                        )
                    }
                }
            }

            is ShoppingListUiEvent.AddShoppingListDialogInteraction -> {
                when (event.value) {
                    is AddShoppingListUiEvent.ShoppingListNameChange -> {
                        _uiState.update { uiState ->
                            uiState.copy(
                                addShoppingListUiState = uiState.addShoppingListUiState.copy(
                                    shoppingListName = event.value.value
                                )
                            )
                        }
                    }
                    is AddShoppingListUiEvent.ButtonSaveClick -> {
                        viewModelScope.launch {
                            try {
                                createShoppingListUseCase.invoke(
                                    name = uiState.value.addShoppingListUiState.shoppingListName
                                )
                                _uiState.update { uiState ->
                                    uiState.copy(
                                        addShoppingListDialogVisible = false,
                                        addShoppingListUiState = uiState.addShoppingListUiState.copy(
                                            shoppingListName = ""
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                // TODO: Show error message
                            }
                        }
                    }
                }
            }

            is ShoppingListUiEvent.ShowShoppingList -> {

            }
        }
    }

}