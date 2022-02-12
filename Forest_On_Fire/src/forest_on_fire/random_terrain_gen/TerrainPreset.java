package forest_on_fire.random_terrain_gen;

import java.util.ArrayList;
import java.util.Arrays;

import forest_on_fire.Biome;
import forest_on_fire.Biome.TypeTerrain;


public enum TerrainPreset {
    
    TERRAIN_CLASSIQUE(new Biome(TypeTerrain.TOUFFUE), 
        new ArrayList<Layer>(Arrays.asList(new Layer(new Biome(TypeTerrain.CONTINUE), Pattern.MAIN_BLOB),//Description de la composition des couches du terrain classique
                                           new Layer(new Biome(TypeTerrain.VILLE), Pattern.RARE_BLOB),
                                           new Layer(new Biome(TypeTerrain.MARECAGE), Pattern.RARE_BLOB),
                                           new Layer(new Biome(TypeTerrain.LAC), Pattern.RARE_BLOB)))),

    FORET(new Biome(TypeTerrain.TOUFFUE), 
    new ArrayList<Layer>(Arrays.asList(new Layer(new Biome(TypeTerrain.CONTINUE), Pattern.MAIN_BLOB),//Description de la composition des couches de la foret                                       
                                       new Layer(new Biome(TypeTerrain.MARECAGE), Pattern.FREQ_BLOB),
                                       new Layer(new Biome(TypeTerrain.LAC), Pattern.RARE_BLOB),
                                       new Layer(new Biome(TypeTerrain.VILLAGE), Pattern.VERY_RARE_BLOB)))),

    ILE(new Biome(TypeTerrain.LAC), 
    new ArrayList<Layer>(Arrays.asList(new Layer(new Biome(TypeTerrain.TOUFFUE), Pattern.MAIN_BLOB),//Description de la composition des couches de l'ile
                                       new Layer(new Biome(TypeTerrain.CONTINUE), Pattern.FREQ_BLOB),
                                       new Layer(new Biome(TypeTerrain.MARECAGE), Pattern.RARE_BLOB),
                                       new Layer(new Biome(TypeTerrain.VILLE), Pattern.RARE_BLOB))));

    private Biome background;//Biome qui compose le "fond" de la carte
    private ArrayList<Layer> listeCouches;//Différentes couches qui sont posées sur le background

    private TerrainPreset(Biome background, ArrayList<Layer> listeCouches) {
        this.background = background;
        this.listeCouches = listeCouches;
    }

    public Biome getBackground() {
        return background;
    }   

    public ArrayList<Layer> getListeCouches() {
        return listeCouches;
    }      
    
}
