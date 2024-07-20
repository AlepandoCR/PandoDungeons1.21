package pandodungeons.pandodungeons.Utils;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator {

    @Override
    public @NotNull ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        // Crear y devolver un ChunkData vacío (todos los bloques serán aire por defecto)
        return createChunkData(world);
    }
}
