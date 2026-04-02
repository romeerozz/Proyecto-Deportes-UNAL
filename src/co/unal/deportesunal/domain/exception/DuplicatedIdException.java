package co.unal.deportesunal.domain.exception;

public class DuplicatedIdException extends RuntimeException {
	public DuplicatedIdException(String message) {
		super(message);
	}
}
