package forest_on_fire;
import java.util.HashSet;
import java.util.SplittableRandom;

import forest_on_fire.Biome.TypeTerrain.ElemDuBiome;

public class Biome {
    /*
    *L'objet biome possède une liste de cases. Ces cases représentent le terrain que couvre le biome.
    *
    *Les cases du biome peuvent prendre deux types (par défaut, VEGETATION et VIDE), dont les proportions (ou densité) sont définies par un coef entre 0 et 1. 
    *Les deux types de cases et le coefficient sont stocké dans l'enum TypeTerrain. Le biome possède cet enum en attribut.
    */

    //L'enum TypeTerrain a été écrit dans la classe biome, afin d'en simplifier l'organisation
    public static enum TypeTerrain{

        //Terrains classiques de l'énoncé
        CLAIRSEMEE("Clairsemée",0.5), //Terrain clairsemé, qui aura des cases VEGETATION et VIDE (choisies par defaut) de densité 0.5
        ESPACEE("Espacée",0.75), 
        TOUFFUE("Touffue",0.9), 
        CONTINUE("Continue",1.0),

        //Terrains supplémentaires
        CHEMIN("Chemin",0.0),
        VILLE("Ville",0.7,ElemDuBiome.MAISON),//Terrain ville, qui aura des cases MAISON et VIDE (choisie par defaut) de densité 0.7
        VILLAGE("Village",0.4, ElemDuBiome.MAISON),
        LAC("Lac",1.0,ElemDuBiome.EAU),
        MARECAGE("Marécage",0.6,ElemDuBiome.VEGETATION,ElemDuBiome.EAU);//Terrain marecage, qui aura des cases VEGETATION et EAU de densité 0.6

        public enum ElemDuBiome {VEGETATION, MAISON, EAU, VIDE};//Quand on donne une densité, il s'agit d'un mélange de cases vides et d'un ElemDuBiome (Vegetation, maison...)

        private String nom;
        private double densite;
        private ElemDuBiome elemBiomeMaj;//Element dont on donne la densité dans le biome
        private ElemDuBiome elemBiomeMin;//Element qui comble le restant de l'espace selon 1-densité

        private TypeTerrain(String nom, double densite) {
            this.nom = nom;
            this.densite = densite;
            this.elemBiomeMaj = ElemDuBiome.VEGETATION;//Par défaut le terrain sera de densité de végétation
            this.elemBiomeMin = ElemDuBiome.VIDE;//Par défaut le terrain sera de 1-densité vide
        }

        private TypeTerrain(String nom, double densite, ElemDuBiome elem) {//Constructeur si on veut imposer le type d'élement de ce terrain en densité principale
            this(nom, densite);
            this.elemBiomeMaj = elem;
        }
        private TypeTerrain(String nom, double densite, ElemDuBiome elem1, ElemDuBiome elem2) {//Constructeur si on veut imposer le type d'élement de ce terrain en densité principale et en densité secondaire
            this(nom, densite);
            this.elemBiomeMaj = elem1;
            this.elemBiomeMin = elem2;
        }
        
        public String getNom() {
            return nom;
        }

        public double getDensite() {
            return densite;
        }

        public ElemDuBiome getElemBiomeMaj() {
            return elemBiomeMaj;
        }  

        public ElemDuBiome getElemBiomeMin() {
            return elemBiomeMin;
        }    
    }    

    private HashSet<Case> cases;//Liste des cases du biome
    private TypeTerrain terrain;//Type de terrain du biome
    

    public Biome(HashSet<Case> cases, Biome.TypeTerrain terrain) {//Creer un nouveau biome en précisant ses cases, et son type de terrain
        this.cases = cases;
        this.terrain = terrain;        
    }
    public Biome(Biome.TypeTerrain terrain) {//Creer un nouveau biome vide en précisant son type de terrain
        this(new HashSet<Case>(), terrain);       
    }


    public void generation(){
        /*
        Methode qui "transforme" les cases vierges d'un biome en cases vegetation/maison/... et vide, selon la densite du terrain
        */
        SplittableRandom rand = new SplittableRandom();
        HashSet<Case> nvlleListe = new HashSet<>();
        for(Case caseI : this.cases){
            //Si la densité est non nulle et (Si la densité n'est pas satuerée(1) ou Si le tirage est inférieur à la densité) 
            if(this.terrain.densite!= 0 && (this.terrain.densite==1 || rand.nextDouble()<=this.terrain.densite)){                
                nvlleListe.add(nvlleCaseSelonTerrain(caseI,this.terrain.getElemBiomeMaj()));//On place une case vegetation/Maison/... à la place de la caseI (de type case) 
            }               
            else{//Sinon soit la densité est de 0 (au quel cas toutes les cases sont vides) soit la probabilité a déclaré que la case sera vide                
                nvlleListe.add(nvlleCaseSelonTerrain(caseI,this.terrain.getElemBiomeMin()));//On place une case vide à la place de la caseI (de type case)//new CaseVide(caseI)
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
    public void viderCasesBiome(){
        this.cases.clear();
    }

    private Case nvlleCaseSelonTerrain(Case caseACombler, ElemDuBiome typeDeCase){
        /**
         * Methode qui prend en entrée une case vide et un type de case T
         * et qui renvoit en sortie une case de type T, créée avec la case vide donnée
         */
        switch(typeDeCase){
            case VEGETATION: return new CaseVegetation(caseACombler, Etat.INTACTE);//Si on est de Terrain VEGETATION, on va créer une case vegetation avec la case vierge donnée
            case MAISON: return new CaseMaison(caseACombler, Etat.INTACTE);//Si on est de Terrain MAISON, on va créer une case maison avec la case vierge donnée
            case EAU: return new CaseEau(caseACombler);//Si on est de Terrain EAU, on va créer une case eau avec la case vierge donnée
            case VIDE: return new CaseVide(caseACombler);//Si on est de Terrain VIDE, on va créer une case vide avec la case vierge donnée
        }
        return null;//Cette ligne ne doit pas être atteinte
    }    
}
