package br.com.wgc.core.testing.architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Testes estáticos de conformidade arquitetural (Architecture Fitness Tests).
 *
 * Garante que regras de design do CoreAndroidNative sejam respeitadas em todos os módulos:
 * 1. Zero Jetpack Compose em módulos de infraestrutura não-visuais.
 * 2. Proibição de chamadas diretas a android.util.Log em código de produção (deve usar CoreLogger).
 * 3. Convenção de namespace corporativo padronizado (`br.com.wgc.`).
 */
@Suppress("NestedBlockDepth")
class ArchitectureFitnessTest {
    private val projectRoot: File by lazy {
        var current: File = File(".").canonicalFile
        while (current.parentFile != null && !File(current, "settings.gradle.kts").exists()) {
            val parent = current.parentFile ?: break
            current = parent
        }
        current
    }

    @Test
    fun `ensure no compose dependencies imported in headless infrastructure modules`() {
        val nonUiModules =
            listOf(
                "infra/core-common",
                "infra/core-storage",
                "infra/core-database",
                "infra/core-network",
                "infra/core-device",
            )

        val violations = mutableListOf<String>()

        nonUiModules.forEach { moduleRelPath ->
            val srcDir = File(projectRoot, "$moduleRelPath/src/main/java")
            if (srcDir.exists()) {
                srcDir.walkTopDown().filter { it.extension == "kt" }.forEach { file ->
                    val lines = file.readLines()
                    lines.forEachIndexed { index, line ->
                        if (line.trim().startsWith("import androidx.compose.")) {
                            violations.add("${file.relativeTo(projectRoot)}:${index + 1} -> $line")
                        }
                    }
                }
            }
        }

        assertTrue(
            "Encontrada violação de acoplamento com Jetpack Compose em módulos não-visuais:\n" +
                violations.joinToString("\n"),
            violations.isEmpty(),
        )
    }

    @Test
    fun `ensure no raw android util Log in production code`() {
        val violations = mutableListOf<String>()

        val infraDir = File(projectRoot, "infra")
        if (infraDir.exists()) {
            infraDir
                .walkTopDown()
                .filter { it.extension == "kt" && it.path.contains("src${File.separator}main") }
                .filterNot { it.name == "DefaultCoreLogger.kt" } // DefaultCoreLogger é a única exceção permitida
                .forEach { file ->
                    val lines = file.readLines()
                    lines.forEachIndexed { index, line ->
                        if (line.contains("android.util.Log.")) {
                            violations.add("${file.relativeTo(projectRoot)}:${index + 1} -> $line")
                        }
                    }
                }
        }

        assertTrue(
            "Uso direto de android.util.Log proibido em produção. Utilize CoreLogger:\n" +
                violations.joinToString("\n"),
            violations.isEmpty(),
        )
    }

    @Test
    fun `ensure all kotlin production files follow br com wgc package standard`() {
        val violations = mutableListOf<String>()

        val srcDirs =
            listOf(
                File(projectRoot, "infra"),
                File(projectRoot, "bundle"),
            )

        srcDirs.forEach { dir ->
            if (dir.exists()) {
                dir
                    .walkTopDown()
                    .filter { it.extension == "kt" && it.path.contains("src${File.separator}main") }
                    .forEach { file ->
                        val firstPackageLine =
                            file.useLines { lines ->
                                lines
                                    .firstOrNull { it.contains("package ") }
                                    ?.replace("\uFEFF", "")
                                    ?.trim()
                            }
                        if (firstPackageLine == null || !firstPackageLine.contains("br.com.wgc")) {
                            violations.add("${file.relativeTo(projectRoot)} -> $firstPackageLine")
                        }
                    }
            }
        }

        assertTrue(
            "Arquivos fora do padrão de namespace corporativo br.com.wgc:\n" +
                violations.joinToString("\n"),
            violations.isEmpty(),
        )
    }
}
