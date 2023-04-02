package forest_on_fire;

public enum Etat {
    /**
     * Enum qui représente les différents états que peut prendre une case inflammable
     * Attributs: nom (String),possibilité de passer le feu (booléen), état suivant (Etat), caractère (char) et couleur (String de code ANSI)
     */

    //Différents états existants
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

    //Constructeur de la classe Etat
    private Etat(String valNom, boolean peutPasserFeu, Etat etatSuivant, char monID, String color){
        this.nom = valNom;        
        this.peutPasserFeu = peutPasserFeu;
        this.etatSuivant = etatSuivant;
        this.monID = monID;
        this.color = color;
    }

    //Getters
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
