package jp.jyn.jbukkitlib.sql;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * {@link SQLException} runtime wrapper
 */
public class RuntimeSQLException extends RuntimeException implements Iterable<Throwable> {
    private final SQLException cause;

    public RuntimeSQLException(SQLException cause) {
        super(cause);
        this.cause = cause;
    }

    public RuntimeSQLException(String message, SQLException cause) {
        super(message, cause);
        this.cause = cause;
    }

    /**
     * {@link SQLException#getSQLState()}
     */
    public String getSQLState() {
        return cause.getSQLState();
    }

    /**
     * {@link SQLException#getErrorCode()}
     */
    public int getErrorCode() {
        return cause.getErrorCode();
    }

    /**
     * {@link SQLException#getNextException()}
     */
    public SQLException getNextException() {
        return cause.getNextException();
    }

    /**
     * {@link SQLException#iterator()}
     */
    @Override
    public Iterator<Throwable> iterator() {
        return cause.iterator();
    }
}
