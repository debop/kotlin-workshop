package io.github.debop.springdata.jpa.mapping.associations.onetomany.set

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Parent
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.FetchType.EAGER
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.JoinColumn


@Entity(name = "onetomany_product")
@DynamicInsert
@DynamicUpdate
data class Product(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String
) : Serializable {

    var description: String? = null
    var initialPrice: BigDecimal? = null
    var reservePrice: BigDecimal? = null

    var startDate: LocalDate? = null

    var endDate: LocalDate? = null

    @Enumerated
    var status: ProductStatus = ProductStatus.ACTIVE

    // detail 에 해당하는 것이 component 일 경우, 한꺼번에 삭제된다.
    @CollectionTable(name = "onetomany_product_image_set", joinColumns = [JoinColumn(name = "product_id")])
    @ElementCollection(targetClass = ProductImage::class, fetch = EAGER)
    val images: MutableSet<ProductImage> = hashSetOf()

    fun addImages(vararg imagesToAdd: ProductImage) {
        imagesToAdd.forEach {
            this.images.add(it)
            it.product = this
        }
    }

    fun removeImages(vararg imagesToRemove: ProductImage) {
        imagesToRemove.forEach {
            if (this.images.remove(it)) {
                it.product = null
            }
        }
    }

    enum class ProductStatus {
        UNKNOWN,
        ACTIVE,
        INACTIVE
    }
}

@Embeddable
@DynamicInsert
@DynamicUpdate
data class ProductImage(@Column(nullable = false) var name: String) : Serializable {

    @Parent
    var product: Product? = null

    var filename: String? = null
    var sizeX: Int? = null
    var sizeY: Int? = null
}

