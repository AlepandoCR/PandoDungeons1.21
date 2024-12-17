package pandodungeons.pandodungeons.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pandodungeons.pandodungeons.PandoDungeons;

import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerParty {
    private Player owner;
    private List<Player> members;
    private final PandoDungeons plugin = PandoDungeons.getPlugin(PandoDungeons.class);
    private HashMap<Player, Player> pendingInvitations; // Almacena las invitaciones pendientes <jugador, owner>

    /**
     * Constructor para crear una nueva party con un jugador como owner.
     *
     * @param owner El jugador que será el owner de la party.
     */
    public PlayerParty(Player owner) {
        this.owner = owner;
        this.members = new ArrayList<>();
        this.members.add(owner); // El owner es miembro automáticamente.
        this.pendingInvitations = new HashMap<>();
    }

    /**
     * Verifica si un jugador es el owner de la party.
     *
     * @param player El jugador a verificar.
     * @return true si el jugador es el owner, de lo contrario false.
     */
    public boolean isOwner(Player player) {
        return owner.equals(player);
    }

    /**
     * Agrega un nuevo miembro a la party.
     *
     * @param player El jugador a agregar.
     * @return true si el jugador fue agregado, false si ya es miembro.
     */
    public boolean addMember(Player player) {
        if (!members.contains(player)) {
            return members.add(player);
        }
        return false;
    }

    public boolean invitePlayer(Player player, JavaPlugin plugin) {
        if (members.contains(player)) {
            return false; // El jugador ya es miembro
        }

        // Almacenar la invitación pendiente
        pendingInvitations.put(player, this.owner);

        // Enviar mensaje en el action bar
        player.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + owner.getName() + ChatColor.GOLD + " te ha invitado a su party.");

        // Crear los mensajes para aceptar o rechazar la invitación
        Component acceptMessage = Component.text(ChatColor.GREEN + "[Click para aceptar] ")
                .color(NamedTextColor.GOLD)
                .clickEvent(ClickEvent.runCommand("/party accept"))
                .hoverEvent(HoverEvent.showText(Component.text("Haz clic para aceptar la invitación.")));

        Component rejectMessage = Component.text(ChatColor.RED +"[Click para rechazar] ")
                .color(NamedTextColor.RED)
                .clickEvent(ClickEvent.runCommand("/party deny"))
                .hoverEvent(HoverEvent.showText(Component.text("Haz clic para rechazar la invitación.")));

        // Enviar los mensajes
        player.sendMessage(acceptMessage.append(Component.text(" ")).append(rejectMessage));

        // Configurar listeners para el clic
        new BukkitRunnable() {
            @Override
            public void run() {
                // Verificar si la invitación aún es válida
                if (pendingInvitations.containsKey(player)) {
                    pendingInvitations.remove(player);
                    owner.sendMessage(player.getName() + " no respondió a la invitación.");
                }
            }
        }.runTaskLater(plugin, 20 * 60L); // 60 segundos para responder

        return true;
    }

    /**
     * Maneja la respuesta del jugador a la invitación.
     *
     * @param player El jugador que responde.
     * @param accept true si acepta la invitación, false si la rechaza.
     */
    public void handleInvitationResponse(Player player, boolean accept) {
        if(pendingInvitations.containsKey(player)){
            Player ownerPlayer = pendingInvitations.get(player);

            if (accept) {
                addMember(player);
                player.sendMessage("Te has unido a la party de " + ownerPlayer.getName() + ".");
                ownerPlayer.sendMessage(player.getName() + " se ha unido a tu party.");
            } else {
                player.sendMessage("Has rechazado la invitación.");
                ownerPlayer.sendMessage(player.getName() + " ha rechazado tu invitación.");
            }

            pendingInvitations.remove(player);
        }else{
            player.sendMessage(ChatColor.RED + "No tienes invitaciones pendientes");
        }
    }

    /**
     * Remueve un miembro de la party.
     *
     * @param player El jugador a remover.
     * @return true si el jugador fue removido, false si no es miembro.
     */
    public boolean removeMember(Player player) {
        if (members.contains(player)) {
            return members.remove(player);
        }
        return false;
    }

    /**
     * Verifica si un jugador es miembro de la party.
     *
     * @param player El jugador a verificar.
     * @return true si el jugador es miembro, false si no lo es.
     */
    public boolean isMember(Player player) {
        return members.contains(player);
    }

    /**
     * Obtiene el owner de la party.
     *
     * @return El owner de la party.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Obtiene la lista de miembros de la party.
     *
     * @return Una lista con los jugadores miembros de la party.
     */
    public List<Player> getMembers() {
        return new ArrayList<>(members);
    }

    /**
     * Disuelve la party, removiendo a todos los miembros.
     */
    public void disbandParty() {
        members.clear();
    }

    /**
     * Cambia el owner de la party.
     *
     * @param newOwner El nuevo owner de la party.
     */
    public void setOwner(Player newOwner) {
        if (members.contains(newOwner)) {
            this.owner = newOwner;
        }
    }



}
