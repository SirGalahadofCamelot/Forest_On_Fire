package forest_on_fire;
 
public class Case {
    /**
     * La classe Case est utilisée pour une case voulue "vierge"
     * Elle n'est pas utilisable pour une simulation d'incendie
     * Elle est le parent de toutes les cases utilisables dans un scenario d'incendie
     */
    private int coordX, coordY;//Coordonnées de la case
    private boolean inflammable;//Indicateur d'inflammabilité de la case    

    //Constructeur de la case vierge
    public Case(int valCoordX, int valCoordY, boolean valInflammable){
        this.coordX = valCoordX;
        this.coordY = valCoordY;
        this.inflammable = valInflammable;        
    }    
    
    //Constructeur de copie de case
    public Case(Case c){
        this.coordX = c.coordX;
        this.coordY = c.coordY;
        this.inflammable = c.inflammable;        
    } 
    
    public char myID(){//Methode qui renvoit le caractère ASCII représentatif de la case vierge.
        return 'X';
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }
    
    public boolean isInflammable() {
        return inflammable;
    }    
    public boolean validePourSim(){//Methode qui indique si la case est utilisable dans un scenario d'incendie (faux)
        return false;
    }
    public String getCouleurCase(){//Methode qui renvoit le code ANSI pour la case vierge (utilisé pour l'affichage console dans les IDE compatibles)
        return "\u001B[40m";
    }
    
    @Override
    public String toString() {//Methode toString de la case vierge
        return "Case: coord(" + coordX + ", " + coordY + "), inflammable=" + inflammable;
    }

    //Les methodes hashCode et equals servent à comparer les instances de Case (ou de classes filles). Deux cases sont considérées égales si elles ont les mêmes coordonnées
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + coordX;
        result = prime * result + coordY;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Case other = (Case) obj;
        if (coordX != other.coordX)
            return false;
        if (coordY != other.coordY)
            return false;
        return true;
    }
    
}
