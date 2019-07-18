package org.javers.hibernate.integration

import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.javers.core.Javers
import org.javers.hibernate.entity.Person
import org.javers.hibernate.entity.PersonCrudRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@SpringBootTest(classes = [JaversFieldHibernateProxyConfig::class])
@Transactional
class ObjectAccessHookFieldTest {

    companion object : KLogging()

    @PersistenceContext
    lateinit var em: EntityManager

    @Autowired
    lateinit var javers: Javers

    @Autowired
    lateinit var repository: PersonCrudRepository

    @Test
    fun `context loading`() {
        javers.shouldNotBeNull()
        repository.shouldNotBeNull()
    }

    @DisplayName("Save Level")
    @ParameterizedTest(name = "run savePointLevel='{0}', modPointLevel='{1}'")
    @CsvSource("0,1", "1,1", "0,2", "1,2")
    fun hiberateHookWithSaveLevel(savePointLevel: Int, modPointLevel: Int) {

        // GIVEN
        val developer = Person(id = "0", name = "kaz")
        val manager = Person(id = "1", name = "pawel")
        val director = Person(id = "2", name = "Steve")
        developer.boss = manager
        manager.boss = director

        repository.saveAll(listOf(developer, manager, director))
        em.flush()
        em.clear()

        val loadedDeveloper = repository.getOne(developer.id)

        val proxy = loadedDeveloper.getBoss(modPointLevel)
        proxy.shouldNotBeNull()

        // WHEN
        proxy.name = "New Name"
        val savePoint = loadedDeveloper.getBoss(savePointLevel)!!
        logger.debug { "savePoint=$savePoint" }
        repository.save(savePoint)
        em.flush()

        // THEN
        val snapshot = javers.getLatestSnapshot(proxy.id, Person::class.java).get()
        logger.debug { "Latest snapshot=\n${javers.jsonConverter.toJson(snapshot)}" }
        snapshot.getPropertyValue("name") shouldEqual "New Name"
    }
}