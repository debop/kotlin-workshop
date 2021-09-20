package io.github.debop.jackson.dataformat.yaml

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.debop.jackson.dataformat.DataSource
import io.github.debop.jackson.dataformat.Database
import io.github.debop.jackson.dataformat.FiveMinuteUser
import io.github.debop.jackson.dataformat.Gender
import io.github.debop.jackson.dataformat.Name
import io.github.debop.jackson.dataformat.Outer
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

/**
 * SerializationExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */
class SerializationExample: AbstractYamlExample() {

    @Test
    fun `serialize and deserialize simple POJO`() {

        val input = FiveMinuteUser("Sunghyouk", "Bae", false, Gender.MALE, byteArrayOf(1, 2, 3, 4))
        val output = yamlMapper.writeValueAsString(input).trimYamlDocMarker()

        logger.debug { "user yaml=$output" }

        val expected = """
            |firstName: "Sunghyouk"
            |lastName: "Bae"
            |verified: false
            |gender: "MALE"
            |userImage: !!binary |-
            |  AQIDBA==
            """.trimMargin()

        output shouldBeEqualTo expected

        val parsed = yamlMapper.readValue<FiveMinuteUser>(output)
        parsed shouldBeEqualTo input
    }

    @Test
    fun `serialize nested POJO`() {
        val input = Outer(Name("Sunghyouk", "Bae"), 51)
        val output = yamlMapper.writeValueAsString(input).trimYamlDocMarker()

        logger.debug { "yaml=$output" }

        val expected = """
            |name:
            |  first: "Sunghyouk"
            |  last: "Bae"
            |age: 51
            """.trimMargin()

        output shouldBeEqualTo expected

        val parsed = yamlMapper.readValue<Outer>(output)
        parsed shouldBeEqualTo input
    }

    @Test
    fun `serialize deserialize dataSource`() {
        val input = Database(DataSource("org.h2.Driver", "jdbc:h2:mem:test", "sa", "",
                                        setOf("cachePrepStmts=true",
                                              "prepStmtCacheSize=250",
                                              "prepStmtCacheSqlLimit=2048",
                                              "useServerPrepStmts=true")))

        val output = yamlMapper.writeValueAsString(input).trimYamlDocMarker()

        logger.debug { "yaml=$output" }

        val expected = """
            |dataSource:
            |  driverClass: "org.h2.Driver"
            |  url: "jdbc:h2:mem:test"
            |  username: "sa"
            |  password: ""
            |  properties:
            |  - "cachePrepStmts=true"
            |  - "prepStmtCacheSize=250"
            |  - "prepStmtCacheSqlLimit=2048"
            |  - "useServerPrepStmts=true"
            """.trimMargin()

        output shouldBeEqualTo expected

        val parsed = yamlMapper.readValue<Database>(output)
        parsed shouldBeEqualTo input
    }
}