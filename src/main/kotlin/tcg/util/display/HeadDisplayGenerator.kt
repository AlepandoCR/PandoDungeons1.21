package tcg.util.display

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Display
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import java.net.URL
import java.util.*

object HeadDisplayGenerator {

    fun spawnDisplayHead(textureUrl: String, location: Location, scale: Double = 1.0): ItemDisplay{
        val headDisplay: ItemDisplay?

        val skull = ItemStack(Material.PLAYER_HEAD)
        val meta = skull.itemMeta as SkullMeta

        val scaleF = scale.toFloat()

        val profile = Bukkit.createProfile(UUID.randomUUID())
        val textures = profile.textures
        textures.skin = URL("https://textures.minecraft.net/texture/$textureUrl")
        profile.setTextures(textures)
        meta.playerProfile = profile

        skull.setItemMeta(meta)


        headDisplay = location.world.spawn(
            location,
            ItemDisplay::class.java
        ) { display: ItemDisplay ->
            display.setItemStack(skull)
            display.billboard = Display.Billboard.CENTER
            display.addScoreboardTag("pando.display")
            display.transformation = Transformation(
                Vector3f(0f, 0f, 0f),
                AxisAngle4f(Math.toRadians(180.0).toFloat(), 0f, 1f, 0f),
                Vector3f(scaleF, scaleF, scaleF),
                AxisAngle4f(0f, 0f, 0f, 0f)
            )
        }

        return headDisplay
    }
}