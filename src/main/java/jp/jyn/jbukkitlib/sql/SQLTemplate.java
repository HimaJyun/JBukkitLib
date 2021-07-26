package jp.jyn.jbukkitlib.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract class to make common SQL easier to handle using Lambda.
 */
public abstract class SQLTemplate {
    private final ConnectionPool pool;

    protected SQLTemplate(ConnectionPool pool) {
        this.pool = pool;
    }

    /**
     * <p>try SELECT, if get nothing, execute INSERT and try SELECT again.</p>
     * <p>This is useful when issuing an auto-increment ID.</p>
     * <p>Note: This method uses a transaction. Change the auto-commit mode.</p>
     *
     * @param select          SELECT SQL
     * @param selectParameter SELECT {@link PreparedStatement} parameter
     * @param insert          INSERT SQL
     * @param insertParameter SELECT {@link PreparedStatement} parameter
     * @param mapper          The mapping function to apply to a {@link ResultSet}.
     *                        If the SELECT returns nothing, it must return null.
     * @param <T>             The type of the value returned from the mapping function.
     * @return The mapped object.
     * @throws RuntimeSQLException Wrapped {@link SQLException} or Nothing was inserted.
     */
    protected <T> T selectInsert(String select, PreparedParameter selectParameter,
                                 String insert, PreparedParameter insertParameter,
                                 ResultMapper<T> mapper) throws RuntimeSQLException {
        try (var connection = pool.getConnection()) {
            connection.setAutoCommit(false);
            try (var selectStatement = connection.prepareStatement(select)) {
                selectParameter.set(selectStatement);
                try (var result = selectStatement.executeQuery()) {
                    T obj = mapper.map(result);
                    if (obj != null) { // ok
                        return obj;
                    }
                }

                // insert
                try (var insertStatement = connection.prepareStatement(insert)) {
                    insertParameter.set(insertStatement);
                    insertStatement.executeUpdate();
                }

                // SELECT again.
                try (var result = selectStatement.executeQuery()) {
                    T obj = mapper.map(result);
                    if (obj != null) { // ok
                        return obj;
                    }
                }

                // failed
                connection.rollback();
                throw new RuntimeSQLException(new SQLException("Nothing was inserted."));
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeSQLException(e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        }
    }

    /**
     * <p>try SELECT, if get nothing, execute INSERT and try SELECT again.</p>
     * <p>This is useful when issuing an auto-increment ID.</p>
     * <p>Note: This method uses a transaction. Change the auto-commit mode.</p>
     *
     * @param select    SELECT SQL
     * @param insert    INSERT SQL
     * @param parameter SELECT and INSERT {@link PreparedStatement} parameter
     * @param mapper    The mapping function to apply to a {@link ResultSet}.
     *                  If the SELECT returns nothing, it must return null.
     * @param <T>       The type of the value returned from the mapping function.
     * @return The mapped object.
     * @throws RuntimeSQLException Wrapped {@link SQLException} or Nothing was inserted.
     */
    protected <T> T selectInsert(String select, String insert, PreparedParameter parameter,
                                 ResultMapper<T> mapper) throws RuntimeSQLException {
        return this.selectInsert(select, parameter, insert, parameter, mapper);
    }

    /**
     * <p>try UPDATE, if nothing has changed, execute INSERT.</p>
     * <p>Note: This method uses a transaction. Change the auto-commit mode.</p>
     *
     * @param update          UPDATE SQL
     * @param updateParameter UPDATE {@link PreparedStatement} parameter
     * @param insert          INSERT SQL
     * @param insertParameter INSERT {@link PreparedStatement} parameter
     * @return Row count
     * @throws RuntimeSQLException Wrapped {@link SQLException} or Nothing was updated.
     */
    protected int upsert(String update, PreparedParameter updateParameter,
                         String insert, PreparedParameter insertParameter) throws RuntimeSQLException {
        try (var connection = pool.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // update
                try (var statement = connection.prepareStatement(update)) {
                    updateParameter.set(statement);
                    int result = statement.executeUpdate();
                    if (result != 0) { // ok
                        return result;
                    }
                }

                // no update -> insert
                try (var statement = connection.prepareStatement(insert)) {
                    insertParameter.set(statement);
                    int result = statement.executeUpdate();
                    if (result != 0) { // ok
                        return result;
                    }
                }

                // failed
                connection.rollback();
                throw new RuntimeSQLException(new SQLException("Nothing was updated."));
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeSQLException(e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        }
    }

    /**
     * Execute {@link PreparedStatement#executeQuery()}.
     *
     * @param select    SELECT SQL
     * @param parameter SELECT {@link PreparedStatement} parameter
     * @param mapper    The mapping function to apply to a {@link ResultSet}.
     * @param <T>       The type of the value returned from the mapping function.
     * @return The mapped object.
     * @throws RuntimeSQLException Wrapped {@link SQLException}
     */
    protected <T> T select(String select, PreparedParameter parameter, ResultMapper<T> mapper) throws RuntimeSQLException {
        try (var connection = pool.getConnection();
             var statement = connection.prepareStatement(select)) {
            parameter.set(statement);
            try (var r = statement.executeQuery()) {
                return mapper.map(r);
            }
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        }
    }

    /**
     * Execute {@link PreparedStatement#executeUpdate()}.
     *
     * @param sql       SQL
     * @param parameter {@link PreparedStatement} parameter
     * @return Row count
     * @throws RuntimeSQLException Wrapped {@link SQLException}
     */
    protected int executeUpdate(String sql, PreparedParameter parameter) throws RuntimeSQLException {
        try (var connection = pool.getConnection();
             var statement = connection.prepareStatement(sql)) {
            parameter.set(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeSQLException(e);
        }
    }

    // region alias

    /**
     * Alias of {@link SQLTemplate#executeUpdate(String, PreparedParameter)}.
     * This method is used for readability.
     *
     * @param insert    INSERT SQL
     * @param parameter INSERT {@link PreparedStatement} parameter
     * @return Row count
     * @throws RuntimeSQLException Wrapped {@link SQLException}
     */
    protected int insert(String insert, PreparedParameter parameter) throws RuntimeSQLException {
        return executeUpdate(insert, parameter);
    }

    /**
     * Alias of {@link SQLTemplate#executeUpdate(String, PreparedParameter)}.
     * This method is used for readability.
     *
     * @param update    UPDATE SQL
     * @param parameter UPDATE {@link PreparedStatement} parameter
     * @return Row count
     * @throws RuntimeSQLException Wrapped {@link SQLException}
     */
    protected int update(String update, PreparedParameter parameter) throws RuntimeSQLException {
        return executeUpdate(update, parameter);
    }

    /**
     * Alias of {@link SQLTemplate#executeUpdate(String, PreparedParameter)}.
     * This method is used for readability.
     *
     * @param delete    DELETE SQL
     * @param parameter DELETE {@link PreparedStatement} parameter
     * @return Row count
     * @throws RuntimeSQLException Wrapped {@link SQLException}
     */
    protected int delete(String delete, PreparedParameter parameter) throws RuntimeSQLException {
        return executeUpdate(delete, parameter);
    }
    // endregion

    /**
     * <p>The @FunctionalInterface that provides the getConnection()</p>
     * <p>Note: getConnection() is called each time the query is executed, and then {@link Connection#close()}.</p>
     */
    @FunctionalInterface
    protected interface ConnectionPool {
        Connection getConnection() throws SQLException;
    }

    /**
     * {@link PreparedStatement} parameter
     */
    @FunctionalInterface
    protected interface PreparedParameter {
        void set(PreparedStatement statement) throws SQLException;
    }

    /**
     * Map a {@link ResultSet} to an arbitrary object.
     *
     * @param <T> Type
     */
    @FunctionalInterface
    protected interface ResultMapper<T> {
        T map(ResultSet result) throws SQLException;
    }
}
