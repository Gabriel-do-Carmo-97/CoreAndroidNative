package br.com.wgc.core.qa

import br.com.wgc.core.qa.bcv.BinaryCompatibilityValidator
import br.com.wgc.core.qa.canary.CanaryDeploymentBucketCalculator
import br.com.wgc.core.qa.chaos.ChaosFaultInjector
import br.com.wgc.core.qa.chaos.InjectedChaosFault
import br.com.wgc.core.qa.flaky.FlakyTestDetector
import br.com.wgc.core.qa.fuzzing.FuzzDataGenerator
import br.com.wgc.core.qa.mutation.MutantStatus
import br.com.wgc.core.qa.mutation.MutationCandidate
import br.com.wgc.core.qa.mutation.MutationTestScoreReporter
import br.com.wgc.core.qa.reproducible.ReproducibleBuildVerifier
import br.com.wgc.core.qa.sbom.SbomComponent
import br.com.wgc.core.qa.sbom.SbomLicenseAuditor
import br.com.wgc.core.qa.security.SecurityHeadersAuditor
import br.com.wgc.core.qa.slas.ServiceLevelObjective
import br.com.wgc.core.qa.slas.SloComplianceReporter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EnterpriseQaBlockTenTest {
    @Test
    fun testMutationTestScoreReporter() {
        val reporter = MutationTestScoreReporter()
        val mutants =
            listOf(
                MutationCandidate("m1", "File.kt:10", "MathMutator", MutantStatus.KILLED),
                MutationCandidate("m2", "File.kt:15", "MathMutator", MutantStatus.SURVIVED),
            )
        val score = reporter.calculateScore(mutants)
        assertEquals(50f, score, 0.001f)
    }

    @Test
    fun testFuzzDataGenerator() {
        val fuzzer = FuzzDataGenerator(seed = 123L)
        val bytes = fuzzer.generateRandomBytes(16)
        assertEquals(16, bytes.size)
        val mutated = fuzzer.mutate(bytes)
        assertEquals(16, mutated.size)
    }

    @Test
    fun testSbomLicenseAuditor() {
        val auditor = SbomLicenseAuditor(forbiddenLicenses = setOf("GPL-3.0"))
        val components =
            listOf(
                SbomComponent("lib-a", "1.0", "pkg:maven/a", "Apache-2.0"),
                SbomComponent("lib-b", "2.0", "pkg:maven/b", "GPL-3.0"),
            )
        val violations = auditor.audit(components)
        assertEquals(1, violations.size)
        assertEquals("lib-b", violations[0].name)
    }

    @Test
    fun testCanaryDeploymentBucketCalculator() {
        val calculator = CanaryDeploymentBucketCalculator()
        val bucket = calculator.computeBucket("user-1234", "new_checkout")
        assertTrue(bucket in 0..99)

        assertTrue(calculator.isEligible("user-1234", "new_checkout", 100))
        assertFalse(calculator.isEligible("user-1234", "new_checkout", 0))
    }

    @Test
    fun testBinaryCompatibilityValidator() {
        val validator = BinaryCompatibilityValidator()
        val prev = setOf("fun foo(): Int", "fun bar(): String")
        val current = setOf("fun foo(): Int") // bar removed
        val breaks = validator.findBreakingChanges(prev, current)
        assertEquals(setOf("fun bar(): String"), breaks)
    }

    @Test
    fun testReproducibleBuildVerifier() {
        val verifier = ReproducibleBuildVerifier()
        assertTrue(verifier.areArtifactsIdentical(byteArrayOf(1, 2), byteArrayOf(1, 2)))
        assertFalse(verifier.areArtifactsIdentical(byteArrayOf(1, 2), byteArrayOf(1, 3)))
    }

    @Test
    fun testChaosFaultInjector() {
        val injector = ChaosFaultInjector()
        injector.injectFault(InjectedChaosFault.NetworkLatency(500L))
        assertTrue(injector.hasFault())
        assertEquals(1, injector.getActiveFaults().size)
        injector.clearFaults()
        assertFalse(injector.hasFault())
    }

    @Test
    fun testFlakyTestDetector() {
        val detector = FlakyTestDetector()
        assertTrue(detector.isFlaky(listOf(true, false, true)))
        assertFalse(detector.isFlaky(listOf(true, true, true)))
    }

    @Test
    fun testSecurityHeadersAuditor() {
        val auditor = SecurityHeadersAuditor()
        val headers = mapOf("Strict-Transport-Security" to "max-age=31536000")
        val missing = auditor.findMissingHeaders(headers)
        assertTrue(missing.contains("X-Content-Type-Options"))
        assertFalse(missing.contains("Strict-Transport-Security"))
    }

    @Test
    fun testSloComplianceReporter() {
        val reporter = SloComplianceReporter()
        val objectives =
            listOf(
                ServiceLevelObjective("p99_latency", 200.0, 150.0, isLowerBetter = true),
                ServiceLevelObjective("availability", 99.9, 99.95, isLowerBetter = false),
            )
        val compliance = reporter.calculateCompliance(objectives)
        assertEquals(100f, compliance, 0.001f)
    }
}
