package br.com.wgc.core.device.security

/**
 * Helper utility for validating runtime bytecode integrity against decompilation/tampering.
 */
object BytecodeObfuscationHelper {
    /**
     * Checks if standard R8/ProGuard class renaming occurred by inspecting test class name.
     */
    fun isObfuscated(clazz: Class<*>): Boolean {
        val simpleName = clazz.simpleName
        // Obfuscated names are typically 1 to 2 characters long (e.g. 'a', 'b', 'c1')
        return simpleName.length <= MAX_OBFUSCATED_NAME_LENGTH
    }

    /**
     * Verifies that debug line numbers have been removed or remapped.
     */
    fun hasStrippedDebugInfo(stackTraceElement: StackTraceElement): Boolean {
        return stackTraceElement.lineNumber <= 0 || stackTraceElement.fileName == null
    }

    private const val MAX_OBFUSCATED_NAME_LENGTH = 2
}
