plugins {
    java
    war
}

group = "ru.matviikk"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Зависимость для сервлетов (jakarta.servlet)
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")

    // Зависимость для JSP (jakarta.servlet.jsp)
    implementation("jakarta.servlet.jsp:jakarta.servlet.jsp-api:3.0.0")

    // JSTL для JSP
    implementation("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:2.0.0")
    runtimeOnly("org.glassfish.web:jakarta.servlet.jsp.jstl:2.0.0")
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    // Поддержка EL (Expression Language)
    implementation("org.glassfish:jakarta.el:4.0.2")
    // Зависимость для тестирования (JUnit)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")


    implementation("org.hibernate.validator:hibernate-validator:7.0.0.Final")

}

tasks.war {
    webAppDirectory.set(file("src/main/webapp"))
}

java {
    sourceSets["main"].java.srcDirs("src/main/java")
    sourceSets["main"].resources.srcDirs(
        "src/main/resources",
        "src/main/webapp"
    )
}

tasks.test {
    useJUnitPlatform()
}