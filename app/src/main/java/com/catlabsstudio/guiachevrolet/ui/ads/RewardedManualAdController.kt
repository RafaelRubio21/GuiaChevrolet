package com.catlabsstudio.guiachevrolet.ui.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * Gerenciador de Anúncio Recompensado (Rewarded Ad) exclusivo para desbloqueio de Manuais em PDF.
 * - Nunca exibe anúncios intersticiais invasivos durante a consulta de códigos.
 * - O usuário opta ativamente por assistir ao vídeo curto para desbloquear o manual completo de 300+ páginas.
 * - Uma vez desbloqueado na sessão para o modelo, o usuário não precisa assistir novamente.
 */
class RewardedManualAdController(context: Context) {

    private val appContext = context.applicationContext
    private var rewardedAd: RewardedAd? = null
    var isLoadingAd by mutableStateOf(false)
        private set

    private val unlockedVehicleIds = mutableSetOf<Long>()

    init {
        preloadAd()
    }

    fun isManualUnlocked(vehicleId: Long): Boolean {
        return unlockedVehicleIds.contains(vehicleId)
    }

    fun preloadAd() {
        if (rewardedAd != null || isLoadingAd) return
        isLoadingAd = true

        RewardedAd.load(
            appContext,
            AdMobConfig.REWARDED_MANUAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoadingAd = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    isLoadingAd = false
                }
            }
        )
    }

    fun showRewardedAd(
        activity: Activity?,
        vehicleId: Long,
        onUnlocked: () -> Unit
    ) {
        val currentAd = rewardedAd
        if (currentAd == null || activity == null) {
            // Se o usuário estiver 100% offline ou a AdMob não tiver inventário no momento,
            // liberamos o manual sem travar a experiência do motorista e pré-carregamos o próximo.
            unlockedVehicleIds.add(vehicleId)
            preloadAd()
            onUnlocked()
            return
        }

        var rewardEarned = false

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                preloadAd()
                if (rewardEarned) {
                    unlockedVehicleIds.add(vehicleId)
                    onUnlocked()
                }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
                preloadAd()
                unlockedVehicleIds.add(vehicleId)
                onUnlocked()
            }
        }

        currentAd.show(activity) {
            rewardEarned = true
        }
    }
}

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

@Composable
fun UnlockManualRewardedDialog(
    vehicleModel: String,
    isLoadingAd: Boolean,
    onDismiss: () -> Unit,
    onWatchVideoClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Desbloquear Manual Completo",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Assista a um vídeo curto de 15 segundos para desbloquear o manual completo deste modelo (Chevrolet $vehicleModel).",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "A consulta rápida de códigos de painel continua 100% gratuita e instantânea.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onWatchVideoClick) {
                if (isLoadingAd) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Abrindo...", fontWeight = FontWeight.Bold)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Assistir e Desbloquear", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Agora não")
            }
        }
    )
}
