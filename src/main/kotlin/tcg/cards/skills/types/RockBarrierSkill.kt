package tcg.cards.skills.types

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import pandodungeons.PandoDungeons
import tcg.cards.engine.CardRarity
import tcg.cards.skills.engine.CardSkill
import tcg.util.text.Description
import tcg.util.text.SkillDescription
import kotlin.random.Random

class RockBarrierSkill(
    plugin: PandoDungeons,
    rarity: CardRarity
) : CardSkill(plugin, rarity) {

    override fun startCondition(): Boolean = true
    override fun shouldWaitForCondition(): Boolean = false

    override fun setSkillType(): String = "rock_barrier"

    override fun setDescription(): Description {
        return SkillDescription("Levanta un muro de piedra temporal", rarity)
    }

    override fun task() {
        if (!hasPlayer()) return
        val player = getDummyPlayer()
        val world = player.world

        val wallLength = 5 + Random.nextInt(5)
        val wallHeight = 5 + Random.nextInt(5)
        val wallDuration = 5 * 20L // 5 seconds
        val wallMaterial = randomWallMaterial()

        // Determine wall placement: In front of player, perpendicular to their direction.
        val startLocation = player.location.add(player.location.direction.multiply(2.0)).block.location
        startLocation.y = player.location.blockY.toDouble() // Start at player's foot level or one block up.

        // Get the direction perpendicular to player's facing for wall orientation
        val direction = player.location.direction.normalize()
        val perpendicularDirection = direction.clone().crossProduct(BlockFace.UP.direction).normalize()
        if (perpendicularDirection.lengthSquared() < 0.1) { // If looking straight up/down, use X-axis
            perpendicularDirection.setX(1.0).setY(0.0).setZ(0.0)
        }


        val originalBlocks = mutableMapOf<Location, BlockData>()
        val wallBlocks = mutableListOf<Location>()

        player.world.playSound(startLocation, Sound.BLOCK_STONE_PLACE, 1.0f, 0.8f)
        player.world.playSound(startLocation, Sound.BLOCK_ROOTED_DIRT_PLACE, 1.0f, 1.2f)


        buildWall(
            wallHeight,
            wallLength,
            startLocation,
            perpendicularDirection,
            world,
            originalBlocks,
            wallMaterial,
            wallBlocks
        )
        
        if (wallBlocks.isEmpty()){
             player.sendMessage("No puedes colocar un muro aquí")
             return
        }

        object : BukkitRunnable() {
            override fun run() {
                for (loc in wallBlocks) {
                    val originalData = originalBlocks[loc]
                    if (originalData != null) {
                        world.getBlockAt(loc).blockData = originalData
                    } else {
                        // Should not happen if logic is correct, but as a fallback:
                        world.getBlockAt(loc).type = Material.AIR
                    }
                }
                world.playSound(startLocation, Sound.BLOCK_STONE_BREAK, 1.0f, 0.8f)
            }
        }.runTaskLater(plugin, wallDuration)
    }

    private fun buildWall(
        wallHeight: Int,
        wallLength: Int,
        startLocation: Location,
        perpendicularDirection: Vector,
        world: World,
        originalBlocks: MutableMap<Location, BlockData>,
        wallMaterial: Material,
        wallBlocks: MutableList<Location>
    ) {
        for (h in 0 until wallHeight) { // Height
            for (l in (-wallLength / 2) until (wallLength / 2 + wallLength % 2)) { // Length
                val blockLocation = startLocation.clone()
                    .add(perpendicularDirection.clone().multiply(l.toDouble()))
                    .add(0.0, h.toDouble(), 0.0)

                val block = world.getBlockAt(blockLocation)

                // Only replace air, grass, water, or other replaceable blocks
                if (block.type.isAir || block.isLiquid || block.type == Material.GRASS_BLOCK || block.type == Material.SNOW || block.type == Material.FERN) {
                    if (!originalBlocks.containsKey(block.location)) { // Avoid overwriting already stored blocks
                        originalBlocks[block.location.clone()] = block.blockData.clone()
                    }
                    block.type = wallMaterial
                    wallBlocks.add(block.location.clone())
                }
            }
        }
    }

    private fun randomWallMaterial(): Material {
        val wallMaterials = listOf(
            Material.STONE_BRICKS,
            Material.MOSSY_STONE_BRICKS,
            Material.CRACKED_STONE_BRICKS,
            Material.DEEPSLATE_BRICKS,
            Material.POLISHED_BLACKSTONE_BRICKS,
            Material.COBBLESTONE,
            Material.MOSSY_COBBLESTONE,
            Material.ANDESITE,
            Material.POLISHED_ANDESITE,
            Material.TUFF,
            Material.CALCITE,
            Material.BASALT
        )

        return wallMaterials.random()
    }

}
