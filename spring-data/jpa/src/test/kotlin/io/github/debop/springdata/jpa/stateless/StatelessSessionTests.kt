package io.github.debop.springdata.jpa.stateless

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import io.github.debop.springdata.jpa.withStatelessSession
import mu.KLogging
import org.amshove.kluent.shouldBeGreaterThan
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.system.measureTimeMillis

/**
 * StatelessSessionTests
 * @author debop (Sunghyouk Bae)
 */

@Suppress("UNCHECKED_CAST")
@TestMethodOrder(OrderAnnotation::class)
class StatelessSessionTests : AbstractDataJpaTest() {

    companion object : KLogging() {
        const val COUNT = 10
    }

    @Order(0)
    @Test
    fun `warm up`() {
        em.entityManager.withStatelessSession {
            repeat(5) {
                val entity = StatelessEntity(name = "debop-$it")
                this.insert(entity)
            }
        }
        repeat(5) {
            em.persist(StatelessEntity(name = "debop-$it"))
        }
        em.flush()
    }

    @RepeatedTest(5)
    fun `simple entity with session`() {

        val elapsed = measureTimeMillis {
            repeat(COUNT) {
                em.persist(StatelessEntity(name = "debop-$it"))
            }
            em.flush()
        }
        log.debug { "Session save: $elapsed msec" }
    }

    @RepeatedTest(5)
    fun `simple entity with stateless`() {
        val elapsed = measureTimeMillis {
            em.entityManager.withStatelessSession {
                repeat(COUNT) {
                    val entity = StatelessEntity(name = "debop-$it")
                    this.insert(entity)
                }
            }
        }
        log.debug { "StatelessSession save: $elapsed msec" }
    }

    @RepeatedTest(5)
    fun `one-to-mamny entity with  session`() {
        val elapsed = measureTimeMillis {
            repeat(COUNT) {
                val master = createMaster("master-$it")
                em.persist(master)
            }
            em.flush()
        }
        log.debug { "Session save: $elapsed msec" }
    }

    @RepeatedTest(5)
    fun `one-to-mamny entity with stateless`() {
        val elapsed = measureTimeMillis {
            em.entityManager.withStatelessSession {
                repeat(COUNT) {
                    val master = createMaster("master-$it")
                    this.insert(master)
                    master.details.forEach { detail ->
                        this.insert(detail)
                    }
                }
            }
        }
        log.debug { "StatelessSession save: $elapsed msec" }
    }

    @Test
    fun `load one-to-many with stateless`() {

        em.entityManager.withStatelessSession {
            repeat(10) {
                val master = createMaster("master-$it")
                this.insert(master)
                log.debug { "Saved master=$master" }
                master.details.forEach { detail ->
                    detail.master = master
                    this.insert(detail)
                    log.debug { "  Saved detail=$detail" }
                }
            }
        }

        //        val masters = em.entityManager.createQuery("from StatelessMaster m", StatelessMaster::class.java).resultList

        val masters = em.entityManager.withStatelessSession {
            createNativeQuery("select m.* from stateless_master m").list()
        } ?: emptyList<Any?>()

        masters.size shouldBeGreaterThan 0
        log.debug { "masters=$masters" }

        masters.forEach {
            val row = it as Array<Any?>
            val id = row[0]?.toString()?.toLong() ?: 0
            val name = row[1]?.toString() ?: ""
            val master = StatelessMaster(id, name)
            log.debug { "master=$master" }
        }

        log.info { "Load finished..." }
    }

    private fun createMaster(name: String, detailCount: Int = 10): StatelessMaster {
        val master = StatelessMaster(name = name)
        repeat(detailCount) { index ->
            val detail = StatelessDetail(name = "details-$index").also { it.master = master }
            master.details.add(detail)
        }
        return master
    }
}