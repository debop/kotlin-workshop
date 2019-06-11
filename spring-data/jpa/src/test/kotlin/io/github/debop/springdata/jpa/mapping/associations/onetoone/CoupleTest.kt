package io.github.debop.springdata.jpa.mapping.associations.onetoone

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

/**
 * CoupleTest
 * @author debop (Sunghyouk Bae)
 */
class CoupleTest : AbstractDataJpaTest() {

    @Test
    fun `bidirectional one to one`() {

        val debop = Husband("debop")
        val midoogi = Wife("midoogi")
        debop.wife = midoogi
        midoogi.husband = debop

        em.persist(debop)
        em.persist(midoogi)
        flushAndClear()

        val debop2 = em.find(Husband::class.java, debop.id)!!
        debop2 shouldEqual debop
        debop2.wife shouldEqual midoogi

        // 서로 관계를 끊고 삭제해야 합니다.
        val wife2 = debop2.wife!!
        debop2.wife = null
        wife2.husband = null

        em.remove(debop2)
        em.remove(wife2)

        flush()
    }
}