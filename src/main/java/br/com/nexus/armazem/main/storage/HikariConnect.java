package br.com.nexus.armazem.main.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariConnect {

    public HikariDataSource hikariDataSource;

    public void MySQLConnectLoad(String ipAddress, String database, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + ipAddress + "/" + database);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setUsername(username);
        config.setPassword(password);
        this.hikariDataSource = new HikariDataSource(config);
    }

    public void closeHikariDataSource() {
        if(hikariDataSource == null) return;
        if(!hikariDataSource.isRunning()) return;
        hikariDataSource.close();
    }

}
