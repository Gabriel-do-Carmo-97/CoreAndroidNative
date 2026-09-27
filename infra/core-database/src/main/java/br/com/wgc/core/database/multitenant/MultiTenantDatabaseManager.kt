package br.com.wgc.core.database.multitenant

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.ConcurrentHashMap

/**
 * Gerenciador corporativo para particionamento multi-tenant de bancos de dados Room.
 * Permite isolamento criptográfico e de dados estrito por organização ou usuário,
 * garantindo troca dinâmica e expurgo atômico no logout sem interferir em outras contas.
 */
class MultiTenantDatabaseManager<T : RoomDatabase>(
    private val context: Context,
    private val dbClass: Class<T>,
    private val dbPrefix: String = "tenant_db_",
    private val configureBuilder: ((RoomDatabase.Builder<T>) -> RoomDatabase.Builder<T>)? = null,
) {
    private val activeDatabases = ConcurrentHashMap<String, T>()
    private var currentTenantId: String? = null

    /**
     * Obtém ou inicializa a base de dados isolada para o tenant informado.
     */
    fun getDatabaseForTenant(tenantId: String): T {
        require(tenantId.isNotBlank()) { "Tenant ID cannot be blank" }

        return activeDatabases.computeIfAbsent(tenantId) { id ->
            val dbName = "${dbPrefix}$id.db"
            var builder = Room.databaseBuilder(context, dbClass, dbName)
            if (configureBuilder != null) {
                builder = configureBuilder.invoke(builder)
            }
            builder.build()
        }
    }

    /**
     * Define o tenant ativo para a sessão atual.
     */
    fun setActiveTenant(tenantId: String): T {
        val db = getDatabaseForTenant(tenantId)
        currentTenantId = tenantId
        return db
    }

    /**
     * Retorna a base de dados do tenant ativo no momento.
     */
    fun getActiveDatabase(): T? {
        val tenant = currentTenantId ?: return null
        return activeDatabases[tenant]
    }

    /**
     * Fecha a conexão e remove da memória a instância do banco do tenant.
     */
    fun closeTenant(tenantId: String) {
        activeDatabases.remove(tenantId)?.let { db ->
            if (db.isOpen) {
                db.close()
            }
        }
        if (currentTenantId == tenantId) {
            currentTenantId = null
        }
    }

    /**
     * Remove fisicamente os arquivos de banco de dados do tenant informado (Wipe out-of-band).
     */
    fun deleteTenantDatabase(tenantId: String): Boolean {
        closeTenant(tenantId)
        val dbName = "${dbPrefix}$tenantId.db"
        return context.deleteDatabase(dbName)
    }

    /**
     * Fecha e libera todos os bancos ativos de todos os tenants.
     */
    fun closeAll() {
        activeDatabases.forEach { (_, db) ->
            if (db.isOpen) {
                db.close()
            }
        }
        activeDatabases.clear()
        currentTenantId = null
    }
}
