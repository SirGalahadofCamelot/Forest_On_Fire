package GUI;

import forest_on_fire.Scenario;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Hashtable;

/**
 *Forest on Fire: Section GUI: Classe PanelAffichageGrille
 * 
 * Panel qui permet la gestion de la grille de cases de la simulation
 * @author Gorgette Nicolas et Jouteau Louis
 */
public class PanelAffichageGrille extends javax.swing.JPanel implements MouseMotionListener, MouseListener{
    
    //Ce "mouse listener" fait partie du panel "PanelAffichageGrille"         
        
    @Override
    public void mouseClicked(MouseEvent e) {        
        CoordGrille coordCase = convertCoordPixelEnGrille(e.getY(), e.getX());//on inverse X et Y pour etre en accord avec notre norme de rotation
        coordSelecX = coordCase.getI();
        coordSelecY = coordCase.getJ();
        System.out.println("X="+coordCase.getI()+" ,Y="+coordCase.getJ());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("Mouse pressed");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("Mouse released");
        //listePos.clear();
    }

    public void mouseDragged(MouseEvent e) {
        CoordGrille coordCase = convertCoordPixelEnGrille(e.getY(), e.getX());//on inverse X et Y pour etre en accord avec notre norme de rotation
        coordSelecX = coordCase.getI();
        coordSelecY = coordCase.getJ();
        if(e.getX()!=-1 && e.getY()!=-1) listePos.add(convertCoordPixelEnGrille(e.getY(), e.getX()));//Si les cases sont dans la grille (donc de coordonnées différentes de -1)
        listePos.forEach((CoordGrille cg) -> System.out.print(cg.getI()+": "+cg.getJ()));
        System.out.println();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
    
    //Petite sous classe pour contenir des coorodonnées
    public static class CoordGrille {private int i, j;

        public CoordGrille(int i, int j) {
            this.i = i;
            this.j = j;
        }
        public int getI() {return i;}
        public int getJ() {return j;}

        //La comparaison de deux coordGrille dépend de leurs coordonnées i et j
        @Override
        public int hashCode() {int hash = 5;hash = 31 * hash + this.i;hash = 31 * hash + this.j;return hash;}

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {return true;}
            if (obj == null) {return false;}
            if (getClass() != obj.getClass()) {return false;}
            final CoordGrille other = (CoordGrille) obj;
            if (this.i != other.i) {return false;}
            if (this.j != other.j) {return false;}
            return true;
        }        
    }         

    /**
     * Code pour le panel 
     */
    private int nbBoutonsX, nbBoutonsY;
    static private Dimension tailleParDefaut = new Dimension(500,500);//On définit la taille par défaut du panel    
    static private Hashtable<Character, Color> corresCharCoul = new Hashtable<>();
    private Scenario scenarioAffiche;
    private int coordSelecX, coordSelecY;//Coorodonnées de la case sélectionnée par la souris
    private HashSet<CoordGrille> listePos = new HashSet<>();
    
    
    public PanelAffichageGrille(Dimension taille, Scenario scenario) {
        initComponents();
        corresCharCoul.put('X', Color.white);corresCharCoul.put('~',Color.blue);corresCharCoul.put('_',new Color(139,108,3));corresCharCoul.put('-',Color.green);corresCharCoul.put('1',Color.yellow);corresCharCoul.put('2',Color.orange);corresCharCoul.put('3',Color.red);corresCharCoul.put('4',Color.gray);corresCharCoul.put('M',Color.darkGray);
        this.nbBoutonsX = scenario.getTailleGrilleX();
        this.nbBoutonsY = scenario.getTailleGrilleY();
        this.setMinimumSize(taille);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.scenarioAffiche = scenario;
                
    }
    public PanelAffichageGrille(Scenario scenario){
        this(tailleParDefaut, scenario);
    }
    @Override
    public void paintComponent(Graphics g){//Permet de dessiner sur le panel        
        super.paintComponent(g);               
        int cotePixel = cotePixel();//on récupère le coté d'un pixel        
        for(int i=0; i<this.scenarioAffiche.getTailleGrilleX();i++){
            for(int j=0; j<this.scenarioAffiche.getTailleGrilleY();j++){
                this.setPixelColor(j, i, corresCharCoul.get(this.scenarioAffiche.getCaseGrille(i, j).myID()), g);  //i et j sont inversés afin de permettre que la grille soit tournée              
            }
        }
        this.setMinimumSize(new Dimension(cotePixel*this.nbBoutonsY,cotePixel*this.nbBoutonsX));//On définit la taille minimale pour le panel
        
    }
    public int cotePixel(){
        int cote1 = (int) this.getHeight()/nbBoutonsX;//On choisit que le coté d'un pixel soit de taille à remplir l'espace donné
        int cote2 = (int) this.getWidth()/nbBoutonsY;  
        int cote = (cote1>cote2)? cote2 : cote1;//On prend le plus petit des deux cotés
        return (cote!=0)? cote : 1;//On retourne 1 si la division a donnée 0 comme résultat
    }
    
    public int getNbBoutonsX() {
        return nbBoutonsX;
    }

    public void setNbBoutonsX(int nbBoutonsX) {
        this.nbBoutonsX = nbBoutonsX;
    }

    public int getNbBoutonsY() {
        return nbBoutonsY;
    }

    public void setNbBoutonsY(int nbBoutonsY) {
        this.nbBoutonsY = nbBoutonsY;
    }

    public void setScenarioAffiche(Scenario scenarioAffiche) {
        this.scenarioAffiche = scenarioAffiche;
    }    

    public int getCoordSelecX() {
        return coordSelecX;
    }

    public int getCoordSelecY() {
        return coordSelecY;
    }

    public HashSet<CoordGrille> getListePos() {
        return listePos;
    }
    
       
    
    public void setPixelColor(int i, int j, Color couleur, Graphics g){//Methode pour changer la couleur d'unecase de coordonnées (i, j) de la grille à la couleur "couleur"
        int cotePixel = cotePixel();//on récupère le coté d'un pixel        
        g.setColor(couleur);
        g.fillRect(i*cotePixel, j*cotePixel, cotePixel, cotePixel);
        
    }
    public CoordGrille convertCoordPixelEnGrille(int X, int Y){//Pour une paire de coordonnées (X,Y) sur le panel, renvoyer les coordonnées de la case (i,j)
        int cotePixel = cotePixel();//on récupère le coté d'un pixel 
        int coordI = (X/cotePixel>=this.nbBoutonsX)? -1 : X/cotePixel;//Les coordonnées (i,j) sont obtenues en divisant les coordonnées (X,Y) par la taille d'un pixel (reste de la division négligé)
        int coordJ = (Y/cotePixel>=this.nbBoutonsY)? -1 : Y/cotePixel;//Si les coord (i,j) obtenues sont supérieures à ce qui est dans la grille, on renvoit -1
        return new CoordGrille(coordI,coordJ);
    }   

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
