package io.github.debop.springdata.jdbc.basic.aggregate

import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

data class Model(var name: String, var description: String)

data class ModelReport(val modelName: String?, val description: String?, val setName: String?)

@Table("Handbuch")
data class Manual(var text: String?, var author: String?) {
    @Id
    @Column("handbuch_id")
    var id: Long? = null
}

class LegoSet {

    @Id var id: Int = 0
    var name: String? = null

    @Column("min_age") var minAge: Int = 0
    @Column("max_age") var maxAge: Int = 0

    /**
     * Since Manuals are part of a {@link LegoSet} and only make sense inside a {@link LegoSet}
     * it is considered part of the Aggregate.
     */
    @Column("handbuch_id")
    var manual: Manual? = null

    // You can build multiple models from one LegoSet
    @Column(keyColumn = "name")
    var models: MutableMap<String, Model> = HashMap()

    fun addModel(name: String, description: String) {
        models[name] = Model(name, description)
    }

    override fun toString(): String {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE)
    }
}