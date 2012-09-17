package org.alternadev.whatsup;

public class InvalidTokenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 42L * 63L;


	public InvalidTokenException(int token) {
		super("The token "+token+" is invalid!");
	}
}
