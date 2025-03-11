package textures;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pandoClass.RPGPlayer;
import pandoToros.Commands.PlayRedondelCommand;
import pandoToros.Commands.ToroStatsCommand;
import pandodungeons.pandodungeons.Game.Stats;
import pandodungeons.pandodungeons.PandoDungeons;

import javax.swing.*;
import java.util.List;

import static pandoToros.Commands.ToroStatsCommand.ToroCommandStats;

public class TextureCommand implements CommandExecutor, TabCompleter {

    private PandoDungeons plugin;

    public TextureCommand(PandoDungeons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("texturas")) {
            // URL del texture pack - usa el enlace raw para que sea un enlace directo
            String texturePackUrl = "https://raw.githubusercontent.com/AlepandoCR/MapachoTextura/main/mapachos.zip";
            if(commandSender instanceof Player player){

                if(strings.length == 1){
                    if(strings[0].equalsIgnoreCase("aplicar")){
                        applyPack(player,texturePackUrl);
                    }else if(strings[0].equalsIgnoreCase("descargar")){
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "Enlace de descarga: " + ChatColor.GOLD + texturePackUrl);
                    }
                    if(strings[0].equalsIgnoreCase("mantener")){
                        new RPGPlayer(player).setTexturePack(true);
                        applyPack(player,texturePackUrl);
                    }
                    if(strings[0].equalsIgnoreCase("eliminar")){
                        new RPGPlayer(player).setTexturePack(false);
                    }
                }

            }
        }


        return true;
    }

    public void applyPack(Player player, String url){
        // Opcional: crea un mensaje usando Component (si usas la API Adventure)
        Component prompt = Component.text("¡Descarga el texture pack para una experiencia completa!");

        // Solicita el texture pack al cliente, forzando su aplicación
        player.setResourcePack(url, null, prompt, true);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new java.util.ArrayList<>(List.of());
        if(strings.length == 1){
            completions.add("aplicar");
            completions.add("descargar");
            completions.add("mantener");
            completions.add("eliminar");
        }
        return completions;
    }
}
