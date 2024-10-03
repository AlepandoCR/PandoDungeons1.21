package pandodungeons.pandodungeons.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.BlockDisplay;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.bukkit.util.Transformation;

import java.util.Optional;

public class DisplayModels {

    public static void createMiniCrystal(World world, Location center, Material material) {
        // Capa inferior - Bloque base
        placeBlock(world, center.clone().add(0, -0.4, 0), material, new Vector3f(0.4f, 0.4f, 0.4f), Optional.empty());

        // Puntas laterales - más pequeñas y compactas (Capa media)
        placeBlock(world, center.clone().add(0, 0.0, 0), material, new Vector3f(0.2f, 0.5f, 0.2f), Optional.of(new AxisAngle4f((float) Math.toRadians(45), 1, 1, 1))); // Puntas laterales
        placeBlock(world, center.clone().add(0, 0.0, 0), material, new Vector3f(0.2f, 0.5f, 0.2f), Optional.of(new AxisAngle4f((float) Math.toRadians(-45), 1, 1, 1)));

        placeBlock(world, center.clone().add(0, 0.0, 0), material, new Vector3f(0.2f, 0.5f, 0.2f), Optional.of(new AxisAngle4f((float) Math.toRadians(45), -1, -1, -1)));
        placeBlock(world, center.clone().add(0, 0.0, 0), material, new Vector3f(0.2f, 0.5f, 0.2f), Optional.of(new AxisAngle4f((float) Math.toRadians(-45), -1, -1, -1)));

        // Puntas superiores (Capa superior)
        placeBlock(world, center.clone().add(0, 0.3, 0), material, new Vector3f(0.2f, 0.6f, 0.2f), Optional.empty());
    }

    public static void placeBlock(World world, Location location, Material material, Vector3f scale, Optional<AxisAngle4f> rotation) {
        location.setYaw(0);
        location.setPitch(0);
        BlockDisplay display = world.spawn(location, BlockDisplay.class);
        BlockData blockData = material.createBlockData();
        display.setBlock(blockData);
        display.setBillboard(Billboard.FIXED);

        // Crear transformación con escala y rotación opcional
        Transformation transformation = new Transformation(
                new Vector3f(0, 0, 0), // Traslación (posición inicial)
                rotation.map(AxisAngle4f::new).orElse(new AxisAngle4f(0, 0, 1, 0)), // Rotación inicial
                scale, // Escala del bloque
                new AxisAngle4f(0, 0, 1, 0) // Rotación final (por defecto no rotar más)
        );

        display.setTransformation(transformation);
    }
}
