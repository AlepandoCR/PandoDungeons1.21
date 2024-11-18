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

    public static void spawnTable(Location center) {
        World world = center.getWorld();
        center.setYaw(0);
        center.setPitch(0);
        // Crear las patas (cuatro palos inclinados)
        float offset = 0.3f; // Desplazamiento desde el centro para las patas
        float height = 1.0f; // Altura de las patas
        Material legMaterial = Material.OAK_LOG; // Material de las patas (usamos OAK_FENCE en vez de STICK)

        // Pata 1 (inclinada hacia afuera /)
        placeTable(world, center.clone().add(offset+0.5, -height+0.8, offset-0.8), legMaterial, new Vector3f(0.1f, height, 0.1f), Optional.of(new AxisAngle4f((float) Math.toRadians(40), 1, 0, 0)));

        // Pata 2 (inclinada hacia afuera \)
        placeTable(world, center.clone().add(-offset+0.5, -height+0.75, offset+1), legMaterial, new Vector3f(0.1f, height, 0.1f), Optional.of(new AxisAngle4f((float) Math.toRadians(-40), 1, 0, 0)));

        // Pata 3 (inclinada hacia afuera /)
        placeTable(world, center.clone().add(-offset+0.4, -height+0.8, offset-0.8), legMaterial, new Vector3f(0.1f, height, 0.1f), Optional.of(new AxisAngle4f((float) Math.toRadians(40), 1, 0, 0)));

        // Pata 4 (inclinada hacia afuera \)
        placeTable(world, center.clone().add(offset+0.5, -height+0.75, offset+1), legMaterial, new Vector3f(0.1f, height, 0.1f), Optional.of(new AxisAngle4f((float) Math.toRadians(-40), 1, 0, 0)));

        // Crear la parte superior (slabs de madera)
        Material topMaterial = Material.OAK_SLAB; // Material de la parte superior
        float slabHeight = 0.2f; // Altura de los slabs

        // Colocar slabs (en el nivel superior de la mesa)
        placeTable(world, center.clone().add(0, 0.5, 0), topMaterial, new Vector3f(1.0f, slabHeight, 1.0f), Optional.empty()); // Bloque principal de la mesa
    }


    public static void placeTable(World world, Location location, Material material, Vector3f scale, Optional<AxisAngle4f> rotation) {
        BlockDisplay display = world.spawn(location, BlockDisplay.class);
        BlockData blockData = material.createBlockData();
        display.setPersistent(true);
        display.setNoPhysics(true);
        display.setNoPhysics(true);
        display.setBlock(blockData);
        display.setBillboard(Billboard.FIXED); // No mirar hacia el jugador

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
