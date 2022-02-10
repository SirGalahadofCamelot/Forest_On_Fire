package forest_on_fire;

import java.util.ArrayList;

public abstract class CaseInflammable extends Case{

    private char defaultID;
    private Etat etatCase;
    private int compteurInflammation=0;
    private ArrayList<Double> probaInflamm;

    //Constructeur pour case vegetation
    public CaseInflammable(int valCoordX, int valCoordY, Etat valEtatCase){        
        super(valCoordX, valCoordY, true);//La case de végétation est inflammable, donc le fait qu'elle soit inflammable est true
        this.etatCase = valEtatCase;    
        this.probaInflamm = new ArrayList<>();
    }
    //Constructeur pour case vegetation, à partir d'une case simple (type Case) comme paramètre
    public CaseInflammable(Case caseSimple, Etat valEtatCase){
        super(caseSimple.getCoordX(),caseSimple.getCoordY(),true);
        this.etatCase = valEtatCase;
        this.probaInflamm = new ArrayList<>();
    }          
    
    @Override
    public char myID() {
        if(this.etatCase.equals(Etat.INTACTE))return this.defaultID;
        else return this.etatCase.getMonID(); 
        
    }

    @Override
    public String toString() {
        return super.toString()+", compteurInflammation=" + compteurInflammation + ", etatCase=" + etatCase;
    }
    public Etat getEtatCase() {
        return etatCase;
    }

    public void setEtatCase(Etat etatCase) {
        this.etatCase = etatCase;
    }

    public int getCompteurInflammation() {
        return compteurInflammation;
    }

    public void setCompteurInflammation(int compteurInflammation) {
        this.compteurInflammation = compteurInflammation;
    }
    public void indenterCompteurInflammation(){
        ++this.compteurInflammation;
    }
    public ArrayList<Double> getProbaInflamm() {
        return probaInflamm;
    }
    public void ajoutProbaInflamm(double probaSup) {
        this.probaInflamm.add(probaSup);
    }
    public void viderProbaInflamm() {
        this.probaInflamm.clear();
    }
    public int dureeVieEtat(){
        return -1;
    }
    public char getDefaultID() {
        return defaultID;
    }
    public void setDefaultID(char defaultID) {
        this.defaultID = defaultID;
    }     
    public void changerDEtat(){//Cette méthode fait passer l'etat de la case vers son état suivant
        if(this.etatCase!=Etat.CENDRE){//L'etat cendre est un état final, donc une case en cendre reste en cendre
            this.etatCase = this.etatCase.getEtatSuivant();//L'etat courant devient l'etat suivant
            this.compteurInflammation=0;//Le compteur de la vie de l'etat est réinitialisé
        }
        
    }
}
