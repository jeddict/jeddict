/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.jcode;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.db.explorer.DatabaseConnection;

/**
 *
 * @author jGauravGupta
 */
public enum DatabaseType {
    DERBY("Derby", "derby", "1527", true,
            new DatabaseDriver("org.apache.derby", "derby", "10.13.1.1", "org.apache.derby.jdbc.ClientDriver"),
            Arrays.asList("--"), false),
    H2("H2", "h2", "test", true,
            new DatabaseDriver("com.h2database", "h2", "1.4.193", "org.h2.Driver"),
            Arrays.asList("--"), false),
    MYSQL("MySQL", "mysql", "3306", false,
            new DatabaseDriver("mysql", "mysql-connector-java", "5.1.38", "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource"),
            Arrays.asList("latest", "5.5", "5.6", "5.7", "8.0"), true),
    MARIADB("MariaDB", "mariadb", "3306", false,
            new DatabaseDriver("org.mariadb.jdbc", "mariadb-java-client", "1.5.8", "org.mariadb.jdbc.MariaDbDataSource"),
            Arrays.asList("latest", "10.1", "10.0", "5.5"), true),
    POSTGRESQL("PostgreSQL", "postgres", "5432", false,
            new DatabaseDriver("postgresql", "postgresql", "9.1-901.jdbc4", "org.postgresql.xa.PGXADataSource"),
            Arrays.asList("latest", "9.6", "9.5", "9.4", "9.3", "9.2"), true);

    private final String displayName;
    private final String dockerImage;
    private final String defaultPort;
    private final DatabaseDriver driver;
    private final boolean embeddedDB;
    private final List<String> version;
    private final boolean dockerSupport;

    private DatabaseType(String displayName, String dockerImage,
            String defaultPort, boolean embeddedDB, DatabaseDriver driver,
            List<String> version, boolean dockerSupport) {
        this.displayName = displayName;
        this.dockerImage = dockerImage;
        this.defaultPort = defaultPort;
        this.driver = driver;
        this.version = version;
        this.embeddedDB = embeddedDB;
        this.dockerSupport = dockerSupport;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getVersion() {
        return version;
    }

    public boolean isDockerSupport() {
        return dockerSupport;
    }

    /**
     * @return the defaultPort
     */
    public String getDefaultPort() {
        return defaultPort;
    }

    /**
     * @return the driver
     */
    public DatabaseDriver getDriver() {
        return driver;
    }
    
    public boolean isMatchingDatabase(DatabaseConnection databaseConnection){
        if (databaseConnection != null) {
            if (StringUtils.containsIgnoreCase(databaseConnection.getDriverClass(), this.name())) {
                return true;
            }
            if (StringUtils.containsIgnoreCase(databaseConnection.getDatabaseURL(), this.name())) {
                return true;
            }
        }
        return false;
        
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * @return the embeddedDB
     */
    public boolean isEmbeddedDB() {
        return embeddedDB;
    }

    /**
     * @return the dockerImage
     */
    public String getDockerImage() {
        return dockerImage;
    }
}
