package restudio.reglass.client.ui;

import java.util.function.Consumer;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class MappedSlider extends AbstractSliderButton {
    private final double min;
    private final double max;
    private final Consumer<Double> onChange;
    private final boolean integer;
    private final Component label;

    public static MappedSlider floatSlider(int x, int y, int width, int height, Component msg,
                                           double min, double max, double init, Consumer<Double> onChange) {
        return new MappedSlider(x, y, width, height, msg, min, max, init, onChange, false);
    }

    public static MappedSlider intSlider(int x, int y, int width, int height, Component msg,
                                         int min, int max, int init, Consumer<Integer> onChange) {
        return new MappedSlider(x, y, width, height, msg, min, max, init,
                d -> onChange.accept(d.intValue()), true);
    }

    private MappedSlider(int x, int y, int width, int height, Component message,
                         double min, double max, double init, Consumer<Double> onChange, boolean integer) {
        super(x, y, width, height, message, inverse(init, min, max));
        this.min = min;
        this.max = max;
        this.onChange = onChange;
        this.integer = integer;
        this.label = message;
        updateMessage();
    }

    private static double inverse(double real, double min, double max) {
        if (max == min) return 0;
        return Math.max(0, Math.min(1, (real - min) / (max - min)));
    }

    private double map(double value) {
        return min + value * (max - min);
    }

    @Override
    protected void updateMessage() {
        double value = map(this.value);
        if (integer) value = Math.round(value);
        this.setMessage(Component.literal(label.getString() + ": " + format(value)));
    }

    @Override
    protected void applyValue() {
        double value = map(this.value);
        if (integer) value = Math.round(value);
        onChange.accept(value);
    }

    private String format(double value) {
        if (integer) return Integer.toString((int) Math.round(value));
        return String.format(java.util.Locale.ROOT, "%.3f", value);
    }
}
