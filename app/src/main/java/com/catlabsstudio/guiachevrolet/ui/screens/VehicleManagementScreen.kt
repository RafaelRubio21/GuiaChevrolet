package com.catlabsstudio.guiachevrolet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.catlabsstudio.guiachevrolet.data.local.entity.VehicleEntity
import com.catlabsstudio.guiachevrolet.ui.components.VehicleFormFields

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleManagementScreen(
    vehicles: List<VehicleEntity>,
    onBack: () -> Unit,
    onAddVehicle: (model: String, year: Int, nickname: String?) -> Unit,
    onUpdateVehicle: (vehicle: VehicleEntity, model: String, year: Int, nickname: String?) -> Unit,
    onDeleteVehicle: (VehicleEntity) -> Unit
) {
    var showFormDialog by remember { mutableStateOf(false) }
    var editingVehicle by remember { mutableStateOf<VehicleEntity?>(null) }
    var deletingVehicle by remember { mutableStateOf<VehicleEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciar Veículos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingVehicle = null
                    showFormDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Novo Veículo") }
            )
        }
    ) { innerPadding ->
        if (vehicles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum veículo cadastrado. Toque em 'Novo Veículo' para adicionar.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vehicles, key = { it.id }) { vehicle ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Chevrolet ${vehicle.model} (${vehicle.year})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!vehicle.nickname.isNullOrBlank()) {
                                    Text(
                                        text = "Apelido: ${vehicle.nickname}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    editingVehicle = vehicle
                                    showFormDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar veículo",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { deletingVehicle = vehicle }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Excluir veículo",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFormDialog) {
        val currentEditing = editingVehicle
        var selectedModel by remember(currentEditing) {
            mutableStateOf(currentEditing?.model ?: VehicleEntity.SUPPORTED_GM_MODELS.first())
        }
        var yearInput by remember(currentEditing) {
            mutableStateOf(currentEditing?.year?.toString() ?: "2015")
        }
        var nicknameInput by remember(currentEditing) {
            mutableStateOf(currentEditing?.nickname ?: "")
        }
        var yearError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showFormDialog = false },
            title = {
                Text(
                    text = if (currentEditing == null) "Adicionar Veículo" else "Editar Veículo",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(4.dp))
                    VehicleFormFields(
                        selectedModel = selectedModel,
                        onModelSelected = { selectedModel = it },
                        yearInput = yearInput,
                        onYearChange = {
                            yearInput = it
                            yearError = null
                        },
                        nicknameInput = nicknameInput,
                        onNicknameChange = { nicknameInput = it },
                        yearError = yearError
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val year = yearInput.toIntOrNull()
                        if (year == null || year !in 1990..2030) {
                            yearError = "Informe um ano válido entre 1990 e 2030"
                        } else {
                            if (currentEditing == null) {
                                onAddVehicle(selectedModel, year, nicknameInput)
                            } else {
                                onUpdateVehicle(currentEditing, selectedModel, year, nicknameInput)
                            }
                            showFormDialog = false
                        }
                    }
                ) {
                    Text("Salvar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFormDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    deletingVehicle?.let { vehicleToDelete ->
        AlertDialog(
            onDismissRequest = { deletingVehicle = null },
            title = { Text("Excluir veículo?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Tem certeza que deseja remover o veículo ${vehicleToDelete.displayTitle}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteVehicle(vehicleToDelete)
                        deletingVehicle = null
                    }
                ) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingVehicle = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
