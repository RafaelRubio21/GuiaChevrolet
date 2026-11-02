package com.guiachevrolet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guiachevrolet.data.local.entity.VehicleEntity
import com.guiachevrolet.ui.components.VehicleFormFields

@Composable
fun OnboardingScreen(
    onSaveVehicle: (model: String, year: Int, nickname: String?) -> Unit
) {
    var selectedModel by rememberSaveable {
        mutableStateOf(VehicleEntity.SUPPORTED_GM_MODELS.first())
    }
    var yearInput by rememberSaveable { mutableStateOf("2015") }
    var nicknameInput by rememberSaveable { mutableStateOf("") }
    var yearError by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bem-vindo ao Guia Chevrolet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cadastre seu veículo GM para consultar rapidamente os códigos de erro do painel 100% offline.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Cadastre seu primeiro veículo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val year = yearInput.toIntOrNull()
                            if (year == null || year !in 1990..2030) {
                                yearError = "Informe um ano válido entre 1990 e 2030"
                            } else {
                                onSaveVehicle(selectedModel, year, nicknameInput)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Salvar e Continuar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
