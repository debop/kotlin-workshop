import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import com.google.protobuf.gradle.testProtobuf

plugins {
    java
    idea
    id(BuildPlugins.protobuf) version BuildPlugins.Versions.protobuf
}

//sourceSets {
//    create("sample") {
//        proto {
//            srcDir("src/sample/protobuf")
//        }
//    }
//}

dependencies {

    api(project(":kotlin-basic"))
    testImplementation(project(":kotlin-tests"))

    api(Libraries.kotlinx_coroutines_jdk8)

    // NOTE: linux-x86_64 를 따로 추가해줘야 제대로 classifier가 지정된다. 이유는 모르겠지만, 이렇게 해야 제대로 된 jar를 참조한다
    implementation(Libraries.netty_transport_native_epoll + ":linux-x86_64")
    implementation(Libraries.netty_transport_native_kqueue + ":osx-x86_64")

    implementation(Libraries.grpc_protobuf)
    implementation(Libraries.grpc_stub)
    implementation(Libraries.grpc_netty)

    implementation(Libraries.protobuf_java)
    implementation(Libraries.kroto_plus_coroutines)


    // Extra proto source files besides the ones residing under
    // "src/main".
    protobuf(files("lib/protos.tar.gz"))
    protobuf(files("ext/"))
    // Extra proto source files for test besides the ones residing under
    // "src/test".
    testProtobuf(files("lib/protos-test.tar.gz"))
}

protobuf {
    protoc {
        artifact = Libraries.protobuf_protoc
    }
    plugins {
        id("grpc") {
            artifact = Libraries.grpc_protoc_gen_grpc_java
        }
        //        id("kroto") {
        //            artifact = Libraries.kroto_plus_protoc_gen_kroto_plus + ":jvm8@jar"
        //        }
        id("kroto") {
            artifact = Libraries.kroto_plus_protoc_gen_grpc_coroutines + ":jvm8@jar"
        }

    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
                id("kroto")
            }
        }
    }
}