package org.javers.spring.boot

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * SpringBootAuditableIntegrationBeansTest
 *
 * @author debop
 * @since 19. 7. 18
 */
@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("beans")
class SpringBootAuditableIntegrationBeansTest: AbstractSpringBootAuditableIntegrationTest() {
}