package com.catlabsstudio.guiachevrolet.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.catlabsstudio.guiachevrolet.data.local.entity.ErrorCodeEntity
import com.catlabsstudio.guiachevrolet.data.local.entity.Severity
import com.catlabsstudio.guiachevrolet.ui.theme.SeverityAlertBg
import com.catlabsstudio.guiachevrolet.ui.theme.SeverityAlertColor
import com.catlabsstudio.guiachevrolet.ui.theme.SeverityCriticalBg
import com.catlabsstudio.guiachevrolet.ui.theme.SeverityCriticalColor
import com.catlabsstudio.guiachevrolet.ui.theme.SeverityMaintBg
import com.catlabsstudio.guiachevrolet.ui.theme.SeverityMaintColor
import com.catlabsstudio.guiachevrolet.ui.viewmodel.LookupResultState
import com.catlabsstudio.guiachevrolet.ui.viewmodel.LookupUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ErrorLookupScreen(
    uiState: LookupUiState,
    onBack: () -> Unit,
    onCodeInputChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    onQuickCodeClick: (Int) -> Unit,
    onOpenManualClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val codeInputFocusRequester = remember { FocusRequester() }
    val activeVehicle = uiState.activeVehicle

    LaunchedEffect(Unit) {
        delay(150)
        codeInputFocusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Consulta de Código", fontWeight = FontWeight.Bold) },
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabeçalho com veículo ativo + botão Ver Manual em PDF
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (activeVehicle != null) {
                                    "Consultando para: ${activeVehicle.model} ${activeVehicle.year}"
                                } else {
                                    "Carregando veículo..."
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (!activeVehicle?.nickname.isNullOrBlank()) {
                                Text(
                                    text = "Veículo: ${activeVehicle?.nickname}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    FilledTonalButton(
                        onClick = {
                            focusManager.clearFocus()
                            onOpenManualClick()
                        },
                        enabled = activeVehicle != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (activeVehicle != null) {
                                "Ver Manual do ${activeVehicle.model} em PDF (No App)"
                            } else {
                                "Ver Manual em PDF"
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Campo numérico para o código
            OutlinedTextField(
                value = uiState.codeInput,
                onValueChange = onCodeInputChange,
                label = { Text("Código do painel (ex: 89)") },
                placeholder = { Text("Digite o número exibido no painel") },
                singleLine = true,
                trailingIcon = {
                    if (uiState.codeInput.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onClearClick()
                                codeInputFocusRequester.requestFocus()
                                keyboardController?.show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpar código"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        onSearchClick()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(codeInputFocusRequester)
            )

            // Atalhos rápidos dos códigos mais comuns
            Column {
                Text(
                    text = "Códigos mais comuns da linha GM:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    uiState.commonCodesQuickList.forEach { code ->
                        AssistChip(
                            onClick = {
                                focusManager.clearFocus()
                                onQuickCodeClick(code)
                            },
                            label = { Text("Cód. $code") }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onClearClick()
                        codeInputFocusRequester.requestFocus()
                        keyboardController?.show()
                    },
                    enabled = uiState.codeInput.isNotEmpty() || uiState.resultState !is LookupResultState.Idle,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Limpar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onSearchClick()
                    },
                    modifier = Modifier
                        .weight(1.4f)
                        .height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Consultar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Área de Resultado
            when (val result = uiState.resultState) {
                is LookupResultState.Idle -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Digite o número que aparece no computador de bordo do seu Chevrolet e toque em Consultar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                is LookupResultState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is LookupResultState.InvalidInput -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = result.message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                is LookupResultState.Found -> {
                    ErrorCodeResultCard(errorCode = result.errorCode)
                }

                is LookupResultState.NotFound -> {
                    NotFoundResultCard(
                        searchedCode = result.searchedCode,
                        vehicleModel = result.vehicleModel,
                        onOpenManualClick = {
                            focusManager.clearFocus()
                            onOpenManualClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorCodeResultCard(errorCode: ErrorCodeEntity) {
    val (badgeBg, badgeColor, badgeIcon) = when (errorCode.severity) {
        Severity.ALERTA -> Triple(SeverityAlertBg, SeverityAlertColor, Icons.Default.Warning)
        Severity.MANUTENCAO -> Triple(SeverityMaintBg, SeverityMaintColor, Icons.Default.Build)
        Severity.CRITICO -> Triple(SeverityCriticalBg, SeverityCriticalColor, Icons.Default.Error)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.5.dp, badgeColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CÓDIGO ${errorCode.codeNumber}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    color = badgeBg,
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, badgeColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = badgeIcon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = errorCode.severity.displayName.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
            }

            Text(
                text = errorCode.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = errorCode.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Compatível com: ${errorCode.applicableModels.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun NotFoundResultCard(
    searchedCode: Int,
    vehicleModel: String,
    onOpenManualClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Código $searchedCode não encontrado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Não encontramos o código $searchedCode cadastrado para o modelo Chevrolet $vehicleModel em nossa base offline.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Sugestão: Verifique se o número foi digitado corretamente ou consulte diretamente o Manual do Proprietário em PDF aqui no aplicativo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = onOpenManualClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Abrir Manual do $vehicleModel em PDF", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
