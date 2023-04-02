package forest_on_fire.random_terrain_gen;

import forest_on_fire.Biome;

public class Layer {

    private Biome biome;
    private Pattern pattern;

    public Layer(Biome biome, Pattern pattern) {
        this.biome = biome;
        this.pattern = pattern;
    }

    public Biome getBiome() {
        return biome;
    }

    public Pattern getPattern() {
        return pattern;
    }    
}

