package pandoToros.game;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static pandoToros.utils.PlayerArmorChecker.hasArmor;

public class ArenaMaker {

    private static final String ORIGINAL_WORLD_NAME = "redondel";
    private static final String WORLD_PREFIX = "redondel_";

    /**
     * Verifica si un mundo es un "redondel".
     *
     * @param worldName Nombre del mundo a verificar.
     * @return true si el mundo es un "redondel", false en caso contrario.
     */
    public static boolean isRedondelWorld(String worldName) {
        return worldName.startsWith(WORLD_PREFIX);
    }

    public static World createRedondelWorld(String creator) {
        String newWorldName = WORLD_PREFIX + creator;
        File source = new File(Bukkit.getWorldContainer(), ORIGINAL_WORLD_NAME);
        File destination = new File(Bukkit.getWorldContainer(), newWorldName);

        // Verificar si el mundo ya existe
        World existingWorld = Bukkit.getWorld(newWorldName);
        if (existingWorld != null) {
            Bukkit.getLogger().info("El mundo ya existe: " + newWorldName);
            return existingWorld;
        }

        if (destination.exists()) {
            Bukkit.getLogger().info("La carpeta del mundo ya existe: " + newWorldName);
            WorldCreator worldCreator = new WorldCreator(newWorldName);
            return Bukkit.createWorld(worldCreator);
        }

        try {
            Bukkit.unloadWorld(newWorldName, true); // true guarda los datos del mundo antes de descargarlo
            copyWorldFolder(source, destination);
        } catch (IOException e) {
            throw new RuntimeException("Error al copiar el mundo: " + e.getMessage(), e);
        }

        WorldCreator worldCreator = new WorldCreator(newWorldName);
        return Bukkit.createWorld(worldCreator);
    }





    /**
     * Copia el contenido de una carpeta a otra, omitiendo el archivo `session.lock`.
     *
     * @param source      Directorio de origen.
     * @param destination Directorio de destino.
     * @throws IOException Si ocurre un error durante la copia.
     */
    public static void copyWorldFolder(File source, File destination) throws IOException {
        Files.walk(source.toPath())
                .forEach(sourcePath -> {
                    Path targetPath = destination.toPath().resolve(source.toPath().relativize(sourcePath));
                    try {
                        String fileName = sourcePath.getFileName().toString();
                        if ("session.lock".equals(fileName) || "uid.dat".equals(fileName)) return;
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (FileAlreadyExistsException ignored) {
                        // Ignore if the file already exists
                    } catch (IOException e) {
                        throw new RuntimeException("Error while copying file: " + sourcePath, e);
                    }
                });

        // Verificar y eliminar uid.dat
        Path uidFile = destination.toPath().resolve("uid.dat");
        System.out.println("Intentando eliminar: " + uidFile.toAbsolutePath());
        if (Files.exists(uidFile)) {
            try {
                Files.delete(uidFile);
                System.out.println("Archivo uid.dat eliminado correctamente.");
            } catch (IOException e) {
                System.err.println("No se pudo eliminar uid.dat: " + e.getMessage());
            }
        } else {
            System.out.println("El archivo uid.dat no existe.");
        }
    }





    /**
     * Elimina un mundo "redondel" del servidor.
     *
     * @param worldName Nombre del mundo a eliminar.
     * @return true si se eliminó correctamente, false en caso contrario.
     */
    public static boolean deleteRedondelWorld(String worldName) {
        if (!isRedondelWorld(worldName)) {
            Bukkit.getLogger().warning("El mundo " + worldName + " no es un redondel.");
            return false;
        }

        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            // Descargar el mundo
            Bukkit.unloadWorld(world, false);
        }

        // Eliminar la carpeta del mundo
        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        try {
            deleteWorldFolder(worldFolder);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().severe("Error al eliminar el mundo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Descarga un mundo si está cargado.
     *
     * @param worldName Nombre del mundo a descargar.
     * @return true si el mundo fue descargado, false si no estaba cargado.
     */
    public static boolean unloadWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            return Bukkit.unloadWorld(world, true); // Guarda los cambios antes de descargar
        }
        return false;
    }



    /**
     * Elimina un directorio de forma recursiva.
     *
     * @param folder Directorio a eliminar.
     * @throws IOException Si ocurre un error durante la eliminación.
     */
    private static void deleteWorldFolder(File folder) throws IOException {
        if (folder.exists()) {
            Files.walk(folder.toPath())
                    .sorted((a, b) -> b.compareTo(a)) // Eliminar en orden inverso
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Error al eliminar archivo: " + path.toString(), e);
                        }
                    });
        }
    }
}
