package net.guavy.gravestones.compat;

import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import java.util.Collection;
import java.util.function.Consumer;
import net.guavy.gravestones.api.GravestonesApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrinketsCompat implements GravestonesApi {
    @Override
    public List<ItemStack> getInventory(PlayerEntity entity) {
        List<ItemStack> itemStacks = new ArrayList<>();

        forEachTrinket(entity, itemStacks::add);
        return itemStacks;
    }

    @Override
    public void setInventory(List<ItemStack> stacks, PlayerEntity entity) {
        PlayerInventory playerInventory = entity.getInventory();
        Collection<Map<String, TrinketInventory>> inventories = getTrinketInventory(entity).values();

        // Yes this is unbelievably awful, I am both sorry and not sorry... schrodinger's apology
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack item = stacks.get(i);

            int size = 0;

            for (Map<String, TrinketInventory> inventoryMap : inventories) {
                for (TrinketInventory inventory : inventoryMap.values()) {
                    for (int j = 0; j < inventory.size(); j++, size++) {
                        //GLFW.glfwSetInputMode(MinecraftClient.getInstance().getWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);

                        if (i != size) {
                            continue;
                        }

                        if (inventory.getStack(j).isEmpty()) {
                            inventory.setStack(j, item);
                            continue;
                        }

                        playerInventory.insertStack(item);

                    }
                }
            }
        }
    }

    @Override
    public int getInventorySize(PlayerEntity entity) {
        return getTrinketInventory(entity).size();
    }

    public static void dropAll(PlayerEntity entity) {
        getTrinketInventory(entity).clear();
    }

    private static void forEachTrinket(PlayerEntity entity, Consumer<ItemStack> callable) {
        for (Map<String, TrinketInventory> map : getTrinketInventory(entity).values()) {
            for (TrinketInventory inventory : map.values()) {
                for(int i = 0; i < inventory.size(); i++) {
                    callable.accept(inventory.getStack(i));
                }
            }
        }
    }

    private static Map<String, Map<String, TrinketInventory>> getTrinketInventory(PlayerEntity entity) {
        return TrinketsApi.getTrinketComponent(entity).get().getInventory();
    }
}
