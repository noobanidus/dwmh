package noobanidus.mods.dwmh.types;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class DimBlockPos {
  private BlockPos pos;
  private DimensionType dim;

  public DimBlockPos(BlockPos pos, DimensionType dim) {
    this.pos = pos;
    this.dim = dim;
  }

  public BlockPos getPos() {
    return pos;
  }

  public DimensionType getDim() {
    return dim;
  }
}
