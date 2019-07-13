package io.github.debop.javers.mongodb.model

import org.bson.Document
import org.javers.core.commit.CommitId

/**
 * MongoDBHeadId
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 12
 */
class MongoDBHeadId(private val id: String) {

    constructor(doc: Document): this(doc.getString(KEY))
    constructor(commitId: CommitId): this(commitId.value())

    companion object {
        const val COLLECTION_NAME = "cv_head_id"
        private const val KEY = "id"
    }

    fun toCommitId(): CommitId = CommitId.valueOf(id)

    fun toDocument(): Document = Document(KEY, id)

    fun getUpdateCommand(): Document = Document("\$set", toDocument())

}