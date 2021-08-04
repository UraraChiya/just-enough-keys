package xyz.starmun.justenoughkeys.data;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import xyz.starmun.justenoughkeys.contracts.IJEKKeyMappingExtensions;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Locale;

public class ModifierKeyMap extends HashMap<Integer, ModifierKey> {

    private final BitSet bitSet = new BitSet();

    public ModifierKey set(ModifierKey key, boolean isPressed){
        if(key== ModifierKey.UNKNOWN)
            return ModifierKey.UNKNOWN;
        bitSet.set(key.id,isPressed);
        if(isPressed){
            return super.put(key.id, key);

        }
        else {
            return super.remove(key.id);
        }
    }
    public ModifierKey set(InputConstants.Key key, boolean isPressed){
        if(ModifierKey.isModifierKey(key)){
            ModifierKey modifierKey = ModifierKey.modifierKeyFromValue(key.getValue());
            return this.set(modifierKey, isPressed);
        }
        return ModifierKey.UNKNOWN;
    }
    public ModifierKey set(InputConstants.Key key){
      return set(key,true);
    }
    public boolean any(){
        return !this.bitSet.isEmpty();
    }
    public void clear(KeyMapping keyMapping){
        set(ModifierKey.modifierKeyFromValue(((IJEKKeyMappingExtensions)keyMapping).jek$getKey().getValue()),false);
    }
    public void clear(){
        bitSet.clear();
        super.clear();
    }
    public boolean isPressed(){
        return IJEKKeyMappingExtensions.CURRENT_PRESSED_MODIFIERS.bitSet.equals(this.bitSet);
    }
    public void setAll(){
        ModifierKey.MODIFIER_KEYS.values()
                .forEach(modifierKey ->  set(ModifierKey.modifierKeyFromValue(modifierKey.value)
                        ,InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), modifierKey.value)));

    }

    public boolean search(String allParametersStrippedQuery) {
        return this.values().stream().anyMatch(modifierKey -> modifierKey.getDisplayName().toLowerCase(Locale.ROOT).contains(allParametersStrippedQuery));
    }
}
