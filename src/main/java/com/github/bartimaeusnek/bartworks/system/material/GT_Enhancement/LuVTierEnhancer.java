/*
 * Copyright (c) 2018-2020 bartimaeusnek Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.github.bartimaeusnek.bartworks.system.material.GT_Enhancement;

import static com.github.bartimaeusnek.bartworks.MainMod.MOD_ID;
import static gregtech.api.enums.Mods.NewHorizonsCoreMod;
import static gregtech.api.enums.OrePrefixes.dust;
import static gregtech.api.enums.OrePrefixes.ingot;
import static gregtech.api.enums.OrePrefixes.plate;
import static gregtech.api.enums.OrePrefixes.plateDense;
import static gregtech.api.enums.OrePrefixes.plateDouble;
import static gregtech.api.enums.OrePrefixes.rotor;
import static gregtech.api.enums.OrePrefixes.stick;
import static gregtech.api.enums.OrePrefixes.stickLong;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.github.bartimaeusnek.bartworks.common.loaders.ItemRegistry;
import com.github.bartimaeusnek.bartworks.system.material.Werkstoff;
import com.github.bartimaeusnek.bartworks.system.material.WerkstoffLoader;
import com.github.bartimaeusnek.bartworks.system.material.processingLoaders.AfterLuVTierEnhacement;
import com.github.bartimaeusnek.bartworks.util.BW_Util;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.objects.ItemData;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Recipe.GT_Recipe_AssemblyLine;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import gregtech.api.util.GT_Shaped_Recipe;
import gregtech.api.util.GT_Utility;
import gregtech.common.items.GT_MetaGenerated_Tool_01;

@SuppressWarnings("ALL")
public class LuVTierEnhancer implements Runnable {

    private static List<ItemStack> blackListForOsmiridium = new ArrayList<>();

    static {
        addToBlackListForOsmiridiumReplacement(ItemList.Casing_MiningOsmiridium.get(1));
        addToBlackListForOsmiridiumReplacement(
                GT_MetaGenerated_Tool_01.INSTANCE
                        .getToolWithStats(170, 1, Materials.Osmiridium, Materials.Osmiridium, null));
        addToBlackListForOsmiridiumReplacement(
                GT_MetaGenerated_Tool_01.INSTANCE
                        .getToolWithStats(172, 1, Materials.Osmiridium, Materials.Osmiridium, null));
        addToBlackListForOsmiridiumReplacement(
                GT_MetaGenerated_Tool_01.INSTANCE
                        .getToolWithStats(174, 1, Materials.Osmiridium, Materials.Osmiridium, null));
        addToBlackListForOsmiridiumReplacement(
                GT_MetaGenerated_Tool_01.INSTANCE
                        .getToolWithStats(176, 1, Materials.Osmiridium, Materials.Osmiridium, null));
    }

    public void run() {

        List<IRecipe> bufferedRecipeList = null;

        try {
            bufferedRecipeList = (List<IRecipe>) FieldUtils
                    .getDeclaredField(GT_ModHandler.class, "sBufferRecipeList", true).get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        HashSet<ItemStack> LuVMachines = new HashSet<>();
        LuVMachines.add(ItemRegistry.cal);
        OrePrefixes[] LuVMaterialsGenerated = { dust, ingot, plate, stick, stickLong, rotor, plateDouble, plateDense };

        Arrays.stream(ItemList.values()).filter(item -> item.toString().contains("LuV") && item.hasBeenSet())
                .forEach(item -> LuVMachines.add(item.get(1)));

        if (NewHorizonsCoreMod.isModLoaded()) {
            addDreamcraftItemListItems(LuVMachines);
        }

        GT_ModHandler.addCraftingRecipe(
                ItemList.Casing_LuV.get(1),
                GT_ModHandler.RecipeBits.BUFFERED | GT_ModHandler.RecipeBits.REVERSIBLE
                        | GT_ModHandler.RecipeBits.NOT_REMOVABLE
                        | GT_ModHandler.RecipeBits.DELETE_ALL_OTHER_RECIPES,
                new Object[] { "PPP", "PwP", "PPP", 'P', WerkstoffLoader.LuVTierMaterial.get(plate) });

        replaceAllRecipes(LuVMachines, LuVMaterialsGenerated, bufferedRecipeList);

        AfterLuVTierEnhacement.run();
    }

    /**
     * @param stack Output item to disable Ruridit replacement in recipes
     */
    public static void addToBlackListForOsmiridiumReplacement(ItemStack stack) {
        blackListForOsmiridium.add(stack);
    }

    private static void replaceAllRecipes(Collection<ItemStack> LuVMachines, OrePrefixes[] LuVMaterialsGenerated,
            List<IRecipe> bufferedRecipeList) {

        for (GT_Recipe_AssemblyLine sAssemblylineRecipe : GT_Recipe_AssemblyLine.sAssemblylineRecipes) {
            for (ItemStack stack : LuVMachines) {
                rewriteAsslineRecipes(stack, LuVMaterialsGenerated, sAssemblylineRecipe);
            }
        }

        for (GT_Recipe_Map map : GT_Recipe_Map.sMappings) {
            for (GT_Recipe recipe : map.mRecipeList) {
                for (ItemStack stack : LuVMachines) {
                    rewriteMachineRecipes(stack, LuVMaterialsGenerated, recipe);
                }
            }
        }

        for (ItemStack stack : LuVMachines) {
            Predicate recipeFilter = obj -> obj instanceof GT_Shaped_Recipe
                    && GT_Utility.areStacksEqual(((GT_Shaped_Recipe) obj).getRecipeOutput(), stack, true);
            rewriteCraftingRecipes(bufferedRecipeList, LuVMaterialsGenerated, recipeFilter);
        }
    }

    private static void addDreamcraftItemListItems(Collection LuVMachines) {
        try {
            Class customItemListClass = Class.forName("com.dreammaster.gthandler.CustomItemList");
            Method hasnotBeenSet = MethodUtils.getAccessibleMethod(customItemListClass, "hasBeenSet");
            Method get = MethodUtils.getAccessibleMethod(customItemListClass, "get", long.class, Object[].class);
            for (Enum customItemList : (Enum[]) FieldUtils.getField(customItemListClass, "$VALUES", true).get(null)) {
                if (customItemList.toString().contains("LuV") && (boolean) hasnotBeenSet.invoke(customItemList))
                    LuVMachines.add((ItemStack) get.invoke(customItemList, 1, new Object[0]));
            }
        } catch (IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void rewriteCraftingRecipes(List<IRecipe> bufferedRecipeList, OrePrefixes[] LuVMaterialsGenerated,
            Predicate recipeFilter) {
        for (OrePrefixes prefixes : LuVMaterialsGenerated) {

            Consumer recipeAction = obj -> LuVTierEnhancer.doStacksContainAndReplace(
                    null,
                    ((GT_Shaped_Recipe) obj).getInput(),
                    GT_OreDictUnificator.get(prefixes, Materials.Chrome, 1),
                    true,
                    WerkstoffLoader.LuVTierMaterial.get(prefixes));

            CraftingManager.getInstance().getRecipeList().stream().filter(recipeFilter).forEach(recipeAction);
            bufferedRecipeList.stream().filter(recipeFilter).forEach(recipeAction);
        }
    }

    private static void rewriteMachineRecipes(ItemStack stack, OrePrefixes[] LuVMaterialsGenerated, GT_Recipe recipe) {
        if (LuVTierEnhancer.doStacksContainAndReplace(recipe, recipe.mInputs, stack, false)) {
            for (OrePrefixes prefixes : LuVMaterialsGenerated) {
                LuVTierEnhancer.doStacksContainAndReplace(
                        recipe,
                        recipe.mInputs,
                        GT_OreDictUnificator.get(prefixes, Materials.Chrome, 1),
                        true,
                        WerkstoffLoader.LuVTierMaterial.get(prefixes));
                LuVTierEnhancer.doStacksContainAndReplace(
                        recipe,
                        recipe.mOutputs,
                        GT_OreDictUnificator.get(prefixes, Materials.Chrome, 1),
                        true,
                        WerkstoffLoader.LuVTierMaterial.get(prefixes));
            }
            LuVTierEnhancer.doStacksContainAndReplace(
                    recipe,
                    recipe.mFluidInputs,
                    Materials.Chrome.getMolten(1),
                    true,
                    WerkstoffLoader.LuVTierMaterial.getMolten(1).getFluid());
            LuVTierEnhancer.doStacksContainAndReplace(
                    recipe,
                    recipe.mFluidOutputs,
                    Materials.Chrome.getMolten(1),
                    true,
                    WerkstoffLoader.LuVTierMaterial.getMolten(1).getFluid());
        }
        if (LuVTierEnhancer.doStacksContainAndReplace(recipe, recipe.mOutputs, stack, false)) {
            for (OrePrefixes prefixes : LuVMaterialsGenerated) {
                LuVTierEnhancer.doStacksContainAndReplace(
                        recipe,
                        recipe.mInputs,
                        GT_OreDictUnificator.get(prefixes, Materials.Chrome, 1),
                        true,
                        WerkstoffLoader.LuVTierMaterial.get(prefixes));
                LuVTierEnhancer.doStacksContainAndReplace(
                        recipe,
                        recipe.mOutputs,
                        GT_OreDictUnificator.get(prefixes, Materials.Chrome, 1),
                        true,
                        WerkstoffLoader.LuVTierMaterial.get(prefixes));
            }
            LuVTierEnhancer.doStacksContainAndReplace(
                    recipe,
                    recipe.mFluidInputs,
                    Materials.Chrome.getMolten(1),
                    true,
                    WerkstoffLoader.LuVTierMaterial.getMolten(1).getFluid());
            LuVTierEnhancer.doStacksContainAndReplace(
                    recipe,
                    recipe.mFluidOutputs,
                    Materials.Chrome.getMolten(1),
                    true,
                    WerkstoffLoader.LuVTierMaterial.getMolten(1).getFluid());
        }
    }

    private static void rewriteAsslineRecipes(ItemStack stack, OrePrefixes[] LuVMaterialsGenerated,
            GT_Recipe.GT_Recipe_AssemblyLine recipe) {
        for (OrePrefixes prefixes : LuVMaterialsGenerated) {
            if (LuVTierEnhancer.doStacksContainAndReplace(recipe, recipe.mInputs, stack, false)) {
                LuVTierEnhancer.doStacksContainAndReplace(
                        recipe,
                        recipe.mInputs,
                        GT_OreDictUnificator.get(prefixes, Materials.Chrome, 1),
                        true,
                        WerkstoffLoader.LuVTierMaterial.get(prefixes));
                LuVTierEnhancer.doStacksContainAndReplace(
                        recipe,
                        new Object[] { recipe.mOutput },
                        GT_OreDictUnificator.get(prefixes, Materials.Chrome, 1),
                        true,
                        WerkstoffLoader.LuVTierMaterial.get(prefixes));
            }
            if (LuVTierEnhancer.doStacksContainAndReplace(recipe, new Object[] { recipe.mOutput }, stack, false)) {
                LuVTierEnhancer.doStacksContainAndReplace(
                        recipe,
                        recipe.mInputs,
                        GT_OreDictUnificator.get(prefixes, Materials.Chrome, 1),
                        true,
                        WerkstoffLoader.LuVTierMaterial.get(prefixes));
                LuVTierEnhancer.doStacksContainAndReplace(
                        recipe,
                        new Object[] { recipe.mOutput },
                        GT_OreDictUnificator.get(prefixes, Materials.Chrome, 1),
                        true,
                        WerkstoffLoader.LuVTierMaterial.get(prefixes));
            }
        }
        if (LuVTierEnhancer.doStacksContainAndReplace(recipe, recipe.mInputs, stack, false)) {
            LuVTierEnhancer.doStacksContainAndReplace(
                    recipe,
                    recipe.mFluidInputs,
                    Materials.Chrome.getMolten(1),
                    true,
                    WerkstoffLoader.LuVTierMaterial.getMolten(1).getFluid());
        }
        if (LuVTierEnhancer.doStacksContainAndReplace(recipe, new Object[] { recipe.mOutput }, stack, false)) {
            LuVTierEnhancer.doStacksContainAndReplace(
                    recipe,
                    recipe.mFluidInputs,
                    Materials.Chrome.getMolten(1),
                    true,
                    WerkstoffLoader.LuVTierMaterial.getMolten(1).getFluid());
        }
    }

    private static boolean isOutputBlackListed(ItemStack output) {
        if (blackListForOsmiridium.stream().anyMatch(s -> GT_Utility.areStacksEqual(s, output))) return true;
        return false;
    }

    private static ItemStack[] replaceArrayWith(ItemStack[] stackArray, Materials source, Werkstoff target,
            Object recipe) {
        boolean replaced = false;
        for (int i = 0; i < stackArray.length; i++) {
            ItemStack stack = stackArray[i];
            if (!BW_Util.checkStackAndPrefix(stack)) continue;
            stackArray[i] = replaceStackWith(stack, source, target, recipe);
        }
        return stackArray;
    }

    private static ItemStack replaceStackWith(ItemStack stack, Materials source, Werkstoff target, Object recipe) {
        ItemData ass = GT_OreDictUnificator.getAssociation(stack);
        if (ass.mMaterial.mMaterial.equals(source)) {
            if (target.hasItemType(ass.mPrefix)) {
                stack = target.get(ass.mPrefix, stack.stackSize);
                if (recipe instanceof GT_Recipe) {
                    ((GT_Recipe) recipe).setOwner(MOD_ID);
                } else if (recipe instanceof GT_Recipe_AssemblyLine) {
                    // todo: update NEI fake recipe
                }
            }
        }
        return stack;
    }

    private static boolean doStacksContainAndReplace(Object recipe, FluidStack[] stacks, FluidStack stack,
            boolean replace, Fluid... replacement) {
        boolean replaced = false;
        for (int i = 0; i < stacks.length; i++) {
            if (GT_Utility.areFluidsEqual(stack, stacks[i])) if (!replace) return true;
            else {
                int amount = stacks[i].amount;
                stacks[i] = new FluidStack(replacement[0], amount);
                replaced = true;
            }
        }
        if (replace && replaced) {
            if (recipe instanceof GT_Recipe) {
                ((GT_Recipe) recipe).setOwner(MOD_ID);
            } else if (recipe instanceof GT_Recipe_AssemblyLine) {
                // todo: update NEI fake recipe
            }
        }
        return replaced;
    }

    private static boolean doStacksContainAndReplace(Object recipe, Object[] stacks, ItemStack stack, boolean replace,
            ItemStack... replacement) {
        boolean replaced = false;
        for (int i = 0; i < stacks.length; i++) {
            if (!GT_Utility.isStackValid(stacks[i])) {
                if (stacks[i] instanceof ArrayList && ((ArrayList) stacks[i]).size() > 0) {
                    if (GT_Utility.areStacksEqual(stack, (ItemStack) ((ArrayList) stacks[i]).get(0), true))
                        if (!replace) return true;
                        else {
                            int amount = ((ItemStack) ((ArrayList) stacks[i]).get(0)).stackSize;
                            stacks[i] = new ArrayList<>();
                            ((ArrayList) stacks[i]).add(BW_Util.setStackSize(replacement[0], amount));
                            replaced = true;
                        }

                } else continue;
            } else if (GT_Utility.areStacksEqual(stack, (ItemStack) stacks[i], true)) if (!replace) return true;
            else {
                int amount = ((ItemStack) stacks[i]).stackSize;
                stacks[i] = BW_Util.setStackSize(replacement[0], amount);
                replaced = true;
            }
        }
        if (replace && replaced) {
            if (recipe instanceof GT_Recipe) {
                ((GT_Recipe) recipe).setOwner(MOD_ID);
            } else if (recipe instanceof GT_Recipe_AssemblyLine) {
                // todo: update NEI fake recipe
            }
        }
        return replaced;
    }
}
