package org.alternadev.whatsup;

public class IncompleteMessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 42L * 63L;

	private int[] data;

	public IncompleteMessageException(String message, int[] input) {
		super(message);
		this.data = input;
	}

	public int[] getData() {
		return data;
	}

	public void setData(int[] data) {
		this.data = data;
	}
}
