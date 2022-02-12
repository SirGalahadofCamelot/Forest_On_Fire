package forest_on_fire;

public class CaseEau extends CaseVide {

    public CaseEau(int valCoordX, int valCoordY) {
        super(valCoordX, valCoordY);
    }

    public CaseEau(Case caseSimple) {
        super(caseSimple);
    }
    @Override
    public char myID(){
        return '~';
    }
    @Override
    public String getCouleurCase(){
        return "\u001B[48;5;33m";
    }
    @Override
    public String toString() {
        return "Case: coord(" + this.getCoordX() + ", " + this.getCoordY() + "), inflammable=" + this.isInflammable()+", TYPE=CASEEAU";
    }    
    
}
