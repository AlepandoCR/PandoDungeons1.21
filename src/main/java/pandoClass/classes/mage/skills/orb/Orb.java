package pandoClass.classes.mage.skills.orb;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import pandoClass.RPGPlayer;
import pandoClass.classes.mage.skills.orb.skills.OrbSkill;
import pandoClass.classes.mage.skills.orb.skills.OrbSkillAttack;
import pandoClass.classes.mage.skills.orb.skills.OrbSkillDefense;
import pandoClass.classes.mage.skills.orb.skills.OrbSkillSlowfall;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Orb {
    private final PandoDungeons plugin;
    private final Player owner;
    private ArmorStand stand;
    private ItemStack orbDisplay; // Objeto que representa el orbe
    private OrbEmotion currentEmotion = OrbEmotion.NEUTRAL;
    private BukkitRunnable mainTask;
    private boolean pauseLevitation = false;
    private OrbSkill currentSkill;
    private int level;
    private final NamespacedKey orbKey;
    private final NamespacedKey playerName;

    // Usamos un ConcurrentHashMap para evitar condiciones de carrera
    private static final ConcurrentMap<Player, ArmorStand> stands = new ConcurrentHashMap<>();

    public Orb(PandoDungeons plugin, Player owner, String orbDisplay, int level) throws MalformedURLException {
        this.plugin = plugin;
        this.owner = owner;
        this.orbKey = new NamespacedKey(plugin, "orbKey");
        this.playerName = new NamespacedKey(plugin, "playerName");

        // Verificar de forma thread-safe si el jugador ya tiene un orbe
        if (stands.containsKey(owner)) {
            owner.sendMessage(ChatColor.RED + "Ya tienes un orb activo.");
            return;
        }

        setOrbDisplay(orbDisplay);
        this.level = level;
        spawnOrb();
        activateSkill(new OrbSkillAttack(plugin, this)); // Habilidad por defecto
    }

    public void setOrbDisplay(String textureUrl) throws MalformedURLException {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // Asigna el perfil con la textura correspondiente
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(new URL("https://textures.minecraft.net/texture/" + textureUrl));
        profile.setTextures(textures);
        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);

        orbDisplay = head;
        updateStand();
    }

    public ArmorStand getStand(){
        return stand;
    }

    public void updateStand(){
        if (stand != null) {
            stand.setHelmet(orbDisplay);
        }
    }

    public void removeStand(){
        if (stand != null && stand.isValid()) {
            stand.remove();
        }
    }

    public void activateSkill(OrbSkill newSkill) {
        if (currentSkill != null) {
            currentSkill.stop();
        }
        currentSkill = newSkill;
        currentSkill.start(level);
    }

    public void setLevel(int level) {
        this.level = level;
        if (currentSkill != null) {
            currentSkill.start(level);
        }
    }

    private void spawnOrb() {
        Location loc = owner.getLocation().add(1, 0.5, 0);
        stand = owner.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            armorStand.setSmall(true);
            armorStand.setInvulnerable(true);
            armorStand.getPersistentDataContainer().set(orbKey, PersistentDataType.BOOLEAN, true);
            armorStand.getPersistentDataContainer().set(playerName, PersistentDataType.STRING, owner.getName());
            armorStand.setGravity(false);
            armorStand.getEquipment().setHelmet(orbDisplay);
        });

        // Registrar el orbe de forma atómica
        stands.put(owner, stand);

        mainLoop();
    }

    public static void handleStands(PandoDungeons plugin) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand armorStand) {
                    NamespacedKey key = new NamespacedKey(plugin, "orbKey");
                    if (armorStand.getPersistentDataContainer().has(key)) {
                        // Remover orbes no registrados en el mapa
                        if (!stands.containsValue(armorStand)) {
                            armorStand.remove();
                        }
                    }
                }
            }
        }
    }

    private void mainLoop() {
        mainTask = new BukkitRunnable() {
            boolean goingUp = true;
            double offset = 0.0;
            private boolean slowfallActive = false;
            private boolean defenseActive = false;
            private boolean firing = false;

            @Override
            public void run() {
                if (!owner.isOnline() || owner.isDead() || !stand.isValid() || stand.isDead()) {
                    stands.remove(owner);
                    remove();
                    return;
                }
                if (stand.isValid()) {
                    handleDefense();
                    handleSlowFall();
                    handleAttack();
                    levitate();
                    teleportToOwner();
                } else {
                    this.cancel();
                }
            }

            private void handleAttack() {
                if (!defenseActive && !slowfallActive) {
                    if (!(currentSkill instanceof OrbSkillAttack) && !firing) {
                        activateSkill(new OrbSkillAttack(plugin, Orb.this));
                        firing = true;
                    }
                } else {
                    firing = false;
                }
            }

            private void handleSlowFall() {
                if (owner.getFallDistance() >= 4) {
                    if (owner.isFlying() || owner.getPose().equals(Pose.FALL_FLYING)) return;
                    if (!slowfallActive && !(currentSkill instanceof OrbSkillSlowfall)) {
                        activateSkill(new OrbSkillSlowfall(plugin, Orb.this));
                        slowfallActive = true;
                    }
                } else {
                    slowfallActive = false;
                }
            }

            private void handleDefense() {
                for (Entity e : owner.getNearbyEntities(5, 5, 5)) {
                    if (e instanceof Projectile) {
                        if (!defenseActive && !(currentSkill instanceof OrbSkillDefense)) {
                            activateSkill(new OrbSkillDefense(plugin, Orb.this));
                            defenseActive = true;
                        }
                        return;
                    } else {
                        defenseActive = false;
                    }
                }
            }

            private void levitate() {
                if (!pauseLevitation) {
                    offset += (goingUp ? 0.02 : -0.02);
                    if (offset >= 0.2) goingUp = false;
                    if (offset <= -0.2) goingUp = true;
                }
            }

            private void teleportToOwner() {
                Location playerLoc = owner.getLocation();
                float yaw = playerLoc.getYaw();
                float pitch = owner.getPitch();
                double radians = Math.toRadians(yaw);
                double xOffset = -Math.cos(radians) * 1;
                double zOffset = -Math.sin(radians) * 1;
                Location orbLocation = playerLoc.clone().add(xOffset, 1.5 + offset, zOffset);
                stand.teleport(orbLocation);
                stand.setHeadPose(new EulerAngle(Math.toRadians(pitch), 0, 0));
            }

        };
        mainTask.runTaskTimer(plugin, 0, 2);
    }

    public Player getOwner() {
        return owner;
    }

    public Location getLocation() {
        return stand.getLocation().add(0, 0.5, 0);
    }

    public void pauseLevitation() {
        pauseLevitation = true;
    }

    public void unPauseLevitation() {
        pauseLevitation = false;
    }

    public void changeEmotion(OrbEmotion newEmotion) {
        if (this.currentEmotion == newEmotion) return;
        this.currentEmotion = newEmotion;
        playEmotionAnimation();
    }

    private void playEmotionAnimation() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            switch (currentEmotion) {
                case HAPPY:
                    try {
                        setOrbDisplay("46994d71b875f087e64dea9b4a0a5cb9f4eb9ab0e8d9060dfde7f6803baa1779");
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    stand.setHeadPose(new EulerAngle(-0.3, 0, 0));
                    break;
                case SAD:
                    stand.setHeadPose(new EulerAngle(0.3, 0, 0));
                    break;
                case ANGRY:
                    try {
                        setOrbDisplay("d500292f4afe52d10f299dfb26036322830450331e003084bb220333530664e1");
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    for (int i = 0; i < 3; i++) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            double randomX = (Math.random() - 0.5) * 0.1;
                            double randomZ = (Math.random() - 0.5) * 0.1;
                            stand.teleport(stand.getLocation().add(randomX, 0, randomZ));
                        }, i * 3);
                    }
                    break;
                case CURIOUS:
                    try {
                        setOrbDisplay("fadc4a024718d401eeae9e95b3c92767f916f323c9e83649ad15c9265ee5092f");
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    stand.setHeadPose(new EulerAngle(0, 0.4, 0));
                    break;
                case SCARED:
                    try {
                        setOrbDisplay("94693e3705467d2b34f186fe8abc8f4b20b28e52a95b5a525107eefa5881ca10");
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    stand.setHeadPose(new EulerAngle(0, -0.4, 0));
                    break;
                default:
                    try {
                        setOrbDisplay("46994d71b875f087e64dea9b4a0a5cb9f4eb9ab0e8d9060dfde7f6803baa1779");
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    stand.setHeadPose(new EulerAngle(0, 0, 0));
                    break;
            }
        });
    }

    public OrbEmotion getCurrentEmotion() {
        return currentEmotion;
    }

    public void remove() {
        if (mainTask != null) {
            mainTask.cancel();
        }
        if (stand != null && stand.isValid()) {
            stand.remove();
        }
        stands.remove(owner);
        plugin.orbsManager.removeOrb(owner, this);
    }
}
