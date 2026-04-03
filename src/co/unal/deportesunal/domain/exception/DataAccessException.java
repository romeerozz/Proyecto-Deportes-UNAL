package co.unal.deportesunal.domain.exception;

import java.io.IOException;

public class DataAccessException extends RuntimeException{
    public DataAccessException(String s, Exception e) {
    }

    public DataAccessException(String s) {
    }

    public DataAccessException(String s, Throwable cause) {
    }
}
