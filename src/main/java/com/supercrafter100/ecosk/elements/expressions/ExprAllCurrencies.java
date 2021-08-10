package com.supercrafter100.ecosk.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.supercrafter100.ecosk.EcoSK;
import org.bukkit.event.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ExprAllCurrencies extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprAllCurrencies.class, String.class, ExpressionType.SIMPLE, "all [of] [the] (currency|currencies)");
    }

    @Override
    protected String[] get(Event e) {
        Connection conn = EcoSK.getDatabaseManager().getConnection();
        String sql = "SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%'";

        ArrayList<String> tables = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tables.add(rs.getString("name"));
            }

            String [] array = new String[tables.size()];
            array = tables.toArray(array);
            return array;
        } catch (SQLException ignored) { ignored.printStackTrace(); }

        return null;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "All currencies expression";
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
