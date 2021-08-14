/*
 * @(#)DBAccess.java  19/04/2010
 *
 * Copyright 2009 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.mina.util;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Connect and Access to databases to get messages
 *
 * @author: ThienTD
 * @since: 19/04/2010
 */
public class DatabaseConnection {

    private String driver;
    private String cnString;
    private String username;
    private String password;
    private Connection conn;
    private Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private boolean instanced = false;

    public void init(String dbFilePath) throws Exception {
        FileReader r = null;
        Properties prop = null;
        try {
            r = new FileReader(dbFilePath);
            prop = new Properties();
            prop.load(r);
            driver = prop.getProperty("driver");
            cnString = prop.getProperty("connection");
//            PassTranformer.setInputKey(AppConfig.VIETTELSECURITYKEY);
//            username = PassTranformer.decrypt(prop.getProperty("username"));
//            password = PassTranformer.decrypt(prop.getProperty("password"));
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            try {
                r.close();
                prop.clear();
            } catch (IOException ex) {
                logger.error(ex);
            }
        }

        if (instanced) {
            logger.info("the DB Connection has been instanced");
            return;
        }

        try {
            connect();
            instanced = true;
        } catch (ClassNotFoundException ex) {
            logger.error(String.format("Connect to DB [cnString : %s, username : %s] failed", cnString, username));
            logger.error(ex.getMessage(), ex);
            instanced = false;
            throw ex;
        } catch (SQLException ex) {
            logger.error(String.format("Connect to DB [cnString : %s, username : %s] failed", cnString, username));
            logger.error(ex.getMessage(), ex);
            instanced = false;
            throw ex;
        }
    }

    public void connect() throws ClassNotFoundException, SQLException {
        logger.info(String.format("Connecting to DB [cnString : %s, username : %s]", cnString, username));

        Class.forName(driver);
        conn = DriverManager.getConnection(cnString, username, password);

        logger.info(String.format("Connected to DB [cnString : %s, username : %s]", cnString, username));
        instanced = true;

    }

    public synchronized Connection getConnection() throws ClassNotFoundException, SQLException {
        if (conn != null && !conn.isClosed()) {
            return conn;
        } else {
            connect();
        }
        return conn;
    }

    public synchronized void closeConnection() throws ClassNotFoundException, SQLException {
        if (conn != null) {
            conn.close();
        }

    }

    public void validate() throws ClassNotFoundException, SQLException {
        if (conn != null || conn.isClosed()) {
            connect();
        }
    }

    public boolean isInstanced() {
        return instanced;
    }
}
