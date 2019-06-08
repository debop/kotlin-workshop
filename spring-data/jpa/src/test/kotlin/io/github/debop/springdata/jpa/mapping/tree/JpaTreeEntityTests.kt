package io.github.debop.springdata.jpa.mapping.tree

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

/**
 * JpaTreeEntityTests
 * @author debop (Sunghyouk Bae)
 */
class JpaTreeEntityTests : AbstractDataJpaTest() {

    @Autowired
    lateinit var repository: TreeNodeRepository

    @Test
    fun `build tree nodes`() {
        val root = TreeNode(title = "root")
        val child1 = TreeNode(title = "child1")
        val child2 = TreeNode(title = "child2")

        root.addChildren(child1, child2)

        val child11 = TreeNode(title = "child11")
        val child12 = TreeNode(title = "child12")
        child1.addChildren(child11, child12)

        repository.saveAndFlush(root)
        clear()

        val loaded = repository.findByIdOrNull(child1.id)

        loaded.shouldNotBeNull()
        loaded shouldEqual child1
        loaded.parent shouldEqual root
        loaded.children.shouldContainAll(setOf(child11, child12))

        val roots = repository.findAllRoot()
        roots.size shouldEqualTo 1
        roots.first() shouldEqual root

        repository.delete(loaded)
        repository.flush()
        clear()

        val roots2 = repository.findAllRoot()
        roots2.size shouldEqualTo 1
        roots2.first() shouldEqual root
        roots2.first().children.shouldContain(child2)
        roots2.first().children.shouldNotContain(child1)
    }
}