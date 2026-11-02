package com.catlabsstudio.guiachevrolet.ui.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.concurrent.TimeUnit

/**
 * Gerenciador de Anúncio Recompensado (Rewarded Ad) exclusivo para desbloqueio de Manuais em PDF.
 * - Nunca exibe anúncios intersticiais invasivos durante a consulta de códigos.
 * - O usuário opta ativamente por assistir ao vídeo curto para desbloquear o manual completo de 300+ páginas.
 * - Uma vez desbloqueado, o manual daquele modelo fica disponível por 7 dias (persistido em SharedPreferences).
 *   Após 7 dias, o desbloqueio expira e o usuário assiste a um novo vídeo para renovar por mais 7 dias.
 */
class RewardedManualAdController(context: Context) {

    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private var rewardedAd: RewardedAd? = null
    var isLoadingAd by mutableStateOf(false)
        private set

    init {
        preloadAd()
    }

    private fun preferenceKeyForModel(vehicleModel: String): String {
        val normalized = vehicleModel.trim().lowercase().replace(Regex("[^a-z0-9]"), "_")
        return "manual_unlocked_until_$normalized"
    }

    fun isManualUnlocked(vehicleModel: String): Boolean {
        val unlockedUntilMillis = prefs.getLong(preferenceKeyForModel(vehicleModel), 0L)
        return System.currentTimeMillis() < unlockedUntilMillis
    }

    private fun unlockManualForSevenDays(vehicleModel: String) {
        val unlockedUntilMillis = System.currentTimeMillis() + UNLOCK_DURATION_MILLIS
        prefs.edit()
            .putLong(preferenceKeyForModel(vehicleModel), unlockedUntilMillis)
            .apply()
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
        vehicleModel: String,
        onUnlocked: () -> Unit
    ) {
        val currentAd = rewardedAd
        if (currentAd == null || activity == null) {
            // Se o usuário estiver offline ou o anúncio ainda estiver carregando, tentamos carregar
            // imediatamente e liberamos caso a rede/AdMob não responda a tempo.
            if (activity != null && !isLoadingAd) {
                isLoadingAd = true
                RewardedAd.load(
                    appContext,
                    AdMobConfig.REWARDED_MANUAL_AD_UNIT_ID,
                    AdRequest.Builder().build(),
                    object : RewardedAdLoadCallback() {
                        override fun onAdLoaded(ad: RewardedAd) {
                            rewardedAd = ad
                            isLoadingAd = false
                            presentLoadedAd(ad, activity, vehicleModel, onUnlocked)
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            rewardedAd = null
                            isLoadingAd = false
                            unlockManualForSevenDays(vehicleModel)
                            onUnlocked()
                        }
                    }
                )
            } else {
                unlockManualForSevenDays(vehicleModel)
                preloadAd()
                onUnlocked()
            }
            return
        }

        presentLoadedAd(currentAd, activity, vehicleModel, onUnlocked)
    }

    private fun presentLoadedAd(
        ad: RewardedAd,
        activity: Activity,
        vehicleModel: String,
        onUnlocked: () -> Unit
    ) {
        var rewardEarned = false

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                preloadAd()
                if (rewardEarned) {
                    unlockManualForSevenDays(vehicleModel)
                    onUnlocked()
                }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
                preloadAd()
                unlockManualForSevenDays(vehicleModel)
                onUnlocked()
            }
        }

        ad.show(activity) {
            rewardEarned = true
        }
    }

    companion object {
        private const val PREFS_NAME = "guia_chevrolet_manual_unlocks"
        val UNLOCK_DURATION_MILLIS: Long = TimeUnit.DAYS.toMillis(7)
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
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
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
                    text = "Assista a um vídeo curto de 15 segundos para desbloquear o manual completo deste modelo (Chevrolet $vehicleModel) por 7 dias.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Durante os 7 dias você poderá abrir o manual quantas vezes quiser sem precisar assistir a outro vídeo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onWatchVideoClick,
                enabled = !isLoadingAd
            ) {
                if (isLoadingAd) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Carregando vídeo...", fontWeight = FontWeight.Bold)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFilled,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Assistir (Libera por 7 dias)", fontWeight = FontWeight.Bold)
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
