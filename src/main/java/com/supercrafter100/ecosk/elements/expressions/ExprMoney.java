package com.supercrafter100.ecosk.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.supercrafter100.ecosk.EcoSK;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExprMoney extends SimpleExpression<Number> {

    static {
        Skript.registerExpression(ExprMoney.class, Number.class, ExpressionType.COMBINED, "[the] (currency|money) of %offlineplayer% of %string%");
    }

    Expression<OfflinePlayer> player;
    Expression<String> type;


    @Override
    protected Number[] get(Event e) {
        OfflinePlayer p = player.getSingle(e);
        String t = type.getSingle(e);

        if (p == null) return null;
        Number amount = getAmount(p, t);
        if (amount == null) amount = 0;
        return new Number[] { amount };
    }

    @Override
    public void change(Event e,Object[] delta, Changer.ChangeMode mode) {
        Number money = (Number) delta[0];
        OfflinePlayer p = player.getSingle(e);
        String t = type.getSingle(e);

        if (p == null || money == null || t == null) return;

        if (mode == Changer.ChangeMode.ADD) {
            Bukkit.getScheduler().runTaskAsynchronously(EcoSK.getInstance(), () -> this.addAmount(p, t, money));
            this.addAmount(p, t, money);
            return;
        }

        if (mode == Changer.ChangeMode.REMOVE) {
            Bukkit.getScheduler().runTaskAsynchronously(EcoSK.getInstance(), () -> this.removeAmount(p, t, money));
            this.addAmount(p, t, money);
            return;
        }

        if (mode == Changer.ChangeMode.SET) {
            Bukkit.getScheduler().runTaskAsynchronously(EcoSK.getInstance(), () -> this.setAmount(p, t, money));
            this.setAmount(p, t, money);
            return;
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Number.class);
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Money expression with player: " + player.toString(e, debug) + " and type: " + type.toString(e, debug);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        player = (Expression<OfflinePlayer>) exprs[0];
        type = (Expression<String>) exprs[1];
        return true;
    }

    private Number getAmount(OfflinePlayer p, String type) {
        Connection conn = EcoSK.getDatabaseManager().getConnection();
        String sql = String.format("SELECT * FROM %s WHERE UUID = ?", type);

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getBigDecimal("AMOUNT");
        } catch (SQLException ignored) { }

        return null;
    }

    private void addAmount(OfflinePlayer p, String type, Number amount) {
        try {
            Connection conn = EcoSK.getDatabaseManager().getConnection();
            if (getAmount(p, type) == null) {
                String sql = String.format("INSERT INTO %s (`UUID`, `AMOUNT`) VALUES ( ?, ? )", type);
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, p.getUniqueId().toString());
                ps.setBigDecimal(2, BigDecimal.valueOf(amount.intValue()));
                ps.execute();
            } else {
                String sql = String.format("UPDATE %s SET `AMOUNT` = `AMOUNT` + ? WHERE UUID = ?;", type);
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setBigDecimal(1, BigDecimal.valueOf(amount.intValue()));
                ps.setString(2, p.getUniqueId().toString());
                ps.execute();
            }
        } catch (SQLException ignored) { }
    }

    private void removeAmount(OfflinePlayer p, String type, Number amount) {
        try {
            Connection conn = EcoSK.getDatabaseManager().getConnection();
            if (getAmount(p, type) == null) {
                String sql = String.format("INSERT INTO %s (`UUID`, `AMOUNT`) VALUES ( ?, ? )", type);
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, p.getUniqueId().toString());
                ps.setBigDecimal(2, BigDecimal.valueOf(-amount.intValue()));
                ps.execute();
            } else {
                String sql = String.format("UPDATE %s SET `AMOUNT` = `AMOUNT` - ? WHERE UUID = ?;", type);
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setBigDecimal(1, BigDecimal.valueOf(amount.intValue()));
                ps.setString(2, p.getUniqueId().toString());
                ps.execute();
            }
        } catch (SQLException ignored) { }
    }

    private void setAmount(OfflinePlayer p, String type, Number amount) {
        try {
            Connection conn = EcoSK.getDatabaseManager().getConnection();
            if (getAmount(p, type) == null) {
                String sql = String.format("INSERT INTO %s (`UUID`, `AMOUNT`) VALUES ( ?, ? )", type);
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, p.getUniqueId().toString());
                ps.setBigDecimal(2, BigDecimal.valueOf(amount.intValue()));
                ps.execute();
            } else {
                String sql = String.format("UPDATE %s SET `AMOUNT` = ? WHERE UUID = ?;", type);
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setBigDecimal(1, BigDecimal.valueOf(amount.intValue()));
                ps.setString(2, p.getUniqueId().toString());
                ps.execute();
            }
        } catch (SQLException ignored) { }
    }

}
