package forest_on_fire;

public enum Etat {

    CENDRE("Cendre",false, null,'4',"\u001B[48;5;249m"),
    BRULEFROID("Brule froid",false, Etat.CENDRE,'3',"\u001B[48;5;124m"),     
    BRULECHAUD("Brule chaud",true, Etat.BRULEFROID,'2',"\u001B[48;5;220m"), 
    ENFLAMME("Enflamme",true, Etat.BRULECHAUD,'1',"\u001B[48;5;226m"),     
    INTACTE("Intact",false,Etat.ENFLAMME,'0',"\u001B[48;5;7m");
    
    private String nom;    
    private boolean peutPasserFeu;
    private Etat etatSuivant;
    private char monID;
    private String color;

    private Etat(String valNom, boolean peutPasserFeu, Etat etatSuivant, char monID, String color){
        this.nom = valNom;        
        this.peutPasserFeu = peutPasserFeu;
        this.etatSuivant = etatSuivant;
        this.monID = monID;
        this.color = color;
    }

    public Etat getEtatSuivant() {
        return etatSuivant;
    }

    public char getMonID() {
        return monID;
    }

    public boolean peutPasserFeu() {
        return peutPasserFeu;
    }  
    public String getColor(){
        return color;
    }
}
