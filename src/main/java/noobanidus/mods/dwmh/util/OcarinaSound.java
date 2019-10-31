package noobanidus.mods.dwmh.util;

import noobanidus.mods.dwmh.init.SoundRegistry;
import net.minecraft.util.SoundEvent;

public enum OcarinaSound {
  NORMAL(0),
  MINOR(1),
  SPECIAL(2),
  BROKEN(3),
  NONE(-1);

  public int type;

  OcarinaSound(int type) {
    this.type = type;
  }

  public SoundEvent getSoundEvent() {
    switch (type) {
      case 0:
        return SoundRegistry.getRandomWhistle();
      case 1:
        return SoundRegistry.getRandomMinorWhistle();
      case 2:
        return SoundRegistry.WHISTLE_SPECIAL;
      case 3:
        return SoundRegistry.WHISTLE_BROKEN;
    }

    return null;
  }

  public static OcarinaSound fromOrdinal(int ordinal) {
    int i = 0;
    for (OcarinaSound generic : OcarinaSound.values()) {
      if (ordinal == i++) return generic;
    }

    return NONE;
  }
}
