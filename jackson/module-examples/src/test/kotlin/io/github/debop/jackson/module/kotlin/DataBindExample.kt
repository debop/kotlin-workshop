package io.github.debop.jackson.module.kotlin

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test

/**
 * DataBindExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 28
 */
class DataBindExample {

    companion object: KLogging()

    val mapper = jacksonObjectMapper()

    interface InviteTo

    @JsonTypeName("CONTACT")
    data class InviteToContact(val name: String? = null): InviteTo

    @JsonTypeName("USER")
    data class InviteToUser(val user: String): InviteTo

    enum class InviteKind {
        CONTACT,
        USER
    }

    data class Invite(
        val kind: InviteKind,
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "kind", visible = true)
        @JsonSubTypes(JsonSubTypes.Type(InviteToContact::class),
                      JsonSubTypes.Type(InviteToUser::class))
        val to: InviteTo
    )

    @Test
    fun `data bind with polymorphic enum`() {

        val contact = InviteToContact("Foo")
        val invite = Invite(InviteKind.CONTACT, contact)

        val json = mapper.writeValueAsString(invite)
        logger.trace { "json=$json" }
        json shouldBeEqualTo """{"kind":"CONTACT","to":{"name":"Foo"}}"""

        val parsed = mapper.readValue<Invite>(json)
        parsed shouldBeEqualTo invite
        parsed.to shouldBeInstanceOf InviteToContact::class
        parsed.to shouldBeEqualTo contact
    }
}