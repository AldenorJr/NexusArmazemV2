package br.com.nexus.armazem.main.listener;

import br.com.nexus.armazem.main.Main;
import br.com.nexus.armazem.main.storage.database.DatabaseMethod;
import com.intellectualcrafters.plot.object.Plot;
import com.plotsquared.bukkit.events.PlayerClaimPlotEvent;
import com.plotsquared.bukkit.events.PlotDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;

@RequiredArgsConstructor
public class PlotListener implements Listener {

    private final DatabaseMethod databaseMethod;
    private final Main main;

    @EventHandler
    void toCreatePlot(PlayerClaimPlotEvent e) throws SQLException {
        if(!databaseMethod.hasPlot(e.getPlot().getId().toString())) {
            databaseMethod.setDefaultPlot(e.getPlot());
            Bukkit.getConsoleSender().sendMessage(main.prefix+"§aplot: "+e.getPlot().getId().toString()+" setado no banco de dados.");
        }
    }

    @EventHandler
    void toDeletePlot(PlotDeleteEvent e) throws SQLException {
        if(databaseMethod.hasPlot(e.getPlot().getId().toString())) {
            databaseMethod.deletePlot(e.getPlot());
            Bukkit.getConsoleSender().sendMessage(main.prefix+"§cplot: "+e.getPlot().getId().toString()+" deletado do banco de dados.");
        }
    }

}
