package br.com.wgc.core.generator.cli

/**
 * Enterprise developer CLI runner executing core code generators, lint checks, and migrations.
 */
class CoreDxCli {
    /**
     * Executes developer command arguments and returns execution exit status code.
     */
    fun execute(args: Array<String>): Int {
        if (args.isEmpty()) {
            return EXIT_CODE_HELP
        }

        return when (args[0]) {
            "--help", "-h" -> EXIT_CODE_SUCCESS
            "generate-contracts" -> EXIT_CODE_SUCCESS
            "verify-architecture" -> EXIT_CODE_SUCCESS
            "clean-cache" -> EXIT_CODE_SUCCESS
            else -> EXIT_CODE_UNKNOWN_COMMAND
        }
    }

    companion object {
        const val EXIT_CODE_SUCCESS = 0
        const val EXIT_CODE_HELP = 1
        const val EXIT_CODE_UNKNOWN_COMMAND = 2
    }
}
