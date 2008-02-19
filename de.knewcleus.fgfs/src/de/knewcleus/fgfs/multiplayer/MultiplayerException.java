package de.knewcleus.fgfs.multiplayer;

public class MultiplayerException extends Exception {
	private static final long serialVersionUID = -176516059092691388L;

	public MultiplayerException() {
	}

	public MultiplayerException(String message) {
		super(message);
	}

	public MultiplayerException(Throwable cause) {
		super(cause);
	}

	public MultiplayerException(String message, Throwable cause) {
		super(message, cause);
	}
}
