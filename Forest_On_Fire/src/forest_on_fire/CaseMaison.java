package forest_on_fire;

public class CaseMaison extends CaseInflammable {

    public CaseMaison(int valCoordX, int valCoordY, Etat valEtatCase) {
        super(valCoordX, valCoordY, valEtatCase);
        this.setDefaultID('M');
    }

    public CaseMaison(Case caseSimple, Etat valEtatCase) {
        super(caseSimple, valEtatCase);
        this.setDefaultID('M');
    }

    @Override
    public int dureeVieEtat() {//Selon l'état de la caseVegetation, la durée de vie de chaque état est différent        
        switch(this.getEtatCase()){
            case INTACTE: return -1;
            case ENFLAMME: return 3;
            case BRULECHAUD: return -1;
            case BRULEFROID: return 5;
            case CENDRE: return -1;
        }
        return -1;        
    } 

    @Override
    public String toString() {        
        return super.toString()+", TYPE=CASEMAISONN";
    }

    @Override
    public boolean validePourSim() {
        return true;
    }
}
