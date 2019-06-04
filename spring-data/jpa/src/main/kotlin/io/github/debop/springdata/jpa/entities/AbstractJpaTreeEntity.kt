package io.github.debop.springdata.jpa.entities

import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.CascadeType
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MappedSuperclass
import javax.persistence.OneToMany

/**
 * Tree 구조를 가지는 엔티티의 기본 Class
 *
 * @author debop (Sunghyouk Bae)
 */
@MappedSuperclass
abstract class AbstractJpaTreeEntity<T : JpaTreeEntity<T>> : JpaTreeEntity<T> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    override var parent: T? = null

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.EXTRA)
    override val children: MutableSet<T> = hashSetOf()
}