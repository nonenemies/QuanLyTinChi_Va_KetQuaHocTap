package Test;

import org.junit.After;
import org.junit.Before;

abstract class DatabaseTestBase {
    protected DatabaseFixture db;

    @Before public void setUpDatabaseFixture() throws Exception {
        db = new DatabaseFixture();
    }

    @After public void cleanDatabaseFixture() throws Exception {
        if (db != null) db.close();
    }
}
