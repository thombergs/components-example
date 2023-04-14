package io.reflectoring.components.testcontainers;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Use this annotation on JUnit tests that need a PostgreSQL database. Spins up a testcontainer with
 * a PostgreSQL database and adds a {@link javax.sql.DataSource} to the application context.
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Import(PostgreSQLContainerConfiguration.class)
@Tag("PostgreSQLTest")
@ActiveProfiles("test")
@DataJdbcTest // required to set up data source and Flyway
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface PostgreSQLTest {}
