package me.drakiz.plugins.nminas.listeners;

import me.drakiz.plugins.nminas.Main;
import me.drakiz.plugins.nminas.util.ItemConverter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class MiningWorldListeners implements Listener {

    public Main plugin = Main.getInstance();
    private Random random = new Random();

    @EventHandler(ignoreCancelled = true)
    void onBlockBreackEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getWorld().getName().equalsIgnoreCase(plugin.getMiningWorld().getWorld().getName())) {
            plugin.getConfig().getStringList("Mining.Effects").forEach(effect ->
                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect.split(",")[0]), 4, Integer.parseInt(effect.split(",")[1].replaceAll(" ", "")))));

        }
        int playerY = player.getLocation().getBlockY();
        if(event.getBlock().getType() == Material.STONE){
            if(player.getItemInHand().getType().toString().contains("PICKAXE")){
                int amount = 1;
                if(player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)){
                    int level = player.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                    amount = level + 1;
                }
                int finalAmount = amount;
                plugin.getConfig().getStringList("Mining.Ores").forEach(ore -> {
                    ore = ore.replaceAll(" ", "");
                    int itemID = Integer.parseInt(ore.split(",")[0].split(":")[0]);
                    short itemData = Short.parseShort(ore.split(",")[0].split(":")[1]);

                    ItemStack item = new ItemConverter(itemID, itemData, 1 ).getItem();
                    int minY = Integer.parseInt(ore.split(",")[1]);
                    int chance = Integer.parseInt(ore.split(",")[2]);
                    if(playerY <= minY){
                        if(Math.random() * 200 <= chance){
                            if(item.getType().equals(Material.COAL_ORE)){
                                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.COAL, finalAmount));
                            }
                            else if(item.getType().equals(Material.LAPIS_ORE)){
                                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.INK_SACK, random.nextInt(3) + finalAmount, (short) 4));
                            }
                            else if(item.getType().equals(Material.REDSTONE_ORE)){
                                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.REDSTONE, random.nextInt(3) + finalAmount));
                            }
                            else if(item.getType().equals(Material.DIAMOND_ORE)){
                                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.DIAMOND, finalAmount));
                            }
                            else if(item.getType().equals(Material.EMERALD_ORE)){
                                if(event.getBlock().getBiome().equals(Biome.EXTREME_HILLS) || event.getBlock().getBiome().equals(Biome.EXTREME_HILLS_MOUNTAINS)) {
                                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.EMERALD, finalAmount));
                                }
                            }else {
                                event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);

                            }


                        }
                    }
                });

            }
        }

        if(!plugin.getConfig().getBoolean("Mining.Rules.DropStone")){
            if(event.getBlock().getType() == Material.STONE){
                event.getBlock().setType(Material.AIR);
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    void onEntitySpawn(EntitySpawnEvent event){
        if(event.getLocation().getWorld().getName().equalsIgnoreCase(plugin.getConfig().getString("Mining.WorldName"))){
            if(!plugin.getConfig().getBoolean("Mining.Rules.MobSpawn")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void weatherChangeEvent(WeatherChangeEvent e) {
        if(!plugin.getConfig().getBoolean("Mining.Rules.Weather")) {
            if (e.toWeatherState()) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if(player.getLocation().getWorld().getName().equalsIgnoreCase(plugin.getConfig().getString("Mining.WorldName"))){
                if(!plugin.getConfig().getBoolean("Mining.Rules.Damage")){
                    event.setCancelled(true);
                }
            }
        }

    }






    @EventHandler(ignoreCancelled = true)
    public void onPopulate(ChunkPopulateEvent e) {
        Chunk c = e.getChunk();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    Block b = c.getBlock(x, y, z);
                    Material m = b.getType();
                    if ((m == Material.COAL_ORE)
                            || (m == Material.IRON_ORE)
                            || (m == Material.GOLD_ORE)
                            || (m == Material.DIAMOND_ORE)
                            || (m == Material.EMERALD_ORE)
                            || (m == Material.LAPIS_ORE)
                            || (m == Material.REDSTONE_ORE)) {
                        b.setType(Material.STONE);
                    }
                }
            }
        }
    }
}
