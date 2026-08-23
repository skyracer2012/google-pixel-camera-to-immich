package com.google.android.apps.photos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.apps.photos.ui.theme.GooglePixelCameraToImmichTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    companion object {
        private const val LOG_TAG = "GooglePixelCameraToImmich"
        private const val IMMICH_PACKAGE = "app.alextran.immich"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(LOG_TAG, "MainActivity entry")

        val status = openImmich()
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
            return getString(R.string.error_immich_not_installed)
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
            return getString(R.string.error_immich_start)
        }

        return null
    }

    // This markup/design is AI
    private fun showErrorUI(message: String) {
        setContent {
            GooglePixelCameraToImmichTheme {
                val context = LocalContext.current

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
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .padding(bottom = 24.dp)
                                    .size(72.dp)
                            )

                            Text(
                                text = getString(R.string.error_title),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 12.dp)
                            )

                            Text(
                                text = getString(R.string.app_name),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 32.dp)
                            )

                            Text(
                                text = getString(R.string.app_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .padding(top = 4.dp)
                            )

                            Row(
                                modifier = Modifier.padding(top = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = getString(R.string.source_code),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                Text(
                                    text = "-",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                Text(
                                    text = getString(R.string.git_provider),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable {
                                        val url = getString(R.string.app_git_url)

                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                url.toUri()
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}