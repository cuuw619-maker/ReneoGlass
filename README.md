# ReGlass by ReStudio

Liquid-glass UI rendering for Minecraft 1.21.1 on NeoForge.

ReGlass is designed as a small rendering API that other Minecraft mods can use to build translucent, rounded and animated UI surfaces.

## Platform

- Minecraft 1.21.1
- NeoForge 21.1.x
- Java 21
- Optional Sodium integration through Sodium's configuration API
- Compatible with Reese's Sodium Options when Sodium and Reese's are installed
- No Fabric Loader, Fabric API, Fabric Loom, Yarn, or Fabric Mixin runtime is required

## Features

- Customizable glass tint and alpha
- Rounded surfaces and outlines
- Configurable shadows
- Smoothing and interaction effects
- Hover and focus scaling
- Refraction, glare and rim-light configuration values
- Pixelated-grid controls
- Standalone configuration screen
- NeoForge Screen events for applying the glass style to vanilla widgets
- Draggable `LiquidGlassWidget`

## API example

```java
ReGlassApi.create(graphics)
        .dimensions(10, 10, 100, 40)
        .cornerRadius(12)
        .style(WidgetStyle.create()
                .tint(0xFFFFFF, 0.35f)
                .shadow(12f, 0.20f, 0f, 3f)
                .smoothing(0.05f))
        .render();
```

`graphics` is the vanilla/NeoForge `GuiGraphics` instance supplied to a screen or render event. The current 1.21.1 implementation intentionally avoids the incompatible 1.21.8 render-state/uniform pipeline and keeps the base renderer NeoForge-native.

## Ready-to-use widget

```java
addRenderableWidget(
        new LiquidGlassWidget(20, 20, 150, 40, WidgetStyle.create())
                .setCornerRadiusPx(12)
                .setMoveable(true)
);
```

## Keybinds

- `G` — open the ReGlass configuration screen
- `H` — open the ReGlass playground

## Configuration

The standalone configuration is stored in `config/reglass.json`. When Sodium is installed, ReGlass also exposes its settings through Sodium's Config API, which allows Reese's Sodium Options to present the same configuration.

## Development

Build with:

```bash
./gradlew build
```

The GitHub Actions workflow uses Java 21 and the NeoForge ModDev Gradle plugin.

## Contributing

Contributions to the NeoForge 1.21.1 renderer, UI redesign and configuration system are welcome.
