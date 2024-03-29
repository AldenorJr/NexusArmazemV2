package br.com.nexus.armazem.main.listener;

import br.com.nexus.armazem.main.Main;
import br.com.nexus.armazem.main.cache.BonusCache;
import br.com.nexus.armazem.main.cache.PlayerCacheRecall;
import br.com.nexus.armazem.main.model.ArmazemModel;
import br.com.nexus.armazem.main.model.BonusVIPModel;
import br.com.nexus.armazem.main.model.PlotModel;
import br.com.nexus.armazem.main.storage.database.DatabaseMethod;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class InventoryArmazemListener implements Listener {

    private final DatabaseMethod databaseMethod;
    private final Main main;

    @EventHandler
    @SneakyThrows
    public void onClickInventory(InventoryClickEvent e) {
        ItemStack itemStack = e.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;
        Inventory inventory = e.getInventory();
        if (!inventory.getName().equalsIgnoreCase("§8Armazem")) return;
        HumanEntity whoClicked = e.getWhoClicked();

        e.setCancelled(true);
        if (!(whoClicked instanceof Player)) return;
        Player player = (Player) whoClicked;
        PlotAPI plotAPI = new PlotAPI();
        Location location = player.getLocation();
        Plot plot = plotAPI.getPlot(location);
        if (plot == null) return;
        PlotModel plotModel = databaseMethod.getPlotModelByID(plot.getId().toString());

        if (plotModel.getArmazemModel().hasItem(itemStack.getTypeId() + "")) {
            ArmazemModel armazemModel = plotModel.getArmazemModel();
            if (e.getClick().isRightClick()) {
                int amountItem = armazemModel.getAmountItemByID(itemStack.getTypeId() + "");
                if (amountItem <= 0) {
                    player.sendMessage("§c§lERROR: §cVocê não tem itens para retirar.");
                    player.closeInventory();
                    return;
                }
                if (!armazemModel.getItemArmazemModel(itemStack.getTypeId() + "").isRecall()) {
                    Set<String> keys = main.getConfig().getConfigurationSection("Itens-armazem").getKeys(false);
                    if (keys.isEmpty()) {
                        player.sendMessage("§c§lERROR: §cContacte um administrador.");
                        return;
                    }
                    if (!armazemModel.getItemArmazemModel(itemStack.getTypeId() + "").isRecall()) {
                        for (String k : keys) {
                            if (main.getConfig().getInt("Itens-armazem." + k + ".ID") == itemStack.getTypeId()) {
                                if (!main.getConfig().getBoolean("Itens-armazem." + k + ".recolher")) {
                                    player.sendMessage("§c§lERROR: §cVocê não pode recolher esse item.");
                                    player.closeInventory();
                                    return;
                                }
                                armazemModel.getItemArmazemModel(itemStack.getTypeId() + "").setRecall(true);
                                break;
                            }
                        }
                    }
                }
                player.sendMessage(new String[]{"", " §a§lRESPONDA: §aDigite a quantidade que deseja recolher. §aDigite §c'cancelar' §apara cancelar.", ""});
                if(!PlayerCacheRecall.playerArrayList.containsKey(player)) PlayerCacheRecall.playerArrayList.put(player, itemStack.getTypeId()+"");
            } else {
                int amountItem = armazemModel.getAmountItemByID(itemStack.getTypeId() + "");
                if (amountItem <= 0) {
                    player.sendMessage("§c§lERROR: §cVocê não tem itens para vender.");
                } else {
                    DecimalFormat df = new DecimalFormat("#,###,###,##0.##");
                    double money = amountItem * armazemModel.getPriceItemByID(itemStack.getTypeId() + "");
                    money = money * +getBonus(player);
                    Main.economy.depositPlayer(player, money);

                    player.sendMessage("");
                    if (amountItem == 1)
                        player.sendMessage("§a§lVENDIDO: §aVocê vendeu: §7" + amountItem + " item §apor: §f" + df.format(money) + "§a coins.");
                    else
                        player.sendMessage("§a§l VENDIDO: §aVocê vendeu: §f" + amountItem + " itens §apor: §f" + df.format(money) + "§a coins.");
                    player.sendMessage("");

                    armazemModel.setAmountItemByID(itemStack.getTypeId() + "", 0);
                    plotModel.setArmazemModel(armazemModel);
                    databaseMethod.saveArmazemModel(plotModel);
                }
            }
        } else {
            if (plotModel.getUpgradePrice() > Main.economy.getBalance(player)) {
                player.sendMessage("§cVocê não tem money suficiente para upar o limite.");
                player.closeInventory();
                return;
            }
            if (!plotModel.hasNextLimite()) {
                player.sendMessage("§cSeu armazém já alcançou o nível máximo.");
                player.closeInventory();
                return;
            }
            Main.economy.withdrawPlayer(player, plotModel.getUpgradePrice());
            for (UUID uuid : plot.getOwners()) {
                Player owner = Bukkit.getPlayer(uuid);
                owner.sendMessage("");
                owner.sendMessage("§6§l UPGRADE: §aSeu armazém tem um novo limite de: §a§l" + plotModel.getNextLimite() + " §aitens.");
                owner.sendMessage("");
            }
            plotModel.setUpgradeLimite();
            databaseMethod.saveLimite(plotModel);
        }
        player.closeInventory();
    }

    public double getBonus(Player player) {
        for (BonusVIPModel bonusVIPModel : BonusCache.bonusVIPModelArrayList) {
            if (player.hasPermission(bonusVIPModel.getPermission())) {
                return bonusVIPModel.getBonus();
            }
        }
        return 1.0;
    }

}
