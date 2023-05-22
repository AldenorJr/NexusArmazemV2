package br.com.nexus.armazem.main.listener;

import br.com.nexus.armazem.main.model.PlotModel;
import br.com.nexus.armazem.main.storage.database.DatabaseMethod;
import br.com.nexus.armazem.main.util.ActionBarAPI;
import br.com.nexus.armazem.main.util.TitleAPI;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@RequiredArgsConstructor
public class SpawnItemListener implements Listener {

    private final DatabaseMethod databaseMethod;

    @EventHandler @SneakyThrows
    public void toSpawnItem(ItemSpawnEvent e) {
        if(verifyIsPlot(e.getLocation())) return;
        Plot plot = new PlotAPI().getPlot(e.getLocation());
        PlotModel plotModel = databaseMethod.getPlotModelByID(plot.getId().toString());
        ItemStack itemStack = e.getEntity().getItemStack();
        if(plotModel.getArmazemModel().hasItem(itemStack.getTypeId()+"")) {
            int limite = plotModel.getLimite();
            int itensSize = plotModel.getArmazemModel().getItensSize();
            int amount = itemStack.getAmount();
            if(limite >= (itensSize+amount)) {
                plotModel.getArmazemModel().addItem(itemStack.getTypeId() + "", amount);
                databaseMethod.saveArmazemModel(plotModel);
                e.setCancelled(true);
                return;
            }
            for (UUID uuid : plot.getOwners()) {
                Player player = Bukkit.getPlayer(uuid);
                ActionBarAPI.send(player, "§cArmazém lotado!");
            }
        }
    }

    private boolean verifyIsPlot(Location l) {
        return new PlotAPI().getPlot(l) == null;
    }


}
