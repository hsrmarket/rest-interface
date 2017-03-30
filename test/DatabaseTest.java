import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;

import java.sql.Connection;

import static org.junit.Assert.assertTrue;

/**
 * Created by Elias on 29.03.17.
 */
public class DatabaseTest {

    Database database;

    @Before
    public void createDatabase() {
        database = Databases.createFrom(
                "org.postgresql.Driver",
                "jdbc:postgresql://duernten.forrer.network/postgres",
                ImmutableMap.of(
                        "username", "postgres",
                        "password", "Pa$$w0rd"
                )
        );
    }

    @After
    public void shutdownDatabase() {
        database.shutdown();
    }

    @Test
    public void testDatabase() throws Exception {
        Connection connection = database.getConnection();
        connection.prepareStatement("insert into MOCK_DATA (ID, IBAN, Author) values (999, 'RO03 CESC YXE8 AMVJ LE1I T600', 'Victor Garrett')").execute();

        assertTrue(
                connection.prepareStatement("select * from mock_data where id = 999").executeQuery().next()
        );

        connection.prepareStatement("delete from mock_data where id = 999").execute();

    }

}
