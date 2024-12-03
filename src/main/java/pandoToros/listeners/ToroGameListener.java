package pandoToros.listeners;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.*;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pandoToros.Entities.toro.Toro;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.Locale;

import static pandoToros.game.ArenaMaker.extractUsername;
import static pandoToros.game.ArenaMaker.isRedondelWorld;
import static pandoToros.game.RedondelGame.hasActiveRedondel;
import static pandodungeons.pandodungeons.Utils.LocationUtils.isDungeonWorld;


public class ToroGameListener implements Listener {

    PandoDungeons plugin = JavaPlugin.getPlugin(PandoDungeons.class);

    @EventHandler
    private void buildBarricades(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if(isRedondelWorld(player.getWorld().getName()) && !player.isOp()) {
            if (!event.getBlock().getType().equals(Material.BAMBOO_BLOCK) || event.getBlock().getLocation().getY() > -2) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void breakBlocks(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(isRedondelWorld(player.getWorld().getName()) && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void dieInGame(PlayerDeathEvent event){
        Player player = event.getPlayer();

        if(isRedondelWorld(player.getWorld().getName())){
            Entity entity = event.getDamageSource().getDirectEntity();
            if(entity instanceof Ravager ravager){
                if(ravager.getTarget() != null && ravager.getTarget() != player){
                    for(Player players : player.getWorld().getPlayers()){
                        players.sendMessage(ravager.getTarget().getName() + " ha matado a" + player.getName());
                    }
                }
            }
            event.setCancelled(true);
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    private void tpIntoRedondel(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();
        if (isRedondelWorld(worldName)) {
            if (!extractUsername(worldName).equalsIgnoreCase(player.getName())) {
                event.setCancelled(hasActiveRedondel(player));

            }
        }
    }

    @EventHandler
    private void eatOnRedondel(PlayerItemConsumeEvent event){
        if(isRedondelWorld(event.getPlayer().getWorld().getName())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void hitOnRedondel(PrePlayerAttackEntityEvent event){
        if(isRedondelWorld(event.getPlayer().getWorld().getName())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void arrowHitCancel(EntityShootBowEvent event){
        if(event.getEntity() instanceof Player player){
            if(isRedondelWorld(player.getWorld().getName())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void crossBowCancel(EntityLoadCrossbowEvent event){
        if(event.getEntity() instanceof Player player){
            if(isRedondelWorld(player.getWorld().getName())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            if (event.getEntity().getShooter() instanceof Player player) {
                World world = player.getWorld();

                if (isRedondelWorld(world.getName())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.DARK_PURPLE + "No puedes usar enderpearls aqui");
                }
            }
        }
    }
}
