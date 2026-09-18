package br.com.wgc.coreandroidnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.com.wgc.core.analytics.consent.ConsentType
import br.com.wgc.core.analytics.consent.LgpdConsentManager
import br.com.wgc.core.database.security.DatabaseEncrypter
import br.com.wgc.core.device.DeviceInfo
import br.com.wgc.core.device.DeviceSecurityHelper
import br.com.wgc.core.device.HapticFeedbackHelper
import br.com.wgc.core.formatters.transformations.CpfVisualTransformation
import br.com.wgc.core.formatters.unmask
import br.com.wgc.core.location.LocationClient
import br.com.wgc.core.logging.CoreLogger
import br.com.wgc.core.network.NetworkMonitor
import br.com.wgc.core.session.SessionManager
import br.com.wgc.core.validators.isValidCpf
import br.com.wgc.coreandroidnative.ui.theme.CoreAndroidNativeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CPF_MAX_DIGITS = 11

/**
 * Activity de demonstração interativa dos recursos corporativos do CoreAndroidNative.
 * Estruturada como um catálogo de componentes (Showcase) com navegação por abas.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var deviceInfo: DeviceInfo

    @Inject
    lateinit var deviceSecurityHelper: DeviceSecurityHelper

    @Inject
    lateinit var hapticHelper: HapticFeedbackHelper

    @Inject
    lateinit var coreLogger: CoreLogger

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var lgpdConsentManager: LgpdConsentManager

    @Inject
    lateinit var databaseEncrypter: DatabaseEncrypter

    @Inject
    lateinit var locationClient: LocationClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoreAndroidNativeTheme {
                ShowcaseScreen(
                    networkMonitor = networkMonitor,
                    deviceInfo = deviceInfo,
                    deviceSecurityHelper = deviceSecurityHelper,
                    hapticHelper = hapticHelper,
                    coreLogger = coreLogger,
                    sessionManager = sessionManager,
                    lgpdConsentManager = lgpdConsentManager,
                    databaseEncrypter = databaseEncrypter,
                    locationClient = locationClient,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseScreen(
    networkMonitor: NetworkMonitor,
    deviceInfo: DeviceInfo,
    deviceSecurityHelper: DeviceSecurityHelper,
    hapticHelper: HapticFeedbackHelper,
    coreLogger: CoreLogger,
    sessionManager: SessionManager,
    lgpdConsentManager: LgpdConsentManager,
    databaseEncrypter: DatabaseEncrypter,
    locationClient: LocationClient,
) {
    val isConnected by networkMonitor.isConnected.collectAsState(initial = true)
    var cpfInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var sessionStatusMessage by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var analyticsConsent by remember {
        mutableStateOf(lgpdConsentManager.isConsentGranted(ConsentType.ANALYTICS))
    }
    var crashConsent by remember {
        mutableStateOf(lgpdConsentManager.isConsentGranted(ConsentType.CRASH_REPORTING))
    }

    val tabs = listOf("🌐 Rede", "🎨 UI", "📳 Haptics", "🔒 Sessão", "🛡️ Segurança")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CoreAndroidNative Showcase") },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            hapticHelper.vibrateClick()
                        },
                        text = { Text(title) },
                    )
                }
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Aba 0: Rede & Device
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🌐 Conectividade & Dispositivo", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isConnected) "Status: Online (Conectado)" else "Status: Offline (Sem Conexão)",
                                    color = if (isConnected) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Aparelho: ${deviceInfo.manufacturer} ${deviceInfo.deviceModel}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Text(
                                    "Android SDK: ${deviceInfo.sdkInt} | Emulador: ${deviceInfo.isEmulator}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                    1 -> {
                        // Aba 1: UI & Máscaras
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🎨 Compose Mask & Validação", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = cpfInput,
                                    onValueChange = {
                                        val digits = it.unmask()
                                        if (digits.length <= CPF_MAX_DIGITS) {
                                            cpfInput = digits
                                        }
                                    },
                                    label = { Text("Digite um CPF") },
                                    visualTransformation = CpfVisualTransformation(),
                                    isError = cpfInput.length == CPF_MAX_DIGITS && !cpfInput.isValidCpf(),
                                    modifier =
                                        Modifier.fillMaxWidth().semantics {
                                            contentDescription = "Campo para inserção de CPF"
                                        },
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val cpfStatus =
                                    when {
                                        cpfInput.isEmpty() -> "Aguardando preenchimento..."
                                        cpfInput.length < CPF_MAX_DIGITS -> "Digitando (${cpfInput.length}/$CPF_MAX_DIGITS)..."
                                        cpfInput.isValidCpf() -> "✅ CPF Válido!"
                                        else -> "❌ CPF Inválido!"
                                    }
                                Text(
                                    text = cpfStatus,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (cpfInput.isValidCpf()) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                    2 -> {
                        // Aba 2: Haptic Feedback
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("📳 Feedback Tátil (Haptics)", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    OutlinedButton(
                                        onClick = { hapticHelper.vibrateClick() },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text("Clique")
                                    }
                                    OutlinedButton(
                                        onClick = { hapticHelper.vibrateSuccess() },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text("Sucesso")
                                    }
                                    OutlinedButton(
                                        onClick = { hapticHelper.vibrateError() },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text("Erro")
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // Aba 3: Sessão & Logs PII
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🔒 Sessão & Observabilidade PII", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        scope.launch {
                                            coreLogger.i(
                                                "Showcase",
                                                "Iniciando logout seguro para CPF: $cpfInput com Bearer token_secret_abc",
                                            )
                                            sessionManager.clearSession(clearCache = true)
                                            sessionStatusMessage = "Sessão e cache limpos com sucesso!"
                                            hapticHelper.vibrateSuccess()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text("Executar Logout Atômico")
                                }
                                if (sessionStatusMessage.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = sessionStatusMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2E7D32),
                                    )
                                }
                            }
                        }
                    }
                    4 -> {
                        // Aba 4: Segurança, LGPD & SQLCipher
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🛡️ Segurança, Root & LGPD", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))

                                val isRooted = remember { deviceSecurityHelper.isRooted() }
                                val isEmulator = remember { deviceSecurityHelper.isEmulator() }
                                val isAdb = remember { deviceSecurityHelper.isAdbEnabled() }

                                Text(
                                    "Dispositivo Rooted: ${if (isRooted) "⚠️ SIM (Risco Detectado)" else "✅ NÃO (Seguro)"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isRooted) MaterialTheme.colorScheme.error else Color(0xFF2E7D32),
                                )
                                Text("Ambiente Emulador: $isEmulator", style = MaterialTheme.typography.bodySmall)
                                Text("USB Debugging (ADB): $isAdb", style = MaterialTheme.typography.bodySmall)

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("🔒 Room com SQLCipher", style = MaterialTheme.typography.labelLarge)
                                val cipherFactoryReady =
                                    remember {
                                        runCatching { databaseEncrypter.getSupportFactory() }.isSuccess
                                    }
                                Text(
                                    "Chave SQLCipher 256-bit: ${if (cipherFactoryReady) "✅ Inicializada via KeyStore" else "❌ Erro"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (cipherFactoryReady) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("📋 Gestão de Consentimento LGPD", style = MaterialTheme.typography.labelLarge)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("Consentimento de Analytics", style = MaterialTheme.typography.bodySmall)
                                    Switch(
                                        checked = analyticsConsent,
                                        onCheckedChange = { granted ->
                                            analyticsConsent = granted
                                            lgpdConsentManager.setConsent(ConsentType.ANALYTICS, granted)
                                            hapticHelper.vibrateClick()
                                        },
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("Consentimento Crash Reporting", style = MaterialTheme.typography.bodySmall)
                                    Switch(
                                        checked = crashConsent,
                                        onCheckedChange = { granted ->
                                            crashConsent = granted
                                            lgpdConsentManager.setConsent(ConsentType.CRASH_REPORTING, granted)
                                            hapticHelper.vibrateClick()
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
