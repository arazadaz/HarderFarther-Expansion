package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.BiomeConfig;
import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.api.DifficultyCalculator;
import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import com.mactso.harderfarther.mixinInterfaces.IExtendedSearchTree;
import com.mactso.harderfarther.utility.Utility;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.util.Holder;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.worldgen.IExtendedParameterList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.mactso.harderfarther.api.DifficultyCalculator.getNearestOutpost;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 995)
public class BiomeGenMixin extends BiomeSource{

    @Final
    @Mutable
    @Shadow
    private MultiNoiseUtil.ParameterRangeList<Holder<Biome>> biomePoints;

    private boolean areListInitialized = false;

    private boolean isDimInitialized = false;

    private String dimension = "";

    private IExtendedSearchTree<Holder<Biome>>[] defaultSearchTrees;
    private MultiNoiseUtil.SearchTree<Holder<Biome>>[][] newSearchTree;

    private ArrayList<Float> difficultySectionNumbers = new ArrayList<>();
    private ArrayList<Integer> emptyListsIndexes = new ArrayList<>();
    private ArrayList<Integer> filledListsIndexes = new ArrayList<>();

    protected BiomeGenMixin(Stream<Holder<Biome>> biomes) {
        super(biomes);
    }

    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource;getNoiseBiome(IIILnet/minecraft/world/biome/source/util/MultiNoiseUtil$MultiNoiseSampler;)Lnet/minecraft/util/Holder;", cancellable = true)
    private void harderfarther$onGenerateDifficultyBiome(int i, int j, int k, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler, CallbackInfoReturnable<Holder<Biome>> cir) {

        if(!areListInitialized) {
            synchronized (this) {
                if (!areListInitialized) {

                    if (PrimaryConfig.getDebugLevel() > 0) {
                        Utility.debugMsg(1, "New Biome Source");
                    }

                    //Return default value if terrablender biomesource is not initialized
                    if (!((IExtendedParameterList<Holder<Biome>>) this.biomePoints).isInitialized()) {

                        if (PrimaryConfig.getDebugLevel() > 1) {
                            Utility.debugMsg(2, "BiomeSource not initiliazed for terrablender");
                        }
                        areListInitialized = true;
                        cir.setReturnValue(this.biomePoints.findValue(multiNoiseSampler.sample(i, j, k)));
                        return;

                    }


                    int regionCount = ((IExtendedParameterList<Holder<Biome>>) this.biomePoints).getTreeCount();
                    List<Pair<MultiNoiseUtil.NoiseHypercube, Holder<Biome>>> modifiedBiomePoints = new ArrayList<>();
                    defaultSearchTrees = new IExtendedSearchTree[regionCount];
                    newSearchTree = new MultiNoiseUtil.SearchTree[BiomeConfig.getDifficultySections().size()][regionCount];


                    for (int iterator = 0; iterator < regionCount; iterator++) {
                        defaultSearchTrees[iterator] = ((IExtendedSearchTree<Holder<Biome>>) (Object) ((IExtendedParameterList<Holder<Biome>>) this.biomePoints).getTree(iterator));

                        List<Pair<MultiNoiseUtil.NoiseHypercube, Holder<Biome>>> biomePairs = defaultSearchTrees[iterator].getOriginalList();


                        final int[] difficultySectionIndex = {0};
                        int regionIndex = iterator;
                        BiomeConfig.getDifficultySections().forEach((difficultySection) -> {

                            if (regionIndex == 0) difficultySectionNumbers.add(difficultySection.first.floatValue());


                            //Iterate through original biome List of a region
                            biomePairs.forEach(noiseHypercubeHolderPair -> {

                                String biome = noiseHypercubeHolderPair.getSecond().getKey().get().getValue().toString();


                                //Add All biomes if biome config list is empty. Otherwise add only if it's apart of the list in the config. - .isEmpty doesn't work as it seems initialized with empty strings.
                                if (difficultySection.second.get(0).equals("")) {
                                    modifiedBiomePoints.add(new Pair<>(noiseHypercubeHolderPair.getFirst(), noiseHypercubeHolderPair.getSecond()));
                                } else if (difficultySection.second.contains(biome)) {
                                    modifiedBiomePoints.add(new Pair<>(noiseHypercubeHolderPair.getFirst(), noiseHypercubeHolderPair.getSecond()));
                                }

                            });


                            if (!modifiedBiomePoints.isEmpty()) {
                                newSearchTree[difficultySectionIndex[0]][regionIndex] = MultiNoiseUtil.SearchTree.create(modifiedBiomePoints);
                                modifiedBiomePoints.clear();  //reset the list to ensure no duplicate values.
                                filledListsIndexes.add(regionIndex);
                            } else {
                                emptyListsIndexes.add(regionIndex);
                            }

                            //Fill empty lists with alternating filled lists
                            if (regionIndex == regionCount - 1) {
                                final int[] filledListIndexesIndex = {0};
                                emptyListsIndexes.forEach(emptyIndex -> {

                                    newSearchTree[difficultySectionIndex[0]][emptyIndex] = newSearchTree[difficultySectionIndex[0]][filledListsIndexes.get(filledListIndexesIndex[0])];

                                    filledListIndexesIndex[0]++;
                                    if (filledListIndexesIndex[0] == filledListsIndexes.size() - 1) {
                                        filledListIndexesIndex[0] = 0;
                                    }
                                });
                            }

                            difficultySectionIndex[0]++;
                        });

                    }

                    areListInitialized = true;
                }
            }
        }
        //end of listInitialization














        //Start of primary logic

        // Fallback on findValue if we are uninitialized (may be the case for non-TerraBlender dimensions) - Also nether is bugged & not initialized in 1.19.2 fabric terrablender
        if(!((IExtendedParameterList<Holder<Biome>>) this.biomePoints).isInitialized()) {

            if (PrimaryConfig.getDebugLevel() > 1) {
                Utility.debugMsg(2, "BiomeSource not initiliazed for terrablender");
            }
            cir.setReturnValue(this.biomePoints.findValue(multiNoiseSampler.sample(i,j,k)));
            return;

        }



        int uniqueness = ((IExtendedParameterList<Holder<Biome>>)this.biomePoints).getUniqueness(i, j, k);

        //Make sure worlds are initialized before running main logic
        if(((IExtendedBiomeSourceHF)this).getInit()) {

            if(!isDimInitialized) {
                dimension = ((IExtendedBiomeSourceHF) (BiomeSource) (Object) this).getDirtyWorld().getRegistryKey().getValue().toString();
                isDimInitialized = true;
            }




            //Main Logic for choosing difficulty biome section
            int x = BiomeCoords.fromChunk(i);
            int z = BiomeCoords.fromChunk(k);
            Vec3d location = new Vec3d(x, 0, z);
            float difficulty = DifficultyCalculator.getDistanceDifficultyHere(((IExtendedBiomeSourceHF) this).getDirtyWorld(), location) * 100;

            //System.out.println(difficulty);

            int[] choosenAreaIndex = {-1};
            difficultySectionNumbers.forEach(difficultySectionNumber -> {
                if(difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
            });




            //Support for overworld only as of now. I want to get a release out :)
            if(this.dimension.equals("minecraft:overworld")) {
                cir.setReturnValue((Holder<Biome>) newSearchTree[choosenAreaIndex[0]][uniqueness].get(multiNoiseSampler.sample(i, j, k), MultiNoiseUtil.SearchTree.TreeNode::getSquaredDistance));
            }else {
                cir.setReturnValue((Holder<Biome>) ((MultiNoiseUtil.SearchTree) (Object) defaultSearchTrees[0]).get(multiNoiseSampler.sample(i, j, k), MultiNoiseUtil.SearchTree.TreeNode::getSquaredDistance));
            }

        }




        //Generates spawn - This is only needed since minecraft generates the spawn before initializing worlds for whatever reason. Spawn should always be overworld unless a mod/datapack changes it. There's one exception, sometimes this will run outside of the overworld once before spawn. I'm not sure why yet..
        if(!((IExtendedBiomeSourceHF)this).getInit()) {

            if (((IExtendedBiomeSourceHF) this).getDirtyWorld() == null) {
                if(PrimaryConfig.getDebugLevel() > 0) {
                    Utility.debugMsg(1, "Structure Feature thing during spawn generation that isn't in the overworld???");  //I really don't know why this happens sometimes(rarely and only once) before spawn generates nor do I know what it does.
                }
            } else {

                //Calculate distance difficulty
                float difficulty = DifficultyCalculator.getDistanceDifficultyHere(((IExtendedBiomeSourceHF) this).getDirtyWorld(), new Vec3d(0, 0, 0));

                int[] choosenAreaIndex = {-1};
                difficultySectionNumbers.forEach(difficultySectionNumber -> {
                    if (difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
                });

                cir.setReturnValue((Holder<Biome>) newSearchTree[choosenAreaIndex[0]][uniqueness].get(multiNoiseSampler.sample(i, j, k), MultiNoiseUtil.SearchTree.TreeNode::getSquaredDistance));
            }
        }




        /*if(x == 0){

            difficultySections.add(this.biomePoints); //Original biome list will be index 0
            List<Pair<MultiNoiseUtil.NoiseHypercube, Holder<Biome>>> modifiedBiomePoints = new ArrayList<>();

            BiomeConfig.getDifficultySections().forEach((difficultySection) -> {
                biomePoints.getEntries().forEach((noiseHypercubeHolderPair -> {

                    String biome = noiseHypercubeHolderPair.getSecond().getKey().get().toString().substring(39);
                    biome = biome.substring(0, biome.length()-1);
                    //System.out.println(noiseHypercubeHolderPair.getSecond().getKey().get().getValue());

                    if(difficultySection.second.contains(biome)){
                        modifiedBiomePoints.add(new Pair<>(noiseHypercubeHolderPair.getFirst(), noiseHypercubeHolderPair.getSecond()));
                    }
                    //RegistryKey.of(BuiltinRegistries.BIOME.getKey(), new Identifier("terrablender", "deferred_placeholder"));
                    if(noiseHypercubeHolderPair.getSecond().isRegistryKey(RegistryKey.of(BuiltinRegistries.BIOME.getKey(), new Identifier("terrablender", "deferred_placeholder")))){
                        System.out.println("Success");
                    }
                }));

                difficultySections.add(new MultiNoiseUtil.ParameterRangeList<>(modifiedBiomePoints));
                modifiedBiomePoints.clear();

            });

                //These are really just notes for me to remember how this stuff work in a way. I don't intend to use them though.
            //modifiedBiomePoints.set(0, new Pair<>(new MultiNoiseUtil.NoiseHypercube(new MultiNoiseUtil.ParameterRange(-10000, 10000), new MultiNoiseUtil.ParameterRange(-10000, 10000), new MultiNoiseUtil.ParameterRange(-10000, 10000), new MultiNoiseUtil.ParameterRange(-10000, 10000), new MultiNoiseUtil.ParameterRange(-10000, 10000), new MultiNoiseUtil.ParameterRange(-10000, 10000), 0), biomeHolder));

            //RegistryKey<Biome> BIOME_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("minecraft:lush_caves"));
            //Holder<Biome> biomeHolder = WorldGenHandler.biomeRegistry.getHolder(BIOME_KEY).get();

            x++;
        }

        //Still need to implement logic for which section to choose & distinguish between overworld/other dimensions.
        this.biomePoints = difficultySections.get(0);

        biomePoints.getEntries().forEach((noiseHypercubeHolderPair -> {

            //System.out.println(noiseHypercubeHolderPair.getSecond().getKey().get().getValue());

            //RegistryKey.of(BuiltinRegistries.BIOME.getKey(), new Identifier("terrablender", "deferred_placeholder"));
            if(noiseHypercubeHolderPair.getSecond().isRegistryKey(RegistryKey.of(BuiltinRegistries.BIOME.getKey(), new Identifier("terrablender", "deferred_placeholder")))){
                System.out.println("Success");
            }
        }));*/

        /*biomePoints.getEntries().forEach((noiseHypercubeHolderPair) -> {
            if(noiseHypercubeHolderPair.getSecond().getKey().get().getValue().toString().equals("byg:tropical_rainforest")){
                System.out.println(noiseHypercubeHolderPair.getSecond().getKey().get().getValue());
            }
        });*/
    }

    @Shadow
    @Override
    public Codec<? extends BiomeSource> getCodec() {
        return null;
    }

    @Shadow
    @Override
    public Holder<Biome> getNoiseBiome(int i, int j, int k, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler) {
        return null;
    }
}