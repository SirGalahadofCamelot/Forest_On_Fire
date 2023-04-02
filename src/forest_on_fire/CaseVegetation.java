package forest_on_fire;

public class CaseVegetation extends CaseInflammable {
    /**
     * Case de vegetation, fille de case inflammable 
     */

    //Constructeur de la case vegetation
    public CaseVegetation(int valCoordX, int valCoordY, Etat valEtatCase) {
        super(valCoordX, valCoordY, valEtatCase);
        this.setDefaultID('-');//Ceci est le symbole par défaut de la case vegetation. Il sera affiché lorsque son état est INTACTE
        this.setDefaultColor("\u001B[42m");//Ceci est la couleur par défaut de la case vegetation. Elle sera affichée lorsque son état est INTACTE
    }
    //Constructeur de la case vegetation, à partir d'une case vierge
    public CaseVegetation(Case caseSimple, Etat valEtatCase) {
        super(caseSimple, valEtatCase);
        this.setDefaultID('-');//Ceci est le symbole par défaut de la case vegetation. Il sera affiché lorsque son état est INTACTE   
        this.setDefaultColor("\u001B[42m");//Ceci est la couleur par défaut de la case vegetation. Elle sera affichée lorsque son état est INTACTE     
    }
    //Constructeur de copie de case vegetation
    public CaseVegetation(CaseVegetation c){
        super(c);
        this.setDefaultID('-');//Ceci est le symbole par défaut de la case vegetation. Il sera affiché lorsque son état est INTACTE   
        this.setDefaultColor("\u001B[42m");//Ceci est la couleur par défaut de la case vegetation. Elle sera affichée lorsque son état est INTACTE 
    }

    @Override
    public int dureeVieEtat() {//Selon l'état de la caseVegetation, la durée de vie de chaque état est différent        
        switch(this.getEtatCase()){
            case INTACTE: return -1;//La valeur -1 signifie que la durée de vie est sans signification pour l'etat INTACTE
            case ENFLAMME: return 2;//La durée de vie d'une case vegetation enflammée est de 2 iterations
            case BRULECHAUD: return -1;//La valeur -1 signifie que la durée de vie est sans signification pour l'etat BRULECHAUD
            case BRULEFROID: return 3;//La durée de vie d'une case vegetation brulée froide est de 3 iterations
            case CENDRE: return -1;//La valeur -1 signifie que la durée de vie est sans signification pour l'etat CENDRE
        }
        return -1;        
    }    

    @Override
    public String toString() {        
        return super.toString()+", TYPE=CASEVEGETATION";
    }

    @Override
    public boolean validePourSim() {//Methode qui indique si la case est utilisable dans un scenario d'incendie (vrai)
        return true;
    } 
    
}
