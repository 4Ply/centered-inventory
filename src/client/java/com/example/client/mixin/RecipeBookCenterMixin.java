package com.example.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(RecipeBookComponent.class)
public class RecipeBookCenterMixin {
    private static final int RECIPE_BOOK_WIDTH = 177;

    @Shadow
    private boolean widthTooNarrow;

    @Shadow
    private int xOffset;

    @Shadow
    public boolean isVisible() {
        throw new UnsupportedOperationException();
    }

    // Center the inventory screen when the recipe book is visible
    @Inject(method = "updateScreenPosition(II)I", at = @At("RETURN"), cancellable = true)
    private void centerInventoryScreen(int width, int imageWidth, CallbackInfoReturnable<Integer> cir) {
        if (this.isVisible() && !this.widthTooNarrow) {
            int centeredLeft = (width - RECIPE_BOOK_WIDTH + 1) / 2;
            cir.setReturnValue(centeredLeft);
        }
    }

    // Reposition the recipe book's background when inventory is opened
    @Inject(method = "init", at = @At("TAIL"))
    private void repositionRecipeBook(int width, int height, Minecraft minecraft, boolean widthTooNarrow, CallbackInfo ci) {
        if (!this.isVisible() || this.widthTooNarrow) return;
        this.xOffset = this.xOffset + (RECIPE_BOOK_WIDTH + 1) / 2 - 12;
    }

    // Reposition the recipe book's background when toggling recipe book visibility
    @Inject(method = "setVisible(Z)V", at = @At("TAIL"))
    private void onSetVisible(boolean visible, CallbackInfo ci) {
        if (!visible || this.widthTooNarrow) return;
        this.xOffset = 86 + (RECIPE_BOOK_WIDTH + 1) / 2 - 12;
    }
}