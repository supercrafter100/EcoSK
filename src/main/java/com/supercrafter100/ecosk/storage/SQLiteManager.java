package com.supercrafter100.ecosk.storage;

import com.supercrafter100.ecosk.EcoSK;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteManager {

    private final String storageFile;
    private Connection conn;

    public SQLiteManager(String storageFile) {
        this.storageFile = storageFile;
        this.Connect();
    }

    private void Connect() {
        try {
            String url = "jdbc:sqlite:" + this.storageFile;
            this.conn = DriverManager.getConnection(url);
            EcoSK.getInstance().getLogger().info("Database connection established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        try {
            this.conn.close();
        } catch (SQLException ignored) { }
    }

    public Connection getConnection() {
        try {
            if (this.conn == null || this.conn.isClosed()) {
                this.Connect();
            }
        } catch (Exception e) {
            this.Connect();
            e.printStackTrace();
        }
        return this.conn;
    }
}
