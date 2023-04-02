package forest_on_fire;

public class Vent {
    /*
    Pour créer une instance de Vent, les paramètres requis sont une valeur de chaque enum, Force et Orientation.
    */
  
    static enum Force{
        /* 
        La classe Force est une classe interne à la classe Vent, car l'existance de Force est inutile sans l'existance de Vent.
        Force est un enum, car nous voulons qu'il puisse prendre des valeurs bien spécifiques.
        Force est statique, afin que sa création soit possible avant l'existance d'une istance de Vent. 
        Force peut prendre trois valeurs différentes
        */

        NUL("Nul",0), 
        MODERE("Modere",1), 
        FORT("Fort",2), 
        VIOLENT("Violent",3);

        private String nom;
        private int puissance;

        private Force(String valNom, int valPuissance){
            this.nom = valNom;
            this.puissance = valPuissance;
        }

        public String getNom() {
            return nom;
        }

        public int getPuissance() {
            return puissance;
        }        
    }
    public enum Orientation {NORD, SUD, EST, OUEST}; 

    private Force force;
    private Orientation orientation;
    private double[][] matriceVent;
    
    public Vent(Force valForce, Orientation valOrientation){
        this.orientation = valOrientation;
        this.force = valForce;
        this.matriceVent = this.creerMatricePropaga();
    }

    private double[][] creerMatricePropaga(){
        
        double[][] matrice;
        switch(this.force){//On sélectionne la matrice qui correspond à la force du vent. L'orientation est gérée ensuite, et est NORD par défaut.
            case NUL: matrice = new double[][] {{0   ,0.01,0.01,0.01, 0  },
                                                {0.01,0.2 ,0.3 ,0.2 ,0.01},
                                                {0.01,0.3 ,-1  ,0.3 ,0.01},
                                                {0.01,0.2 ,0.3 ,0.2 ,0.01},
                                                {0   ,0.01,0.01,0.01,0   }};break;

            case MODERE: matrice = new double[][] {{0 ,0   ,0   ,0   , 0},
                                                   {0 ,0.1 ,0.2 ,0.1 , 0},
                                                   {0 ,0.3 ,-1  ,0.3 , 0},
                                                   {0 ,0.3 ,0.4 ,0.3 , 0},
                                                   {0 ,0.02,0.05,0.02, 0}};break;

            case FORT: matrice = new double[][] {{0 ,0 ,0   ,0   ,0   , 0, 0},
                                                 {0 ,0 ,0   ,0   ,0   , 0, 0},
                                                 {0 ,0 ,0.05,0.1 ,0.05, 0, 0},
                                                 {0 ,0 ,0.25,-1  ,0.25, 0, 0},
                                                 {0 ,0 ,0.4 ,0.5 ,0.4 , 0, 0},
                                                 {0 ,0 ,0.05,0.1 ,0.05, 0, 0},
                                                 {0 ,0 ,0   ,0.01,0   , 0, 0}};break;

            case VIOLENT: matrice = new double[][] {{0 ,0 ,0   ,0   ,0   , 0, 0},
                                                    {0 ,0 ,0   ,0   ,0   , 0, 0},
                                                    {0 ,0 ,0   ,0   ,0   , 0, 0},
                                                    {0 ,0 ,0.1 ,-1  ,0.1 , 0, 0},
                                                    {0 ,0 ,0.5 ,0.7 ,0.5 , 0, 0},
                                                    {0 ,0 ,0.2 ,0.3 ,0.2 , 0, 0},
                                                    {0 ,0 ,0.01,0.05,0.01, 0, 0}};break; 
            default: matrice=null;break;           
        }
        if(this.force.equals(Force.NUL)) return matrice;//Si le vent est nul, il est inutile de prendre en compte l'orientation donc on renvoit la matrice 

        int nbRotaHoraires=0;
        switch(orientation){//On tourne la matrice afin qu'elle corressponde à l'orientation du vent
            case NORD: nbRotaHoraires=0; break;//Car NORD est l'orientation par défaut, la rotation est inutile
            case SUD: nbRotaHoraires=2;break;
            case EST: nbRotaHoraires=3;break;
            case OUEST: nbRotaHoraires=1;break;
        }
        return rotationHoraire(matrice, nbRotaHoraires);
        
    }
    static double[][] rotationHoraire(double[][] matrice, int nbRotations){
        
        if(matrice.length!=matrice[0].length){//Pour effectuer les rotations sur ces matrices, elles doivent être carrées
            System.out.println("Matrice non carrée; rotation impossible");
            return null;
        }
        //Algorithme de rotation de matrice, effectué "nbRotations" fois
        double[][] matriceIntermed;
        int taille=matrice.length;
        for(int r=0;r<nbRotations;r++){
            matriceIntermed = new double[taille][taille];
            for(int i=0;i<taille;i++){
                for(int j=0;j<taille;j++){
                    matriceIntermed[i][j] = matrice[j][taille-i-1];
                }
            }
            matrice=matriceIntermed;
        }
        return matrice;
    }
    public void afficherMatrice(){
        for(double[] ligne:this.matriceVent){
            for(double valeur:ligne){
                System.out.print(""+valeur+"\t");
            }
            System.out.println();
        }
    }

    public Force getForce() {
        return force;
    }

    public void setForce(Force force) {
        this.force = force;
        this.matriceVent=this.creerMatricePropaga();
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        this.matriceVent=this.creerMatricePropaga();
    }

    public double[][] getMatriceVent() {
        return matriceVent;
    }    
}
