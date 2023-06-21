package com.ferbo.tools;

public class MailException extends Exception {

	private static final long serialVersionUID = 8569730442124004854L;
	
	public MailException() {
		super();
	}
	
	public MailException(String message) {
		super(message);
	}
	
	public MailException(Throwable cause) {
		super(cause);
	}
	
	public MailException(String message, Throwable cause) {
		super(message, cause);
	}

}
