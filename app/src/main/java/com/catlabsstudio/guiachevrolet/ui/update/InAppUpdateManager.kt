package com.catlabsstudio.guiachevrolet.ui.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * Gerenciador de Verificação e Atualização In-App Oficial (Google Play In-App Updates).
 * - Consulta automaticamente a Google Play Store ao abrir o aplicativo.
 * - Se houver nova versão disponível, exibe um diálogo avisando o usuário.
 * - Ao confirmar, inicia o fluxo nativo da Google Play para atualizar sem precisar sair do app,
 *   ou redireciona para a página da Play Store caso o fluxo flexível não esteja disponível.
 */
class InAppUpdateManager(private val context: Context) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)

    var isUpdateAvailable by mutableStateOf(false)
        private set

    private var appUpdateInfo: AppUpdateInfo? = null

    fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateInfo = info
                isUpdateAvailable = true
            }
        }.addOnFailureListener {
            isUpdateAvailable = false
        }
    }

    fun startUpdateFlow(activity: Activity?, updateLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        val info = appUpdateInfo
        if (info != null && activity != null) {
            val isFlexibleAllowed = info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            val isImmediateAllowed = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            val updateType = when {
                isFlexibleAllowed -> AppUpdateType.FLEXIBLE
                isImmediateAllowed -> AppUpdateType.IMMEDIATE
                else -> null
            }

            if (updateType != null) {
                runCatching {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateLauncher,
                        AppUpdateOptions.newBuilder(updateType).build()
                    )
                }.onFailure {
                    openPlayStorePage(activity)
                }
            } else {
                openPlayStorePage(activity)
            }
        } else if (activity != null) {
            openPlayStorePage(activity)
        }
    }

    fun dismissDialog() {
        isUpdateAvailable = false
    }

    private fun openPlayStorePage(activity: Activity) {
        val packageName = context.packageName
        try {
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            )
        } catch (_: Exception) {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }
}

@Composable
fun UpdateAvailableDialog(
    onDismiss: () -> Unit,
    onUpdateClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.SystemUpdate,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Nova Versão Disponível!",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Uma versão mais recente do Guia Chevrolet está disponível na Google Play Store.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Atualize para receber novos códigos de painel, melhorias de desempenho e correções.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onUpdateClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Atualizar Agora", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Lembrar mais tarde")
            }
        }
    )
}
