package forest_on_fire;

public class CaseVegetation extends CaseInflammable {

    public CaseVegetation(int valCoordX, int valCoordY, Etat valEtatCase) {
        super(valCoordX, valCoordY, valEtatCase);
        this.setDefaultID('-');//Ceci est le symbole par défaut de la case vegetation. Il sera affiché lorsque son état est INTACTE
        this.setDefaultColor("\u001B[42m");
    }

    public CaseVegetation(Case caseSimple, Etat valEtatCase) {
        super(caseSimple, valEtatCase);
        this.setDefaultID('-');   
        this.setDefaultColor("\u001B[42m");     
    }

    @Override
    public int dureeVieEtat() {//Selon l'état de la caseVegetation, la durée de vie de chaque état est différent        
        switch(this.getEtatCase()){
            case INTACTE: return -1;
            case ENFLAMME: return 2;
            case BRULECHAUD: return -1;
            case BRULEFROID: return 3;
            case CENDRE: return -1;
        }
        return -1;        
    }    

    @Override
    public String toString() {        
        return super.toString()+", TYPE=CASEVEGETATION";
    }

    @Override
    public boolean validePourSim() {
        return true;
    } 
    
}
