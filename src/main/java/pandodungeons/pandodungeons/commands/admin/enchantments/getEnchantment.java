package pandodungeons.pandodungeons.commands.admin.enchantments;

import controlledEntities.modeled.pets.types.miner.MinerPet;
import controlledEntities.modeled.pets.types.racoon.RacoonPet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import pandoClass.Camp;
import pandoClass.ExpandableClassMenu;
import pandoClass.InitMenu;
import pandoClass.classes.mage.skills.orb.Orb;
import pandoClass.gachaPon.prizes.epic.InstaUpgradeShard;
import pandoClass.gachaPon.prizes.epic.PuertoViejoPipePrize;
import pandoClass.gachaPon.prizes.epic.ReparationShardPrize;
import pandoClass.gachaPon.prizes.epic.RocketBootsPrize;
import pandoClass.gachaPon.prizes.legendary.EscudoReflectantePrize;
import pandoClass.gachaPon.prizes.legendary.StormSwordPrize;
import pandoClass.gachaPon.prizes.legendary.TeleportationHeartPrize;
import pandoClass.gachaPon.prizes.mithic.*;
import pandoClass.gambling.GamblingSession;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;

import static pandoQuests.npc.human.variations.explorer.ExplorerSpawner.spawnExplorerNearPlayer;
import static pandoToros.Entities.toro.Toro.summonToro;
import static pandodungeons.pandodungeons.Elements.LootTableManager.createCustomGacha;
import static pandodungeons.pandodungeons.Game.enchantments.souleater.SoulEaterEnchantment.*;
import static pandodungeons.pandodungeons.Utils.DisplayModels.createMiniCrystal;
import static pandodungeons.pandodungeons.Utils.DisplayModels.spawnTable;
import static pandodungeons.pandodungeons.Utils.ItemUtils.*;

public class getEnchantment implements CommandExecutor {
    private PandoDungeons plugin;

    public getEnchantment(PandoDungeons plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }

        Player player = ((Player) sender).getPlayer();

        assert player != null;
        if(!player.isOp()){
            player.sendMessage(ChatColor.RED + "No tienes permiso para hacer esto");
            return true;
        }

        if(args.length == 3){
            if (args[1].equalsIgnoreCase("setSouls")) {
                int quantity = Integer.parseInt(args[2]);
                ItemStack item  = player.getInventory().getItemInMainHand();
                if(hasSoulEater(item)){
                    if(getSoulCount(item) > quantity){
                        reduceSouls(item, (getSoulCount(item) - quantity));
                    }else if(getSoulCount(item) < quantity) {
                        while(getSoulCount(item) < quantity){
                            addSoul(item);
                        }
                    }
                }
            }
            if(args[1].equalsIgnoreCase("setBateria")){
                int amount = Integer.parseInt(args[2]);
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if(isGarabiThor(itemStack)){
                    setBatery(itemStack, amount);
                }
            }
        }

        if (args.length != 2) {
            return true;
        }

        if(args[1].equalsIgnoreCase("souleater")){
            player.getInventory().addItem(createSoulEaterEnchantedBook());
        } else if (args[1].equalsIgnoreCase("garabithor")) {
            player.getInventory().addItem(garabiThor(1));
        }
        else if (args[1].equalsIgnoreCase("pergamino")) {
            player.getInventory().addItem(soulWritter(1));
        }
        else if (args[1].equalsIgnoreCase("cristal")) {
            createMiniCrystal(player.getWorld(), player.getLocation(), Material.DIAMOND_BLOCK);
        } else if (args[1].equalsIgnoreCase("toro")) {
            summonToro(player.getLocation());
        } else if (args[1].equalsIgnoreCase("mesa")){
            spawnTable(player.getLocation());
        } else if(args[1].equalsIgnoreCase("explorer")){
            spawnExplorerNearPlayer(player);
        } else if(args[1].equalsIgnoreCase("upgrade")){
            try {
                createArmorStand(player.getLocation(), InitMenu.Reason.SKILL_MENU);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if(args[1].equalsIgnoreCase("change")){
            try {
                createArmorStand(player.getLocation(), InitMenu.Reason.SHOP);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if(args[1].equalsIgnoreCase("gamble")){
            Location start = new Location(player.getWorld(), 43.5,73,276.5);
            Location end = new Location(player.getWorld(), 37.5,73,276.5);
            try {
                GamblingSession session = new GamblingSession(plugin,start,end);
                session.addBet(player,1, 100);
                session.startRace();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if(args[1].equalsIgnoreCase("gachapon")){
            try {
                createGachaponArmorStand(player.getLocation());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else if(args[1].equalsIgnoreCase("mapachoblade")){
            player.getInventory().addItem(new MapachoBladePrize(plugin).getItem());
        }
        else if(args[1].equalsIgnoreCase("teleshard")){
            player.getInventory().addItem(new TeleShardPrize(plugin).getItem());
        }
        else if(args[1].equalsIgnoreCase("teleVshard")){
            player.getInventory().addItem(new TeleVillagerShardPrize(plugin).getItem());
        }
        else if(args[1].equalsIgnoreCase("repairShard")){
            player.getInventory().addItem(new ReparationShardPrize(plugin).getItem());
        }
        else if(args[1].equalsIgnoreCase("jetpack")){
            player.getInventory().addItem(new JetPackPrize(plugin).getItem());
        }
        else if(args[1].equalsIgnoreCase("horde")){
            new Camp(plugin).startHorde(player.getLocation(), plugin);
        }
        else if(args[1].equalsIgnoreCase("teleport")){
            player.getInventory().addItem(new TeleportationHeartPrize(plugin).getItem());
        }
        else if(args[1].equalsIgnoreCase("upgradeItem")){
            spawnItemUpgradeVil(player.getLocation());
        } else if(args[1].equalsIgnoreCase("upgradeShard")){
            player.getInventory().addItem(new InstaUpgradeShard(plugin).getItem());
        }
        else if(args[1].equalsIgnoreCase("boots")){
            player.getInventory().addItem(new RocketBootsPrize(plugin).getItem());
        } else if(args[1].equalsIgnoreCase("stormsword")){
            player.getInventory().addItem(new StormSwordPrize(plugin).getItem());
        } else if(args[1].equalsIgnoreCase("upgradeLingot")){
            player.getInventory().addItem(new InstaMegaUpgradeShard(plugin).getItem());
        }else if(args[1].equalsIgnoreCase("gachasucio")){
            player.getInventory().addItem(createCustomGacha(1));
        }else if(args[1].equalsIgnoreCase("classoc")){

            try {
                player.openInventory(new ExpandableClassMenu(player,plugin).createExpandableClassMenu());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        else if(args[1].equalsIgnoreCase("orb")){
            try {
                new Orb(plugin,player,"46994d71b875f087e64dea9b4a0a5cb9f4eb9ab0e8d9060dfde7f6803baa1779",10);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }else if(args[1].equalsIgnoreCase("pipa")){
                player.getInventory().addItem(new PuertoViejoPipePrize(plugin).getItem());
        }else if(args[1].equalsIgnoreCase("rebota")){
            player.getInventory().addItem(new EscudoReflectantePrize(plugin).getItem());
        }else if(args[1].equalsIgnoreCase("mapachin")){
            new RacoonPet(player,plugin);
        }else if(args[1].equalsIgnoreCase("star")){
            player.getInventory().addItem(new InmortalityStar(plugin).getItem());
        }else if(args[1].equalsIgnoreCase("miner")){
            new MinerPet(player,plugin);
        }else if(args[1].equalsIgnoreCase("resortera")){
            player.getInventory().addItem(new SlingShotPrize(plugin).getItem());
        }

        return false;
    }

    public void spawnItemUpgradeVil(Location location) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setAI(false);
        villager.setVillagerType(Villager.Type.SNOW);
        villager.setPersistent(true);
        villager.setInvulnerable(true);
        NamespacedKey key = new NamespacedKey(plugin, "ItemUpgrade");
        villager.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
    }

    private void createArmorStand(Location location, InitMenu.Reason reason) throws MalformedURLException {
        // Se crea el item para la cabeza
        ItemStack skillMenu = createHead("rune", "31ea5a315bc5cf1a6c16ebce57b34100e6d2e7acba3cc99ac6368f71c8598cc8");
        ItemStack shopMenu = createHead("shop", "4c48044e937c044920ec0d537cfb31d89d07a51724f38db19f8bf7cc7293d65d");

        // Se instancia el ArmorStand en la ubicación indicada
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);

        // Configuraciones del ArmorStand
        armorStand.setGravity(false);               // Sin gravedad
        armorStand.setVisible(false);               // Invisible
        armorStand.setInvulnerable(true);           // Invulnerable
        armorStand.setRemoveWhenFarAway(false);     // Persistente (no se elimina al estar lejos)

        if(reason.equals(InitMenu.Reason.SHOP)){
            armorStand.getEquipment().setHelmet(shopMenu);
            armorStand.addScoreboardTag("RPGshop");

        }

        if(reason.equals(InitMenu.Reason.SKILL_MENU)){
            // Se asigna el item a la cabeza del ArmorStand
            armorStand.getEquipment().setHelmet(skillMenu);
            armorStand.addScoreboardTag("RPGSkillMenu");
        }


    }

    private void createGachaponArmorStand(Location location) throws MalformedURLException {
        // Se crea el item para la cabeza
        ItemStack head = createHead("black", "b6dd8919fe8f7507b4641bf3aa72b056e0857cc202a8e5eb66c9c21aa73c3876");

        // Se instancia el ArmorStand en la ubicación indicada
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);

        // Configuraciones del ArmorStand
        armorStand.setGravity(false);               // Sin gravedad
        armorStand.setVisible(false);               // Invisible
        armorStand.setInvulnerable(true);           // Invulnerable
        armorStand.setRemoveWhenFarAway(false);     // Persistente (no se elimina al estar lejos)
        // Se asigna el item a la cabeza del ArmorStand
        armorStand.getEquipment().setHelmet(head);
        armorStand.addScoreboardTag("Gachapon");
    }

}
