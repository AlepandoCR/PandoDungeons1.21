package pandoClass.golem;

import org.bukkit.Location;
import pandoClass.golem.limb.Limb;
import pandoClass.golem.limbs.Arm;
import pandoClass.golem.limbs.Chest;
import pandoClass.golem.limbs.Leg;
import pandodungeons.pandodungeons.PandoDungeons;

import java.util.ArrayList;
import java.util.List;

public class Golem {
    private Arm brazoDerecho, brazoIzquierdo;
    private Leg rightLeg, leftLeg;
    private Chest chest;
    private List<Limb> extremidades = new ArrayList<>();

    public Golem(Location spawnLocation, PandoDungeons plugin) {
        this.brazoDerecho = new Arm(spawnLocation.clone().add(1, 2, 0),plugin);
        this.brazoIzquierdo = new Arm(spawnLocation.clone().add(-1, 2, 0),plugin);

        extremidades.add(brazoDerecho);
        extremidades.add(brazoIzquierdo);
    }

    public void actualizarMovimientos() {
        for (Limb extremidad : extremidades) {
            extremidad.move();
        }
    }
}
