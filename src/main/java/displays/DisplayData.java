package displays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.UUID;
import java.util.function.Supplier;

public class DisplayData {
    private final Supplier<String> dataSupplier;
    private final UUID playerUUID;
    private ItemDisplay headDisplay;
    private TextDisplay textDisplay;

    public DisplayData(Supplier<String> dataSupplier, UUID playerUUID) {
        this.dataSupplier = dataSupplier;
        this.playerUUID = playerUUID;
    }

    public void generateDisplays(@NotNull Location location, float scale, int positionIndex) {
        double spacing = positionIndex == 0 ? 0 : positionIndex*2;

        Location newLoc = location.clone().setRotation(0,0);
        Location headLoc = newLoc.clone().add(0, -spacing * (0.6 * scale), 0);
        Location textLoc = headLoc.clone().add(0, -1 * scale, 0);

        // Generar ItemStack de cabeza
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));
        skull.setItemMeta(meta);

        headDisplay = location.getWorld().spawn(headLoc, ItemDisplay.class, display -> {
            display.setItemStack(skull);
            display.setBillboard(Display.Billboard.CENTER);
            display.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0), // offset de posición
                    new AxisAngle4f((float) Math.toRadians(180), 0f, 1f, 0f), // rotación Y 180°
                    new Vector3f(scale, scale, scale), // escala
                    new AxisAngle4f(0, 0, 0, 0) // rotación posterior
            ));
        });

        if (positionIndex == 0) {
            headDisplay.setGlowing(true);
            headDisplay.setGlowColorOverride(org.bukkit.Color.fromRGB(255, 215, 0)); // oro
        } else if (positionIndex == 1) {
            headDisplay.setGlowing(true);
            headDisplay.setGlowColorOverride(org.bukkit.Color.fromRGB(192, 192, 192)); // plata
        } else if (positionIndex == 2) {
            headDisplay.setGlowing(true);
            headDisplay.setGlowColorOverride(org.bukkit.Color.fromRGB(184, 115, 51)); // cobre
        }

        textDisplay = location.getWorld().spawn(textLoc, TextDisplay.class, display -> {
            display.setText(dataSupplier.get());
            display.setBillboard(Display.Billboard.CENTER);
            display.setShadowed(true);
            display.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new AxisAngle4f(0, 0, 0, 0),
                    new Vector3f(scale, scale, scale),
                    new AxisAngle4f(0, 0, 0, 0)
            ));
        });
    }

    public void update() {
        if (textDisplay != null) {
            textDisplay.setText(dataSupplier.get());
        }
    }

    public void updateHead(){
       headDisplay.setItemStack(HeadUtils.getPlayerHead(playerUUID));
    }

    public void setScale(float scale) {
        if (headDisplay != null) headDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new org.joml.Vector3f(scale, scale, scale), new AxisAngle4f()));
        if (textDisplay != null) textDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new org.joml.Vector3f(scale, scale, scale), new AxisAngle4f()));
    }

    public void remove(){
        textDisplay.remove();
        headDisplay.remove();
    }
}
