package br.com.wgc.core.analytics.startup

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classificação do tipo de inicialização do aplicativo para fins de telemetria APM.
 */
enum class StartupType {
    /** Inicialização fria: o processo foi criado do zero pelo sistema operacional. */
    COLD,

    /** Inicialização morna: o processo já existia na memória, mas a Activity precisou ser recriada. */
    WARM,

    /** Inicialização quente: a Activity foi apenas trazida para o primeiro plano. */
    HOT,
}

/**
 * Métricas consolidadas de inicialização da aplicação.
 *
 * @property type O tipo de inicialização ([StartupType]).
 * @property totalDurationMs Tempo total decorrido entre o início e a finalização do trace em milissegundos.
 * @property stages Mapa contendo o nome do marco/etapa e o tempo decorrido desde o início do trace.
 * @property isCompleted Indica se o trace foi finalizado com sucesso.
 */
data class StartupMetrics(
    val type: StartupType,
    val totalDurationMs: Long,
    val stages: Map<String, Long>,
    val isCompleted: Boolean,
)

/**
 * Rastreador de tempo de inicialização (Cold/Warm Start Tracer) e marcos de performance.
 *
 * Permite instrumentar a inicialização do app desde o `Application.onCreate` até o primeiro frame
 * renderizado na tela (Time To First Draw - TTFD), demarcando etapas intermediárias críticas como DI,
 * inicialização de banco de dados e conexão de rede.
 */
@Singleton
class AppStartupTracer
    @Inject
    constructor() {
        private val lock = Any()

        @Volatile
        private var startupStartTimeMs: Long = 0L

        @Volatile
        private var currentType: StartupType = StartupType.COLD

        @Volatile
        private var isTracing: Boolean = false

        @Volatile
        private var isCompleted: Boolean = false

        private val stageTimings = ConcurrentHashMap<String, Long>()

        /**
         * Inicia a medição de startup.
         *
         * @param type Tipo de startup sendo monitorado ([StartupType.COLD] por padrão).
         * @param startTimeMs Timestamp inicial em milissegundos (default: [System.currentTimeMillis]).
         */
        fun startTrace(
            type: StartupType = StartupType.COLD,
            startTimeMs: Long = System.currentTimeMillis(),
        ) {
            synchronized(lock) {
                this.currentType = type
                this.startupStartTimeMs = startTimeMs
                this.isTracing = true
                this.isCompleted = false
                this.stageTimings.clear()
            }
        }

        /**
         * Registra um marco intermediário do startup.
         *
         * @param stageName Identificador do marco (ex: "di_initialized", "network_ready").
         * @return O tempo decorrido em milissegundos desde o início do trace até este marco, ou null se não estiver rastreando.
         */
        fun markStage(stageName: String): Long? {
            if (!isTracing) return null
            val now = System.currentTimeMillis()
            val elapsed = (now - startupStartTimeMs).coerceAtLeast(0L)
            stageTimings[stageName] = elapsed
            return elapsed
        }

        /**
         * Registra o Time To First Draw (TTFD) / primeiro desenho de tela.
         *
         * @return O tempo decorrido até o primeiro desenho em milissegundos.
         */
        fun markFirstDraw(): Long? = markStage("first_draw")

        /**
         * Finaliza a coleta do trace de startup e consolida as métricas.
         *
         * @return As métricas consolidadas [StartupMetrics], ou null se o trace não foi iniciado.
         */
        fun finishTrace(): StartupMetrics? {
            synchronized(lock) {
                if (!isTracing) return null
                val now = System.currentTimeMillis()
                val totalDuration = (now - startupStartTimeMs).coerceAtLeast(0L)
                isTracing = false
                isCompleted = true
                return StartupMetrics(
                    type = currentType,
                    totalDurationMs = totalDuration,
                    stages = HashMap(stageTimings),
                    isCompleted = true,
                )
            }
        }

        /**
         * Retorna se há um trace ativo no momento.
         */
        fun isTracing(): Boolean = isTracing

        /**
         * Reseta o estado interno do rastreador.
         */
        fun reset() {
            synchronized(lock) {
                isTracing = false
                isCompleted = false
                startupStartTimeMs = 0L
                stageTimings.clear()
            }
        }
    }
