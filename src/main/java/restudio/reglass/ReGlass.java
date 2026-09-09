package restudio.reglass;

import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ReGlass.MOD_ID)
public final class ReGlass {
    public static final String MOD_ID = "reglass";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ReGlass() {
        LOGGER.info("Initializing ReGlass for NeoForge 1.21.1");
    }
}
