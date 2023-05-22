package br.com.nexus.armazem.main.inventory;

import br.com.nexus.armazem.main.Main;
import br.com.nexus.armazem.main.model.PlotModel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ArmazemInventory {

    private final Main main;

    public void openInventory(Player player, PlotModel plotModel) {
        Inventory inventory = Bukkit.createInventory(null, 5*9, "§8Armazem");
        for(String k : main.getConfig().getConfigurationSection("InventoryConfiguration").getKeys(false)) {
            List<String> lore;
            if(k.equalsIgnoreCase("limite")) {
                if(!plotModel.hasNextLimite()) lore = main.getConfig().getStringList("InventoryConfiguration."+k+".lore-finish");
                else lore = main.getConfig().getStringList("InventoryConfiguration."+k+".lore-upgrade");
            } else lore = main.getConfig().getStringList("InventoryConfiguration."+k+".lore");
            String ID = main.getConfig().getString("Itens-armazem."+k+".ID");
            ItemStack itemStack = new ItemStack(Material.getMaterial(Integer.parseInt(ID)));
            ItemMeta itemMeta = itemStack.getItemMeta();
            String name = main.getConfig().getString("Itens-armazem."+k+".name");
            Double price = main.getConfig().getDouble("Itens-armazem."+k+".price");
            itemMeta.setDisplayName(name.replaceAll("&", "§"));
            ArrayList<String> loreMeta = new ArrayList<>();
            int quantidade = plotModel.getArmazemModel().getAmountItemByID(ID);
            double money = quantidade*price;
            DecimalFormat df = new DecimalFormat("#,###,###,##0.##");
            for(String line : lore) {
                String loreAdd = line.replaceAll("@quantidade", df.format(quantidade) + "")
                        .replaceAll("@money", df.format(money) + "");
                if(plotModel.hasNextLimite()) {
                    loreAdd = loreAdd
                            .replaceAll("@atual", df.format(plotModel.getLimite()))
                            .replaceAll("@futuro", df.format(plotModel.getNextLimite()))
                            .replaceAll("@dinheiro", df.format(plotModel.getUpgradePrice()))
                            .replaceAll("&", "§");
                } else {
                    loreAdd = loreAdd
                            .replaceAll("@atual", df.format(plotModel.getLimite()))
                            .replaceAll("&", "§");
                }
                loreMeta.add(loreAdd);
            }
            itemMeta.setLore(loreMeta);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(main.getConfig().getInt("InventoryConfiguration."+k+".slot"), itemStack);
        }
        player.openInventory(inventory);
    }



}

