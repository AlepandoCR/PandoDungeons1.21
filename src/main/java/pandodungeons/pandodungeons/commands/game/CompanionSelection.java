package pandodungeons.pandodungeons.commands.game;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import pandodungeons.pandodungeons.Utils.CompanionUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class CompanionSelection implements CommandExecutor {

    private final JavaPlugin plugin;

    public CompanionSelection(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        openMenu(player);

        return true;
    }

    private static ItemStack createCompanionHead(String companionType, int level) throws MalformedURLException {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // Obtener la textura del companion según el tipo
        String texture;
        switch (companionType.toLowerCase()) {
            case "allay":
                texture = "5475ea53649f914a17fe74cab59fb4f1508a53ff66d741d22430c3c84ef5430c";
                break;
            case "breeze":
                texture = "80843a94f925b5598924ff9b52b7999c8d29d1b790ad487dd54e27956e540d20";
                break;
            case "armadillo":
                texture = "404d9164e8f76dcdde5f8d10ff67deb2d658a6008d23c44fb2f844e08591c01d";
                break;
            case "oso":
                texture = "cbe8fa7f6651302712b233949552f3a6893d06d8e56894f4c4fee305722ba447";
                break;
            default:
                return null; // Manejar caso si el tipo de companion no es reconocido
        }

        // Crear perfil y texturas del jugador
        PlayerProfile profile = Bukkit.createProfile(companionType.toLowerCase());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(new URL("https://textures.minecraft.net/texture/" + texture)); // Establecer la textura de la piel del companion
        profile.setTextures(textures);

        meta.setPlayerProfile(profile);
        meta.setCustomModelData(420);
        meta.setDisplayName(ChatColor.RESET.toString() + ChatColor.WHITE + ChatColor.BOLD + companionType.toUpperCase(Locale.ROOT)); // Nombre del companion
        meta.setLore(Collections.singletonList(ChatColor.RESET.toString() + ChatColor.DARK_AQUA + ChatColor.BOLD + "Nivel: " + ChatColor.RESET + ChatColor.GOLD + level)); // Lore con el nivel del companion
        head.setItemMeta(meta);
        return head;
    }

    public static void openMenu(Player player){
        // Obtener los companions desbloqueados del jugador
        Map<String, Integer> unlockedCompanions = CompanionUtils.getUnlockedCompanions(player);

        // Crear un inventario para mostrar los companions desbloqueados
        Inventory companionMenu = Bukkit.createInventory(player, 9, (ChatColor.BOLD + "Selecciona tu Compañero"));

        // Colocar ítems en el inventario para cada companion desbloqueado
        for (Map.Entry<String, Integer> entry : unlockedCompanions.entrySet()) {
            String companionType = entry.getKey();
            int level = entry.getValue();

            // Crear ItemStack de cabeza de jugador con la textura del companion
            ItemStack companionHead;
            try {
                companionHead = createCompanionHead(companionType, level);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            // Agregar el ítem al inventario
            if (companionHead != null) {
                companionMenu.addItem(companionHead);
            }
        }

        // Abrir el inventario para el jugador
        player.openInventory(companionMenu);
    }
}
