package io.github.centeredinventory.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Centers the inventory screen horizontally when the recipe book is open,
 * rather than the vanilla behavior of pushing it to the right edge.
 */
@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookCenterMixin {
    @Unique
    private static final int RECIPE_BOOK_WIDTH = 177;
    @Unique
    private static final int OFFSET_DELTA = (RECIPE_BOOK_WIDTH + 1) / 2 - 12;

    /**
     * Tracks whether a mouse click handler is currently executing, used to
     * prevent the recipe book from closing during recipe clicks.
     */
    @Unique
    private boolean isHandlingMouseClick;

    @Shadow
    private boolean widthTooNarrow;
    @Shadow
    private int xOffset;
    @Shadow
    private List<RecipeBookTabButton> tabButtons;
    @Shadow
    protected CycleButton<Boolean> filterButton;
    @Shadow
    private EditBox searchBox;
    @Shadow
    private RecipeBookPage recipeBookPage;

    @Shadow
    public abstract boolean isVisible();

    /**
     * Overrides the inventory screen X position to center it within the
     * remaining space to the right of the recipe book panel.
     */
    @Inject(method = "updateScreenPosition(II)I", at = @At("RETURN"), cancellable = true)
    private void centerInventoryScreen(int width, int imageWidth, CallbackInfoReturnable<Integer> cir) {
        if (this.isVisible() && !this.widthTooNarrow) {
            cir.setReturnValue((width - RECIPE_BOOK_WIDTH + 1) / 2);
        }
    }

    /**
     * Shifts the recipe book widgets left after init to align with the
     * centered inventory position.
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void repositionRecipeBook(int width, int height, Minecraft minecraft, boolean widthTooNarrow, CallbackInfo ci) {
        if (!this.isVisible() || widthTooNarrow) return;
        this.xOffset += OFFSET_DELTA;
        applyWidgetOffset();
    }

    /**
     * Shifts the recipe book widgets left when toggling visibility, matching
     * the centered inventory layout.
     */
    @Inject(method = "setVisible(Z)V", at = @At("TAIL"))
    private void onSetVisible(boolean visible, CallbackInfo ci) {
        if (!visible || this.widthTooNarrow) return;
        this.xOffset = 86 + OFFSET_DELTA;
        applyWidgetOffset();
    }

    /**
     * Prevents the recipe book from closing during a mouse click.
     * <p>
     * Vanilla calls {@code setVisible(false)} after clicking a recipe when
     * {@code isOffsetNextToMainGUI()} returns false (i.e. xOffset != 86).
     * Our centering changes xOffset, which triggers this vanilla close behavior.
     * This guard blocks that unintended close.
     */
    @Inject(method = "setVisible(Z)V", at = @At("HEAD"), cancellable = true)
    private void guardSetVisible(boolean visible, CallbackInfo ci) {
        if (!visible && this.isHandlingMouseClick) {
            ci.cancel();
        }
    }

    /**
     * Prevents the recipe book from closing when pressing ESC while it's open.
     * <p>
     * Same root cause as the click guard: vanilla's {@code keyPressed} calls
     * {@code setVisible(false)} when {@code isOffsetNextToMainGUI()} is false.
     */
    @Inject(method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z", at = @At("HEAD"), cancellable = true)
    private void fixKeyPressed(net.minecraft.client.input.KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (event.key() == 256) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z", at = @At("HEAD"))
    private void beforeMouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        this.isHandlingMouseClick = true;
    }

    @Inject(method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z", at = @At("RETURN"))
    private void afterMouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        this.isHandlingMouseClick = false;
    }

    @Unique
    private void applyWidgetOffset() {
        for (AbstractWidget tab : this.tabButtons) {
            tab.setX(tab.getX() - OFFSET_DELTA);
        }
        this.filterButton.setX(this.filterButton.getX() - OFFSET_DELTA);
        this.searchBox.setX(this.searchBox.getX() - OFFSET_DELTA);

        RecipeBookPageAccessor page = (RecipeBookPageAccessor) (Object) this.recipeBookPage;
        for (AbstractWidget button : page.getButtons()) {
            button.setX(button.getX() - OFFSET_DELTA);
        }
        page.getForwardButton().setX(page.getForwardButton().getX() - OFFSET_DELTA);
        page.getBackButton().setX(page.getBackButton().getX() - OFFSET_DELTA);
    }
}
