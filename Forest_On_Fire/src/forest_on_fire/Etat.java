package forest_on_fire;

public enum Etat {

    CENDRE("Cendre",false, null,'4'),
    BRULEFROID("Brule froid",false, Etat.CENDRE,'3'),     
    BRULECHAUD("Brule chaud",true, Etat.BRULEFROID,'2'), 
    ENFLAMME("Enflamme",true, Etat.BRULECHAUD,'1'),     
    INTACTE("Intact",false,Etat.ENFLAMME,'0');
    
    private String nom;    
    private boolean peutPasserFeu;
    private Etat etatSuivant;
    private char monID;

    private Etat(String valNom, boolean peutPasserFeu, Etat etatSuivant, char monID){
        this.nom = valNom;        
        this.peutPasserFeu = peutPasserFeu;
        this.etatSuivant = etatSuivant;
        this.monID = monID;
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
}
