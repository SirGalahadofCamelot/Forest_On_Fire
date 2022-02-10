package forest_on_fire;

public class CaseVide extends Case{

    //Constructeur pour case vide
    public CaseVide(int valCoordX, int valCoordY) {
        super(valCoordX, valCoordY, false);//Une case vide ne peut pas bruler, donc son inflammable est false
    }
    //Constructeur pour case vide, à partir d'une case simple (type Case) comme paramètre
    public CaseVide(Case caseSimple){
        super(caseSimple.getCoordX(),caseSimple.getCoordY(),false);
    }
    public boolean validePourSim(){
        return true;
    }
    @Override
    public char myID(){
        return '_';
    }
    @Override
    public String getCouleurCase(){
        return "\u001B[48;5;143m";
    }
    @Override
    public String toString() {
        return super.toString()+", TYPE=CASEVIDE";
    }
    
}
