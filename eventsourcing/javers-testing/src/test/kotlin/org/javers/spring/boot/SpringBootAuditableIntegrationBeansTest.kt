package org.javers.spring.boot

import org.junit.jupiter.api.Disabled
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled("기본 MappingType이 FIELD이고, 대상 엔티티를 FIELD 에 맞추었습니다. 따로 테스트 해야 합니다.")
@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("beans")
class SpringBootAuditableIntegrationBeansTest: AbstractSpringBootAuditableIntegrationTest() {
}