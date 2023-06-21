package com.ferbo.mail.beans;

import java.util.Arrays;

public class Adjunto {
	public static final String TP_ARCHIVO_PDF = "application/pdf";
	public static final String TP_ARCHIVO_XML = "application/xml";
	public static final String TP_ARCHIVO_XLS = "application/vnd.ms-excel";
	public static final String TP_ARCHIVO_ZIP = "application/zip";
	String nombreArchivo = null;
	String tipoArchivo = null;
	byte[] contenido = null;
	
	
	
	public Adjunto(String nombreArchivo, String tipoArchivo, byte[] contenido) {
		super();
		this.nombreArchivo = nombreArchivo;
		this.tipoArchivo = tipoArchivo;
		this.contenido = contenido;
	}
	public String getNombreArchivo() {
		return nombreArchivo;
	}
	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}
	public String getTipoArchivo() {
		return tipoArchivo;
	}
	public void setTipoArchivo(String tipoArchivo) {
		this.tipoArchivo = tipoArchivo;
	}
	public byte[] getContenido() {
		return contenido;
	}
	public void setContenido(byte[] contenido) {
		this.contenido = contenido;
	}
    @Override
    public String toString() {
        return "{\"nombreArchivo\":\"" + nombreArchivo + "\", \"tipoArchivo\":\"" + tipoArchivo + "\", \"contenido\":\""
                + Arrays.toString(contenido) + "\"}";
    }
}
