package forest_on_fire;


public class CaseEau extends CaseVide {
    /**
     * Classe de la case eau, fille de case vide
     * Cette case n'est pas inflammable.
     */
    
    //Constructeur pour case eau
    public CaseEau(int valCoordX, int valCoordY) {
        super(valCoordX, valCoordY);
    }
    //Constructeur pour case eau, à partir d'une case simple (type Case) comme paramètre
    public CaseEau(Case caseSimple) {
        super(caseSimple);
    }
    //Constructeur de copie de case eau
    public CaseEau(CaseEau c){
        super(c);
    }
    @Override
    public char myID(){//Methode qui renvoit le caractère ASCII pour la case
        return '~';
    }
    @Override
    public String getCouleurCase(){//Methode qui renvoit le code ANSI pour la case (utilisé pour l'affichage console dans les IDE compatibles)
        return "\u001B[48;5;33m";
    }
    @Override
    public String toString() {
        return "Case: coord(" + this.getCoordX() + ", " + this.getCoordY() + "), inflammable=" + this.isInflammable()+", TYPE=CASEEAU";
    }    
    
}
