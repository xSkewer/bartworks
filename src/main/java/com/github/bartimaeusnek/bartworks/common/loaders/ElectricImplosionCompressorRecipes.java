package com.github.bartimaeusnek.bartworks.common.loaders;

import static com.github.bartimaeusnek.bartworks.common.tileentities.multis.GT_TileEntity_ElectricImplosionCompressor.eicMap;
import static gregtech.api.enums.GT_Values.M;
import static gregtech.api.enums.Mods.EternalSingularity;
import static gregtech.api.enums.Mods.GoodGenerator;
import static gregtech.api.enums.Mods.OpenComputers;
import static gregtech.api.enums.Mods.SuperSolarPanels;
import static gregtech.api.enums.Mods.UniversalSingularities;
import static gregtech.api.util.GT_ModHandler.getModItem;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GT_OreDictUnificator;

public class ElectricImplosionCompressorRecipes implements Runnable {

    private static void addElectricImplosionRecipe(final ItemStack[] inputItems, final FluidStack[] inputFluids,
            final ItemStack[] outputItems, final FluidStack[] outputFluids, final int durationInTicks,
            final int EUPerTick) {
        eicMap.addRecipe(
                false,
                inputItems,
                outputItems,
                null,
                inputFluids,
                outputFluids,
                durationInTicks,
                EUPerTick,
                1);
    }

    private static final ItemStack[] circuits = new ItemStack[] { ItemList.Circuit_ExoticProcessor.get(1),
            ItemList.Circuit_OpticalAssembly.get(1), ItemList.Circuit_Biowaresupercomputer.get(1),
            ItemList.Circuit_Wetwaremainframe.get(1) };

    @Override
    public void run() {
        // Custom electric implosion compressor recipes. Cannot be overclocked.

        if (EternalSingularity.isModLoaded()) {

            addElectricImplosionRecipe(
                    // IN.
                    new ItemStack[] { GT_Values.NI },
                    new FluidStack[] { Materials.SpaceTime.getMolten(72L) },
                    // OUT.
                    new ItemStack[] { getModItem(EternalSingularity.ID, "eternal_singularity", 1L) },
                    new FluidStack[] { GT_Values.NF },
                    // Recipe stats.
                    100 * 20,
                    (int) TierEU.RECIPE_UMV);

            if (UniversalSingularities.isModLoaded())
                // Raw Exposed Optical Chip
                addElectricImplosionRecipe(
                        // IN.
                        new ItemStack[] { ItemList.Circuit_Silicon_Wafer7.get(1L),
                                // Fluxed Electrum Singularity.
                                getModItem(UniversalSingularities.ID, "universal.general.singularity", 1L, 20) },
                        new FluidStack[] { GT_Values.NF },
                        // OUT.
                        new ItemStack[] { ItemList.Circuit_Chip_Optical.get(16L) },
                        new FluidStack[] { GT_Values.NF },
                        5 * 20,
                        (int) TierEU.RECIPE_UMV);

        }

        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] { getModItem(GoodGenerator.ID, "highDensityPlutoniumNugget", 5L) },
                new FluidStack[] { Materials.Infinity.getMolten(9L) },
                // OUT.
                new ItemStack[] { getModItem(GoodGenerator.ID, "highDensityPlutonium", 1L) },
                new FluidStack[] { GT_Values.NF },
                // Recipe stats.
                1,
                (int) TierEU.RECIPE_UEV);

        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] { getModItem(GoodGenerator.ID, "highDensityUraniumNugget", 5L) },
                new FluidStack[] { Materials.Infinity.getMolten(9L) },
                // OUT.
                new ItemStack[] { getModItem(GoodGenerator.ID, "highDensityUranium", 1L) },
                new FluidStack[] { GT_Values.NF },
                // Recipe stats.
                1,
                (int) TierEU.RECIPE_UEV);

        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] { getModItem(GoodGenerator.ID, "highDensityThoriumNugget", 5L) },
                new FluidStack[] { Materials.Infinity.getMolten(9L) },
                // OUT.
                new ItemStack[] { getModItem(GoodGenerator.ID, "highDensityThorium", 1L) },
                new FluidStack[] { GT_Values.NF },
                // Recipe stats.
                1,
                (int) TierEU.RECIPE_UEV);

        // Magneto material recipe for base fluid.
        addElectricImplosionRecipe(
                // IN.
                new ItemStack[] { GT_OreDictUnificator.get(OrePrefixes.nanite, Materials.WhiteDwarfMatter, 1L),
                        GT_OreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 1L),
                        GT_OreDictUnificator.get(OrePrefixes.nanite, Materials.BlackDwarfMatter, 1L) },
                new FluidStack[] { Materials.RawStarMatter.getFluid(64 * 144L) },
                // OUT.
                new ItemStack[] { GT_Values.NI },
                new FluidStack[] { Materials.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(16 * 144L) },
                // Recipe stats.
                20 * 4,
                (int) TierEU.RECIPE_UXV);

        addMagnetohydrodynamicallyConstrainedStarMatterPartRecipes();
    }

    private void addMagnetohydrodynamicallyConstrainedStarMatterPartRecipes() {

        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.frameGt, 1, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.nugget, 9, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.ingot, 1, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.plate, 1, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.plateDense, 1, 3);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.stick, 2, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.round, 8, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.bolt, 8, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.screw, 8, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.ring, 4, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.foil, 8, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.itemCasing, 2, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.gearGtSmall, 1, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.rotor, 1, 2);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.stickLong, 1, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.springSmall, 2, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.spring, 1, 1);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.gearGt, 1, 2);
        addWhiteDwarfMagnetoEICRecipe(OrePrefixes.wireFine, 8, 1);
    }

    private void addWhiteDwarfMagnetoEICRecipe(final OrePrefixes part, final int multiplier,
            final int circuitMultiplier) {

        final int partFraction = (int) (144 * part.mMaterialAmount / M);

        for (ItemStack circuit : circuits) {
            addElectricImplosionRecipe(
                    new ItemStack[] { circuit.splitStack(circuitMultiplier),
                            getModItem(SuperSolarPanels.ID, "solarsplitter", 1, 0),
                            getModItem(OpenComputers.ID, "hologram2", circuitMultiplier, 0),
                            GT_OreDictUnificator.get(part, Materials.Universium, multiplier), },
                    new FluidStack[] { Materials.MagnetohydrodynamicallyConstrainedStarMatter
                            .getMolten((long) partFraction * multiplier) },
                    new ItemStack[] { GT_OreDictUnificator
                            .get(part, Materials.MagnetohydrodynamicallyConstrainedStarMatter, multiplier) },
                    new FluidStack[] { GT_Values.NF },
                    (int) (multiplier * (20 * partFraction / 144.0)),
                    (int) TierEU.RECIPE_UXV);
        }
    }
}
