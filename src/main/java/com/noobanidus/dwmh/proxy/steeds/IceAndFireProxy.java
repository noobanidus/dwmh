package com.noobanidus.dwmh.proxy.steeds;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.noobanidus.dwmh.DWMH;
import com.noobanidus.dwmh.config.DWMHConfig;
import com.noobanidus.dwmh.proxy.steeds.wrappers.IceAndFireWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@SuppressWarnings("unused")
public class IceAndFireProxy implements ISteedProxy {
    @Override
    public boolean isTeleportable(Entity entity, EntityPlayer player) {
        if (!isListable(entity, player)) {
            return false;
        }

        IceAndFireWrapper wrapper = new IceAndFireWrapper(entity);

        if (wrapper.getIsDragon() && wrapper.getDragonStage() <= 2)
            return false;

        // TODO: Should they still be summonable via config option?
        return !wrapper.isDead() && wrapper.isHorseSaddled() && !wrapper.isSitting() && globalTeleportCheck(entity, player);
    }

    @Override
    public boolean isListable(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return false;

        IceAndFireWrapper wrapper = new IceAndFireWrapper(entity);

        if (!wrapper.isTame() || wrapper.dimension != player.dimension || !wrapper.isListable()) return false;

        return wrapper.getOwnerUniqueId() != null && wrapper.getOwnerUniqueId().equals(player.getUniqueID());
    }

    // Carrot
    @Override
    public boolean isTameable(Entity entity, EntityPlayer player) {
        IceAndFireWrapper wrapper = new IceAndFireWrapper(entity);
        return notExcluded(entity) && !wrapper.isTame();
    }

    @Override
    public int tame(Entity entity, EntityPlayer player) {
        IceAndFireWrapper wrapper = new IceAndFireWrapper(entity);

        wrapper.setTamedBy(player);
        wrapper.world.setEntityState(entity, (byte) 7);

        doGenericMessage(entity, player, Generic.TAMING);

        return 1;
    }

    @Override
    public boolean isAgeable(Entity entity, EntityPlayer player) {
        IceAndFireWrapper wrapper = new IceAndFireWrapper(entity);

        return notExcluded(entity) && wrapper.isChild();
    }

    @Override
    public int age(Entity entity, EntityPlayer player) {
        IceAndFireWrapper wrapper = new IceAndFireWrapper(entity);

        wrapper.setGrowingAge(0);
        wrapper.world.setEntityState(entity, (byte) 7);

        doGenericMessage(entity, player, Generic.AGING);

        return 1;
    }

    // Not currently implemented
    @Override
    public boolean isBreedable(Entity entity, EntityPlayer player) {
        IceAndFireWrapper wrapper = new IceAndFireWrapper(entity);

        return notExcluded(entity) && !wrapper.isChild() && wrapper.getGrowingAge() == 0 && !wrapper.isInLove();
    }

    @Override
    public int breed(Entity entity, EntityPlayer player) {
        IceAndFireWrapper wrapper = new IceAndFireWrapper(entity);

        wrapper.setInLove(player);
        wrapper.world.setEntityState(entity, (byte) 7);

        doGenericMessage(entity, player, Generic.BREEDING);

        return 1;
    }

    private boolean notExcluded(Entity entity) {
        if (!isMyMod(entity)) return false;

        return !DWMH.sets("iceandfire_exclusions").contains(entity.getClass().getName());
    }

    @Override
    public boolean isMyMod(Entity entity) {
        if (!(entity instanceof EntityDragonBase) && !(entity instanceof EntityHippocampus) && !(entity instanceof EntityHippogryph))
            return false;

        String clazz = entity.getClass().getName();

        if (DWMH.sets("iceandfire").contains(clazz)) return true;

        DWMH.sets("ignore").add(clazz);
        return false;
    }

    @Override
    public ITextComponent getResponseKey(Entity entity, EntityPlayer player) {
        if (!isMyMod(entity)) return null;

        IceAndFireWrapper wrapper = new IceAndFireWrapper(entity);
        ITextComponent temp;
        if (wrapper.isDead()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.dead").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (wrapper.getDragonStage() <= 2 && wrapper.getIsDragon()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.child");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (wrapper.isSitting()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.sitting").setStyle(new Style().setColor(TextFormatting.DARK_RED));
        } else if (wrapper.hasHome() && wrapper.world.getTileEntity(wrapper.getHomePosition()) != null) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.working");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (wrapper.getLeashed()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.leashed");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (!wrapper.isHorseSaddled()) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.unsaddled");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (wrapper.isBeingRidden() && wrapper.isRidingSameEntity(player)) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (wrapper.isBeingRidden() && !DWMHConfig.Ocarina.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.unsummonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_RED);
        } else if (wrapper.isBeingRidden() && DWMHConfig.Ocarina.otherRiders) {
            temp = new TextComponentTranslation("dwmh.strings.summonable.ridden_other");
            temp.getStyle().setColor(TextFormatting.DARK_AQUA);
        } else {
            temp = new TextComponentTranslation("dwmh.strings.summonable");
            temp.getStyle().setColor(TextFormatting.AQUA);
        }

        return temp;
    }

    @Override
    public String proxyName() {
        return "iceandfire";
    }
}
