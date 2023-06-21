package com.ferbo.tools;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SmtpAuthenticator extends Authenticator {
	
	private String _username = null;
	private String _password = null;
	
	public SmtpAuthenticator() {
		super();
	}
	
	public void setUserName(String name) {
		this._username = name;
	}
	
	public void setPassword(String passwd) {
		this._password = passwd;
	}
	
	public PasswordAuthentication getPasswordAuthentication(){
		String username = this._username;
		String password = this._password;
		
		PasswordAuthentication authentication = null;
		
		if(!"".equals(username) || !"".equals(password))
			authentication = new PasswordAuthentication(username, password);
		
		return authentication;
	}
}