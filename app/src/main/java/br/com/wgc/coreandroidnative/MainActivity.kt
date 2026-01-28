
package br.com.wgc.coreandroidnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.coreandroidnative.ui.theme.CoreAndroidNativeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: DataStorePreferencesCore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoreAndroidNativeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Button(modifier = Modifier.padding(innerPadding), onClick = {
                        lifecycleScope.launch {
                            dataStore.clear()
                        }
                    }) {
                        Text(text = "Teste")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CoreAndroidNativeTheme {
        Greeting("Android")
    }
}