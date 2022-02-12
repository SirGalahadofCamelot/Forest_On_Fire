package forest_on_fire.random_terrain_gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.SplittableRandom;

import forest_on_fire.Biome;
import forest_on_fire.Case;
import forest_on_fire.Scenario;

public class RandTerrainGen {
    
    private TerrainPreset preset;
    private Scenario scenario;

    public RandTerrainGen(TerrainPreset preset, Scenario scenario) {
        this.preset = preset;
        this.scenario = scenario;
    }

    public void generateRandomMap(){              
        int nbRows = this.scenario.getTailleGrilleX();
        int nbColumns = this.scenario.getTailleGrilleY();
        SimpleCell[][] grid = new SimpleCell[nbRows][nbColumns];   

        int firstIter=0;
        for(Layer layer:this.preset.getListeCouches()){
            //Generate the random blob according to blob settings and give it to the biome of the layer, then give it to the scenario
            double densite = layer.getPattern().getDensity();
            int nbIterations = layer.getPattern().getIterations(); 

            grid=bolbGeneration(noiseGrid(grid, densite), nbIterations);//On remplit la grille de bruit (alternance de cases FLOOR/WALL selon la densité donnée), puis on en fait des blobs
            for(int i=0;i<nbRows;i++){
                for(int j=0;j<nbColumns;j++){
                    if(grid[i][j]==SimpleCell.FLOOR) layer.getBiome().ajouterCase(new Case(i, j, false));//Ajouter toutes les cases du blob au biome correspondant
                    else if(firstIter==0) this.preset.getBackground().ajouterCase(new Case(i,j,false));//On ajoute les cases du fond seulement à la 1ere iteration
                }
            }            
            this.scenario.addBiome(layer.getBiome());//On ajoute le biome créé au scénario
            firstIter++;
        }
        this.scenario.addBiome(0, this.preset.getBackground());//On ajoute le fond en premier parmi les biomes 

        ArrayList<Biome> tousLesBiomes = this.scenario.getBiomes();//Tous les biomes du scenario, avec des cases en redondance
        Biome currentBiome;
        //On retire toutes les cases répétées en partant du premier biome 
        for(int indBiome=tousLesBiomes.size()-1;indBiome>=0;indBiome--){//Pour chaque biome du dernier au premier
            currentBiome = tousLesBiomes.get(indBiome);
            for(int nestedInd=indBiome-1;nestedInd>=0;nestedInd--){//On compare ce biome à chacun de ceux avant lui
                for(Case caseI:currentBiome.getCases()){
                    if(tousLesBiomes.get(nestedInd).getCases().contains(caseI))tousLesBiomes.get(nestedInd).getCases().remove(caseI);//Si la case est contenue dans le biome inférieur, elle est retirée
                }
            }
        }

    }
    public enum SimpleCell {WALL, FLOOR};

    static SimpleCell[][] noiseGrid(SimpleCell[][] grid, double densite){
        SplittableRandom rand = new SplittableRandom();  
        for(int i=0;i<grid.length;i++){            
            for(int j=0;j<grid[i].length;j++){
                grid[i][j] = (rand.nextDouble()<=densite) ? SimpleCell.FLOOR : SimpleCell.WALL;
            }            
        }
        return grid;
    }
    static SimpleCell[][] bolbGeneration(SimpleCell[][] grid, int nbIter){
        int floorNeighbors;

        for(int iter=0; iter<nbIter; iter++){

            for(int i=0;i<grid.length;i++){            
                for(int j=0;j<grid[i].length;j++){

                    floorNeighbors = 0;

                    for(int x=0;x<3;x++){
                        for(int y=0;y<3;y++){
                            if(i!=x && j!=y){//Pour tous les voisins sauf la case centrale
                                try{if(grid[i-1+x][j-1+y]==SimpleCell.FLOOR) floorNeighbors++;}
                                catch(ArrayIndexOutOfBoundsException e){}
                            }                            
                        }
                    }
                    grid[i][j] = (floorNeighbors>=5) ? SimpleCell.FLOOR : SimpleCell.WALL;
                }
            }
            
        }
        return grid;
    }
       
}
