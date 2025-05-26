package gambling.utils

import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.World

object LocationParser {

    /**
     * Parses a location string into a Bukkit Location object.
     * Expected format: "worldName,x,y,z" or "worldName,x,y,z,yaw,pitch"
     * Logs an error and returns null if parsing fails or world doesn't exist.
     */
    fun parseLocation(locationString: String, server: Server): Location? {
        val parts = locationString.split(',')
        if (parts.size < 4 || parts.size > 6) {
            server.logger.warning("[LocationUtils] Invalid location string format: \"$locationString\". Expected 4 or 6 parts.")
            return null
        }

        val worldName = parts[0]
        val world: World? = server.getWorld(worldName)
        if (world == null) {
            server.logger.warning("[LocationUtils] World \"$worldName\" not found for location string: \"$locationString\".")
            return null
        }

        return try {
            val x = parts[1].toDouble()
            val y = parts[2].toDouble()
            val z = parts[3].toDouble()
            val yaw = if (parts.size > 4) parts[4].toFloat() else 0.0f
            val pitch = if (parts.size > 5) parts[5].toFloat() else 0.0f
            Location(world, x, y, z, yaw, pitch)
        } catch (e: NumberFormatException) {
            server.logger.warning("[LocationUtils] Error parsing coordinates/rotation in location string: \"$locationString\". ${e.message}")
            null
        }
    }

    /**
     * Serializes a Bukkit Location object into a string.
     * Format: "worldName,x,y,z,yaw,pitch"
     */
    fun serializeLocation(location: Location): String {
        return "${location.world?.name},${location.x},${location.y},${location.z},${location.yaw},${location.pitch}"
    }
}
