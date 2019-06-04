package io.github.debop.springdata.mapping.tree

import io.github.debop.springdata.jpa.entities.AbstractJpaTreeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table


interface TreeNodeRepository : JpaRepository<TreeNode, Long> {

    @Query("select n from tree_treenode n where n.parent is null")
    fun findAllRoot(): List<TreeNode>

}

@Entity(name = "tree_treenode")
@Table(indexes = [Index(name = "ix_tree_treenode_parent", columnList = "parent_id")])
data class TreeNode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var title: String
) : AbstractJpaTreeEntity<TreeNode>() {

    var description: String? = null
}