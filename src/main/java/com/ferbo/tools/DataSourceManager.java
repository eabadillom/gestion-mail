package com.ferbo.tools;

import java.util.Properties;

import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataSourceManager {
	
	private static Logger log = LogManager.getLogger(DataSourceManager.class);
	
	public String getJndiName(String name){
        InitialContext initContext = null;
        String jndiName = null;
            
        try {
            initContext = new InitialContext();
            jndiName = (String) initContext.lookup(name);
        } catch(NamingException ex) {
            log.error(ex);
        } catch(Exception ex) {
            log.error(ex);
        }
        
        return jndiName;
    }
	
	public Properties getServerProperties(String jndiName) throws NamingException {
        Session     sesion = null;
        Object      obj = null;
        Context     ic = null;
        Context     env = null;
        
        ic = new InitialContext();
        env = (Context) ic.lookup("java:/comp/env");
        obj = env.lookup(jndiName);
        sesion = (Session) obj;
        
        log.debug("Obteniendo propiedades de configuracion JNDI...");
        Properties props = sesion.getProperties();
        return props;
    }

}
