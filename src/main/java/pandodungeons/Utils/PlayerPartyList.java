package pandodungeons.Utils;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PlayerPartyList {

    public final List<PlayerParty> parties;

    public PlayerPartyList() {
        this.parties = new ArrayList<>();
    }

    /**
     * Crea una nueva party y la añade a la lista.
     *
     * @param owner El jugador que será el dueño de la nueva party.
     * @return La nueva PlayerParty creada.
     */
    public PlayerParty createParty(Player owner) {
        PlayerParty newParty = new PlayerParty(owner);
        parties.add(newParty);
        return newParty;
    }

    /**
     * Obtiene una party por su dueño.
     *
     * @param owner El dueño de la party que se busca.
     * @return La PlayerParty correspondiente al dueño, o null si no existe.
     */
    public PlayerParty getPartyByOwner(Player owner) {
        for (PlayerParty party : parties) {
            if (party.getOwner().equals(owner)) {
                return party;
            }
        }
        return null;
    }

    /**
     * Elimina una party de la lista.
     *
     * @param party La PlayerParty a eliminar.
     */
    public void removeParty(PlayerParty party) {
        parties.remove(party);
    }

    /**
     * Elimina una party de la lista basándose en su dueño.
     *
     * @param owner El dueño de la PlayerParty a eliminar.
     */
    public void removePartyByOwner(Player owner) {
        PlayerParty party = getPartyByOwner(owner);
        if (party != null) {
            parties.remove(party);
        }
    }

    /**
     * Obtiene todas las parties activas.
     *
     * @return Una lista de todas las PlayerParty activas.
     */
    public List<PlayerParty> getAllParties() {
        return new ArrayList<>(parties);
    }

    /**
     * Verifica si un jugador es dueño de alguna party.
     *
     * @param owner El jugador a verificar.
     * @return true si el jugador es dueño de una party, false en caso contrario.
     */
    public boolean isOwner(Player owner) {
        return getPartyByOwner(owner) != null;
    }

    /**
     * Verifica si un jugador es miembro de alguna party.
     *
     * @param player El jugador a verificar.
     * @return true si el jugador es miembro de una party, false en caso contrario.
     */
    public boolean isMember(Player player) {
        for (PlayerParty party : parties) {
            if (party.getMembers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Busca la party en la que un jugador es miembro.
     *
     * @param player El jugador a buscar.
     * @return La PlayerParty en la que el jugador es miembro, o null si no es miembro de ninguna.
     */
    @Nullable
    public PlayerParty getPartyByMember(Player player) {
        if(player == null){
            return null;
        }
        for (PlayerParty party : parties) {
            if (party.getMembers().contains(player)) {
                return party;
            }
        }
        return null;
    }
}
