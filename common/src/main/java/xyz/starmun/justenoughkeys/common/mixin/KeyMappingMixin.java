package xyz.starmun.justenoughkeys.common.mixin;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.starmun.justenoughkeys.common.contracts.IJEKKeyMappingExtensions;
import xyz.starmun.justenoughkeys.common.data.ModifierKeyMap;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


@Mixin(KeyMapping.class)
public class KeyMappingMixin  implements  Comparable<KeyMapping>, IJEKKeyMappingExtensions {

    @Shadow
    private InputConstants.Key key;
    @Shadow @Final private String name;

    @Shadow
    private int clickCount;

    @Override
    public InputConstants.Key jek$getKey() {
        return key;
    }

    @Override
    public void jek$setClickCount(int i) {
       this.clickCount = i;
    }

    @Override
    public int jek$getClickCount() {
       return clickCount;
    }
    @Unique
    private ModifierKeyMap modifierKeyMap = new ModifierKeyMap();

    @Inject(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V", at=@At("RETURN"))
    public void fillMap(String string, InputConstants.Type type, int i, String string2, CallbackInfo ci){
        IJEKKeyMappingExtensions.ALL.put(this.name,(KeyMapping)(Comparable<KeyMapping>)this);
        IJEKKeyMappingExtensions.initMAP((KeyMapping)(Comparable<KeyMapping>)this);
    }
    @Inject(method = "click", at=@At("HEAD"), cancellable = true)
    private static void click(InputConstants.Key key, CallbackInfo ci) {
        IJEKKeyMappingExtensions.click(key);
        ci.cancel();
    }

    @Inject(method = "set", at=@At("HEAD"), cancellable = true)
    private static void set(InputConstants.Key key, boolean pressed, CallbackInfo ci) {
        IJEKKeyMappingExtensions.set(key, pressed);
        ci.cancel();
    }
    @Inject(method = "getTranslatedKeyMessage", at=@At("INVOKE"),cancellable = true)
    public void getTranslatedKeyMessage(CallbackInfoReturnable<Component> cir) {
        StringBuilder builder = new StringBuilder();
        this.getModifierKeyMap().forEach((id, modifierKey) -> builder.append(modifierKey.name));
        cir.setReturnValue(new TextComponent(builder.toString()).append(((IJEKKeyMappingExtensions) this).jek$getKey().getDisplayName()));
    }
    @Inject(method = "matches", at=@At("INVOKE"),cancellable = true)
    public void matches(int i, int j, CallbackInfoReturnable<Boolean> cir) {
        if (modifierKeyMap.any() && !modifierKeyMap.isPressed()){
            {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "matchesMouse", at=@At("INVOKE"),cancellable = true)
    public void matchesMouse(int i, CallbackInfoReturnable<Boolean> cir) {
        if (modifierKeyMap.any() && !modifierKeyMap.isPressed()){
            {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "same", at=@At("HEAD"), cancellable = true)
    public void same(KeyMapping keyMapping, CallbackInfoReturnable<Boolean> cir){
       if(!((IJEKKeyMappingExtensions)keyMapping).getModifierKeyMap().equals(modifierKeyMap)){
          cir.setReturnValue(false);
       }
    }

    @Inject(method = "isDefault", at=@At("HEAD"),cancellable = true)
    public void isDefault(CallbackInfoReturnable<Boolean> cir){
       if(modifierKeyMap.any()){
           cir.setReturnValue(false);
       }
    }

    @Inject(method = "releaseAll", at=@At("HEAD"), cancellable = true)
    private static void releaseAll(CallbackInfo ci){
       IJEKKeyMappingExtensions.releaseAll();
       ci.cancel();
    }
    @Override
    public ModifierKeyMap getModifierKeyMap() {
        return modifierKeyMap;
    }

    @Shadow
     public int compareTo(@NotNull KeyMapping o) {
        return 0;
    }
}
