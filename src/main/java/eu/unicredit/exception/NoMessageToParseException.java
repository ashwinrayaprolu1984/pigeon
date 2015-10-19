package eu.unicredit.exception;

public class NoMessageToParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4211003281297411105L;

	public NoMessageToParseException() {
		super();
	}

	public NoMessageToParseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoMessageToParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoMessageToParseException(String message) {
		super(message);
	}

	public NoMessageToParseException(Throwable cause) {
		super(cause);
	}

}
