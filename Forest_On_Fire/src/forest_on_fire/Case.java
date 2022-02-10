package forest_on_fire;
 
public class Case {
    private int coordX, coordY;
    private boolean inflammable;    

    public Case(int valCoordX, int valCoordY, boolean valInflammable){
        this.coordX = valCoordX;
        this.coordY = valCoordY;
        this.inflammable = valInflammable;        
    }    
    public char myID(){
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
    public boolean validePourSim(){
        return false;
    }
    
    @Override
    public String toString() {
        return "Case: coord(" + coordX + ", " + coordY + "), inflammable=" + inflammable;
    }

    //Les methodes hashCode et equals servent à comparer les instances de Case. Deux cases sont considérées égales si elles ont les mêmes coordonnées
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
