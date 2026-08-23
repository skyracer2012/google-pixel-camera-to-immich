package com.google.android.apps.photos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.apps.photos.ui.theme.GooglePixelCameraToImmichTheme

class MainActivity : ComponentActivity() {
    companion object {
        private const val LOG_TAG = "GooglePixelCameraToImmich"
        private const val IMMICH_PACKAGE = "app.alextran.immich"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(LOG_TAG, "MainActivity entry")

        val status = openImmich();
        Log.e(LOG_TAG, "Immich open status: $status")

        if (status !== null) {
            showErrorUI(status)
        }
    }

    private fun openImmich(): String? {
        Log.d(LOG_TAG, "Opening Immich")

        val immichLaunchIntent = packageManager.getLaunchIntentForPackage(IMMICH_PACKAGE)

        if(immichLaunchIntent == null)
        {
            Log.d(LOG_TAG, "Immich not existing $immichLaunchIntent")
            return "Immich ist nicht installiert"
        }

        val immichViewIntent = Intent(Intent.ACTION_VIEW).apply {
            setPackage(IMMICH_PACKAGE)
            setDataAndType(intent.data, intent.type)
            addFlags(intent.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
        }

        try {
            startActivity(immichViewIntent)

            Log.d(LOG_TAG, "Immich started")
            Log.d(LOG_TAG, "Forwarded action: ${immichViewIntent.action}")
            Log.d(LOG_TAG, "Forwarded data: ${immichViewIntent.data}")
            Log.d(LOG_TAG, "Forwarded type: ${immichViewIntent.type}")

            finishAndRemoveTask()
        }catch (e: ActivityNotFoundException){
            Log.e(LOG_TAG, "Immich activity error", e)
            return "Es gab einen Fehler.";
        }

        return null
    }

    private fun showErrorUI(message: String) {
        setContent {
            GooglePixelCameraToImmichTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )

                            Text(
                                text = "Immich konnte nicht geöffnet werden",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 16.dp)
                            )

                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}