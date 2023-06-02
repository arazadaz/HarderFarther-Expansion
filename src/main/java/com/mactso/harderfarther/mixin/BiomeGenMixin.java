package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.BiomeConfig;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/*@Mixin(BiomeSource.class)
public class BiomeGenMixin {

    @Final
    @Mutable
    @Shadow
    private Set<Holder<Biome>> biomes;

    @Inject(at = @At(value = "TAIL"), method = "<init>(Ljava/util/List;)V", cancellable = false)
    private void onGenerate(List biomesList, CallbackInfo ci) {
        Set<Holder<Biome>> biomes = this.biomes;
        biomes.removeIf((biome) -> !biome.isRegistryKeyId(new Identifier("minecraft", "snowy_plains")));
        this.biomes = biomes;
    }
}*/

@Mixin(MultiNoiseBiomeSource.class)
public class BiomeGenMixin extends BiomeSource{

    @Final
    @Mutable
    @Shadow
    private MultiNoiseUtil.ParameterRangeList<Holder<Biome>> biomePoints;

    private static ArrayList<MultiNoiseUtil.ParameterRangeList<Holder<Biome>>> difficultySections = new ArrayList<>();

    private static int x = 0;

    protected BiomeGenMixin(Stream<Holder<Biome>> biomes) {
        super(biomes);
    }

    @Inject(at = @At(value = "RETURN"), method = "Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource;getNoiseBiome(IIILnet/minecraft/world/biome/source/util/MultiNoiseUtil$MultiNoiseSampler;)Lnet/minecraft/util/Holder;", cancellable = false)
    private void onGenerate(int i, int j, int k, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler, CallbackInfoReturnable<Holder<Biome>> cir) {


        if(x == 0){

            difficultySections.add(this.biomePoints); //Original biome list will be index 0
            List<Pair<MultiNoiseUtil.NoiseHypercube, Holder<Biome>>> modifiedBiomePoints = new ArrayList<>();

            BiomeConfig.getDifficultySections().forEach((difficultySection) -> {
                biomePoints.getEntries().forEach((noiseHypercubeHolderPair -> {

                    String biome = noiseHypercubeHolderPair.getSecond().getKey().get().toString().substring(39);
                    biome = biome.substring(0, biome.length()-1);

                    if(difficultySection.second.contains(biome)){
                        modifiedBiomePoints.add(new Pair<>(noiseHypercubeHolderPair.getFirst(), noiseHypercubeHolderPair.getSecond()));
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
        this.biomePoints = difficultySections.get(1);
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

/*@Mixin(PalettedContainer.class)
public class BiomeGenMixin {

    @Inject(at = @At(value = "RETURN"), method = "m_hnfmqovh", remap = false)
    private void onGenerate(CallbackInfoReturnable<Set<Holder<Biome>>> cir) {
        //cir.setReturnValue(this.get(15));

    }

    @Shadow
    protected <T> T get(int index) {
        return null;
    }
}*/

/*@Mixin(Chunk.class)
public class BiomeGenMixin {

    @Final
    @Shadow
    protected ChunkSection[] sectionArray;

    @Shadow
    private static void fillSectionArray(HeightLimitView world, Registry<Biome> biomeRegistry, ChunkSection[] sectionArray){};

    @Inject(at = @At(value = "TAIL"), method = "<init>")
    private void onGenerate(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry biomeRegistry, long inhabitedTime, ChunkSection[] sectionArrayInitializer, BlendingData blendingData, CallbackInfo ci) {

        ExtraRegistry newBiomeList = (ExtraRegistry)biomeRegistry;

        Set<Identifier> biomeIds = biomeRegistry.getIds();

        for (Identifier biomeId : biomeIds) {
            if (biomeId != new Identifier("minecraft", "snowy_plains")){
                newBiomeList.removeId(biomeId);
            }
        }

        biomeRegistry = (Registry<Biome>)newBiomeList;
        fillSectionArray(heightLimitView, biomeRegistry, this.sectionArray);

    }

}*/

/*@Mixin(MultiNoiseBiomeSource.class)
public class BiomeGenMixin {

    @Final
    @Shadow
    private MultiNoiseUtil.ParameterRangeList<Holder<Biome>> biomePoints;

    @Inject(at = @At(value = "TAIL"), method = "<init>(Lnet/minecraft/world/biome/source/util/MultiNoiseUtil$ParameterRangeList;Ljava/util/Optional;)V")
    private void onNoiseBiomes(MultiNoiseUtil.ParameterRangeList biomePoints, Optional instance, CallbackInfo ci){
        System.out.println(biomePoints.toString());
        System.out.println(biomePoints.getEntries().toString());
    }

}*/


