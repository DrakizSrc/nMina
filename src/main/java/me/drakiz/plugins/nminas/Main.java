package me.drakiz.plugins.nminas;

import me.drakiz.plugins.nminas.listeners.MiningWorldListeners;
import me.drakiz.plugins.nminas.objects.MiningWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Main extends JavaPlugin {

    private MiningWorld miningWorld;
    private ArrayList<Player> countDownPlayers = new ArrayList<>();

    @Override
    public void onLoad() {
        miningWorld = new MiningWorld(getConfig().getString("Mining.WorldName"));
        miningWorld.deleteMiningWorld();
    }

    public static Main getInstance(){
        return JavaPlugin.getPlugin(Main.class);
    }

    public void onEnable(){
        saveDefaultConfig();
        getCommand("minas").setExecutor(this::onCommand);
        getServer().getPluginManager().registerEvents(new MiningWorldListeners(), this);
        miningWorld.createMiningWorld();

        getLogger().info("[nMinas] Plugin carregado com sucesso.");

    }

    @Override
    public void onDisable() {
        miningWorld.getWorld().getPlayers().forEach(player -> player.teleport(getServer().getWorld("world").getSpawnLocation()));

    }

    public MiningWorld getMiningWorld() {
        return miningWorld;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            World world = player.getWorld();

            if(args.length == 1){
                if(args[0].equalsIgnoreCase("sair")){
                    if(world.getName().equalsIgnoreCase(miningWorld.getWorld().getName())) {
                        player.teleport(getServer().getWorld("world").getSpawnLocation());
                        player.sendMessage(getConfig().getString("Messages.LeaveMiningWorld").replaceAll("&", "§"));
                        return true;
                    }else{
                        player.sendMessage(getConfig().getString("Messages.NotinMiningWorld").replaceAll("&", "§"));
                        return false;
                    }
                }
            }


            if(!player.hasPermission(getConfig().getString("Teleport.Permission"))){
                player.sendMessage(getConfig().getString("Teleport.PermissionMessage").replaceAll("&", "§"));
                return false;
            }

            if(countDownPlayers.contains(player)){
                player.sendMessage(getConfig().getString("Teleport.CountDownMessage")
                                .replace("@countdown", String.valueOf(getConfig().getInt("Teleport.CountDown")))
                        .replaceAll("&", "§"));
                return false;
            }

            countDownPlayers.add(player);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> countDownPlayers.remove(player), 20L * getConfig().getInt("Teleport.CountDown"));


            if(world.getName().equals(miningWorld.getWorld().getName())){
                player.sendMessage(getConfig().getString("Messages.AlreadyinMiningWorld").replaceAll("&", "§"));
                return false;
            }else{
                miningWorld.teleportPlayer(player);
            }
        }else{
            sender.sendMessage("[nMinas] Este comando é apenas para jogadores.");
        }
        return false;
    }

}
