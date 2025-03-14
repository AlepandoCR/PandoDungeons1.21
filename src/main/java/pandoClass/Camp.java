package pandoClass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pandoClass.classes.tank.Tank;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.*;

public class Camp {
    private final Random random = new Random();
    // List of mobs in the camp (horde)
    private List<Player> players = new ArrayList<>();

    // Level of the camp (difficulty, size, etc.)
    private int lvl;
    // List of mobs in the camp (horde)
    private List<LivingEntity> entities = new ArrayList<>();

    private boolean rewardsSent = false;

    private PandoDungeons plugin;
    // Constructor
    public Camp(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    /**
     * Calcula el nivel promedio de los RPGPlayers conectados.
     */
    private void updateLevel() {
        int totalLevel = 0;
        int count = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);
            totalLevel += rpgPlayer.getLevel();
            count++;
        }

        if (count > 0) {
            this.lvl = totalLevel / count;
        } else {
            this.lvl = 1; // Nivel mínimo si no hay jugadores
        }

        applyLevelScaling();
    }

    /**
     * Aplica escalado de estadísticas a los mobs según el nivel de la horda.
     */
    private void applyLevelScaling() {
        for (LivingEntity entity : entities) {
            double healthMultiplier = 1.0 + (lvl * 0.005);
            double damageMultiplier = 1.0 + (lvl * 0.001);
            double baseHealth = entity.getHealth();
            entity.setMaxHealth(baseHealth * healthMultiplier);
            if (entity.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
                double baseDamage = entity.getAttribute(Attribute.ATTACK_DAMAGE).getBaseValue();
                entity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(baseDamage * damageMultiplier);
            }
        }
    }

    // Método para spawnear la horda:
    // Spawnea varios arqueros, varios enemigos melee y un jefe (boss)
    public void spawnHorde(Location center, int numArchers, int numMelees) {
        World world = center.getWorld();
        // Limpiar la lista actual (si se quiere reiniciar)
        entities.clear();

        // Spawnea arqueros en posiciones ligeramente aleatorias alrededor del centro
        for (int i = 0; i < numArchers; i++) {
            Location loc = center.clone().add(randomOffset(3), 0, randomOffset(3));
            // Suponiendo que getRandomArcherType() devuelve un EntityType de un arqueros válido
            LivingEntity archer = (LivingEntity) world.spawnEntity(loc, getRandomArcherType(), CreatureSpawnEvent.SpawnReason.NATURAL);
            entities.add(archer);
        }

        // Spawnea enemigos melee
        for (int i = 0; i < numMelees; i++) {
            Location loc = center.clone().add(randomOffset(3), 0, randomOffset(3));
            LivingEntity melee = (LivingEntity) world.spawnEntity(loc, getRandomMeleeType(), CreatureSpawnEvent.SpawnReason.NATURAL);
            entities.add(melee);
        }

        // Spawnea un jefe de la horda en el centro (o ligeramente desplazado)
        Location bossLoc = center.clone().add(0, 0, 0);
        LivingEntity boss = (LivingEntity) world.spawnEntity(bossLoc, getBossType(), CreatureSpawnEvent.SpawnReason.NATURAL);
        entities.add(boss);
    }

    private double randomOffset(double max) {
        return (random.nextDouble() * 2 * max) - max;
    }

    public void addPlayers(){
        for(Entity entity : entities){
            if(entity instanceof Mob mob){
                if(mob.getTarget() != null){
                    if(mob.getTarget() instanceof Player player){
                        if(!players.contains(player)){
                            players.add(player);
                        }
                    }
                }
            }
        }
    }

    public void rewardPlayers(){
        if(players.isEmpty())return;

        if(rewardsSent) return;

        int i  = 0;

        for(Player player : players){
            player.sendMessage("¡Haz derrotado la horda!");
            new RPGPlayer(player, plugin).addCamp(1);
            if(2 > i){
                player.getInventory().addItem(plugin.prizeManager.gachaToken());
            }
            i++;
        }
        rewardsSent = true;
    }

    public boolean hasTarget(){
        for(Entity entity : entities){
            if(entity instanceof Mob mob){
                if(mob.getTarget() != null){
                    return true;
                }
            }
        }
        return false;
    }

    // Método para organizar la formación de la horda alrededor de un centro dado.
    // Por ejemplo, una formación en V o en círculo.
    public void arrangeFormation(Location formationCenter) {
        if(formationCenter == null)return;
        World world = formationCenter.getWorld();
        int size = entities.size();
        if (size == 0) return;
        double angleStep = 360.0 / size;
        double radius = 5.0; // Ajusta el radio según se necesite
        int index = 0;
        for (LivingEntity mob : entities) {
            double angleDeg = index * angleStep;
            double angleRad = Math.toRadians(angleDeg);
            double offsetX = radius * Math.cos(angleRad);
            double offsetZ = radius * Math.sin(angleRad);

            // Calcula la posición base en X y Z
            Location targetLoc = formationCenter.clone().add(offsetX, 0, offsetZ);
            // Ajusta la altura para que el mob esté sobre el bloque más alto en esa posición
            int highestY = world.getHighestBlockYAt(targetLoc.getBlockX(), targetLoc.getBlockZ());
            targetLoc.setY(highestY + 1);

            mob.teleport(targetLoc);
            index++;
        }
    }

    public void moveHordeTo(Location destination) {
        if(destination == null){
            return;
        }
        // Calcula el centro actual de la horda
        Location center = getHordeCenter();
        Vector moveVector = destination.toVector().subtract(center.toVector());

        // Para cada mob, calcula su posición destino manteniendo su offset respecto al centro de la horda.
        for (Entity entity : entities) {
            if (entity instanceof Mob mob) { // Solo los Mob pueden usar pathfinding
                Location mobLoc = mob.getLocation();
                Vector offset = mobLoc.toVector().subtract(center.toVector());
                Location newLoc = destination.clone().add(offset);

                mob.getPathfinder().moveTo(newLoc);
            }
        }
    }


    // Método para verificar si todos los mobs de la horda están muertos
    public boolean areAllDead() {
        for (LivingEntity mob : entities) {
            if (mob != null && mob.isValid() && !mob.isDead()) {
                return false;
            }
        }
        return true;
    }

    public boolean areAllValid(){
        for (LivingEntity mob : entities) {
            if (mob == null || (!mob.isValid() && !mob.isDead())) {
                return false;
            }
        }
        return true;
    }

    // Método que devuelve el centro de la horda (promedio de las posiciones)
    public Location getHordeCenter() {
        if (entities.isEmpty()) return null;
        double sumX = 0, sumY = 0, sumZ = 0;
        int count = 0;
        for (LivingEntity mob : entities) {
            if (mob != null && mob.isValid() && !mob.isDead()) {
                Location loc = mob.getLocation();
                sumX += loc.getX();
                sumY += loc.getY();
                sumZ += loc.getZ();
                count++;
            }
        }
        if (count == 0) return null;
        World world = entities.get(0).getWorld();
        return new Location(world, sumX / count, sumY / count, sumZ / count);
    }

    // Actualiza la horda: elimina mobs muertos y si se cumple cierta condición, ejecuta una acción (por ejemplo, finalizar la horda)
    public void updateHorde() {
        // Eliminar de la lista los mobs que ya no son válidos
        entities.removeIf(mob -> mob == null || !mob.isValid() || mob.isDead());
        // Si la horda está vacía, quizá finalizar la horda o notificar
        if (entities.isEmpty()) {
            Bukkit.getLogger().info("La horda ha sido eliminada.");
        }
    }

    // Ejemplo de métodos auxiliares para obtener tipos de mobs de forma aleatoria
    private EntityType getRandomArcherType() {
        // Lista de tipos de mobs arqueros
        EntityType[] archers = {EntityType.SKELETON, EntityType.STRAY, EntityType.BREEZE};
        return archers[random.nextInt(archers.length)];
    }

    private EntityType getRandomMeleeType() {
        // Lista de tipos de mobs melee
        EntityType[] melees = {EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.WITHER_SKELETON, EntityType.VINDICATOR};
        return melees[random.nextInt(melees.length)];
    }

    private EntityType getBossType() {
        // Un tipo de mob grande como jefe; puede ser por ejemplo, un vindicator o un pillager
        EntityType[] bosses = {EntityType.RAVAGER, EntityType.ILLUSIONER, EntityType.CREAKING};
        return bosses[random.nextInt(bosses.length)];
    }

    public Location findAccessibleLocation(Location start) {
        int maxDistance = 5; // Distancia máxima reducida
        World world = start.getWorld();
        final int directions = 4; // Solo se evalúan 4 direcciones cardinales (N, E, S, O)
        double angleStep = 360.0 / directions;
        double[] cosCache = new double[directions];
        double[] sinCache = new double[directions];

        // Precalcula cosenos y senos para cada dirección
        for (int i = 0; i < directions; i++) {
            double radians = Math.toRadians(i * angleStep);
            cosCache[i] = Math.cos(radians);
            sinCache[i] = Math.sin(radians);
        }

        int baseX = start.getBlockX();
        int baseZ = start.getBlockZ();

        // Bucle desde maxDistance hasta 0, reduciendo en saltos de 10 bloques
        for (int distance = maxDistance; distance > 0; distance -= 10) {
            for (int i = 0; i < directions; i++) {
                int offsetX = (int) (cosCache[i] * distance);
                int offsetZ = (int) (sinCache[i] * distance);
                int checkX = baseX + offsetX;
                int checkZ = baseZ + offsetZ;
                int y = world.getHighestBlockYAt(checkX, checkZ);

                if (y > 0) {
                    Location testLocation = new Location(world, checkX, y, checkZ);
                    if (isLocationAccessibleForHorde(testLocation)) {
                        return testLocation;
                    }
                }
            }
        }

        return null; // No se encontró una ubicación válida
    }




    private boolean isLocationAccessibleForHorde(Location location) {
        for (Entity entity : entities) {
            if (entity instanceof Mob mob) {
                if(mob.getPathfinder().findPath(location) != null){
                    if (!Objects.requireNonNull(mob.getPathfinder().findPath(location)).canReachFinalPoint()) {
                        return false; // Si algún mob no puede llegar, la ubicación no es válida
                    }
                }
            }
        }
        return true;
    }



    private void assureHordeTarget() {
        // Recopilamos en un Set los jugadores que sean Tanks, a partir del target actual de cada enemigo
        Set<Player> tankCandidates = new HashSet<>();
        for (Entity entity : entities) {
            if (entity instanceof Mob enemy) { // Solo los Mobs pueden tener target
                Entity target = enemy.getTarget(); // getTarget() en Mobs devuelve su objetivo actual
                if (target instanceof Player player) {
                    RPGPlayer rpgPlayer = new RPGPlayer(player, plugin);
                    if (rpgPlayer.getClassRpg() instanceof Tank) {
                        tankCandidates.add(player);
                    }
                }
            }
        }

        // Si no hay candidatos, no hacemos nada
        if (tankCandidates.isEmpty()) {
            return;
        }

        // Convertimos el set a lista para asignar targets de forma ordenada
        List<Player> tankList = new ArrayList<>(tankCandidates);
        if (tankList.size() == 1) {
            // Solo hay un tanque: asignamos ese target a todos los enemigos
            Player tankTarget = tankList.get(0);
            for (Entity entity : entities) {
                if (entity instanceof Mob enemy) {
                    enemy.setTarget(tankTarget);
                }
            }
        } else {
            // Hay más de un tanque: distribuimos los enemigos entre ellos (round-robin)
            int index = 0;
            for (Entity entity : entities) {
                if (entity instanceof Mob enemy) {
                    enemy.setTarget(tankList.get(index % tankList.size()));
                    index++;
                }
            }
        }
    }


    public Location getHordeLocation() {
        if (entities.isEmpty()) {
            return null; // Si no hay mobs en la horda, no hay una ubicación válida
        }

        double sumX = 0, sumY = 0, sumZ = 0;
        World world = null;

        for (Entity entity : entities) {
            if (entity.getWorld() != null) {
                world = entity.getWorld(); // Obtenemos el mundo de los mobs
            }
            Location loc = entity.getLocation();
            sumX += loc.getX();
            sumY += loc.getY();
            sumZ += loc.getZ();
        }

        int size = entities.size();
        return new Location(world, sumX / size, sumY / size, sumZ / size);
    }


    // Método para iniciar la horda y moverlos en formación hasta que todos estén muertos.
    public void startHorde(final Location spawnCenter, PandoDungeons plugin) {
        spawnHorde(spawnCenter, 20, 30);
        arrangeFormation(spawnCenter);
        updateLevel(); // Calculamos el nivel al inicializar

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {

                if(!areAllValid()){
                    Bukkit.getLogger().info("La horda ha sido desaparecida.");
                    entities.forEach(Entity::remove);
                    cancel();
                    return;
                }
                // Mover la horda en formación
                if (areAllDead()) {
                    Bukkit.getLogger().info("La horda ha sido derrotada.");
                    rewardPlayers();
                    cancel();
                    return;
                }

                ticks++;

                if(!hasTarget()){
                    arrangeFormation(getHordeLocation());
                }

                addPlayers();
                assureHordeTarget();
                updateHorde();

            }
        }.runTaskTimer(plugin, 0L, 1L); // Actualiza cada segundo
    }
}
