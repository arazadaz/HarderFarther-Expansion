package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.BiomeConfig;
import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import com.mactso.harderfarther.mixinInterfaces.IExtendedSearchTree;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.util.Holder;
import net.minecraft.world.biome.Biome;
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

@Mixin(value = MultiNoiseBiomeSource.class, priority = 995)
public class BiomeGenMixin extends BiomeSource{

    @Final
    @Mutable
    @Shadow
    private MultiNoiseUtil.ParameterRangeList<Holder<Biome>> biomePoints;

    private static ArrayList<MultiNoiseUtil.SearchTree<Holder<Biome>>> difficultySections = new ArrayList<>();

    private boolean areListInitialized = false;

    private boolean isDimInitialized = false;

    private String dimension = "";

    private IExtendedSearchTree<Holder<Biome>>[] defaultSearchTrees;
    private MultiNoiseUtil.SearchTree<Holder<Biome>> newSearchTree;

    protected BiomeGenMixin(Stream<Holder<Biome>> biomes) {
        super(biomes);
    }

    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource;getNoiseBiome(IIILnet/minecraft/world/biome/source/util/MultiNoiseUtil$MultiNoiseSampler;)Lnet/minecraft/util/Holder;", cancellable = true)
    private void onGenerate(int i, int j, int k, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler, CallbackInfoReturnable<Holder<Biome>> cir) {

        if(!areListInitialized) {

            int regionCount = ((IExtendedParameterList<Holder<Biome>>) this.biomePoints).getTreeCount();
            List<Pair<MultiNoiseUtil.NoiseHypercube, Holder<Biome>>> modifiedBiomePoints = new ArrayList<>();
            defaultSearchTrees = new IExtendedSearchTree[regionCount];


            for(int iterator = 0; iterator<regionCount; iterator++) {
                defaultSearchTrees[iterator] = ((IExtendedSearchTree<Holder<Biome>>) (Object) ((IExtendedParameterList<Holder<Biome>>) this.biomePoints).getTree(iterator));

                List<Pair<MultiNoiseUtil.NoiseHypercube, Holder<Biome>>> biomePairs = defaultSearchTrees[iterator].getOriginalList();

                BiomeConfig.getDifficultySections().forEach((difficultySection) -> {
                    biomePairs.forEach(noiseHypercubeHolderPair -> {

                        String biome = noiseHypercubeHolderPair.getSecond().getKey().get().getValue().toString();
                        System.out.println(biome);

                        if(difficultySection.second.contains(biome)){
                            modifiedBiomePoints.add(new Pair<>(noiseHypercubeHolderPair.getFirst(), noiseHypercubeHolderPair.getSecond()));
                        }

                    });
                });
                if(!modifiedBiomePoints.isEmpty()) {
                    newSearchTree = MultiNoiseUtil.SearchTree.create(modifiedBiomePoints);
                }
            }

            areListInitialized = true;
        }





        //Make sure worlds are initialized before running main logic
        if(((IExtendedBiomeSourceHF)this).getInit()) {

            if(!isDimInitialized) {
                dimension = ((IExtendedBiomeSourceHF) (BiomeSource) (Object) this).getDirtyWorld().getRegistryKey().getValue().toString();
                isDimInitialized = true;
            }



            //Main Logic for choosing difficulty biome section
            int uniqueness = ((IExtendedParameterList<Holder<Biome>>)this.biomePoints).getUniqueness(i, j, k);

            if(this.dimension.equals("minecraft:overworld")) {
                cir.setReturnValue((Holder<Biome>) newSearchTree.get(multiNoiseSampler.sample(i, j, k), MultiNoiseUtil.SearchTree.TreeNode::getSquaredDistance));
            }else {
                cir.setReturnValue((Holder<Biome>) ((MultiNoiseUtil.SearchTree) (Object) defaultSearchTrees[uniqueness]).get(multiNoiseSampler.sample(i, j, k), MultiNoiseUtil.SearchTree.TreeNode::getSquaredDistance));
            }

        }

        //Generates spawn - This is only needed since minecraft generates the spawn before initializing worlds for whatever reason. Spawn will always be overworld unless a mod/datapack changes it.
        if(!((IExtendedBiomeSourceHF)this).getInit()) {
            cir.setReturnValue((Holder<Biome>) newSearchTree.get(multiNoiseSampler.sample(i, j, k), MultiNoiseUtil.SearchTree.TreeNode::getSquaredDistance));
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