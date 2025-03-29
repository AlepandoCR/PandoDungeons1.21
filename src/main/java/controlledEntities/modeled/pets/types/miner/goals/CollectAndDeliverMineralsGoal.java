package controlledEntities.modeled.pets.types.miner.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CollectAndDeliverMineralsGoal extends Goal {
    private static final Set<Material> MINERAL_TYPES = EnumSet.of(
            Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT, Material.IRON_INGOT,
            Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.GOLD_ORE, Material.IRON_ORE,
            Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_IRON_ORE, Material.ANCIENT_DEBRIS,Material.NETHER_GOLD_ORE
    );

    private static final int MAX_ITEMS = 64 ;
    private static final int DROP_COOLDOWN_TICKS = 30;  // 1.5 segundos
    private static final int PICKUP_COOLDOWN_TICKS = 40; // 2 segundos
    private static final int ITEM_EXPIRATION_TICKS = 200; // 10 segundos

    private final Mob mob;
    private final Player targetPlayer;
    private final double speedModifier;
    private final double trackDistance;
    private final double throwDistance;
    private final List<ItemStack> collectedItems = new ArrayList<>();
    private final Map<UUID, Long> droppedItems = new HashMap<>();
    private ItemEntity currentTargetItem = null;

    private long lastDropTime = 0;
    private long lastPickupTime = 0;

    public CollectAndDeliverMineralsGoal(Mob mob, Player targetPlayer, double speedModifier, double trackDistance, double throwDistance) {
        this.mob = mob;
        this.targetPlayer = targetPlayer;
        this.speedModifier = speedModifier;
        this.trackDistance = trackDistance;
        this.throwDistance = throwDistance;
    }

    @Override
    public boolean canUse() {
        return this.targetPlayer != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPlayer != null;
    }

    @Override
    public void tick() {
        long currentTime = mob.level().getGameTime();

        // Entregar ítems al jugador cada 1.5s si está cerca
        if (!collectedItems.isEmpty() && mob.distanceTo(targetPlayer) <= throwDistance) {
            if (currentTime - lastDropTime >= DROP_COOLDOWN_TICKS) {
                dropItemsToPlayer();
                lastDropTime = currentTime;
            }
            return;
        }

        // Limpiar ítems descartados que ya expiraron
        droppedItems.entrySet().removeIf(entry -> currentTime - entry.getValue() > ITEM_EXPIRATION_TICKS);

        // Buscar un nuevo ítem si no hay objetivo
        if (currentTargetItem == null || currentTargetItem.isRemoved() || droppedItems.containsKey(currentTargetItem.getUUID())) {
            findNewItemTarget();
        }

        // Moverse hacia el ítem si hay objetivo
        if (currentTargetItem != null) {
            moveToItem();
        } else {
            moveToPlayer();
        }
    }

    private void findNewItemTarget() {
        List<ItemEntity> nearbyItems = mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(trackDistance),
                item -> MINERAL_TYPES.contains(((CraftItem) item.getBukkitEntity()).getItemStack().getType()) &&
                        !droppedItems.containsKey(item.getUUID())); // Evita recoger sus propios ítems

        if (!nearbyItems.isEmpty()) {
            nearbyItems.sort(Comparator.comparingDouble(mob::distanceTo));
            currentTargetItem = nearbyItems.getFirst();
        }
    }

    private void moveToItem() {
        if (currentTargetItem != null) {
            mob.getNavigation().moveTo(currentTargetItem, speedModifier);

            // Recoger el ítem si está lo suficientemente cerca y han pasado 2 segundos
            long currentTime = mob.level().getGameTime();
            if (mob.distanceTo(currentTargetItem) <= 1.5 && currentTime - lastPickupTime >= PICKUP_COOLDOWN_TICKS) {
                pickUpItem(currentTargetItem);
                lastPickupTime = currentTime;
                currentTargetItem = null;
            }
        }
    }

    private void moveToPlayer() {
        if (mob.distanceTo(targetPlayer) > 20) {
            mob.getBukkitEntity().teleport(targetPlayer.getBukkitEntity().getLocation());
        }
        if (mob.distanceTo(targetPlayer) > 3) {
            mob.getNavigation().moveTo(targetPlayer, speedModifier);
        } else {
            mob.getNavigation().stop();
        }
    }

    private void pickUpItem(ItemEntity itemEntity) {
        if (collectedItems.size() >= MAX_ITEMS) return;

        ItemStack stack = ((CraftItem) itemEntity.getBukkitEntity()).getItemStack().clone();
        collectedItems.add(stack);
        itemEntity.discard(); // Eliminar la entidad correctamente
    }

    private void dropItemsToPlayer() {
        if (!collectedItems.isEmpty()) {
            ItemStack item = collectedItems.removeFirst();
            ItemEntity droppedItem = ((CraftItem) targetPlayer.getBukkitEntity().getWorld().dropItemNaturally(targetPlayer.getBukkitEntity().getLocation(), item)).getHandle() ;

            // Registrar el ítem soltado
            droppedItems.put(droppedItem.getUUID(), mob.level().getGameTime());
        }
    }
}
