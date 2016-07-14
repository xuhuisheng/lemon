package com.mossle.core.dbmigrate;

import java.util.Map;

import javax.annotation.PostConstruct;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DatabaseMigrator implements ApplicationContextAware {
    private static Logger logger = LoggerFactory
            .getLogger(DatabaseMigrator.class);
    private ApplicationContext applicationContext;
    private boolean enabled;
    private boolean clean;
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        if (!enabled) {
            logger.info("skip dbmigrate");

            return;
        }

        if (clean) {
            logger.info("clean database");

            Flyway flyway = new Flyway();
            flyway.setDataSource(dataSource);
            flyway.clean();
        }

        Map<String, ModuleSpecification> map = applicationContext
                .getBeansOfType(ModuleSpecification.class);

        for (ModuleSpecification moduleSpecification : map.values()) {
            if (!moduleSpecification.isEnabled()) {
                logger.info("skip migrate : {}, {}",
                        moduleSpecification.getSchemaTable(),
                        moduleSpecification.getSchemaLocation());

                continue;
            }

            this.doMigrate(moduleSpecification.getSchemaTable(),
                    moduleSpecification.getSchemaLocation());

            if (moduleSpecification.isInitData()) {
                this.doMigrate(moduleSpecification.getDataTable(),
                        moduleSpecification.getDataLocation());
            }
        }
    }

    public void doMigrate(String table, String location) {
        logger.info("migrate : {}, {}", table, location);

        Flyway flyway = new Flyway();
        flyway.setPlaceholderPrefix("$${");
        // flyway.setInitOnMigrate(true);
        flyway.setBaselineOnMigrate(true);
        // flyway.setInitVersion("0");
        flyway.setBaselineVersionAsString("0");
        flyway.setDataSource(dataSource);
        flyway.setTable(table);
        flyway.setLocations(new String[] { location });

        try {
            flyway.repair();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        flyway.migrate();
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
