# Centered Inventory

A Fabric mod for Minecraft `26.1.1` that centers the player inventory screen and the recipe book layout in the GUI. This mod adjusts the inventory screen rendering so the combined inventory + recipe book UI is centered on the game window, improving symmetry and usability.

## What it does

- Centers the player inventory screen when the recipe book is visible.
- Preserves normal layout behavior when the recipe book is hidden or the window is too narrow.
- Applies the adjustment on the client side via a Fabric mixin.

## Build Instructions

1. Open a terminal in the project root: `c:\repos\centered_inventory`
2. Run the Fabric Gradle build:
   - `./gradlew.bat build --console=plain`
3. If you only need to validate Java compilation, run:
   - `./gradlew.bat compileJava --console=plain`
   - `./gradlew.bat compileClientJava --console=plain`

## Installation

1. Build the mod JAR using the Gradle `build` task.
2. Copy the generated JAR from `build/libs/` into your Fabric `mods/` folder.
3. Run Minecraft with Fabric Loader and Fabric API for `1.26.1`.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
