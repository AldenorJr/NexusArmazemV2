package br.com.nexus.armazem.main.storage.database;

import br.com.nexus.armazem.main.Main;
import br.com.nexus.armazem.main.model.ArmazemModel;
import br.com.nexus.armazem.main.model.ItemArmazemModel;
import br.com.nexus.armazem.main.model.PlotModel;
import br.com.nexus.armazem.main.storage.HikariConnect;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellectualcrafters.plot.object.Plot;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@RequiredArgsConstructor
public class DatabaseMethod {

    private final HikariConnect hikariConnect;
    private final Main main;

    public void createTable() throws SQLException {
        try(Connection connection = hikariConnect.hikariDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                    "`NexusArmazem`(`Plot_ID` VARCHAR(24), `Armazem` LONGTEXT, `limite` LONGTEXT);")) {
            preparedStatement.executeUpdate();
        }
    }

    public void setDefaultPlot(Plot plot) throws SQLException {
        try (Connection connection = hikariConnect.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `NexusArmazem`(`Plot_ID`,`Armazem`, `Limite`) VALUES (?, ?, ?);")) {

            preparedStatement.setString(1, plot.getId().toString());
            preparedStatement.setString(2, getJsonByArmazemModel(createArmazemDefault()));
            preparedStatement.setString(3, "default");
            preparedStatement.executeUpdate();
        }
    }

    public void saveArmazemModel(PlotModel plotModel) throws SQLException {
        try (Connection connection = hikariConnect.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `NexusArmazem` SET `Armazem` = ? WHERE `Plot_ID` = ?;")) {
            preparedStatement.setString(1, getJsonByArmazemModel(plotModel.getArmazemModel()));
            preparedStatement.setString(2, plotModel.getID());
            preparedStatement.executeUpdate();
        }
    }

    public void saveLimite(PlotModel plotModel) throws SQLException {
        try (Connection connection = hikariConnect.hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `NexusArmazem` SET `Limite` = ? WHERE `Plot_ID` = ?;")) {
            preparedStatement.setString(1, plotModel.getLimiteKey());
            preparedStatement.setString(2, plotModel.getID());
            preparedStatement.executeUpdate();
        }
    }

    public PlotModel getPlotModelByID(String ID) throws SQLException {
        ResultSet rs = null;
        try(Connection connection = hikariConnect.hikariDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `NexusArmazem` WHERE `Plot_ID` = ?;")) {
            preparedStatement.setString(1, ID);

            rs = preparedStatement.executeQuery();
            rs.next();
            return createPlotModel(ID, getArmazemModelByJson(rs.getString("Armazem")), rs.getString("Limite"));
        } finally {
            if(rs != null) rs.close();
        }
    }

    public Boolean hasPlot(String ID) throws SQLException {
        ResultSet rs = null;
        try(Connection connection = hikariConnect.hikariDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `NexusArmazem` WHERE `Plot_ID` = ?;")) {
            preparedStatement.setString(1, ID);

            rs = preparedStatement.executeQuery();
            return rs.next();
        } finally {
            if(rs != null) rs.close();
        }
    }

    public void updateNivelLimite(Plot plot, String limite) throws SQLException {
        try(Connection connection = hikariConnect.hikariDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `NexusArmazem` SET `limite` = ? WHERE `Plot_ID` = ?;")) {

            preparedStatement.setString(1, limite);
            preparedStatement.setString(2, plot.getId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public void deletePlot(Plot plot) throws SQLException {
        try(Connection connection = hikariConnect.hikariDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `NexusArmazem` WHERE `Plot_ID` = ?;")) {

            preparedStatement.setString(1, plot.getId().toString());
            preparedStatement.executeUpdate();
        }
    }

    private PlotModel createPlotModel(String ID, ArmazemModel armazemModel, String limite) {
        return new PlotModel(ID, armazemModel, limite, main);
    }

    private ArmazemModel createArmazemDefault() {
        ArrayList<ItemArmazemModel> itensArmazem = new ArrayList<>();
        for(String key : main.getConfig().getConfigurationSection("Itens-armazem").getKeys(false)) {
            String name = main.getConfig().getString("Itens-armazem."+key+".name");
            String ID = main.getConfig().getString("Itens-armazem."+key+".ID");
            int price = main.getConfig().getInt("Itens-armazem."+key+".price");
            itensArmazem.add(new ItemArmazemModel(name, ID, 0, price));
        }
        return new ArmazemModel(itensArmazem);
    }

    private String getJsonByArmazemModel(ArmazemModel armazemModel) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(armazemModel, ArmazemModel.class);
    }

    private ArmazemModel getArmazemModelByJson(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, ArmazemModel.class);
    }

}
