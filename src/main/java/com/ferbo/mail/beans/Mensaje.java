package com.ferbo.mail.beans;

import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import java.util.Collections;

public class Mensaje {
	private Integer id = null;
	private List<Correo> toList;
	private List<Correo> ccList;
	private List<Correo> bccList;
	private String subject;
	private String body;
	private List<Adjunto> attachments;

	@Generated("SparkTools")
	private Mensaje(Builder builder) {
		this.id = builder.id;
		this.toList = builder.toList;
		this.ccList = builder.ccList;
		this.bccList = builder.bccList;
		this.subject = builder.subject;
		this.body = builder.body;
		this.attachments = builder.attachments;
	}
	
	@Override
	public int hashCode() {
		if(this.id == null)
			return System.identityHashCode(this);
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mensaje other = (Mensaje) obj;
		
		if(this.id == null || other.id == null)
			return Objects.equals(System.identityHashCode(this), System.identityHashCode(other));
		
		return Objects.equals(id, other.id);
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<Correo> getToList() {
		return toList;
	}
	public void setToList(List<Correo> toList) {
		this.toList = toList;
	}
	public List<Correo> getCcList() {
		return ccList;
	}
	public void setCcList(List<Correo> ccList) {
		this.ccList = ccList;
	}
	public List<Correo> getBccList() {
		return bccList;
	}
	public void setBccList(List<Correo> bccList) {
		this.bccList = bccList;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public List<Adjunto> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<Adjunto> attachments) {
		this.attachments = attachments;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		private Integer id = null;
		private List<Correo> toList = Collections.emptyList();
		private List<Correo> ccList = Collections.emptyList();
		private List<Correo> bccList = Collections.emptyList();
		private String subject;
		private String body;
		private List<Adjunto> attachments = Collections.emptyList();

		private Builder() {
		}

		public Builder withId(Integer id) {
			this.id = id;
			return this;
		}

		public Builder withToList(List<Correo> toList) {
			this.toList = toList;
			return this;
		}

		public Builder withCcList(List<Correo> ccList) {
			this.ccList = ccList;
			return this;
		}

		public Builder withBccList(List<Correo> bccList) {
			this.bccList = bccList;
			return this;
		}

		public Builder withSubject(String subject) {
			this.subject = subject;
			return this;
		}

		public Builder withBody(String body) {
			this.body = body;
			return this;
		}

		public Builder withAttachments(List<Adjunto> attachments) {
			this.attachments = attachments;
			return this;
		}

		public Mensaje build() {
			return new Mensaje(this);
		}
	}
}
