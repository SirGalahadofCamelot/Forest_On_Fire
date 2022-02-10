package forest_on_fire;
import java.util.HashSet;
import java.util.SplittableRandom;

public class Biome {

    static enum TypeTerrain{

        //Terrains classiques de l'énoncé
        CLAIRSEMEE("Clairsemee",0.5), 
        ESPACEE("Espacee",0.75), 
        TOUFFUE("Touffue",0.9), 
        CONTINUE("Continue",1.0),

        //Terrains supplémentaires
        CHEMIN("Chemin",0.0),
        VILLE("Ville",0.7,ElemDuBiome.MAISON),
        VILLAGE("Village",0.4, ElemDuBiome.MAISON);

        public enum ElemDuBiome {VEGETATION, MAISON};//Quand on donne une densité, il s'agit d'un mélange de cases vides et d'un ElemDuBiome (Vegetation, maison...)

        private String nom;
        private double densite;
        private ElemDuBiome elemBiome;

        private TypeTerrain(String nom, double densite) {
            this.nom = nom;
            this.densite = densite;
            this.elemBiome = ElemDuBiome.VEGETATION;//Par défaut le terrain sera de densité de végétation
        }

        private TypeTerrain(String nom, double densite, ElemDuBiome elem) {//Constructeur si on veut imposer le type d'élement de ce terrain
            this(nom, densite);
            this.elemBiome = elem;
        }
        
        public String getNom() {
            return nom;
        }

        public double getDensite() {
            return densite;
        }

        public ElemDuBiome getElemBiome() {
            return elemBiome;
        }   
    }    

    private HashSet<Case> cases;
    private TypeTerrain terrain;
    

    public Biome(HashSet<Case> cases, Biome.TypeTerrain terrain) {
        this.cases = cases;
        this.terrain = terrain;        
    }

    public void generation(){
        /*
        Fonction qui "transforme" les cases vierges d'un biome en cases vegetation/maison/... et vide, selon la densite du terrain
        */
        SplittableRandom rand = new SplittableRandom();
        HashSet<Case> nvlleListe = new HashSet<>();
        for(Case caseI : this.cases){
            if(rand.nextDouble()<=this.terrain.densite){//Si le tirage est inférieur à la densité                
                nvlleListe.add(nvlleCaseSelonTerrain(caseI));//On place une case vegetation/Maison/... à la place de la caseI (de type case) 
            }               
            else{                
                nvlleListe.add(new CaseVide(caseI));//On place une case vide à la place de la caseI (de type case)
            }                 
        }
        this.cases=nvlleListe;//On met la liste de cases mises à jour dans le this.cases du biome
    }
    public void ajouterCase(Case caseSupp){
        this.cases.add(caseSupp);
    }

    public HashSet<Case> getCases() {
        return cases;
    }

    public void setCases(HashSet<Case> cases) {
        this.cases = cases;
    }

    public TypeTerrain getTerrain() {
        return terrain;
    }

    public void setTerrain(TypeTerrain terrain) {
        this.terrain = terrain;
    } 

    private Case nvlleCaseSelonTerrain(Case caseACombler){
        switch(this.terrain.getElemBiome()){
            case VEGETATION: return new CaseVegetation(caseACombler, Etat.INTACTE);//Si on est de Terrain VEGETATION, on va créer une case vegetation avec la case vierge donnée
            case MAISON: return new CaseMaison(caseACombler, Etat.INTACTE);
        }
        return null;
    }

    
}
