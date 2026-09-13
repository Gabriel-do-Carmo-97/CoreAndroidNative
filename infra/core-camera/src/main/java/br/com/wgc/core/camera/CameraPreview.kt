package br.com.wgc.core.camera

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

/**
 * Componente Jetpack Compose que renderiza o preview do sensor de câmera utilizando CameraX.
 *
 * O componente vincula a câmera automaticamente ao [LocalLifecycleOwner] atual, iniciando o streaming
 * quando a tela entra em foco e desligando o sensor automaticamente quando a tela é pausada ou destruída.
 *
 * ### Exemplo de Uso:
 * ```kotlin
 * if (hasCameraPermission) {
 *     CameraPreview(
 *         modifier = Modifier.fillMaxSize(),
 *         cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
 *         onPreviewViewCreated = { previewView ->
 *             // Configurações adicionais se necessário
 *         }
 *     )
 * }
 * ```
 *
 * @param modifier O modificador Compose aplicado ao contêiner de renderização.
 * @param cameraSelector O seletor da câmera (padrão: [CameraSelector.DEFAULT_BACK_CAMERA]).
 * @param onPreviewViewCreated Callback executado quando a instância interna do [PreviewView] for instanciada.
 */
@Composable
@RequiresPermission(Manifest.permission.CAMERA)
@Suppress("TooGenericExceptionCaught")
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    onPreviewViewCreated: (PreviewView) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }

    AndroidView(
        factory = {
            onPreviewViewCreated(previewView)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview =
                    Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                    )
                } catch (_: Exception) {
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        },
        modifier = modifier,
    )
}
