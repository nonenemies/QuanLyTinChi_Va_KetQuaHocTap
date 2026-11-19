package DAO;

import Util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

/** Shared database access helpers for all DAO classes. */
public abstract class BaseDAO {
    protected static Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
}
