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
package io.github.jeddict.jpa.spec.extend.cache;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import org.netbeans.api.db.explorer.DatabaseConnection;

@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseConnectionCache {

    @XmlTransient
    public static final String DEFAULT_URL = "jdbc:h2:mem:JPA_MODELER_EDB;DB_CLOSE_DELAY=-1";
    @XmlTransient
    public static final String DEFAULT_DRIVER = "org.h2.Driver";

    @XmlAttribute(name = "u")
    private String url;
    @XmlAttribute(name = "n")
    private String userName;
    @XmlAttribute(name = "p")
    private String password;
    @XmlAttribute(name = "d")
    private String driverClassName;

    @XmlTransient
    private Class driverClass;
    @XmlTransient
    private DatabaseConnection databaseConnection;

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the driverClassName
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * @param driverClassName the driverClassName to set
     */
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    /**
     * @return the driverClass
     */
    public Class getDriverClass() {
        return driverClass;
    }

    /**
     * @param driverClass the driverClass to set
     */
    public void setDriverClass(Class driverClass) {
        this.driverClass = driverClass;
    }

    /**
     * @return the databaseConnection
     */
    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    /**
     * @param databaseConnection the databaseConnection to set
     */
    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

}
