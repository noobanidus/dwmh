package noobanidus.mods.dwmh.types;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class DimBlockPos {
  private BlockPos pos;
  private RegistryKey<World> dim;

  public DimBlockPos(BlockPos pos, RegistryKey<World> dim) {
    this.pos = pos;
    this.dim = dim;
  }

  public BlockPos getPos() {
    return pos;
  }

  public RegistryKey<World> getDim() {
    return dim;
  }
}
