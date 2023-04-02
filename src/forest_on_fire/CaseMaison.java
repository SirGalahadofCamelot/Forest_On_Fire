package forest_on_fire;

public class CaseMaison extends CaseInflammable {
    /**
     * Case de maison, fille de case inflammable 
     */

    //Constructeur de la case maison
    public CaseMaison(int valCoordX, int valCoordY, Etat valEtatCase) {
        super(valCoordX, valCoordY, valEtatCase);
        this.setDefaultID('M');//Ceci est le symbole par défaut de la case maison. Il sera affiché lorsque son état est INTACTE
        this.setDefaultColor("\u001B[48;5;59m");//Ceci est la couleur par défaut de la case maison. Elle sera affichée lorsque son état est INTACTE
    }
    //Constructeur de la case maison, à partir d'une case vierge
    public CaseMaison(Case caseSimple, Etat valEtatCase) {
        super(caseSimple, valEtatCase);
        this.setDefaultID('M');//Ceci est le symbole par défaut de la case maison. Il sera affiché lorsque son état est INTACTE
        this.setDefaultColor("\u001B[48;5;59m");//Ceci est la couleur par défaut de la case maison. Elle sera affichée lorsque son état est INTACTE
    }
    //Constructeur de copie de case maison
    public CaseMaison(CaseMaison c){
        super(c);
        this.setDefaultID('M');//Ceci est le symbole par défaut de la case maison. Il sera affiché lorsque son état est INTACTE
        this.setDefaultColor("\u001B[48;5;59m");//Ceci est la couleur par défaut de la case maison. Elle sera affichée lorsque son état est INTACTE
    }

    @Override
    public int dureeVieEtat() {//Selon l'état de la casemaison, la durée de vie de chaque état est différent        
        switch(this.getEtatCase()){
            case INTACTE: return -1;//La valeur -1 signifie que la durée de vie est sans signification pour l'etat INTACTE
            case ENFLAMME: return 5;//La durée de vie d'une case maison enflammée est de 5 iterations
            case BRULECHAUD: return -1;//La valeur -1 signifie que la durée de vie est sans signification pour l'etat BRULECHAUD
            case BRULEFROID: return 5;//La durée de vie d'une case maison brulée froide est de 5 iterations
            case CENDRE: return -1;//La valeur -1 signifie que la durée de vie est sans signification pour l'etat CENDRE
        }
        return -1;        
    } 

    @Override
    public String toString() {        
        return super.toString()+", TYPE=CASEMAISONN";
    }

    @Override
    public boolean validePourSim() {//Methode qui indique si la case est utilisable dans un scenario d'incendie (vrai)
        return true;
    }
}
