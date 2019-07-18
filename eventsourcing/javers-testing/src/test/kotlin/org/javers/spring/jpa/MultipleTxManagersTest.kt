package org.javers.spring.jpa

import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.javers.core.Javers
import org.javers.hibernate.entity.Person
import org.javers.hibernate.entity.PersonCrudRepository
import org.javers.repository.jql.QueryBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.sql.DataSource

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [MultipleTxManagersConfig::class])
class MultipleTxManagersTest {

    @Autowired
    private lateinit var javers: Javers
    @Autowired
    private lateinit var repository: PersonCrudRepository
    @Autowired
    private lateinit var dataSource: DataSource

    @BeforeEach
    fun setup() {
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute("DELETE jv_snapshot")
            }
        }
    }

    @Test
    fun contextLoading() {
        javers.shouldNotBeNull()
        repository.shouldNotBeNull()
        dataSource.shouldNotBeNull()
    }


    @Test
    fun `복수의 TransactionManager가 있더라도 @Primary 의 것을 사용합니다`() {
        val person = Person(id = "debop")
        repository.save(person)

        javers.findSnapshots(QueryBuilder.byInstanceId(person.id, Person::class.java).build()).size shouldEqualTo 1
    }
}