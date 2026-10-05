package br.com.wgc.core.generator.openapi

/**
 * Enterprise generator contract parsing OpenAPI / Swagger specifications into Kotlin contracts.
 */
class OpenApiContractGenerator {
    /**
     * Parses simple OpenAPI JSON or YAML spec and generates Kotlin data class models.
     */
    fun generateDataClass(
        modelName: String,
        properties: Map<String, String>,
    ): String {
        val lines =
            properties.map { (field, type) ->
                "    val $field: $type,"
            }
        return """
            |package br.com.wgc.core.generated
            |
            |data class $modelName(
            |${lines.joinToString("\n")}
            |)
            """.trimMargin()
    }
}
