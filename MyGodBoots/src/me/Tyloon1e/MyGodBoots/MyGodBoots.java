package me.Tyloon1e.MyGodBoots;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MyGodBoots extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
		if (label.equalsIgnoreCase("mygodboots") || label.equalsIgnoreCase("godboots")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("No!");
				return true;
			}

			Player player = (Player) sender;

			if (player.getInventory().firstEmpty() == -1) /* checks if the inventory is full */ {
				Location location = player.getLocation();
				World world = player.getWorld();

				world.dropItemNaturally(location, getBoots());
				player.sendMessage(ChatColor.GOLD + "Tyrone airdropped some new sneaks at your location since your bag is full!");
				return true;
			}

			player.getInventory().addItem(getBoots());
			player.sendMessage(ChatColor.GOLD + "Tyrone airdropped you some new sneaks.");
			return true;
		}

		return false;
	}

	public ItemStack getBoots() {
		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		ItemMeta meta = boots.getItemMeta(); // you add the lore, enchants etc. to the meta itself

		meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Boots of God");

		List<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Special boots straight from upstairs.");

		meta.setLore(lore); // setLore accepts an array
		meta.addEnchant(Enchantment.PROTECTION_FALL, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setUnbreakable(true);

		boots.setItemMeta(meta);

		return boots;
	}

	@EventHandler
	public void onJump(PlayerMoveEvent event) {
		Player player = (Player) event.getPlayer();

		// is player wearing boots?
		if (player.getInventory().getBoots() != null) {
			// do the boots have this specific display name?
			if (player.getInventory().getBoots().getItemMeta().getDisplayName().contains("Boots of God")) {
				// do the boots have a lore?
				if (player.getInventory().getBoots().getItemMeta().hasLore()) {
					// is the player jumping (based on Y coordinate) from a block?
					if (event.getFrom().getY() < event.getTo().getY() && player.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
						player.setVelocity(player.getLocation().getDirection().multiply(2).setY(2));
					}
				}

			}
		}
	}

	@EventHandler
	public void onFall(EntityDamageEvent event) {
		// make sure the entity is a player, could be a zombie etc.
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (event.getCause() == DamageCause.FALL) {
				// now the same safety checks as in the PlayerMoveEvent
				if (player.getInventory().getBoots() != null) {
					if (player.getInventory().getBoots().getItemMeta().getDisplayName().contains("Boots of God")) {
						if (player.getInventory().getBoots().getItemMeta().hasLore()) {
							event.setCancelled(true); // cancel event so player takes no damage
						}
					}
				}
			}
		}
	}
}
