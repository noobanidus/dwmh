package noobanidus.mods.dwmh.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigManager {

  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

  public static ForgeConfigSpec COMMON_CONFIG;
  public static Whitelist whitelist = new Whitelist();
  public static Blacklist blacklist = new Blacklist();

  public static void init () {
    COMMON_BUILDER.comment("Whitelist").push("whitelist");
    whitelist.apply(COMMON_BUILDER);
    COMMON_BUILDER.pop();
    COMMON_BUILDER.comment("Blacklist").push("blacklist");
    blacklist.apply(COMMON_BUILDER);
    COMMON_BUILDER.pop();

    COMMON_CONFIG = COMMON_BUILDER.build();
  }

  public static void loadConfig(ForgeConfigSpec spec, Path path) {
    CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
    configData.load();
    spec.setConfig(configData);
  }

  public static class Colourlist {
    private Set<ResourceLocation> entityList = null;

    private ForgeConfigSpec.ConfigValue<List<String>> baseList;

    private String type;

    public Colourlist(String type) {
      this.type = type;
    }

    public Set<ResourceLocation> getEntityList() {
      if (entityList == null) {
        entityList = baseList.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
      }
      return entityList;
    }

    public void apply(ForgeConfigSpec.Builder builder) {
      baseList = builder.comment("list of resourcelocations in string form").define(type, Lists.newArrayList(""));
    }
  }

  public static class Blacklist extends Colourlist {
    public Blacklist() {
      super("blacklist");
    }
  }

  public static class Whitelist extends Colourlist {
    public Whitelist () {
      super("whitelist");
    }
  }
}

