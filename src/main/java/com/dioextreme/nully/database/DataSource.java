package com.dioextreme.nully.database;

import com.dioextreme.nully.env.Env;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource
{
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static
    {
        String host = Env.getEnvOrDockerSecret(Env.dbHostEnvVariable, "nully_db_host");
        String username = Env.getEnvOrDockerSecret(Env.dbUsernameEnvVariable, "nully_db_username");
        String password = Env.getEnvOrDockerSecret(Env.dbPasswordEnvVariable, "nully_db_password");
        String database = System.getenv(Env.dbNameEnvVariable);
        String schema = System.getenv(Env.dbSchemaEnvVariable);

        config.setJdbcUrl("jdbc:postgresql://" + host + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.setSchema(schema);

        config.addDataSourceProperty("reWriteBatchedInserts", true);
        ds = new HikariDataSource(config);
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException
    {
        return ds.getConnection();
    }
}
