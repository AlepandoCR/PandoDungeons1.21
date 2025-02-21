package pandodungeons.pandodungeons.CustomEntities.Companions.Types;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.jetbrains.annotations.NotNull;
import pandodungeons.pandodungeons.CustomEntities.Companions.PufferFish.GuardianBeamAttackGoal;
import pandodungeons.pandodungeons.CustomEntities.Companions.PufferFish.HoverAndFollowPlayerGoal;

import java.util.Objects;

public class CompanionPufferfishBehavior extends Pufferfish {
    private final int lvl;

    public CompanionPufferfishBehavior(EntityType<? extends Pufferfish> type, Level world, int lvl) {
        super(type, world);
        this.lvl = lvl;
        this.setBaby(false);

        if (this.getAttribute(Attributes.ATTACK_DAMAGE) == null) {
            this.getAttributes().registerAttribute(Attributes.ATTACK_DAMAGE);
            Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(lvl * 2);
        }
    }

    @Override
    public void setPuffState(int puffState) {
        // Forzar siempre que el estado de inflado sea 1
        super.setPuffState(1);
    }

    @Override
    public void tick(){
        this.setPuffState(1);
        super.tick();
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(1, new HoverAndFollowPlayerGoal(this, 0.5,3,0,1));

        this.goalSelector.addGoal(0, new GuardianBeamAttackGoal(this, lvl));

        //this.goalSelector.addGoal(2, new HoverAndFollowPlayerGoal(this, 1.0D, 2.0F, 1.0F,1.5));
    }

    @Override
    public void playerTouch(@NotNull Player player) {
        int i = this.getPuffState();
        if (player instanceof ServerPlayer && i > 0 && player.hurtOrSimulate(this.damageSources().mobAttack(this), (float)(1 + i))) {
            if (!this.isSilent()) {
                ((ServerPlayer)player).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0F));
            }

            player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 300 * i, lvl/10), this, EntityPotionEffectEvent.Cause.ATTACK);
        }

    }
}



