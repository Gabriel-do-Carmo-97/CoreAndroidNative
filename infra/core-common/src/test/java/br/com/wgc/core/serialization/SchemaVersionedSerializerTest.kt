package br.com.wgc.core.serialization

import org.junit.Assert.assertEquals
import org.junit.Test

class SchemaVersionedSerializerTest {
    data class UserProfile(
        val name: String,
        val email: String,
    )

    class UserProfileMigrator : SchemaMigrator<UserProfile> {
        override fun migrate(
            fromVersion: Int,
            targetVersion: Int,
            rawPayload: String,
        ): String {
            if (fromVersion == 1 && targetVersion == 2) {
                // v1 had only "name", migrate to "name|email@unknown"
                return "$rawPayload|unknown@domain.com"
            }
            return rawPayload
        }

        override fun deserialize(currentVersionPayload: String): UserProfile {
            val parts = currentVersionPayload.split("|")
            return UserProfile(name = parts[0], email = parts[1])
        }
    }

    @Test
    fun should_migrate_older_schema_versions_seamlessly() {
        val serializer =
            SchemaVersionedSerializer(
                currentVersion = 2,
                migrator = UserProfileMigrator(),
            )

        // v1 payload
        val v1Payload = VersionedPayload(schemaVersion = 1, payload = "Alice")
        val user = serializer.parse(v1Payload)

        assertEquals("Alice", user.name)
        assertEquals("unknown@domain.com", user.email)

        // v2 payload
        val v2Payload = VersionedPayload(schemaVersion = 2, payload = "Bob|bob@work.com")
        val v2User = serializer.parse(v2Payload)

        assertEquals("Bob", v2User.name)
        assertEquals("bob@work.com", v2User.email)
    }
}
