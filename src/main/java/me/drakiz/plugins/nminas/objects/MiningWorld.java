package me.drakiz.plugins.nminas.objects;

import me.drakiz.plugins.nminas.Main;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.WorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.logging.Level;

public class MiningWorld {

    private Main plugin = Main.getInstance();

    private String name;
    private World world;

    public MiningWorld(String name){
        this.name = name;
    }

    public void createMiningWorld(){

        plugin.getLogger().log(Level.INFO, "[nMinas] Criando o mundo de mineracao: " + name);

        WorldCreator wc = new WorldCreator(name);
        wc.generateStructures(false);
        wc.environment(World.Environment.NORMAL);



        world = Bukkit.getServer().createWorld(wc);

        if(!plugin.getConfig().getBoolean("Mining.Rules.Time")){
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setTime(6000);
        }

        plugin.getLogger().log(Level.INFO, "[nMinas] Mundo de mineracao criado com sucesso!");

    }

    public void deleteMiningWorld(){


        if(Bukkit.getServer().getWorld(name) != null){
            Bukkit.getServer().unloadWorld(name, false);
        }

        if(new File(plugin.getServer().getWorldContainer().getAbsolutePath() + "/" + name).exists()){
            plugin.getLogger().log(Level.INFO, "[nMinas] Deletando antigo mundo de mineracao...");
            File file = new File(plugin.getServer().getWorldContainer().getAbsolutePath() + "/" + name);
            if(deleteWorld(file)) {
                plugin.getLogger().log(Level.INFO, "[nMinas] Mundo de mineracao deletado com sucesso!");
            } else  {
                plugin.getLogger().log(Level.SEVERE, "[nMinas] Erro ao deletar mundo de mineracao!");
            }
        }
    }

    public void teleportPlayer(Player player){
        World world = Bukkit.getServer().getWorld(name);
        int range = plugin.getConfig().getInt("Teleport.Range");
        int highestBlock = world.getHighestBlockYAt(player.getLocation());
        player.teleport(world.getSpawnLocation().add(new Random().nextInt(range), highestBlock, new Random().nextInt(range)));
        plugin.getConfig().getStringList("Mining.WelcomeMessage").forEach(message -> player.sendMessage(message.replaceAll("&", "§")));
    }

    public boolean deleteWorld(File path) {
        if(path.exists()) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }
    public World getWorld() {
        return world;
    }
}
