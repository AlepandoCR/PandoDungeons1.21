package controlledEntities.modeled.pets;

import com.destroystokyo.paper.profile.PlayerProfile;
import controlledEntities.modeled.pets.types.miner.MinerPet;
import controlledEntities.modeled.pets.types.racoon.RacoonPet;
import controlledEntities.modeled.pets.types.sakura.SakuraPet;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandoClass.RPGPlayer;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class PetGachapon {

    private final Map<Quality, List<Pet>> petLists = new EnumMap<>(Quality.class);
    private final Map<Pet, String> petTextures = new HashMap<>();
    private final Map<Quality, Double> qualityProbabilities = new EnumMap<>(Quality.class);

    private final List<ArmorStand> animationArmorStands = new ArrayList<>();
    private final Map<ArmorStand, Vector> animationVelocities = new HashMap<>();
    private BukkitRunnable animationTask;

    private final Random random = new Random();
    private final PandoDungeons plugin;
    private final Player player;
    private final RPGPlayer rpgPlayer;

    public static PetGachapon activePetGachapon = null;



    public PetGachapon(PandoDungeons plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.rpgPlayer = new RPGPlayer(player, plugin);
        init();
    }

    public void init(){
        // Definir mascotas por calidad
        addPet(new RacoonPet(player, plugin), Quality.RARO);
        addPet(new MinerPet(player, plugin), Quality.LEGENDARIO);
        addPet(new SakuraPet(player, plugin), Quality.MITICO);
        addPet(new MinerPet(player, plugin), Quality.EPICO);
        addPet(new RacoonPet(player, plugin), Quality.INFERIOR);

        // Probabilidades de cada calidad
        qualityProbabilities.put(Quality.INFERIOR, 30.0);
        qualityProbabilities.put(Quality.RARO, 40.0);
        qualityProbabilities.put(Quality.EPICO, 20.0);
        qualityProbabilities.put(Quality.LEGENDARIO, 8.0);
        qualityProbabilities.put(Quality.MITICO, 2.0);
    }

    private void addPet(Pet pet, Quality quality) {
        petLists.putIfAbsent(quality, new ArrayList<>()); // Asegura que haya una lista inicializada
        List<Pet> qualityPets = petLists.get(quality);
        qualityPets.add(pet);
        petTextures.put(pet, pet.getDisplayValue());
        pet.destroy();
    }

    public void trigger() {

        Location center = new Location(Bukkit.getWorld("spawn"), 416.5, 87.5, 212.5);

        if(activePetGachapon != null){
            player.sendMessage("Hay otro gachapon activo");
            return;
        }

        activePetGachapon = this;

        startAnimation(center);
        Quality selectedQuality = chooseQuality();
        List<Pet> pets = petLists.get(selectedQuality);

        if (pets == null || pets.isEmpty()) return;

        Pet selectedPet = pets.get(random.nextInt(pets.size()));
        String textureUrl = petTextures.get(selectedPet);

        new BukkitRunnable() {
            @Override
            public void run() {
                finishAnimation(center, selectedPet, textureUrl, selectedQuality);
                grantPermission(selectedPet.getPermission());
                activePetGachapon = null;
            }
        }.runTaskLater(plugin, 100L);
    }

    private void grantPermission(String permission) {
        // Verifica si el jugador ya tiene el permiso
        if (player.hasPermission(permission)) {
            player.sendMessage(ChatColor.RED + "¡Ya tienes esa mascota!");
            player.sendMessage(ChatColor.GREEN + "Recibiste " + ChatColor.GOLD + "2 " + ChatColor.GREEN + "fichas de Gachapon normal");
            player.getInventory().addItem(plugin.prizeManager.gachaToken(2));

            return; // Salir del método si el jugador ya tiene el permiso
        }

        // Si no tiene el permiso, lo otorga
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set " + permission);
        player.sendMessage(ChatColor.GREEN + "¡Has obtenido la mascota: " + ChatColor.GOLD + permission + ChatColor.GREEN + "!");
    }


    private Quality chooseQuality() {
        double roll = random.nextDouble() * 100;
        double cumulative = 0.0;
        if (rpgPlayer.getGachaopen() % 50 == 0 && rpgPlayer.getGachaopen() != 0) {
            rpgPlayer.resetGachaOpens();
            return Quality.MITICO;
        }
        for (Quality q : Quality.values()) {
            cumulative += qualityProbabilities.get(q);
            if (roll <= cumulative) {
                if (q.equals(Quality.MITICO)) {
                    rpgPlayer.resetGachaOpens();
                }
                return q;
            }
        }
        return Quality.INFERIOR;
    }

    private void startAnimation(Location center) {
        World world = center.getWorld();
        double radius = 1.5; // Ajusta el radio de la esfera

        for (Quality quality : petLists.keySet()) {
            for (Pet pet : petLists.get(quality)) {
                String textureUrl = petTextures.get(pet);
                if (textureUrl == null) continue;

                // Generar coordenadas sobre una esfera
                double theta = 2 * Math.PI * random.nextDouble();
                double phi = Math.acos(2 * random.nextDouble() - 1);
                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.sin(phi) * Math.sin(theta);
                double z = radius * Math.cos(phi);
                Location spawnLoc = center.clone().add(x, y, z);

                ArmorStand stand = world.spawn(spawnLoc, ArmorStand.class, a -> {
                    a.setGravity(false);
                    a.setVisible(false);
                    a.setMarker(true);
                    try {
                        a.getEquipment().setHelmet(createHead(textureUrl));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                });
                animationArmorStands.add(stand);

                // Crear velocidad tangencial
                Vector velocity = new Vector(-z, (random.nextDouble() - 0.5) * 0.05, x).normalize().multiply(0.1);
                animationVelocities.put(stand, velocity);
            }
        }

        // Iniciar animación
        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (ArmorStand stand : animationArmorStands) {
                    Vector velocity = animationVelocities.get(stand);
                    Location currentLoc = stand.getLocation();
                    Vector directionToCenter = center.clone().subtract(currentLoc).toVector().normalize();

                    // Ajustar movimiento para que sea tangencial
                    Vector newVelocity = velocity.clone().subtract(directionToCenter.clone().multiply(velocity.dot(directionToCenter))).normalize().multiply(0.1);
                    animationVelocities.put(stand, newVelocity);

                    stand.teleport(currentLoc.add(newVelocity));
                }
            }
        };
        animationTask.runTaskTimer(plugin, 0L, 1L);
    }


    private void finishAnimation(Location center, Pet pet, String textureUrl, Quality quality) {
        if (animationTask != null) {
            animationTask.cancel();
        }
        animationArmorStands.forEach(ArmorStand::remove);
        animationArmorStands.clear();
        animationVelocities.clear();

        spawnFireworkExplosion(center, getColorByQuality(quality));
        center.getWorld().playSound(center, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        ArmorStand finalStand = center.getWorld().spawn(center, ArmorStand.class, a -> {
            a.setGravity(false);
            a.setVisible(false);
            a.setMarker(true);
            try {
                a.getEquipment().setHelmet(createHead(textureUrl));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            a.setGlowing(true);
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                finalStand.remove();
            }
        }.runTaskLater(plugin, 100L);

    }

    private void spawnFireworkExplosion(Location center, Color color) {
        Firework firework = center.getWorld().spawn(center, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().withColor(color).with(FireworkEffect.Type.BALL_LARGE).build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        new BukkitRunnable() {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(plugin, 1L);
    }

    private Color getColorByQuality(Quality quality) {
        return switch (quality) {
            case INFERIOR -> Color.GRAY;
            case RARO -> Color.GREEN;
            case EPICO -> Color.BLUE;
            case LEGENDARIO -> Color.ORANGE;
            case MITICO -> Color.PURPLE;
            default -> Color.WHITE;
        };
    }

    private ItemStack createHead(String textureUrl) throws MalformedURLException {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        // Crear perfil y texturas del jugador
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(new URL("https://textures.minecraft.net/texture/" + textureUrl)); // Establecer la textura de la piel del companion
        profile.setTextures(textures);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);
        return head;
    }
}
