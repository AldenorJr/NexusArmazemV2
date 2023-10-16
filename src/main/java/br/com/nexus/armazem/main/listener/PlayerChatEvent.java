package br.com.nexus.armazem.main.listener;

import br.com.nexus.armazem.main.cache.PlayerCacheRecall;
import br.com.nexus.armazem.main.model.ArmazemModel;
import br.com.nexus.armazem.main.model.ItemArmazemModel;
import br.com.nexus.armazem.main.model.PlotModel;
import br.com.nexus.armazem.main.storage.database.DatabaseMethod;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerChatEvent implements Listener {

    private final DatabaseMethod databaseMethod;

    public PlayerChatEvent(DatabaseMethod databaseMethod) {
        this.databaseMethod = databaseMethod;
    }

    @EventHandler @SneakyThrows
    public void onSendChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if(PlayerCacheRecall.playerArrayList.containsKey(player)) {
            String type = PlayerCacheRecall.playerArrayList.get(player);
            if(e.getMessage().equalsIgnoreCase("cancelar")) {
                player.sendMessage("§cVocê cancelou a operação.");
                PlayerCacheRecall.playerArrayList.remove(player);
                e.setCancelled(true);
                return;
            }
            int value;
            try {
                value = Integer.parseInt(e.getMessage());
            } catch (Exception ignored) {
                player.sendMessage("§4§lERROR: §cPor favor digite um valor valido.");
                e.setCancelled(true);
                return;
            }
            if(value > contarSlotsVazios(player)) {
                player.sendMessage("§4§lERROR: §cVocê não tem espaço suficiente no inventario.");
                e.setCancelled(true);
                return;
            } else if(value <= 0) {
                player.sendMessage("§4§lERROR: §cPor favor digite um valor valido.");
                e.setCancelled(true);
                return;
            }
            PlotAPI plotAPI = new PlotAPI();
            Location loc = player.getLocation();
            if(plotAPI.getPlot(loc) == null) {
                player.sendMessage("§cVocê APENAS pode executar esse comando dentro de um plot.");
                return;
            }
            Plot plot = plotAPI.getPlot(loc);
            if(!plot.getOwners().contains(player.getUniqueId())) {
                if(!plot.getTrusted().contains(player.getUniqueId())) {
                    player.sendMessage("§cApenas donos ou pessoas com trust, podem usar esse comando.");
                    return;
                }
            }
            if(!databaseMethod.hasPlot(plot.getId().toString())) {
                databaseMethod.setDefaultPlot(plot);
            }
            PlotModel plotModel = databaseMethod.getPlotModelByID(plot.getId().toString());
            ArmazemModel armazemModel = plotModel.getArmazemModel();
            if(armazemModel.getAmountItemByID(type) < value) {
                player.sendMessage("§4§lERROR: §cVocê não tem essa quantidade de item no armazem.");
                e.setCancelled(true);
                return;
            }
            addSlotsInventory(player, type, value);
            armazemModel.setAmountItemByID(type, armazemModel.getAmountItemByID(type)-value);
            plotModel.setArmazemModel(armazemModel);
            databaseMethod.saveArmazemModel(plotModel);
            player.sendMessage(new String[]{"", " §a§lSUCESSO: §aVocê retirou §7" + value + " §aitem(s) do armazem.", "s"});
            PlayerCacheRecall.playerArrayList.remove(player);
            e.setCancelled(true);
        }

    }

    public void addSlotsInventory(Player player, String type, int quantidade) {
        PlayerInventory inventario = player.getInventory();
        int slotsVazios = 0;

        for (ItemStack item : inventario.getContents()) {
            if (item == null) {
                if(quantidade > 64) {
                    inventario.addItem(new ItemStack(Material.getMaterial(Integer.parseInt(type)), 64));
                    quantidade = quantidade - 64;
                } else {
                    inventario.addItem(new ItemStack(Material.getMaterial(Integer.parseInt(type)), quantidade));
                    return;
                }
            }
        }
    }

    public int contarSlotsVazios(Player jogador) {
        PlayerInventory inventario = jogador.getInventory();
        int slotsVazios = 0;

        for (ItemStack item : inventario.getContents()) {
            if (item == null) {
                slotsVazios++;
            }
        }

        return slotsVazios*64;
    }

}
