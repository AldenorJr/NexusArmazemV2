package br.com.nexus.armazem.main.model;

import br.com.nexus.armazem.main.Main;

import java.util.ArrayList;

public class PlotModel {

    public PlotModel(String ID, ArmazemModel armazemModel, String limiteKey, Main main) {
        this.ID = ID;
        this.armazemModel = armazemModel;
        this.limiteKey = limiteKey;
        this.main = main;
    }

    private final Main main;
    private String ID;
    private ArmazemModel armazemModel;
    private String limiteKey;

    public void addItem(String ID, int amount) {
        if(armazemModel.getItensSize() < getLimite()) armazemModel.addItem(ID, amount);
    }

    public int getLimite() {
        return main.getConfig().getInt("Limite-Configuration."+limiteKey+".limite");
    }

    public int getNextLimite() {
        return main.getConfig().getInt("Limite-Configuration."+getNextLimiteKey()+".limite");
    }

    public String getNextLimiteKey() {
        return main.getConfig().getString("Limite-Configuration." + limiteKey + ".next");
    }

    public boolean hasNextLimite() {
        return !(getNextLimiteKey().equalsIgnoreCase("finish"));
    }

    public int getUpgradePrice() {
        return main.getConfig().getInt("Limite-Configuration."+limiteKey+".price");
    }

    public void setUpgradeLimite() {
        if(hasNextLimite()) limiteKey = getNextLimiteKey();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public ArmazemModel getArmazemModel() {
        return armazemModel;
    }

    public void setArmazemModel(ArmazemModel armazemModel) {
        this.armazemModel = armazemModel;
    }

    public String getLimiteKey() {
        return limiteKey;
    }

    public void setLimiteKey(String limiteKey) {
        this.limiteKey = limiteKey;
    }
}
