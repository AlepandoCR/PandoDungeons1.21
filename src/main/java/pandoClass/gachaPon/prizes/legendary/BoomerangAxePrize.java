package pandoClass.gachaPon.prizes.legendary;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import pandoClass.gachaPon.PrizeItem;
import pandoClass.gachaPon.Quality;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;

public class BoomerangAxePrize extends PrizeItem {

    private NamespacedKey axeKey;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long cooldownTime = 2000L; // 2 segundos en milisegundos

    public BoomerangAxePrize(PandoDungeons plugin) {
        super(plugin);
    }

    @Override
    protected ItemStack createItem() {
        this.axeKey = new NamespacedKey(plugin, "boomerangAxe");
        return createBoomerangAxe(1);
    }

    @Override
    protected Quality selectQuality() {
        return Quality.LEGENDARIO;
    }

    private ItemStack createBoomerangAxe(int amount) {
        ItemStack item = new ItemStack(Material.NETHERITE_AXE, amount);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "Machete Volador");
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Machete afilado por tu abuelo"));
        meta.getPersistentDataContainer().set(axeKey, PersistentDataType.BYTE, (byte) 1);

        CustomModelDataComponent component = meta.getCustomModelDataComponent();

        component.setStrings(List.of("machete"));

        meta.setCustomModelDataComponent(component);

        item.setItemMeta(meta);
        return item;
    }

    private static boolean isBoomerangAxe(PandoDungeons plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "boomerangAxe"), PersistentDataType.BYTE);
    }

    public static void handleRightClick(PlayerInteractEvent event, PandoDungeons plugin) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) return;
        if (!isBoomerangAxe(plugin,item)) return;

        if(isOnCooldown(player.getUniqueId()))return;

        setCooldown(player.getUniqueId());

        event.setCancelled(true);
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection().normalize();

        ItemDisplay axeEntity = player.getWorld().spawn(loc.add(direction.multiply(1)), ItemDisplay.class);
        axeEntity.setItemStack(item.clone());
        axeEntity.setBillboard(Display.Billboard.FIXED);
        axeEntity.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),             // Translation
                new Quaternionf(),                    // Left rotation (no rotación inicial)
                new Vector3f(0.7f, 0.7f, 0.7f),             // Escala
                new Quaternionf()                     // Right rotation
        ));

        new BukkitRunnable() {
            int ticks = 0;
            final double maxDistance = 15;
            final Vector velocity = direction.clone().multiply(0.7);
            final Location start = axeEntity.getLocation().clone();
            boolean returning = false;

            @Override
            public void run() {
                if (!axeEntity.isValid() || !player.isOnline()) {
                    axeEntity.remove();
                    cancel();
                    return;
                }

                Location current = axeEntity.getLocation();
                double traveled = current.distance(start);

                if (traveled >= maxDistance && !returning) {
                    returning = true;
                }

                Vector move;
                if (returning) {
                    move = player.getEyeLocation().toVector().subtract(current.toVector()).normalize().multiply(0.7);
                } else {
                    move = velocity;
                }

                // Teleportar el hacha
                axeEntity.teleport(current.add(move));

                // Animar rotación frontal
                float angle = ticks * 30f; // grados de rotación
                Transformation transform = axeEntity.getTransformation();
                Vector axis = direction.clone().normalize(); // el eje de rotación = dirección de disparo
                Quaternionf rotation = new Quaternionf().fromAxisAngleRad(
                        new Vector3f((float) axis.getX(), 0, (float) axis.getZ()),
                        (float) Math.toRadians(angle)
                );
                transform.getLeftRotation().set(rotation);
                axeEntity.setTransformation(transform);


                // Colisión con entidades
                for (Entity entity : axeEntity.getNearbyEntities(1.5, 1.5, 1.5)) {
                    if (entity instanceof LivingEntity target && !entity.equals(player)) {
                        double damage = calculateDamage(player, player.getInventory().getItemInMainHand(), target);
                        target.damage(damage, player);
                    }
                }

                // Devolver al jugador si llegó
                if (returning && current.distance(player.getEyeLocation()) < 1.5) {
                    axeEntity.remove();
                    cancel();
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static double calculateDamage(Player player, ItemStack weapon, LivingEntity target) {
        double baseDamage = 9.0 + target.getHealth() * 0.2;

        for (Map.Entry<Enchantment, Integer> entry : weapon.getEnchantments().entrySet()) {
            Enchantment ench = entry.getKey();
            int level = entry.getValue();
            switch (ench.getKey().getKey()) {
                case "sharpness":
                    baseDamage += 1 + 2.5 * level;
                    break;
                case "smite":
                    if (target instanceof Zombie || target instanceof Skeleton || target instanceof Wither || target instanceof Stray || target instanceof WitherSkeleton) {
                        baseDamage += 2.5 * level;
                    }
                    break;
                case "bane_of_arthropods":
                    if (target instanceof Spider || target instanceof Silverfish || target instanceof Endermite) {
                        baseDamage += 2.5 * level;
                    }
                    break;
            }
        }

        return baseDamage;
    }

    // Llama cuando quieras poner al jugador en cooldown
    private static void setCooldown(UUID playerId) {
        cooldowns.put(playerId, System.currentTimeMillis());
    }

    // Retorna true si el jugador todavía está en cooldown
    private static boolean isOnCooldown(UUID playerId) {
        if (!cooldowns.containsKey(playerId)) return false;

        long timePassed = System.currentTimeMillis() - cooldowns.get(playerId);
        return timePassed < cooldownTime;
    }

    // Retorna el tiempo restante (en milisegundos)
    private static long getRemaining(UUID playerId) {
        if (!cooldowns.containsKey(playerId)) return 0;

        long timePassed = System.currentTimeMillis() - cooldowns.get(playerId);
        long remaining = cooldownTime - timePassed;
        return Math.max(0, remaining);
    }
}
