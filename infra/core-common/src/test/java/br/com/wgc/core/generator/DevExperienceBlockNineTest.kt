package br.com.wgc.core.generator

import br.com.wgc.core.benchmark.BaselineProfileEvaluator
import br.com.wgc.core.benchmark.MacrobenchmarkMetric
import br.com.wgc.core.generator.cli.CoreDxCli
import br.com.wgc.core.generator.depgraph.DependencyGraphAnalyzer
import br.com.wgc.core.generator.depgraph.ModuleNode
import br.com.wgc.core.generator.docs.ArchitectureDocGenerator
import br.com.wgc.core.generator.flags.FeatureFlagDefinition
import br.com.wgc.core.generator.flags.FeatureFlagRegistry
import br.com.wgc.core.generator.migration.MigrationStep
import br.com.wgc.core.generator.migration.SchemaMigrationPlanner
import br.com.wgc.core.generator.openapi.OpenApiContractGenerator
import br.com.wgc.core.generator.template.FeatureModuleTemplateGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DevExperienceBlockNineTest {
    @Test
    fun testOpenApiContractGenerator() {
        val generator = OpenApiContractGenerator()
        val code = generator.generateDataClass("UserDto", mapOf("id" to "String", "age" to "Int"))
        assertTrue(code.contains("data class UserDto("))
        assertTrue(code.contains("val id: String,"))
    }

    @Test
    fun testCoreDxCli() {
        val cli = CoreDxCli()
        assertEquals(CoreDxCli.EXIT_CODE_HELP, cli.execute(emptyArray()))
        assertEquals(CoreDxCli.EXIT_CODE_SUCCESS, cli.execute(arrayOf("--help")))
        assertEquals(CoreDxCli.EXIT_CODE_SUCCESS, cli.execute(arrayOf("verify-architecture")))
    }

    @Test
    fun testBaselineProfileEvaluator() {
        val evaluator = BaselineProfileEvaluator()
        val compliant = MacrobenchmarkMetric(startupTimeMs = 800L, frameJankRate = 2.0f, peakMemoryMb = 120f)
        assertTrue(evaluator.isCompliant(compliant))

        val slow = MacrobenchmarkMetric(startupTimeMs = 2000L, frameJankRate = 2.0f, peakMemoryMb = 120f)
        assertFalse(evaluator.isCompliant(slow))
    }

    @Test
    fun testFeatureModuleTemplateGenerator() {
        val generator = FeatureModuleTemplateGenerator()
        val script = generator.generateBuildGradleKts("br.com.wgc.feature.login", includeCompose = true)
        assertTrue(script.contains("wgc.android.library.compose"))
        assertTrue(script.contains("namespace = \"br.com.wgc.feature.login\""))
    }

    @Test
    fun testSchemaMigrationPlanner() {
        val planner = SchemaMigrationPlanner()
        val steps =
            listOf(
                MigrationStep(1, 2, "Add column age"),
                MigrationStep(2, 3, "Add table addresses"),
            )
        val plan = planner.planMigration(1, 3, steps)
        assertEquals(2, plan.size)
        assertEquals(1, plan[0].fromVersion)
        assertEquals(3, plan[1].toVersion)
    }

    @Test
    fun testDependencyGraphAnalyzerCycles() {
        val analyzer = DependencyGraphAnalyzer()
        val acyclic =
            listOf(
                ModuleNode(":app", setOf(":feature:home")),
                ModuleNode(":feature:home", setOf(":core")),
                ModuleNode(":core", emptySet()),
            )
        assertFalse(analyzer.hasCycles(acyclic))

        val cyclic =
            listOf(
                ModuleNode(":a", setOf(":b")),
                ModuleNode(":b", setOf(":a")),
            )
        assertTrue(analyzer.hasCycles(cyclic))
    }

    @Test
    fun testFeatureFlagRegistry() {
        val registry = FeatureFlagRegistry()
        val flag = FeatureFlagDefinition("dark_mode", false, "Dark theme toggle")
        registry.register(flag)

        assertEquals(flag, registry.get<Boolean>("dark_mode"))
        assertTrue(registry.allKeys().contains("dark_mode"))
    }

    @Test
    fun testArchitectureDocGenerator() {
        val generator = ArchitectureDocGenerator()
        val md = generator.generateModuleSummary(listOf(Triple(":infra:core-common", "Foundation", "Core Team")))
        assertTrue(md.contains("# Architecture Modules"))
        assertTrue(md.contains("`:infra:core-common`"))
    }
}
