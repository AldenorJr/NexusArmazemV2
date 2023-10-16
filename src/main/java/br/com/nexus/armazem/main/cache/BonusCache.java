package br.com.nexus.armazem.main.cache;

import br.com.nexus.armazem.main.Main;
import br.com.nexus.armazem.main.model.BonusVIPModel;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BonusCache {

    public static ArrayList<BonusVIPModel> bonusVIPModelArrayList = new ArrayList<>();

    private final Main main;

    public BonusCache(Main main) {
        this.main = main;
    }

    public void loadCacheBonus() {
        main.getConfig().getConfigurationSection("BonusVIP").getKeys(false).forEach(k -> {
            String permission = main.getConfig().getString("BonusVIP."+k+".permission");
            double bonus = main.getConfig().getDouble("BonusVIP."+k+".bonus");
            bonusVIPModelArrayList.add(new BonusVIPModel(k, permission, bonus));
        });
    }

}
