import org.gradle.external.javadoc.StandardJavadocDocletOptions

plugins {
    `java-library`
    `maven-publish`
    signing
}

val artifactIdProp = property("artifact_id").toString()
val javaVersionProp = property("java_version").toString().toInt()
val junitVersionProp = property("junit_version").toString()

group = property("group").toString()
version = property("version").toString()
description = property("pom_description").toString()

base {
    archivesName.set(artifactIdProp)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersionProp))
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // Strictly zero compile or runtime dependencies outside java.base
    testImplementation(platform("org.junit:junit-bom:$junitVersionProp"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaVersionProp)
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        charSet = "UTF-8"
    }
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(rootProject.file("LICENSE")) {
        into("META-INF")
    }
    from(rootProject.file("AUTHORS")) {
        into("META-INF")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = artifactIdProp
            version = project.version.toString()

            pom {
                name.set(property("pom_name").toString())
                description.set(project.description)
                url.set(property("pom_url").toString())
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set(property("pom_developer_id").toString())
                        name.set(property("pom_developer_name").toString())
                        email.set(property("pom_developer_email").toString())
                        organization.set(property("pom_organization_name").toString())
                        organizationUrl.set(property("pom_organization_url").toString())
                    }
                }
                scm {
                    connection.set(property("pom_scm_connection").toString())
                    developerConnection.set(property("pom_scm_dev_connection").toString())
                    url.set(property("pom_scm_url").toString())
                }
            }
        }
    }
    repositories {
        maven {
            name = "MavenCentral"
            val releasesUrl = uri(
                System.getenv("MAVEN_CENTRAL_RELEASE_URL")
                    ?: (findProperty("mavenCentralReleaseUrl") as String?)
                    ?: "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"
            )
            val snapshotsUrl = uri(
                System.getenv("MAVEN_CENTRAL_SNAPSHOT_URL")
                    ?: (findProperty("mavenCentralSnapshotUrl") as String?)
                    ?: "https://central.sonatype.com/repository/maven-snapshots/"
            )
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            credentials {
                username = System.getenv("MAVEN_CENTRAL_USERNAME")
                    ?: (findProperty("mavenCentralUsername") as String?)
                password = System.getenv("MAVEN_CENTRAL_PASSWORD")
                    ?: (findProperty("mavenCentralPassword") as String?)
            }
        }
    }
}

signing {
    val signingKey = System.getenv("SIGNING_KEY")
        ?: (findProperty("signingInMemoryKey") as String?)
    val signingPassword = System.getenv("SIGNING_PASSWORD")
        ?: (findProperty("signingInMemoryKeyPassword") as String?)
        ?: ""
    isRequired = !signingKey.isNullOrBlank()
    if (!signingKey.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    }
}
