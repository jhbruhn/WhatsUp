package org.alternadev.whatsup;

public class IncompleteMessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 42L * 63L;

	private String data;

	public IncompleteMessageException(String message, String data) {
		super(message);
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
