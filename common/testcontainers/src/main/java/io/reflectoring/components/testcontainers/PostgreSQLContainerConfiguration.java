package io.reflectoring.components.testcontainers;

import jakarta.annotation.PostConstruct;
import org.jooq.tools.jdbc.SingleConnectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Starts an empty PostgreSQL container and configures a DataSource against it.
 */
@Configuration
class PostgreSQLContainerConfiguration {

	private static final Logger logger =
		LoggerFactory.getLogger(PostgreSQLContainerConfiguration.class);
	private static final String DATABASE_NAME = "components";
	private static final String USERNAME = "components";
	private static final String PASSWORD = "components";

	private PostgreSQLContainer container;

	@Bean
	PostgreSQLContainer<?> postgreSQLContainer(DataSourceProperties dataSourceProperties) {
		container =
			new PostgreSQLContainer<>("postgres:15")
				.withDatabaseName(DATABASE_NAME)
				.withPassword(PASSWORD)
				.withUsername(USERNAME);
		container.start();
		logger.info(
			String.format(
				"Started PostgreSQL testcontainer. JDBC URL: %s", container.getJdbcUrl()));
		return container;
	}

	@PostConstruct
	void cleanup(){
		if(container != null) {
			container.stop();
		}
	}

	@Bean
	DataSource postgresqlDataSource(PostgreSQLContainer postgreSQLContainer) throws SQLException {
		DataSource postgresDataSource = DataSourceBuilder.create()
			.password(PASSWORD)
			.username(USERNAME)
			.url(postgreSQLContainer.getJdbcUrl())
			.driverClassName(postgreSQLContainer.getDriverClassName())
			.build();

		// We use a single connection datasource so that JOOQ queries and Spring Data queries
		// share the same connection in tests. Otherwise, data added with Spring Data and
		// queried with JOOQ (or vice versa) within the same test won't work, because they would
		// use different connections.
		return new SingleConnectionDataSource(postgresDataSource.getConnection());
	}
}
