package org.javers.core

import org.javers.common.date.DateProvider
import org.javers.common.string.ShaDigest
import org.javers.core.commit.CommitFactory
import org.javers.core.graph.LiveCdoFactory
import org.javers.core.json.JsonConverter
import org.javers.core.json.JsonConverterBuilder
import org.javers.core.metamodel.`object`.GlobalIdFactory
import org.javers.core.metamodel.`object`.InstanceId
import org.javers.core.metamodel.`object`.UnboundedValueObjectId
import org.javers.core.metamodel.`object`.ValueObjectId
import org.javers.core.metamodel.property.Property
import org.javers.core.metamodel.type.TypeMapper
import org.javers.core.model.DummyAddress
import org.javers.core.snapshot.SnapshotFactory
import org.javers.repository.api.JaversExtendedRepository
import org.javers.repository.api.JaversRepository
import org.javers.shadow.ShadowFactory
import org.polyjdbc.core.query.QueryRunner

/**
 * JaversTestBuilder
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 15
 */
class JaversTestBuilder(val builder: JaversBuilder = JaversBuilder()) {

    fun javers(): Javers = builder.getContainerComponent(Javers::class.java)

    companion object {

        fun javersTestAssembly() = JaversTestBuilder().apply {
            builder.withMappingStyle(MappingStyle.FIELD).build()
        }

        fun javersTestAssembly(packagesToScan: String) = JaversTestBuilder().apply {
            builder.withPackagesToScan(packagesToScan).build()
        }

        fun javersTestAssembly(classToScan: Class<*>) = JaversTestBuilder().apply {
            builder.scanTypeName(classToScan).build()
        }

        fun javersTestAssembly(mappingStyle: MappingStyle) = JaversTestBuilder().apply {
            builder.withMappingStyle(mappingStyle).build()
        }

        fun javersTestAssembly(dateProvider: DateProvider) = JaversTestBuilder().apply {
            builder.withDateTimeProvider(dateProvider).build()
        }

        fun javersTestAssembly(repository: JaversRepository) = JaversTestBuilder().apply {
            builder.registerJaversRepository(repository).build()
        }

        fun newInstance() = javersTestAssembly().javers()
    }

    //    fun createCdoWrapper(cdo: Any): Cdo {
    //        val mType = typeMapper.getJaversManagedType(cdo.javaClass)
    //        val id = instanceId(cdo)
    //
    //        return LiveCdoWrapper(cdo, id, mType)
    //    }

    fun getProperty(type: Class<*>, propertyName: String): Property =
        typeMapper.getJaversManagedType(type).getProperty(propertyName)

    internal inline fun <reified T: Any> getComponent(): T = builder.getContainerComponent(T::class.java)

    val snapshotFactory: SnapshotFactory by lazy { getComponent<SnapshotFactory>() }

    val javersRepository: JaversExtendedRepository by lazy { getComponent<JaversExtendedRepository>() }

    val typeMapper: TypeMapper by lazy { getComponent<TypeMapper>() }

    val queryRunner: QueryRunner by lazy { getComponent<QueryRunner>() }

    val globalIdFactory: GlobalIdFactory by lazy { getComponent<GlobalIdFactory>() }

    val liveCdoFactory: LiveCdoFactory by lazy { getComponent<LiveCdoFactory>() }

    val commitFactory: CommitFactory by lazy { getComponent<CommitFactory>() }

    val jsonConverter: JsonConverter by lazy { getComponent<JsonConverter>() }

    val shadowFactory: ShadowFactory by lazy { getComponent<ShadowFactory>() }

    val jsonConverterMinifiedPrint: JsonConverter by lazy {
        JaversBuilder.javers().withPrettyPrint(false).build().jsonConverter
    }

    fun getJsonConverterBuilder(): JsonConverterBuilder = getComponent()

    fun hash(obj: Any): String {
        val jsonState = jsonConverter.toJson(javers().commit("", obj).snapshots[0].state)
        return ShaDigest.longDigest(jsonState)
    }

    fun addressHash(city: String): String = hash(DummyAddress(city = city))

    //    fun createLiveGraph(liveCdo:Any) =
    //        builder.getComponent(LiveGraphFactory::class.java).createLiveGraph(liveCdo).root()

    fun instanceId(instance: Any): InstanceId = globalIdFactory.createIdFromInstance(instance)

    fun <T> instanceId(localId: Any, clazz: Class<T>): InstanceId = globalIdFactory.createInstanceId(localId, clazz)

    fun valueObjectId(localId: Any, owningClass: Class<*>, fragment: String): ValueObjectId =
        globalIdFactory.createValueObjectIdFromPath(instanceId(localId, owningClass), fragment)

    fun <V> unboundedValueObjectId(valueObjectClass: Class<V>): UnboundedValueObjectId =
        globalIdFactory.createUnboundedValueObjectId(valueObjectClass)

}