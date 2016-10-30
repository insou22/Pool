package co.insou.pool.example;

import co.insou.pool.CredentialPackageFactory;
import co.insou.pool.Pool;
import co.insou.pool.PoolDriver;
import co.insou.pool.properties.PropertyFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PoolExample {

    // Pool variable, wraps the HikariDataSource, etc.
    private Pool pool;

    // Example onEnable method
    // Note this should not be in your onEnable, and should be in some sort of managing delegate wrapper like a PoolManager
    public void onEnable() {
        // Creates a new pool with username "username", password "password" and sets the driver to MySQL
        pool = new Pool(CredentialPackageFactory.get("username", "password"), PoolDriver.MYSQL);
        // Sets min and max connections to 5 (min and max should be the same unless you know what you're doing)
        // and adds a MySQL url on the hostname "insouciance.co" with database "spigot-database"
        // This defaults to port 3306, if you would like to change the port or are not using the MySQL driver
        // use pool.withUrl(String url) instead.
        pool.withMin(5).withMax(5).withMysqlUrl("insouciance.co", "spigot-database");
        // Adds a property to set the connection timeout to 30 seconds (30000 milliseconds)
        // Use pool.withProperties() to add more than one property at once
        // Use the PropertyFactory to generate HikariProperty instances easily, or alternatively invoke their constructors directly
        pool.withProperty(PropertyFactory.connectionTimeout(30000));
        // Always remember to build the pool once you have finished your configuration
        pool.build();
    }

    public void runSql() {
        // try-with-resources [RECOMMENDED]

        // Beginning of try block, include your Connection resource so it will automatically be closed afterwards
        try ( Connection connection = pool.getConnection() ) {
            // Prepare a statement from the connection
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS  `foo` (" +
                    "uuid VARCHAR(30) NOT NULL," +
                    "coins INT DEFAULT 0," +
                    ") PRIMARY KEY(uuid);");
            // Execute
            statement.execute();
        // All above statements can throw SQLExceptions, gotta catch 'em all
        } catch (SQLException e) {
            // In the case of an exception, log the error and current stack
            e.printStackTrace();
        }
        // Connection will be closed (returned to the pool in the case of Connection Pools) automatically (try-with-resources)



        // try-catch (without resources) [NOT RECOMMENDED]
        // Create a null connection variable to close in finally block
        Connection connection = null;
        try {
            // Assign connection variable to a connection from pool
            connection = pool.getConnection();
            // Prepare a statement using the connection
            PreparedStatement statement = connection.prepareStatement("SELECT coins FROM foo WHERE uuid=?;");
            // Inject the uuid to prevent SQLi (SQL Injection)
            statement.setString(1, UUID.randomUUID().toString());
            // Get the result of the statement
            ResultSet result = statement.executeQuery();
            // <iterate over result, etc.>

        // All above statements can throw SQLExceptions, gotta catch 'em all
        } catch (SQLException e) {
            // In the case of an exception, log the error and current stack
            e.printStackTrace();
        // Finally block to close the connection manually after try-catch
        } finally {
            // Assert the connection's null status to prevent a possible NullPointerException
            if (connection != null) {
                try {
                    // Close (return to the pool) the connection
                    connection.close();
                // Can throw potential SQLException, must be caught
                } catch (SQLException e) {
                    // In the case of an exception, log the error and current stack
                    e.printStackTrace();
                }
            }
        }
    }

}
