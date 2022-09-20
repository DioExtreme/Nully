package com.dioextreme.nully.env;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Env
{
    public static final String tokenEnvVariable = "NULLY_BOT_TOKEN";
    public static final String shardIdEnvVariable = "NULLY_BOT_SHARD_ID";

    public static final String dbHostEnvVariable = "NULLY_DB_HOST";
    public static final String dbUsernameEnvVariable = "NULLY_DB_USERNAME";
    public static final String dbPasswordEnvVariable = "NULLY_DB_PASSWORD";
    public static final String dbNameEnvVariable = "NULLY_DB_NAME";
    public static final String dbSchemaEnvVariable = "NULLY_DB_SCHEMA";

    public static boolean hasEnvVariables()
    {
        if (System.getenv(tokenEnvVariable) == null)
        {
            System.err.println("Missing bot token environmental variable " + tokenEnvVariable);
            return false;
        }
        if (System.getenv(shardIdEnvVariable) == null)
        {
            System.err.println("Missing shard id environmental variable " + shardIdEnvVariable);
            return false;
        }
        if (System.getenv(dbHostEnvVariable) == null)
        {
            System.err.println("Missing database host environmental variable " + dbNameEnvVariable);
            return false;
        }
        if (System.getenv(dbUsernameEnvVariable) == null)
        {
            System.err.println("Missing database username environmental variable " + dbUsernameEnvVariable);
            return false;
        }
        if (System.getenv(dbPasswordEnvVariable) == null)
        {
            System.err.println("Missing database password environmental variable " + dbPasswordEnvVariable);
            return false;
        }
        if (System.getenv(dbNameEnvVariable) == null)
        {
            System.err.println("Missing database name environmental variable " + dbNameEnvVariable);
            return false;
        }
        if (System.getenv(dbSchemaEnvVariable) == null)
        {
            System.err.println("Missing database schema environmental variable " + dbSchemaEnvVariable);
            return false;
        }
        return true;
    }

    public static String getEnvOrDockerSecret(String env, String dockerSecretFile)
    {
        String envValue = System.getenv(env);

        if (envValue == null)
        {
            return "";
        }

        if (dockerSecretFile == null)
        {
            return envValue;
        }

        if (!dockerSecretFile.startsWith("/run"))
        {
            dockerSecretFile = "/run/secrets/" + dockerSecretFile;
        }

        if (envValue.equals(dockerSecretFile))
        {
            try
            {
                // Secrets may contain newlines if not created with "echo -n"
                return new String(Files.readAllBytes(Paths.get(dockerSecretFile))).trim();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return "";
            }
        }
        return envValue;
    }
}
