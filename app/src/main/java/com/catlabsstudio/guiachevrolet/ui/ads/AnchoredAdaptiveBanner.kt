package com.catlabsstudio.guiachevrolet.ui.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Formato Principal: Banner Adaptativo de Rodapé (Anchored Adaptive Banner).
 * - Fica fixado e isolado no rodapé da tela (bottomBar do Scaffold), respeitando as políticas do Google Play.
 * - Ajusta automaticamente a largura/altura e densidade de pixels conforme o tamanho da tela.
 * - Não bloqueia o teclado numérico nem a leitura rápida do código de erro.
 */
@Composable
fun AnchoredAdaptiveBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobConfig.ADAPTIVE_BANNER_AD_UNIT_ID
) {
    val configuration = LocalConfiguration.current
    val adWidthDp = configuration.screenWidthDp.coerceAtLeast(320)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    AdView(context).apply {
                        this.adUnitId = adUnitId
                        val adaptiveSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                            context,
                            adWidthDp
                        )
                        setAdSize(adaptiveSize)
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        }
    }
}
