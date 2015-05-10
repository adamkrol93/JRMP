package net.amg.jira.plugins.exceptions;

/**
 * Wyjątek rzucany, jeżeli Query nie zwraca poprawnych wartości.
 */
public class NoIssuesFoundException extends Exception{

    public NoIssuesFoundException() {
        super();
    }

    public NoIssuesFoundException(String message) {
        super(message);
    }

    public NoIssuesFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
