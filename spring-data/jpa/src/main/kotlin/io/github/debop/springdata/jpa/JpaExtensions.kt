package io.github.debop.springdata.jpa

import org.hibernate.Session
import org.hibernate.StatelessSession
import org.hibernate.internal.SessionImpl
import javax.persistence.EntityManager

/**
 * JpaExtensions
 * @author debop (Sunghyouk Bae)
 */
fun EntityManager.currentSession(): SessionImpl {
    return unwrap(Session::class.java) as SessionImpl
}

fun EntityManager.openStatlessSession(): StatelessSession {
    return currentSession().sessionFactory.openStatelessSession()
}

/**
 * [StatelessSession] 을 이용하여 작업을 수행합니다. 대량 생성 등 Session에 캐시할 필요가 없는 경우에 사용하면 성능을 높힐 수 있습니다.
 *
 * @param T
 * @param block
 * @return
 */
fun <T : Any> EntityManager.withStatelessSession(block: StatelessSession.() -> T): T? {
    openStatlessSession().use { stateless ->
        val tx = stateless.beginTransaction()
        return try {
            val result = block(stateless)
            tx.commit()
            result
        } catch (e: Exception) {
            try {
                tx.rollback()
            } catch (ignored: Throwable) {
                ignored.printStackTrace()
            }
            null
        }
    }
}