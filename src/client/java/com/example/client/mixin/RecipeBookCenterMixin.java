package com.example.client.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipeBookCenterMixin {
    private static final int RECIPE_BOOK_WIDTH = 177;

    @Shadow
    private boolean widthTooNarrow;

    @Shadow
    public boolean isVisible() {
        throw new UnsupportedOperationException();
    }

    @Inject(method = "updateScreenPosition(II)I", at = @At("RETURN"), cancellable = true)
    private void centerRecipeBookScreen(int width, int imageWidth, CallbackInfoReturnable<Integer> cir) {
        if (this.isVisible() && !this.widthTooNarrow) {
            int centeredLeft = (width - RECIPE_BOOK_WIDTH + 1) / 2;
            cir.setReturnValue(centeredLeft);
        }
    }
}
