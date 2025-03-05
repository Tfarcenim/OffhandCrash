package tfar.offhandcrash;

import io.netty.channel.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(OffhandCrash.MODID)
public class OffhandCrash {
    // Directly reference a log4j logger.

    //    private final Setting<Boolean> doCrash = sgGeneral.add(new BoolSetting.Builder()
    //        .name("do-crash")
    //        .description("Sends X number of offhand swap sound packets to the server per tick.")
    //        .defaultValue(true)
    //        .build()
    //    );
    //
    //    private final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
    //        .name("speed")
    //        .description("The amount of swaps per tick.")
    //        .defaultValue(2000)
    //        .min(1)
    //        .sliderRange(1, 10000)
    //        .visible(doCrash::get)
    //        .build()
    //    );
    //
    //    private final Setting<Boolean> antiCrash = sgGeneral.add(new BoolSetting.Builder()
    //        .name("anti-crash")
    //        .description("Attempts to prevent you from crashing yourself.")
    //        .defaultValue(true)
    //        .build()
    //    );

    public static final String MODID = "offhandcrash";

    private static final Logger LOGGER = LogManager.getLogger();
    public static OffhandCrash INSTANCE;

    static ForgeConfigSpec.IntValue SPEED;
    public static ForgeConfigSpec.BooleanValue ANTI_CRASH;

    public OffhandCrash() {

        Pair<OffhandCrash, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(builder -> {
            builder.push("general");
            SPEED = builder.defineInRange("speed",2000,1,Integer.MAX_VALUE);
            ANTI_CRASH = builder.define("anti_crash",true);
            builder.pop();
            return this;
        });
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,pair.getRight());
        INSTANCE = pair.getLeft();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        if (FMLEnvironment.dist.isClient()) {
            Mod.init(bus);
        }
    }

    public static class Mod {
        private static final KeyBinding KEY = new KeyBinding("activate", GLFW.GLFW_KEY_I,MODID);
        private static final CPlayerDiggingPacket PACKET = new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                new BlockPos(0, 0, 0) , Direction.UP);


        static void setup(FMLClientSetupEvent event) {
            ClientRegistry.registerKeyBinding(KEY);
        }

        public static void init(IEventBus bus) {
            bus.addListener(Mod::setup);
            MinecraftForge.EVENT_BUS.addListener(Mod::onTick);
        }

        private static void onTick(TickEvent.ClientTickEvent event) {
            if (KEY.isKeyDown() && event.phase == TickEvent.Phase.START) {
                Channel channel = Minecraft.getInstance().player.connection.getNetworkManager().channel();
                for (int i = 0; i < SPEED.get(); ++i) channel.write(PACKET);
                channel.flush();
            }
        }

        public static boolean isAntiCrash() {
            return KEY.isKeyDown() && ANTI_CRASH.get();
        }
    }
}
