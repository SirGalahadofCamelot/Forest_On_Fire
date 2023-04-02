package forest_on_fire;

public class CaseVide extends Case{
    /**
     * Classe de la case vide, fille de case vierge
     * Cette case n'est pas inflammable.
     */

    //Constructeur pour case vide
    public CaseVide(int valCoordX, int valCoordY) {
        super(valCoordX, valCoordY, false);//Une case vide ne peut pas bruler, donc son inflammable est false
    }
    //Constructeur pour case vide, à partir d'une case simple (type Case) comme paramètre
    public CaseVide(Case caseSimple){
        super(caseSimple.getCoordX(),caseSimple.getCoordY(),false);
    }
    public CaseVide(CaseVide c){
        super(c);
    }
    
    @Override
    public boolean validePourSim(){//Methode qui indique si la case est utilisable dans un scenario d'incendie (vrai)
        return true;
    }
    @Override
    public char myID(){//Methode qui renvoit le caractère ASCII pour la case
        return '_';
    }
    @Override
    public String getCouleurCase(){//Methode qui renvoit le code ANSI pour la case (utilisé pour l'affichage console dans les IDE compatibles)
        return "\u001B[48;5;143m";
    }
    @Override
    public String toString() {
        return super.toString()+", TYPE=CASEVIDE";
    }
    
}
