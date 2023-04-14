# Database

This module contains the [database migration scripts](src/main/resources/db/migration) that are managed by Flyway and is responsible for generating Java classes from the database to be used with the JOOQ library.

After changing the SQL scripts in this module, run `./gradlew generateJooq` in the main folder of this project to generate JOOQ classes from an adhoc-database that has been initialized with these scripts via Flyway.

The `generateJooq` task will start up a local PostgreSQL database, run the Flyway scripts against it to update the schema, and use JOOQ's source code generator to generate Java classes from the database model. This ensures that the database model in the database and in the code stay in sync.

If you get a Flyway error like "Migration checksum mismatch for migration", you can purge your local PostgreSQL database via the command `./gradlew composeDownForced` from the main folder of this project.