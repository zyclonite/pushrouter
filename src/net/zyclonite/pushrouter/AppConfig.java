/*
 * PushRouter
 *
 * Copyright 2011   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.pushrouter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author lprettenthaler
 */
public final class AppConfig extends XMLConfiguration {

    private static final Log LOG = LogFactory.getLog(AppConfig.class);
    private static AppConfig instance;
    private static final String configFile = "config.xml";
    

    static {
        instance = new AppConfig(configFile);
    }

    private AppConfig(final String fileName) {
        super();
        this.setReloadingStrategy(new FileChangedReloadingStrategy());
        init(fileName);
    }

    private void init(final String fileName) {
        setFileName(fileName);
        try {
            load();
            AppConfig.LOG.info("Configuration reloaded");
        } catch (ConfigurationException e) {
            AppConfig.LOG.error("Configuration not loaded!");
        }
    }

    public static AppConfig getInstance() {
        return instance;
    }
}
