import com.google.protobuf.gradle.ProtobufExtension
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf) // Подключаем плагин
}

android {
    namespace = "ru.vasilev.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    // gRPC требует некоторых исключений в ресурсах
    packaging {
        resources {
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

configure<ProtobufExtension> {
    protoc {
        // Указываем версии напрямую строками
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.60.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc") {
                    option("lite")
                }
            }
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":util"))

    // gRPC зависимости
    api(libs.grpc.okhttp)
    api(libs.grpc.protobuf.lite)
    api(libs.grpc.stub)
    api("io.grpc:grpc-android:1.60.0")
    api("io.grpc:grpc-api:1.60.0")


    compileOnly(libs.javax.annotation) // Нужно для компиляции стабов

    // Твой текущий стек
    implementation(libs.simple.xml)
    implementation(libs.retrofit.simplexml)
    api(libs.retrofit)
    api(libs.retrofit.gson)
    api(libs.okhttp.logging)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)

    testImplementation(libs.grpc.testing)
    androidTestImplementation(libs.grpc.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}