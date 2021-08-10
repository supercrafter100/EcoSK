package com.supercrafter100.ecosk.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.supercrafter100.ecosk.EcoSK;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EffDeleteCurrency extends Effect {

    static {
        Skript.registerEffect(EffDeleteCurrency.class, "remove [a] currency named %string%");
    }

    private Expression<String> name;

    @Override
    protected void execute(Event e) {
        String currencyName = name.getSingle(e);
        if (currencyName.contains(" ")) {
            Skript.error("Currency name cannot contain spaces.");
            return;
        }

        // Run async stuff
        Bukkit.getScheduler().runTaskAsynchronously(EcoSK.getInstance(), () -> {
            Connection conn = EcoSK.getDatabaseManager().getConnection();
            String sql = String.format("DROP TABLE IF EXISTS %s", currencyName);

            try {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Create currency effect with name " + name.toString(e, debug);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.name = (Expression<String>) exprs[0];
        return true;
    }
}
