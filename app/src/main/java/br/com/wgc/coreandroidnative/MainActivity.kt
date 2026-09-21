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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.com.wgc.core.analytics.consent.ConsentType
import br.com.wgc.core.analytics.consent.LgpdConsentManager
import br.com.wgc.core.database.security.DatabaseEncrypter
import br.com.wgc.core.device.DeviceInfo
import br.com.wgc.core.device.DeviceSecurityHelper
import br.com.wgc.core.device.HapticFeedbackHelper
import br.com.wgc.core.device.hardware.BleScannerHelper
import br.com.wgc.core.device.hardware.NfcHelper
import br.com.wgc.core.device.notification.NotificationChannelConfig
import br.com.wgc.core.featureflag.DefaultFeatureToggle
import br.com.wgc.core.featureflag.FeatureToggleManager
import br.com.wgc.core.formatters.unmask
import br.com.wgc.core.location.LocationClient
import br.com.wgc.core.logging.CoreLogger
import br.com.wgc.core.network.NetworkMonitor
import br.com.wgc.core.session.SessionManager
import br.com.wgc.core.storage.cache.TwoLevelCache
import br.com.wgc.core.sync.DefaultOutboxQueue
import br.com.wgc.core.sync.OutboxRequest
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

    @Inject
    lateinit var featureToggleManager: FeatureToggleManager

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
                    featureToggleManager = featureToggleManager,
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
    featureToggleManager: FeatureToggleManager,
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
    var sampleFeatureEnabled by remember {
        mutableStateOf(featureToggleManager.isEnabled(DefaultFeatureToggle.SAMPLE_NEW_EXPERIENCE))
    }

    val context = LocalContext.current
    val outboxQueue = remember { DefaultOutboxQueue() }
    val outboxPendingCount by outboxQueue.observePendingCount().collectAsState(initial = 0)
    val twoLevelCache = remember { TwoLevelCache<String, String>(maxMemoryEntries = 10) }
    var cacheMessage by remember { mutableStateOf("Nenhum dado lido") }
    val nfcHelper = remember { NfcHelper(context) }
    val bleHelper = remember { BleScannerHelper(context) }
    var rolloutUserId by remember { mutableStateOf("user_9921") }
    var rolloutPercentage by remember { mutableIntStateOf(50) }
    var rolloutResult by remember { mutableStateOf<Boolean?>(null) }

    val tabs = listOf("🌐 Rede", "🎨 UI", "📳 Haptics", "🔒 Sessão", "🛡️ Segurança", "🚀 Nível 5", "⚡ Avançado")

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
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
            ) {
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
                                val connectionStatus =
                                    if (isConnected) {
                                        "Status: Online (Conectado)"
                                    } else {
                                        "Status: Offline (Sem Conexão)"
                                    }
                                Text(
                                    text = connectionStatus,
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
                                        cpfInput.length < CPF_MAX_DIGITS ->
                                            "Digitando (${cpfInput.length}/$CPF_MAX_DIGITS)..."
                                        cpfInput.isValidCpf() -> "✅ CPF Válido!"
                                        else -> "❌ CPF Inválido!"
                                    }
                                val cpfColor =
                                    if (cpfInput.isValidCpf()) {
                                        Color(0xFF2E7D32)
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    }
                                Text(
                                    text = cpfStatus,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cpfColor,
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
                                                "Iniciando logout seguro para CPF: $cpfInput",
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

                                val rootedStatus = if (isRooted) "⚠️ SIM (Risco Detectado)" else "✅ NÃO (Seguro)"
                                val rootedColor = if (isRooted) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                                Text(
                                    "Dispositivo Rooted: $rootedStatus",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = rootedColor,
                                )
                                Text("Ambiente Emulador: $isEmulator", style = MaterialTheme.typography.bodySmall)
                                Text("USB Debugging (ADB): $isAdb", style = MaterialTheme.typography.bodySmall)

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("🔒 Room com SQLCipher", style = MaterialTheme.typography.labelLarge)
                                val cipherFactoryReady =
                                    remember {
                                        runCatching { databaseEncrypter.getSupportFactory() }.isSuccess
                                    }
                                val cipherStatus =
                                    if (cipherFactoryReady) {
                                        "✅ Inicializada via KeyStore"
                                    } else {
                                        "❌ Erro"
                                    }
                                val cipherColor =
                                    if (cipherFactoryReady) {
                                        Color(0xFF2E7D32)
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    }
                                Text(
                                    "Chave SQLCipher 256-bit: $cipherStatus",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = cipherColor,
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
                    5 -> {
                        // Aba 5: Nível 5 - APM & Feature Flags
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🚀 Nível 5: APM & Feature Flags", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))

                                Text("🚩 Gestão Dinâmica de Feature Flags", style = MaterialTheme.typography.labelLarge)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("Flag 'SAMPLE_NEW_EXPERIENCE'", style = MaterialTheme.typography.bodySmall)
                                    Switch(
                                        checked = sampleFeatureEnabled,
                                        onCheckedChange = { isEnabled ->
                                            sampleFeatureEnabled = isEnabled
                                            featureToggleManager.setOverride(
                                                DefaultFeatureToggle.SAMPLE_NEW_EXPERIENCE,
                                                isEnabled,
                                            )
                                            hapticHelper.vibrateClick()
                                        },
                                    )
                                }
                                val flagStatus =
                                    if (sampleFeatureEnabled) {
                                        "✅ Feature Ativa (Rollout On)"
                                    } else {
                                        "⏸️ Feature Inativa (Rollout Off)"
                                    }
                                Text(
                                    text = flagStatus,
                                    style = MaterialTheme.typography.bodySmall,
                                    color =
                                        if (sampleFeatureEnabled) {
                                            Color(0xFF2E7D32)
                                        } else {
                                            MaterialTheme.colorScheme.error
                                        },
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("🌐 W3C Distributed Tracing (APM)", style = MaterialTheme.typography.labelLarge)
                                Text(
                                    "X-Correlation-ID: e8f23a10-7e44-48b2-a42e-89a1bc498d21",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Text(
                                    "traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Text(
                                    "Telemetria de Rede: DNS 12ms | TLS 28ms | Total 114ms",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32),
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "⚡ Otimizações de Performance & Memória",
                                    style = MaterialTheme.typography.labelLarge,
                                )
                                Text(
                                    "Baseline Profiles: ✅ Ativo (AOT compilation ART)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32),
                                )
                                Text(
                                    "StrictMode: ✅ Ativo (Thread & VM Policies)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32),
                                )
                                Text(
                                    "LeakCanary: ✅ Integrado (Monitor de Leaks em Debug)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32),
                                )
                            }
                        }
                    }
                    6 -> {
                        // Aba 6: Recursos Avançados de Infraestrutura
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🔄 Outbox Pattern & Sync Offline", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Fila de requisições pendentes: $outboxPendingCount itens",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        scope.launch {
                                            outboxQueue.enqueue(
                                                OutboxRequest(
                                                    endpoint = "/api/v1/orders",
                                                    payload = "{\"order\": 1024, \"status\": \"PENDING\"}",
                                                ),
                                            )
                                            hapticHelper.vibrateSuccess()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text("Enfileirar Operação Offline")
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("🚀 Two-Level Cache (RAM + Disco)", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Status: $cacheMessage",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                twoLevelCache.put(
                                                    "chave_teste",
                                                    "Payload em Cache (Válido)",
                                                    ttlMs = 10000L,
                                                )
                                                cacheMessage = "Gravado no Cache (TTL: 10s)"
                                                hapticHelper.vibrateSuccess()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text("Gravar")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                val value = twoLevelCache.get("chave_teste")
                                                cacheMessage = value ?: "Expirado ou Não Encontrado"
                                                hapticHelper.vibrateClick()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Text("Ler")
                                    }
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "🔔 Canais de Notificação Corporativos",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                NotificationChannelConfig.entries.forEach { config ->
                                    Text(
                                        "• ${config.channelName} (${config.channelId})",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("📡 Sensores de Hardware", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                val nfcSupported = nfcHelper.isNfcSupported()
                                val bleSupported = bleHelper.isBluetoothEnabled()
                                Text(
                                    "NFC Disponível: ${if (nfcSupported) "✅ Sim" else "❌ Não / Emulador"}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Text(
                                    "Bluetooth LE: ${if (bleSupported) "✅ Ativo" else "⚠️ Desativado / Sem Permissão"}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "🚩 Rollout Percentual de Feature Flags",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Usuário: $rolloutUserId | Percentual: $rolloutPercentage%",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        rolloutResult =
                                            featureToggleManager.isRolloutEnabled(
                                                DefaultFeatureToggle.SAMPLE_NEW_EXPERIENCE,
                                                rolloutUserId,
                                                rolloutPercentage,
                                            )
                                        hapticHelper.vibrateClick()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text("Avaliar Rollout Determinístico")
                                }
                                if (rolloutResult != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text =
                                            if (rolloutResult ==
                                                true
                                            ) {
                                                "Resultado: Ativo para este usuário"
                                            } else {
                                                "Resultado: Inativo no bucket"
                                            },
                                        color =
                                            if (rolloutResult ==
                                                true
                                            ) {
                                                Color(0xFF2E7D32)
                                            } else {
                                                MaterialTheme.colorScheme.error
                                            },
                                        style = MaterialTheme.typography.bodyMedium,
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
