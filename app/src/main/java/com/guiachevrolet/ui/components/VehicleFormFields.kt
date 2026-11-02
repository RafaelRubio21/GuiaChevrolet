package com.guiachevrolet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.guiachevrolet.data.local.entity.VehicleEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleFormFields(
    selectedModel: String,
    onModelSelected: (String) -> Unit,
    yearInput: String,
    onYearChange: (String) -> Unit,
    nicknameInput: String,
    onNicknameChange: (String) -> Unit,
    yearError: String?,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedModel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Modelo GM (Chevrolet)") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                VehicleEntity.SUPPORTED_GM_MODELS.forEach { model ->
                    DropdownMenuItem(
                        text = { Text(model) },
                        onClick = {
                            onModelSelected(model)
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = yearInput,
            onValueChange = { input ->
                onYearChange(input.filter { it.isDigit() }.take(4))
            },
            label = { Text("Ano do Veículo (ex: 2015)") },
            singleLine = true,
            isError = yearError != null,
            supportingText = {
                if (yearError != null) {
                    Text(text = yearError, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nicknameInput,
            onValueChange = onNicknameChange,
            label = { Text("Apelido (Opcional - ex: Meu Carro, Carro da Firma)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
