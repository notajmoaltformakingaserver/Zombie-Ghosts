package com.example.ghostzombies;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class GhostZombiePlugin extends JavaPlugin implements Listener {

    private final Set<Player> enabledPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("GhostZombiePlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GhostZombiePlugin disabled!");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (enabledPlayers.contains(player) && player.getKiller() != null &&
                player.getKiller().getType() == org.bukkit.entity.EntityType.ZOMBIE) {

            Zombie ghostZombie = player.getWorld().spawn(player.getLocation(), Zombie.class);

            ghostZombie.setCustomName("Zombified " + player.getName());
            ghostZombie.setCustomNameVisible(true);
            ghostZombie.setCanPickupItems(true);

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(player);
                playerHead.setItemMeta(meta);
            }
            ghostZombie.getEquipment().setHelmet(playerHead);

            ghostZombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            ghostZombie.setHealth(40);
            ghostZombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8);
            ghostZombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35);
            ghostZombie.setTarget(null);
            ghostZombie.setAware(true);

            // Optional: broadcast message to nearby players
            player.getWorld().getPlayers().forEach(p ->
                p.sendMessage("A Zombified " + player.getName() + " rises from the dead!")
            );
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("zombifiedplayers")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
                if (enabledPlayers.contains(player)) {
                    enabledPlayers.remove(player);
                    player.sendMessage("Zombified players feature disabled.");
                } else {
                    enabledPlayers.add(player);
                    player.sendMessage("Zombified players feature enabled.");
                }
                return true;
            } else {
                player.sendMessage("Usage: /zombifiedplayers toggle");
                return true;
            }
        }
        return false;
    }
}
