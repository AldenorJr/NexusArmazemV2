package br.com.nexus.armazem.main.command;

import br.com.nexus.armazem.main.inventory.ArmazemInventory;
import br.com.nexus.armazem.main.model.PlotModel;
import br.com.nexus.armazem.main.storage.database.DatabaseMethod;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CommandArmazem implements CommandExecutor {

    private final ArmazemInventory armazemInventory;
    private final DatabaseMethod databaseMethod;

    @Override @SneakyThrows
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cApenas jogadores podem executar esse comando.");
            return true;
        }
        Player player = (Player) commandSender;
        PlotAPI plotAPI = new PlotAPI();
        Location loc = player.getLocation();
        if(plotAPI.getPlot(loc) == null) {
            player.sendMessage("§cVocê APENAS pode executar esse comando dentro de um plot.");
            return true;
        }
        Plot plot = plotAPI.getPlot(loc);
        if(!plot.getOwners().contains(player.getUniqueId())) {
            if(!plot.getTrusted().contains(player.getUniqueId())) {
                player.sendMessage("§cApenas donos ou pessoas com trust, podem usar esse comando.");
                return true;
            }
        }
        PlotModel plotModel = databaseMethod.getPlotModelByID(plot.getId().toString());
        armazemInventory.openInventory(player, plotModel);

        return false;
    }
}
