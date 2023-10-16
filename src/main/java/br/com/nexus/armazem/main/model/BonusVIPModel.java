package br.com.nexus.armazem.main.model;

import br.com.nexus.armazem.main.cache.BonusCache;
import org.bukkit.entity.Player;

public class BonusVIPModel {

    private String keyname;
    private String permission;
    private double bonus;

    public BonusVIPModel(String keyname, String permission, double bonus) {
        this.keyname = keyname;
        this.permission = permission;
        this.bonus = bonus;
    }

    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }
}
