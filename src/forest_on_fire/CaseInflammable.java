package forest_on_fire;

import java.util.ArrayList;

public abstract class CaseInflammable extends Case{
    /**
     * Classe abstraite, fille de la classe Case, et parente de toutes les cases inflammables
     * Elle sert à regrouper tous les attributs et méthodes communs aux cases inflammables
     * Nous ne chercherons pas à créer un objet CaseInflammable, mais des objets fils de celui-ci, d'où sa nature abstraite
     */

    private char defaultID;//Caractère représentatif de la case. Il est utilisé pour représenter la case lorsque celle-ci est intacte  
    private String defaultColor;//Couleur représentative de la case (intacte). (utilisé pour l'affichage console dans les IDE compatibles)
    private Etat etatCase;//Etat de la case inflammable
    private int compteurInflammation=0;//Compteur servant à suivre le nombre d'iteration d'incendie
    private ArrayList<Double> probaInflamm;//Liste qui contient des coefficients de proabilité d'inflammation. Elle est replie et vidée à chaque tour pour les cases intactes.

    //Constructeur classique pour case inflammable
    public CaseInflammable(int valCoordX, int valCoordY, Etat valEtatCase){        
        super(valCoordX, valCoordY, true);//La case de végétation est inflammable, donc le fait qu'elle soit inflammable est true
        this.etatCase = valEtatCase;    
        this.probaInflamm = new ArrayList<>();
    }
    //Constructeur pour case inflammable, à partir d'une case simple (type Case) comme paramètre
    public CaseInflammable(Case caseSimple, Etat valEtatCase){
        super(caseSimple.getCoordX(),caseSimple.getCoordY(),true);
        this.etatCase = valEtatCase;
        this.probaInflamm = new ArrayList<>();
    }
    //Constructeur de copie de case inflammable
    public CaseInflammable(CaseInflammable c){
        super(c);
        this.etatCase=c.etatCase;
        this.probaInflamm = new ArrayList<>();
    }

    @Override
    public char myID() {//Methode qui renvoit le caractère ASCII représentatif de la case
        if(this.etatCase.equals(Etat.INTACTE))return this.defaultID;//On renvoit le caractère par défaut si la case est intacte
        else return this.etatCase.getMonID();//Sinon on renvoit le caractère correspondante à l'état d'inflammation
    }
    @Override
    public String getCouleurCase(){//Methode qui renvoit la couleur ANSI représentative de la case
        if(this.etatCase.equals(Etat.INTACTE))return this.defaultColor;//On renvoit la couleur par défaut si la case est intacte
        else return this.etatCase.getColor();//Sinon on renvoit la couleur correspondante à l'état d'inflammation
    }

    @Override
    public String toString() {//Methode toString de la case inflammable
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
    public void ajoutProbaInflamm(double probaSup) {//Methode pour ajouter une probabilité d'inflammation à la liste de la case
        this.probaInflamm.add(probaSup);
    }
    public void viderProbaInflamm() {//Methode pour vider la liste de probabilités d'inflammation de la case
        this.probaInflamm.clear();
    }
    public int dureeVieEtat(){//Methode qui renvoit la durée de vie de l'etat (d'inflammation) dans lequel est la case (@Override par les classes filles)
        return -1;//Valeur sans signification
    }
    public char getDefaultID(){//Methode qui renvoit le caractère ASCII pour la case intacte
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
    public String getDefaultColor() {//Methode qui renvoit le code ANSI pour la case intacte (utilisé pour l'affichage console dans les IDE compatibles)
        return defaultColor;
    }
    public void setDefaultColor(String defaultColor) {//Methode qui permet de changer le code ANSI pour la case intacte (utilisé pour l'affichage console dans les IDE compatibles)
        this.defaultColor = defaultColor;
    }    
}
