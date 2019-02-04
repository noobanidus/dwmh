package com.noobanidus.dwmh.proxy.steeds;

import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityMountEvent;

import javax.annotation.Nullable;

public interface ISteedProxy {
    boolean isTeleportable(Entity entity, EntityPlayer player);

    default boolean hasCustomName(Entity entity) {
        return entity.hasCustomName();
    }

    default String getCustomNameTag(Entity entity) {
        return entity.getCustomNameTag();
    }

    default void setCustomNameTag(Entity entity, String name) {
        entity.setCustomNameTag(name);
    }

    default boolean globalTeleportCheck(Entity entity, EntityPlayer player) {
        EntityAnimal animal = (EntityAnimal) entity;

        if (animal.getLeashed() || (animal.isBeingRidden() && animal.isRidingSameEntity(player))) {
            return false;
        }

        // And prevent you from summoning animals being ridden by other players
        if (animal.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            return false;
        }

        // Compatibility for Horse Power device-attached animals
        if (animal.hasHome() && animal.world.getTileEntity(animal.getHomePosition()) != null) {
            return false;
        }

        return true;
    }

    boolean isListable(Entity entity, EntityPlayer player);

    // Carrot
    boolean isTameable(Entity entity, EntityPlayer player);

    void tame(Entity entity, EntityPlayer player);

    boolean isAgeable(Entity entity, EntityPlayer player);

    void age(Entity entity, EntityPlayer player);

    default boolean isHealable(Entity entity, EntityPlayer player) {
        if (isMyMod(entity)) {
            EntityLiving horse = (EntityLiving) entity;
            if (horse.getHealth() < horse.getMaxHealth()) return true;

            return false;
        }

        return false;
    }

    default void heal(Entity entity, EntityPlayer player) {
        EntityLiving horse = (EntityLiving) entity;
        horse.heal(horse.getMaxHealth() - horse.getHealth());
        horse.world.setEntityState(horse, (byte) 7);

        if (DWMHConfig.EnchantedCarrot.messages.healing) {
            doGenericMessage(entity, player, Generic.HEALING);
        }
    }

    // Not currently implemented
    boolean isBreedable(Entity entity, EntityPlayer player);

    void breed(Entity entity, EntityPlayer player);

    ITextComponent getResponseKey(Entity entity, EntityPlayer player);

    default boolean isLoaded() {
        return true;
    }

    default boolean isMyMod(Entity entity) {
        return false;
    }

    default String proxyName() {
        return "default";
    }

    default ITextComponent getEntityTypeName(Entity entity, EntityPlayer player) {
        String entityKey = resolveEntityKey(String.format("entity.%s.name", EntityList.getEntityString(entity)));
        return new TextComponentTranslation(entityKey);
    }

    default String resolveEntityKey(String key) {
        return key;
    }

    default boolean onDismount(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer && isMyMod(event.getEntityBeingMounted()) && DWMHConfig.Ocarina.home && !DWMHConfig.Ocarina.skipDismount) {
            EntityCreature entity = (EntityCreature) event.getEntityBeingMounted();
            entity.detachHome();
            // DWMH.LOG.info("Removed home for " + entity.getDisplayName());
            return true;
        }

        return false;
    }

    default void doGenericMessage(Entity entity, EntityPlayer player, String keyOverride) {
        doGenericMessage(entity, player, Generic.EMPTY, keyOverride, null);
    }

    default void doGenericMessage(Entity entity, EntityPlayer player, String keyOverride, TextFormatting format) {
        doGenericMessage(entity, player, Generic.EMPTY, keyOverride, format);
    }

    default void doGenericMessage(Entity entity, EntityPlayer player, Generic generic, TextFormatting format) {
        doGenericMessage(entity, player, generic, null, format);
    }

    default void doGenericMessage(Entity entity, EntityPlayer player, Generic generic) {
        doGenericMessage(entity, player, generic, null, null);
    }

    // This... probably shouldn't be here
    default void doGenericMessage(Entity entity, EntityPlayer player, Generic generic, @Nullable String keyOverride, @Nullable TextFormatting format) {
        String langKey;

        if (generic != null && !generic.isEmpty()) {
            langKey = generic.getLanguageKey();
        } else if (keyOverride != null && !keyOverride.isEmpty()) {
            langKey = keyOverride;
        } else {
            DWMH.LOG.error("DWMH: No valid language key passed for entity %s.", entity.getDisplayName().toString());
            return;
        }

        TextFormatting formatKey;

        if (format == null) {
            formatKey = TextFormatting.YELLOW;
        } else {
            formatKey = format;
        }

        ITextComponent temp = new TextComponentTranslation(langKey, entity.getDisplayName());
        temp.getStyle().setColor(formatKey);
        player.sendMessage(temp);
    }

    default void stopIt () {
    }

    enum Generic {
        TAMING("dwmh.strings.generic.tamed"),
        HEALING("dwmh.strings.generic.healed"),
        AGING("dwmh.strings.generic.aged"),
        BREEDING("dwhm.strings.generic.breed"),
        EMPTY("");

        String languageKey;

        Generic(String langKey) {
            this.languageKey = langKey;
        }

        public String getLanguageKey() {
            return this.languageKey;
        }

        public boolean isEmpty() {
            return this.equals(Generic.EMPTY);
        }
    }
}
