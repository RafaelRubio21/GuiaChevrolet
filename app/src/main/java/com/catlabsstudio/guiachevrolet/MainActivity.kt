package com.catlabsstudio.guiachevrolet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.catlabsstudio.guiachevrolet.ui.navigation.AppNavGraph
import com.catlabsstudio.guiachevrolet.ui.theme.GuiaChevroletTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as GuiaChevroletApp

        setContent {
            GuiaChevroletTheme {
                AppNavGraph(repository = app.repository)
            }
        }
    }
}
