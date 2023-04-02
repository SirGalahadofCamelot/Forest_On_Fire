package GUI;
import GUI.PanelAffichageGrille.CoordGrille;
import forest_on_fire.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import forest_on_fire.Biome.TypeTerrain;
import forest_on_fire.Scenario.Statistiques;
import forest_on_fire.random_terrain_gen.TerrainPreset;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 *Forest on Fire: Section GUI: Classe FPagePrincipale
 * 
 * Frame principal qui affiche le logiciel de simulation d'incendies
 * @author Gorgette Nicolas et Jouteau Louis
 */

public class FPagePrincipale extends javax.swing.JFrame {   
    
    /**
     * Creates new form FPagePrincipale
     */
    private ArrayList<Scenario> listeScenarCharges = new ArrayList<>();//Liste des scénarios chargés dans la page
    static private LinkedHashMap<Double, String> listeHumidite = new LinkedHashMap<Double, String>(); //Listes des humidités possibles
    private boolean simCommencee = false;//Indicateur de si la simulation a déjà commencé
    private Timer timerSim = new Timer();    
    private static Scenario scenarSimul;
    static private FPagePrincipale self;
    static final private String[] optionsPinceau ={"Village", "Eau", "Vegetation", "Route"}; 
    private SplittableRandom rand = new SplittableRandom();
    JInternalFrame iframe = new JInternalFrame("", false, false, false);
    private JFreeChart graphique;//Graphique de la simulation de l'incendie
    private ChartPanel contenantGraph;
    private DefaultPieDataset donneesIncendie;//Dataset qui contient les infos de l'incendie pour le graphique
    
    class taskSimulation extends TimerTask{
        @Override
        public void run(){
                if(!simCommencee) initierSimulation();//Si la simulation de la carte n'a pas commencé, on l'initie
                if(scenarSimul!=null && !scenarSimul.isFireFinished()){//Si le feu n'est pas terminé
                    scenarSimul.passerUneGeneration();
                    actualiserInfoSim();
                    initGrille(scenarSimul);
                    modifDataset(donneesIncendie);//On met à jour les données du dataset                    
                }   
                else{//Si la propagation du feu est finie
                    timerSim.cancel();//On arrête le timer
                    bPlayPauseSim.setText("▶");
                    int opt = JOptionPane.showOptionDialog(self, "La simulation est terminée", "Options",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,new Object[] { "Relancer", "Ok" }, JOptionPane.YES_OPTION); 
                    if(opt==JOptionPane.YES_OPTION){//L'utilisateur veut relancer la simulation
                        //on remet la simulation à zéro
                        //on conserve les memes paramètres vent départ de feu                        
                        initGrille();
                        lLabelHorloge.setText("T=0");
                    }                    
                    simCommencee=false;     
                }
            }    
    }
    
    
    public FPagePrincipale() {   
        self=this;
        listeHumidite.put(0.1, "Humide");listeHumidite.put(0.35, "Normale");listeHumidite.put(0.6, "Sec");listeHumidite.put(0.9, "Très sec");//On remplit la liste des différentes humidités possibles
        initComponents();
        setLocationRelativeTo(null);//Cette ligne fait apparaître la fenêtre au centre de l'écran de l'utilisateur        
        //Le scenario ouvert initialement est un scenario "sans titre"
        Scenario scenDefaut = new Scenario(this.getHumiditeGUI(), this.getLongGrilleGUI(), this.getLargGrilleGUI(), this.getVentGUI(), new ArrayList<Biome>());
        scenDefaut.setNom(nomSansTitreUnique());//Nom du scénario créé
        this.ajouterScenarAuGUI(scenDefaut);
        this.tfNomSauvegarde.setText(scenDefaut.getNom());//Le nom du scénario par défaut est présent dans le champs du nom de la sauvegarde        
        
        //Internal frame qui va contenir la grille
        try{this.iframe.setMaximum(true);}//On essaie de maximiser la fenêtre de la grille
        catch (PropertyVetoException ex) {Logger.getLogger(FPagePrincipale.class.getName()).log(Level.SEVERE, null, ex);}//Affichage de l'erreur//Affichage de l'erreur
        iframe.setBorder(null);
        this.iframe.setVisible(true);
                
        initGrille();//On initie la grille de cases 
        
        this.cbPinceau.setVisible(false);this.slTaillePinceau.setVisible(false);//Etat par défaut des composants relatifs au pinceau
        
    }

    public void initGrille(Scenario scenario){
        //Redessine la grille dans la fenêtre pour le scenario précisé        
        this.pPanelGrille = new PanelAffichageGrille(scenario);//On définit le panel de la grille  
        
        //On actualise le contenu du iframe
        this.iframe.getContentPane().removeAll();
        iframe.getContentPane().add(pPanelGrille);        
        this.dpPourGrille.removeAll();
        this.dpPourGrille.add(iframe);        
               
        pPanelGrille.addMouseListener(new java.awt.event.MouseAdapter() {          
            //On ajoute la méthode qui réagit au click de la souris sur le pPanelGrille
            public void mouseClicked(java.awt.event.MouseEvent evt) { pPanelGrilleMouseClicked(evt, pPanelGrille);}});
        pPanelGrille.addMouseMotionListener(new java.awt.event.MouseMotionListener(){
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt){ pPanelGrilleMouseDragged(evt, pPanelGrille);}//Methode qui réagit à la souris trainée sur la grille
            @Override
            public void mouseMoved(MouseEvent e) {}//Champ nécessaire à la création d'un MouseMotionListener
        });           
       
    }
    //Redessine la fenêtre pour le scenario courant
    public void initGrille(){
        initGrille(this.ScenarioCourant());
    }
        
    public Scenario ScenarioCourant(){
        String nomSceSelect = (String) cbScenarioSwitcher.getSelectedItem();
        if(nomSceSelect!=null){//Si une valeur est bien sélectionnée dans la combobox
            for(Scenario scenar: this.listeScenarCharges){//Si le scnéario sélectionné est dans la liste des scénario chargés, on le renvoit
                if(nomSceSelect.equals(scenar.getNom())) return scenar;
            }
        }        
        return null;//Cette ligne est atteinte seulement si aucun scénario n'est sélectionné 
    }
    
    public Vent getVentGUI(){
        //Lorsqu'on souhaite récupérer le vent sans préciser les combobox, on récupère le Vent choisi du tab "Créer"
        return getVentGUI(cbForceVent, cbOrientaVent);
    }
    public Vent getVentGUI(JComboBox<String> cbForce, JComboBox<String> cbOrientation){
        //Methode qui renvoit le vent sélectionné per l'utilisateur dans le GUI
        
        Vent.Force force = Vent.Force.NUL;//Valeur par défaut de la force
        Vent.Orientation orientation= Vent.Orientation.NORD;//Valeur par défaut de l'orientation
        //On récupère la force
        for(Vent.Force typeDeForce: Arrays.asList(Vent.Force.values())){
            if(typeDeForce.getNom().equals((String)cbForce.getSelectedItem())) force = typeDeForce;
        }     
        //On récupère l'orientation
        for(Vent.Orientation typeOrientation: Arrays.asList(Vent.Orientation.values())){
            if(typeOrientation.name().equals((String)cbOrientation.getSelectedItem())) orientation = typeOrientation;
        }         
        System.out.println("Vent: "+force.getNom()+" de "+orientation.name());
        return new Vent(force, orientation);
    }
    public void setVentGUI(Vent vent, JComboBox cbForce, JComboBox cbOrientation){
        //Changement des valeurs de vents pour les combobox données en entrée
        try{
            cbForce.setSelectedItem(vent.getForce().getNom());//On met la combobox à la valeur de la force du vent
            cbOrientation.setSelectedItem(vent.getOrientation().toString());//On met la combobox à la valeur de l'orientation du vent
        }
        catch(Exception e){System.out.println("Les combobox indiquées ne contiennent pas les infos relatives au vent");}
        
    }   
    
    public void setVentGUI(Vent vent){
        //Quand on veut afficher une valeur de vent dans le GUI, appeler cette methode le fera pour le tab "Créer" 
        setVentGUI(vent,cbForceVent, cbOrientaVent);   
    }
    
    
    
    public double getHumiditeGUI(){
        double humidite = 0.9; //Humidité par défaut
        for(Map.Entry valHumidite : listeHumidite.entrySet()){//Pour chaque type d'humidité possible
            if(valHumidite.getValue().equals((String)cbHumidite.getSelectedItem())) humidite = (double) valHumidite.getKey();//On récupère la valeur numérique de l'humidité sélectionnée
        }
        return humidite;    
    }
    
    public void setHumiditeGUI(Double humidite){        
        this.cbHumidite.setSelectedItem(listeHumidite.get(humidite));
    }
    
    public int getLongGrilleGUI(){//Récupérer la taille (nombre de cases) en longeur entrée par l'utilisateur
        return Integer.parseInt(this.tfLongGrille.getText());
    }   
    
    public void setLongGrilleGUI(int longeur){
        tfLongGrille.setText(""+longeur);
    }    
            
    public int getLargGrilleGUI(){//Récupérer la taille (nombre de cases) en largeur entrée par l'utilisateur
        return Integer.parseInt(this.tfLargGrille.getText());
    }   
    public void setLargGrilleGUI(int largeur){
        tfLargGrille.setText(""+largeur);
    }
    
    
    public void ajouterScenarAuGUI(Scenario scenario){
        //Ajouter le scénario à la liste des scénarios chargés
        this.listeScenarCharges.add(scenario);
        //Ajouter ce scenario à la combobox cbScenarioSwitcher
        cbScenarioSwitcher.addItem(scenario.getNom());
    }
    
    //--Methodes de gestion des sauvegardes--
    
    //Obtenir un nom de type "sans titre" qui n'existe pas
    public String nomSansTitreUnique(){
        String modele = "Sans_titre_";
        int i=0;
        while(nomSauvgExistant(modele+i)){
            i++;
        }
        return modele+i;
    }
    public boolean nomSauvgExistant(String nom, File emplacement){//Indique si le nom entré est celui d'une sauvegarde dans le fichier de sauvegarde précisé
       File file = new File(emplacement.getAbsoluteFile()+File.separator+nom+".txt");
        return file.exists(); 
    }
    
    public boolean nomSauvgExistant(String nom){//Indique si le nom entré est celui d'une sauvegarde dans le fichier de sauvegarde par défaut
        return nomSauvgExistant(nom,Scenario.getEmplacementSauvDefaut());
    }
    
    
    public void afficherScenarTabCreer(Scenario scenario){
        //On entre les infos relatives au scénario dans les champs du tab "créer" (GUI)
        
        //On entre la taille de la grille dans le GUI
        setLongGrilleGUI(scenario.getTailleGrilleX());
        setLargGrilleGUI(scenario.getTailleGrilleY());
        //On entre le vent dans le GUI
        setVentGUI(scenario.getVent());
        //On entre l'humidité dans le GUI
        setHumiditeGUI(scenario.getHumidite());        
        bgTerrain.clearSelection();//Aucune option relative à la nature du terrain est sélectionnée       
        //this.pack();
    }
    
    //Methodes pour la simulation de la propagation du feu
    
    public void initierSimulation(){
        //On initie différentes choses pour préparer le lancement de la simulation
        //On s'assure que la grille contient uniquement des cases valides pour la simulation
        if(this.ScenarioCourant().isReadyForSim()){            
            //On fabrique une copie du scenario courant            
            this.scenarSimul = new Scenario(this.ScenarioCourant());//L'attribut scenarSim contient le scenario qui sera simulé            
            this.scenarSimul.setNom("Clone_de_simulation");
            //On initie le scenario pour la simulation
            this.scenarSimul.initListesInflamm(new HashSet<>());
            this.initGrille(scenarSimul);//On affiche ce scenario dans la fenetre   
            this.simCommencee =true;
            scenarSimul.setVent(getVentGUI(cbForceVentSim,cbOrientaVentSim));//On affecte au scenario de simulation le vent du tab "Simuler"
            pPanelComposition.removeAll();
            
            this.donneesIncendie=createDataset();
            this.graphique = createChart(donneesIncendie);
            this.contenantGraph = new ChartPanel(graphique);
            pPanelComposition.add(contenantGraph); //On fabrique le graphique
        }
        else JOptionPane.showMessageDialog(this, "Il reste des cases vides sur la carte");           
    }
    
    public void playScenario(){
        //Methode qui fait progresser la simulation du feu
        this.timerSim.schedule(new taskSimulation(), 0, this.sVitesseFeu.getValue());//On lance la simulation         
    }
    public void actualiserInfoSim(){
        if(scenarSimul!=null){//Si le scenario de simulation a été initié
            lLabelHorloge.setText("T="+scenarSimul.getHorloge());
            Statistiques stats = scenarSimul.getStats();//Statistiques du scenario
            double total = stats.getNbCases();//Nb total de cases de la grille
            DecimalFormat f = new DecimalFormat("#.##");//On utilise cette variable pour arrondir un calcul à 2 décimales
            this.lVitessePropa.setText("Vitesse de propagation: "+stats.getVitesseFeu()+" cases/tour");
            this.lCasesIntactes.setText("Cases inflammables: "+f.format(stats.getNbCasesIntactes()*100/total)+"%");
            this.lCasesEnflammees.setText("Cases enflammées: "+f.format(stats.getNbCasesEnflammees()*100/total)+"%");
            this.lCasesBruChaud.setText("Cases brûlées chaud: "+f.format(stats.getNbCasesBrulChaud()*100/total)+"%");
            this.lCasesBruFroid.setText("Cases brûlées froid: "+f.format(stats.getNbCasesBrulFroid()*100/total)+"%");
            this.lCasesCendre.setText("Cases cendres: "+f.format(stats.getNbCasesCendre()*100/total)+"%");            
        }
    }
        
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgTerrain = new javax.swing.ButtonGroup();
        bgPinceauOuEnflam = new javax.swing.ButtonGroup();
        jLabel14 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        spSeparateur = new javax.swing.JSplitPane();
        pSideBar = new javax.swing.JPanel();
        tpTabs = new javax.swing.JTabbedPane();
        lSidePanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        tfLongGrille = new javax.swing.JTextField();
        tfLargGrille = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        rbBiomeUniforme = new javax.swing.JRadioButton();
        cbBiomeUniforme = new javax.swing.JComboBox<>();
        rbBiomePreset = new javax.swing.JRadioButton();
        cbBiomePresets = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        cbPinceau = new javax.swing.JComboBox<>();
        slTaillePinceau = new javax.swing.JSlider();
        jLabel6 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        cbOrientaVent = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        cbForceVent = new javax.swing.JComboBox<>();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        cbHumidite = new javax.swing.JComboBox<>();
        jSeparator5 = new javax.swing.JSeparator();
        ckbPinceau = new javax.swing.JCheckBox();
        jSeparator6 = new javax.swing.JSeparator();
        ckbEnflammerCase = new javax.swing.JCheckBox();
        bLancerSimulation = new javax.swing.JButton();
        pSauverCarte = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        tfNomSauvegarde = new javax.swing.JTextField();
        bLancerSimulation2 = new javax.swing.JButton();
        fcChoixDossSauv = new javax.swing.JFileChooser();
        jLabel13 = new javax.swing.JLabel();
        bSauverScenario = new javax.swing.JButton();
        pChargerCarte = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        bLancerSimulation1 = new javax.swing.JButton();
        fcChoixChargSauv = new javax.swing.JFileChooser();
        bChargerScenario = new javax.swing.JButton();
        pSimuler = new javax.swing.JPanel();
        jSeparator7 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        bPlayPauseSim = new javax.swing.JButton();
        bResetSim = new javax.swing.JButton();
        bPasserUneGeneration = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        sVitesseFeu = new javax.swing.JSlider();
        jLabel17 = new javax.swing.JLabel();
        cbOrientaVentSim = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        cbForceVentSim = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        lLabelHorloge = new javax.swing.JLabel();
        lCasesBruChaud = new javax.swing.JLabel();
        lCasesEnflammees = new javax.swing.JLabel();
        lCasesIntactes = new javax.swing.JLabel();
        lCasesBruFroid = new javax.swing.JLabel();
        lCasesCendre = new javax.swing.JLabel();
        lVitessePropa = new javax.swing.JLabel();
        pPanelComposition = new javax.swing.JPanel();
        cbScenarioSwitcher = new javax.swing.JComboBox<>();
        bFermerScenarioCourant = new javax.swing.JButton();
        dpPourGrille = new javax.swing.JDesktopPane();

        jLabel14.setText("Emplacement pour la sauvegarde:");

        jButton2.setText("jButton2");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jButton1.setText("jButton1");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        spSeparateur.setDividerSize(5);
        spSeparateur.setResizeWeight(1.0);
        spSeparateur.setContinuousLayout(true);

        tpTabs.setOpaque(true);

        lSidePanel.setBackground(new java.awt.Color(0, 102, 102));
        lSidePanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel1.setText("Taille : ");

        tfLongGrille.setText("50");
        tfLongGrille.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfLongGrilleActionPerformed(evt);
            }
        });

        tfLargGrille.setText("50");
        tfLargGrille.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfLongGrilleActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Microsoft YaHei Light", 0, 11)); // NOI18N
        jLabel2.setText("X");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel3.setText("Terrain:");

        bgTerrain.add(rbBiomeUniforme);
        rbBiomeUniforme.setSelected(true);
        rbBiomeUniforme.setText("Biome uniforme");
        rbBiomeUniforme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbBiomeUniformeActionPerformed(evt);
            }
        });

        cbBiomeUniforme.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Vegetation clairsemée", "Vegetation espacée", "Vegetation touffue", "Vegetation continue" }));
        cbBiomeUniforme.setSelectedIndex(3);
        cbBiomeUniforme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbBiomeUniformeActionPerformed(evt);
            }
        });

        bgTerrain.add(rbBiomePreset);
        rbBiomePreset.setText("Biome prédéfini");
        rbBiomePreset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbBiomePresetActionPerformed(evt);
            }
        });

        cbBiomePresets.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Terrain classique", "Forêt", "Ile" }));
        cbBiomePresets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbBiomePresetActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel4.setText("Personnalisation du terrain:");

        cbPinceau.setModel(new javax.swing.DefaultComboBoxModel<>(optionsPinceau));

        slTaillePinceau.setMajorTickSpacing(1);
        slTaillePinceau.setMaximum(10);
        slTaillePinceau.setPaintTicks(true);
        slTaillePinceau.setSnapToTicks(true);

        jLabel6.setText("Epaisseur trait:");

        jLabel7.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel7.setText("Vent:");

        cbOrientaVent.setModel(new DefaultComboBoxModel(Arrays.asList(Vent.Orientation.values()).stream().map(Vent.Orientation::name).collect(Collectors.toCollection(ArrayList::new)).stream().toArray(String[]::new)));
        cbOrientaVent.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                changementVentGUI(evt);
            }
        });

        jLabel10.setText("de puissance");

        cbForceVent.setModel(new DefaultComboBoxModel(Arrays.asList(Vent.Force.values()).stream().map(Vent.Force::getNom).collect(Collectors.toCollection(ArrayList::new)).stream().toArray(String[]::new)));
        cbForceVent.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                changementVentGUI(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel8.setText("Humidité:");

        cbHumidite.setModel(new DefaultComboBoxModel(listeHumidite.values().stream().toArray(String[]::new)));
        cbHumidite.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbHumiditeItemStateChanged(evt);
            }
        });

        bgPinceauOuEnflam.add(ckbPinceau);
        ckbPinceau.setText("Pinceau:");
        ckbPinceau.setToolTipText("Sélectionner les cases inflammables de la grille pour les enflammer");
        ckbPinceau.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ckbPinceauCocheDecoche(evt);
            }
        });

        bgPinceauOuEnflam.add(ckbEnflammerCase);
        ckbEnflammerCase.setText("Enflammer case");
        ckbEnflammerCase.setToolTipText("Sélectionner les cases inflammables de la grille pour les enflammer");

        bLancerSimulation.setFont(new java.awt.Font("Perpetua Titling MT", 1, 11)); // NOI18N
        bLancerSimulation.setText("Lancer la simulation");
        bLancerSimulation.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bLancerSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLancerSimulationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout lSidePanelLayout = new javax.swing.GroupLayout(lSidePanel);
        lSidePanel.setLayout(lSidePanelLayout);
        lSidePanelLayout.setHorizontalGroup(
            lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator2)
            .addComponent(jSeparator3)
            .addComponent(jSeparator4)
            .addComponent(jSeparator5)
            .addComponent(jSeparator6)
            .addGroup(lSidePanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ckbPinceau, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slTaillePinceau, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(lSidePanelLayout.createSequentialGroup()
                        .addComponent(cbPinceau, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(43, 43, 43))
            .addGroup(lSidePanelLayout.createSequentialGroup()
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lSidePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(lSidePanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfLongGrille, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfLargGrille, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(lSidePanelLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(cbHumidite, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(lSidePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rbBiomePreset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbBiomeUniforme, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbBiomeUniforme, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbBiomePresets, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(lSidePanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(lSidePanelLayout.createSequentialGroup()
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lSidePanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(ckbEnflammerCase, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(bLancerSimulation, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(lSidePanelLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbOrientaVent, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbForceVent, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        lSidePanelLayout.setVerticalGroup(
            lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lSidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfLongGrille, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfLargGrille, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(23, 23, 23)
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbBiomeUniforme)
                    .addComponent(cbBiomeUniforme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbBiomePreset)
                    .addComponent(cbBiomePresets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbPinceau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ckbPinceau))
                .addGap(18, 18, 18)
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(slTaillePinceau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(29, 29, 29)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbOrientaVent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10)
                    .addComponent(cbForceVent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(lSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cbHumidite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ckbEnflammerCase)
                .addGap(18, 18, 18)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 145, Short.MAX_VALUE)
                .addComponent(bLancerSimulation, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tpTabs.addTab("Créer", lSidePanel);

        pSauverCarte.setBackground(new java.awt.Color(0, 102, 102));

        jLabel9.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel9.setText("Sauver la carte : ");

        jLabel12.setText("Nom de la sauvegarde:");

        bLancerSimulation2.setFont(new java.awt.Font("Perpetua Titling MT", 1, 11)); // NOI18N
        bLancerSimulation2.setText("Lancer la simulation");
        bLancerSimulation2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bLancerSimulation2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLancerSimulationActionPerformed(evt);
            }
        });

        fcChoixDossSauv.setControlButtonsAreShown(false);
        fcChoixDossSauv.setCurrentDirectory(Scenario.getEmplacementSauvDefaut());
        fcChoixDossSauv.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        fcChoixDossSauv.setSelectedFiles(new File[] {Scenario.getEmplacementSauvDefaut()});

        jLabel13.setText("Destination de la sauvegarde:");

        bSauverScenario.setText("Sauver");
        bSauverScenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSauverScenarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pSauverCarteLayout = new javax.swing.GroupLayout(pSauverCarte);
        pSauverCarte.setLayout(pSauverCarteLayout);
        pSauverCarteLayout.setHorizontalGroup(
            pSauverCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pSauverCarteLayout.createSequentialGroup()
                .addGroup(pSauverCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pSauverCarteLayout.createSequentialGroup()
                        .addGroup(pSauverCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pSauverCarteLayout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfNomSauvegarde, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pSauverCarteLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel9)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pSauverCarteLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pSauverCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pSauverCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(fcChoixDossSauv, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(bLancerSimulation2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
            .addGroup(pSauverCarteLayout.createSequentialGroup()
                .addGap(120, 120, 120)
                .addComponent(bSauverScenario, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pSauverCarteLayout.setVerticalGroup(
            pSauverCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pSauverCarteLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabel9)
                .addGap(41, 41, 41)
                .addGroup(pSauverCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(tfNomSauvegarde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fcChoixDossSauv, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bSauverScenario)
                .addGap(94, 94, 94)
                .addComponent(bLancerSimulation2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tpTabs.addTab("Sauver", pSauverCarte);

        pChargerCarte.setBackground(new java.awt.Color(0, 102, 102));

        jLabel11.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel11.setText("Charger une carte:");

        bLancerSimulation1.setFont(new java.awt.Font("Perpetua Titling MT", 1, 11)); // NOI18N
        bLancerSimulation1.setText("Lancer la simulation");
        bLancerSimulation1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bLancerSimulation1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLancerSimulationActionPerformed(evt);
            }
        });

        fcChoixChargSauv.setControlButtonsAreShown(false);
        fcChoixChargSauv.setCurrentDirectory(Scenario.getEmplacementSauvDefaut()
        );

        bChargerScenario.setText("Charger");
        bChargerScenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bChargerScenarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pChargerCarteLayout = new javax.swing.GroupLayout(pChargerCarte);
        pChargerCarte.setLayout(pChargerCarteLayout);
        pChargerCarteLayout.setHorizontalGroup(
            pChargerCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bLancerSimulation1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(fcChoixChargSauv, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(pChargerCarteLayout.createSequentialGroup()
                .addGroup(pChargerCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pChargerCarteLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11))
                    .addGroup(pChargerCarteLayout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(bChargerScenario, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pChargerCarteLayout.setVerticalGroup(
            pChargerCarteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pChargerCarteLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fcChoixChargSauv, javax.swing.GroupLayout.PREFERRED_SIZE, 523, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(bChargerScenario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bLancerSimulation1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tpTabs.addTab("Charger", pChargerCarte);

        pSimuler.setBackground(new java.awt.Color(0, 102, 102));

        bPlayPauseSim.setText("▶");
        bPlayPauseSim.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bPlayPauseSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPlayPauseSimActionPerformed(evt);
            }
        });

        bResetSim.setText("■");
        bResetSim.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bResetSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bResetSimActionPerformed(evt);
            }
        });

        bPasserUneGeneration.setText("⏩");
        bPasserUneGeneration.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bPasserUneGeneration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPasserUneGenerationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(bResetSim, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(bPlayPauseSim)
                .addGap(18, 18, 18)
                .addComponent(bPasserUneGeneration, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {bPasserUneGeneration, bPlayPauseSim, bResetSim});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bResetSim)
                    .addComponent(bPlayPauseSim)
                    .addComponent(bPasserUneGeneration, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {bPasserUneGeneration, bPlayPauseSim, bResetSim});

        jLabel15.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel15.setText("Gérer la progression:");

        jLabel16.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel16.setText("Statistiques:");

        sVitesseFeu.setMajorTickSpacing(10);
        sVitesseFeu.setMaximum(500);
        sVitesseFeu.setMinimum(1);
        sVitesseFeu.setMinorTickSpacing(10);
        sVitesseFeu.setPaintTicks(true);
        sVitesseFeu.setInverted(true);
        sVitesseFeu.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderVitesseFeu(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel17.setText("Vitesse du feu:");

        cbOrientaVentSim.setModel(new DefaultComboBoxModel(Arrays.asList(Vent.Orientation.values()).stream().map(Vent.Orientation::name).collect(Collectors.toCollection(ArrayList::new)).stream().toArray(String[]::new)));
        cbOrientaVentSim.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                changementVentGUISim(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jLabel18.setText("Vent:");

        cbForceVentSim.setModel(new DefaultComboBoxModel(Arrays.asList(Vent.Force.values()).stream().map(Vent.Force::getNom).collect(Collectors.toCollection(ArrayList::new)).stream().toArray(String[]::new)));
        cbForceVentSim.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                changementVentGUISim(evt);
            }
        });

        jLabel19.setText("de puissance");

        lLabelHorloge.setBackground(new java.awt.Color(255, 255, 255));
        lLabelHorloge.setFont(new java.awt.Font("Bookman Old Style", 1, 18)); // NOI18N
        lLabelHorloge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lLabelHorloge.setText("T = 0 ");
        lLabelHorloge.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lCasesBruChaud.setText("Cases brûlées chaud:");

        lCasesEnflammees.setText("Cases enflammées:");

        lCasesIntactes.setText("Cases intactes:");

        lCasesBruFroid.setText("Cases brûlées froid:");

        lCasesCendre.setText("Cases cendres:");

        lVitessePropa.setText("Vitesse de propagation:");

        pPanelComposition.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout pSimulerLayout = new javax.swing.GroupLayout(pSimuler);
        pSimuler.setLayout(pSimulerLayout);
        pSimulerLayout.setHorizontalGroup(
            pSimulerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pSimulerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pSimulerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pSimulerLayout.createSequentialGroup()
                        .addGroup(pSimulerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pSimulerLayout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbOrientaVentSim, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbForceVentSim, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pSimulerLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(sVitesseFeu, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(31, 31, 31))
                    .addGroup(pSimulerLayout.createSequentialGroup()
                        .addGroup(pSimulerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pSimulerLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(56, 56, 56)
                                .addComponent(lLabelHorloge, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel16)
                            .addComponent(pPanelComposition, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lCasesEnflammees)
                            .addComponent(lCasesIntactes)
                            .addComponent(lVitessePropa)
                            .addComponent(lCasesCendre)
                            .addComponent(lCasesBruChaud)
                            .addComponent(lCasesBruFroid))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pSimulerLayout.setVerticalGroup(
            pSimulerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pSimulerLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pSimulerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(lLabelHorloge, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(pSimulerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sVitesseFeu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addGroup(pSimulerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(cbForceVentSim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbOrientaVentSim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(18, 18, 18)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pPanelComposition, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lVitessePropa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lCasesIntactes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lCasesEnflammees)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lCasesBruChaud)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lCasesBruFroid)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lCasesCendre)
                .addGap(24, 24, 24))
        );

        tpTabs.addTab("Simuler", pSimuler);

        cbScenarioSwitcher.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbScenarioSwitcherItemStateChanged(evt);
            }
        });

        bFermerScenarioCourant.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        bFermerScenarioCourant.setText("X");
        bFermerScenarioCourant.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bFermerScenarioCourant.setMaximumSize(new java.awt.Dimension(20, 20));
        bFermerScenarioCourant.setMinimumSize(new java.awt.Dimension(20, 20));
        bFermerScenarioCourant.setPreferredSize(new java.awt.Dimension(20, 20));
        bFermerScenarioCourant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bFermerScenarioCourantActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pSideBarLayout = new javax.swing.GroupLayout(pSideBar);
        pSideBar.setLayout(pSideBarLayout);
        pSideBarLayout.setHorizontalGroup(
            pSideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pSideBarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pSideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pSideBarLayout.createSequentialGroup()
                        .addComponent(cbScenarioSwitcher, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bFermerScenarioCourant, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tpTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pSideBarLayout.setVerticalGroup(
            pSideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pSideBarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pSideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pSideBarLayout.createSequentialGroup()
                        .addComponent(cbScenarioSwitcher)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pSideBarLayout.createSequentialGroup()
                        .addComponent(bFermerScenarioCourant, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(tpTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 766, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        spSeparateur.setRightComponent(pSideBar);

        dpPourGrille.setLayout(new java.awt.BorderLayout());
        spSeparateur.setLeftComponent(dpPourGrille);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spSeparateur, javax.swing.GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spSeparateur)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bLancerSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLancerSimulationActionPerformed
        tpTabs.setSelectedComponent(this.pSimuler);//On met le panel actif comme étant celui de la simulation
    }//GEN-LAST:event_bLancerSimulationActionPerformed

    public void sauverScenarioCourant(){
        //L'utilisateur veut sauvegarder le scénario courant
        boolean aSauver=true;
        Scenario scenarioCourant = ScenarioCourant();//On récupère le scénario courant
        File emplacement = this.fcChoixDossSauv.getCurrentDirectory();//Emplacement où enregistrer le fichier
        String nom = this.tfNomSauvegarde.getText();//On récupère le nom que l'utilisateur a entré pour la sauvegarde        
        if(nomSauvgExistant(nom,emplacement)){//Si le fichier est déjà sauvegardé dans l'emplacement proposé            
            int choix = JOptionPane.showOptionDialog(this, "Ecraser la sauvegarde existante?", "Sauvegarde",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,new Object[] { "Oui", "Annuler" }, JOptionPane.YES_OPTION);
            if(choix==JOptionPane.YES_OPTION){//Si l'utilisateur veut écraser la sauvegarde existante
                //On supprime l'ancienne sauvegarde
                File ancienneSauvg = new File(emplacement.getAbsoluteFile()+File.separator+nom+".txt");
                ancienneSauvg.delete();                    
            }
            else aSauver=false;
        }
        if(aSauver){
            //On sauve le fichier:
            this.cbScenarioSwitcher.removeItem(scenarioCourant.getNom());//On retire l'ancien nom du scénario du combobox "cbScenarioSwitcher"
            this.cbScenarioSwitcher.addItem(nom);//Et on met le nouveau nom du scénario en place dans le combobox
            this.cbScenarioSwitcher.setSelectedItem(nom);//On s'assure que le scénario sélectionné est bien le scénario qu'on va sauver
            scenarioCourant.setNom(nom);//On affecte le nom de la sauvegarde au scenario 
            boolean reussite = scenarioCourant.sauverScenario(nom, emplacement);//On sauve le scénario
            if(reussite)JOptionPane.showMessageDialog(this, "Sauvegarde réussie");
        }
    }
    private void bSauverScenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSauverScenarioActionPerformed
        sauverScenarioCourant();          
    }//GEN-LAST:event_bSauverScenarioActionPerformed

    private void bChargerScenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bChargerScenarioActionPerformed
        //Bouton "Charger" pour permettre de charger le fichier scénario sélectionné 
        File sauvegarde = this.fcChoixChargSauv.getSelectedFile();
        try{
            Scenario scenarCharge = new Scenario(sauvegarde);//On essaie de charger la sauvegarde                       
            this.ajouterScenarAuGUI(scenarCharge);//On ajoute le scénario au GUI
            this.cbScenarioSwitcher.setSelectedItem(scenarCharge.getNom());//Le scenario sélectionné est celui qu'on vient de rajouter            
        }
        catch(IOException ex){
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement du fichier");//Si la charge échoue, l'utilisateur est prévenu
        }        
    }//GEN-LAST:event_bChargerScenarioActionPerformed

    private void tfLongGrilleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfLongGrilleActionPerformed
        //On change la taille de la grille
        if(this.rbBiomePreset.isSelected()) rbBiomePresetActionPerformed(evt);//Si le scenario sélectionné est le biome preset
        else if (this.rbBiomeUniforme.isSelected())rbBiomeUniformeActionPerformed(evt);//Si le scenario sélectionné est le biome uniforme        
    }//GEN-LAST:event_tfLongGrilleActionPerformed

    private void bResetSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bResetSimActionPerformed
        //L'utilisateur veut arrêter la simulation de l'incendie
        this.timerSim.cancel();//On arrête le timer
        bPlayPauseSim.setText("▶");//On s'assure que le bouton play/pause est en configuration de pause (symbole "play")
        this.simCommencee = false;//L'indicateur de commencement de la simulation est réinitialisé
        this.initGrille();//On affiche le scenario courant dans le panel grille
        this.lLabelHorloge.setText("T=0");
        
    }//GEN-LAST:event_bResetSimActionPerformed

    private void bPlayPauseSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPlayPauseSimActionPerformed
        if(bPlayPauseSim.getText().equals("▶")){//Si l'utilisateur a appuyé sur le bouton en mode "play"
            //Lancer la simulation
            this.timerSim = new Timer();
            this.playScenario();
            bPlayPauseSim.setText("⏸");            
        }
        else if (bPlayPauseSim.getText().equals("⏸")){
            //Mettre en pause la simulation
            bPlayPauseSim.setText("▶");
            this.timerSim.cancel();            
        }
    }//GEN-LAST:event_bPlayPauseSimActionPerformed

    private void cbScenarioSwitcherItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbScenarioSwitcherItemStateChanged
        Scenario scenarioCourant = ScenarioCourant();
        //Le scénario sélectionné n'est plus le même
        if(scenarioCourant!=null){//On s'assure qu'un scénario est sélectionné
            this.afficherScenarTabCreer(scenarioCourant); //On affiche les infos du scenario courant dans le tab "Créer" 
            this.initGrille();//On rafraichit l'affichage de la grille de cases
            this.tfNomSauvegarde.setText(scenarioCourant.getNom());//On met le nom du scénario courant dans le champs du nom de sauvegarde
            this.fcChoixDossSauv.setSelectedFile(this.fcChoixChargSauv.getSelectedFile());//On affiche l'emplacement de la sauvegarde du scénario courant dans le tab "sauver"
        }        
    }//GEN-LAST:event_cbScenarioSwitcherItemStateChanged

    private void changementVentGUI(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_changementVentGUI
        // Le choix d'un critère du vent a changé
        Vent vent = getVentGUI();//Choix du vent
        this.ScenarioCourant().setVent(vent);//On change le vent du scenario
        //On change aussi le vent qui sera affiché pour le tab "simuler"
        setVentGUI(vent, cbForceVentSim, cbOrientaVentSim);        
    }//GEN-LAST:event_changementVentGUI

    private void cbHumiditeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbHumiditeItemStateChanged
        // Le choix de l'humidité a changé
        this.ScenarioCourant().setHumidite(this.getHumiditeGUI());
    }//GEN-LAST:event_cbHumiditeItemStateChanged

    private void bFermerScenarioCourantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bFermerScenarioCourantActionPerformed
        // L'utilisateur souhaite fermer le scénario courant
        Scenario scenarioCourant = this.ScenarioCourant();
        if(scenarioCourant!=null && this.cbScenarioSwitcher.getItemCount()>1){//On s'assure qu'un scénario courant est sélectionné, et qu'il reste plus d'un scenario           
            //On demande si l'utilisateur veut sauvegarder son scenario
            int choix = JOptionPane.showOptionDialog(this, "Voulez-vous sauvegarder le scénario?", "Sauvegarde",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,new Object[] { "Oui", "Non" }, JOptionPane.YES_OPTION);
            if(choix==JOptionPane.YES_OPTION)this.sauverScenarioCourant();
                
            //On supprime le scénario du GUI
            cbScenarioSwitcher.removeItem(scenarioCourant.getNom()); 
            this.listeScenarCharges.remove(scenarioCourant);
                
        }  
    }//GEN-LAST:event_bFermerScenarioCourantActionPerformed

    private void pPanelGrilleMouseClicked(MouseEvent evt, PanelAffichageGrille grille) {//Quand la souris est cliquée sur le panel qui contient la grille      
        int X = grille.getCoordSelecX();
        int Y = grille.getCoordSelecY();//On récupère les coord sélectionnées
        Scenario scenario = this.ScenarioCourant();
        System.out.println(ckbEnflammerCase.isSelected());
        
        if(ckbEnflammerCase.isSelected()&& X!=-1 && Y!=-1 && scenario.getCaseGrille(X, Y).isInflammable()){//Si l'utilisateur a coché l'option "enflammer la case", la case est bien dans la grille et la case est inflammable
            ((CaseInflammable)scenario.getCaseGrille(X, Y)).setEtatCase(Etat.ENFLAMME);
            this.pPanelGrille.repaint();
            System.out.println("On a enflammé la case: X="+X+"; Y="+Y);
         }  
    }
    private boolean entreValeurs(int valeur, int min, int max){
        return (valeur>=min && valeur<max);
    }
    
    private void pPanelGrilleMouseDragged(MouseEvent evt, PanelAffichageGrille grille){
        //Méthode appelée lorque la souris est trainée sur la grille
        HashSet<CoordGrille> listePositions = grille.getListePos();//Liste des positions que la souris a parcouru
        int rayon = slTaillePinceau.getValue();//On récupère la taille du rayon pour le trait
        HashSet<CoordGrille> posAjouter = new HashSet<>();
        if(rayon>0){
            for(CoordGrille coord:listePositions){//Pour chaque case parcourue
                int X = coord.getJ();
                int Y = coord.getI();
                for(int i=X-rayon; i<X+rayon;i++){
                    for(int j=Y-rayon;j<Y+rayon;j++){
                        if(entreValeurs(X,0,ScenarioCourant().getTailleGrilleY()) && entreValeurs(Y,0,ScenarioCourant().getTailleGrilleX()))
                            posAjouter.add(new CoordGrille(j,i));//On ajoute la nouvelle case à la liste de cases à ajouter                            
                    }
                }
            }            
        } 
        listePositions.addAll(posAjouter);//On rajoute les nouvelles cases à modifier
        
        for(CoordGrille coord:listePositions){
            int X = coord.getI();
            int Y = coord.getJ();//On récupère les coord sélectionnées
            System.out.println("Coord récupérées drag: "+X+" :"+Y);
            Scenario scenario = this.ScenarioCourant();

            if(ckbPinceau.isSelected() && X!=-1 && Y!=-1){//Si le pinceau est activé et la case sont bien dans la grille
                System.out.println("Ajout case");
                String option = (String)cbPinceau.getSelectedItem();
                //Differentes cases qui sont dessinées selon l'option choisie pour le pinceau
                if(option.equals("Village")){
                    Case caseAjout = (TypeTerrain.VILLAGE.getDensite()<=rand.nextDouble())? new CaseMaison(X,Y,Etat.INTACTE): new CaseVide(X,Y);
                    scenario.setCaseDansGrille(caseAjout);
                }
                if(option.equals("Eau"))scenario.setCaseDansGrille(new CaseEau(X,Y));
                if(option.equals("Vegetation"))scenario.setCaseDansGrille(new CaseVegetation(X,Y, Etat.INTACTE));
                if(option.equals("Route"))scenario.setCaseDansGrille(new CaseVide(X,Y));  
                grille.repaint();
            }                
        }
        listePositions.clear();//On vide la liste des positions pour le prochain drag de la souris        
    }
    
    private void rbBiomeUniformeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbBiomeUniformeActionPerformed
        //L'utilisateur a coché "biome uniforme"
        //On va générer un nouveau scénario (avec les critères choisis dans le GUI) qui remplace l'ancien
        this.rbBiomeUniforme.setSelected(true);//On s'assure que le choix est sélectionné
        Hashtable<Integer, Biome.TypeTerrain> listeTerrains =  new Hashtable<>();//Liste pour récupérer le terrain en fonction du choix dans la combobox "cbBiomeUniforme"
        listeTerrains.put(0, Biome.TypeTerrain.CLAIRSEMEE);listeTerrains.put(1, Biome.TypeTerrain.ESPACEE);listeTerrains.put(2, Biome.TypeTerrain.TOUFFUE);listeTerrains.put(3, Biome.TypeTerrain.CONTINUE);
        //On récupère le biome choisi
        Biome.TypeTerrain choixTerrain = listeTerrains.get(cbBiomeUniforme.getSelectedIndex());
        String nom = this.ScenarioCourant().getNom();
        int idxScenarCourant = this.listeScenarCharges.indexOf(this.ScenarioCourant());//On récupère le scenario courant
        Scenario scenarCourant = new Scenario(getHumiditeGUI() ,getLongGrilleGUI(),getLargGrilleGUI(), getVentGUI(), new Biome(choixTerrain));
        listeScenarCharges.set(idxScenarCourant, scenarCourant);//On affecte au scenario courant un nouveau scenar avec le terrain choisi
        scenarCourant.setNom(nom);//On s'assure que le nveau scenario prote le nom de celui qu'il remplace
        scenarCourant.genererLaCarte();//On génère la carte (creation aléatoire des cases du biome)
        this.initGrille();        
    }//GEN-LAST:event_rbBiomeUniformeActionPerformed

    private void rbBiomePresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbBiomePresetActionPerformed
        //L'utilisateur a coché la case "biome preset"
        //On va générer un nouveau scenario (avec les critères chosis dans le GUI) qui remplace l'ancien
        this.rbBiomePreset.setSelected(true);//On s'assure que le choix est sélectionné
        Hashtable<Integer, TerrainPreset> listeTerrains =  new Hashtable<>();//Liste pour récupérer le terrain en fonction du choix dans la combobox "cbBiomePresets"
        listeTerrains.put(0, TerrainPreset.TERRAIN_CLASSIQUE);listeTerrains.put(1, TerrainPreset.FORET);listeTerrains.put(2, TerrainPreset.ILE);
        //On récupère le biome choisi
        TerrainPreset choixTerrain = listeTerrains.get(this.cbBiomePresets.getSelectedIndex());
        choixTerrain.resetContent();//On s'assure que le preset est bien vierge, et prêt à être utilisé
        String nom = this.ScenarioCourant().getNom();
        int idxScenarCourant = this.listeScenarCharges.indexOf(this.ScenarioCourant());//On récupère le scenario courant
        Scenario scenarCourant = new Scenario(getHumiditeGUI() ,getLongGrilleGUI(),getLargGrilleGUI(), getVentGUI(), new ArrayList<Biome>());        
        listeScenarCharges.set(idxScenarCourant, scenarCourant);//On affecte au scenario courant un nouveau scenar avec le terrain choisi
        scenarCourant.setNom(nom);//On s'assure que le nveau scenario porte le nom de celui qu'il remplace
        scenarCourant.genererCarteAleatoire(choixTerrain);//On génère la carte (creation aléatoire des cases du biome)        
        this.initGrille(scenarCourant); 
    }//GEN-LAST:event_rbBiomePresetActionPerformed

    private void changementVentGUISim(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_changementVentGUISim
        //L'utilisateur veut modifier l'orientation du vent lors de la simulation
        if(scenarSimul!=null)this.scenarSimul.setVent(this.getVentGUI(this.cbForceVentSim,this.cbOrientaVentSim));
    }//GEN-LAST:event_changementVentGUISim

    private void bPasserUneGenerationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPasserUneGenerationActionPerformed
        //On passe une génération lorsqu'on est en pause
        if(this.bPlayPauseSim.getText().equals("▶")){//On s'assure que le bouton pay/pause est en configuration pause (affichage "play")
            taskSimulation sim = new taskSimulation(); sim.run();//On fait passer une génération de la simulation
        }
    }//GEN-LAST:event_bPasserUneGenerationActionPerformed

    private void sliderVitesseFeu(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderVitesseFeu
        //On a modifié le slider de vitesse du feu
        if(this.timerSim!=null && this.bPlayPauseSim.getText().equals("⏸")){//Si le timer est lancé et on est en play (symbole "pause")
            this.timerSim.cancel();
            this.timerSim = new Timer();
            this.timerSim.schedule(new taskSimulation(), 100, this.sVitesseFeu.getValue());
        }
    }//GEN-LAST:event_sliderVitesseFeu

    private void ckbPinceauCocheDecoche(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ckbPinceauCocheDecoche
        // Le choix du pinceau a été coché/décoché
        boolean etat = (this.ckbPinceau.isSelected());
        this.cbPinceau.setVisible(etat);
        this.slTaillePinceau.setVisible(etat);        
    }//GEN-LAST:event_ckbPinceauCocheDecoche

    private static DefaultPieDataset createDataset( ) {
      //Fabrique un dataSet qui contient les statistiques sur l'incendie      
      return modifDataset(new DefaultPieDataset());
               
   }
    private static DefaultPieDataset modifDataset(DefaultPieDataset dataset){
       //Modifie le dataset donné
       
       Statistiques stats = scenarSimul.getStats();//Statistiques du scenario
      dataset.setValue( "Inflammables", stats.getNbCasesIntactes() );   
      dataset.setValue( "Enflammées", stats.getNbCasesEnflammees() );    
      dataset.setValue( "Brûlées chaud", stats.getNbCasesBrulChaud() );  
      dataset.setValue( "Brûlées froid", stats.getNbCasesBrulFroid() ); 
      dataset.setValue( "Cendres", stats.getNbCasesCendre() );  
      dataset.setValue("Non inflammables", stats.getNbCasesNonInfl());
      return dataset;   
    }
    
    private static JFreeChart createChart(PieDataset dataset) {
      //Création d'un graphique en cercle
      JFreeChart chart = ChartFactory.createPieChart("Composition", dataset,true,true,false);
      //Modification des couleurs
      PiePlot piePlot = (PiePlot) chart.getPlot();
      piePlot.setSectionPaint(3, Color.red);
      piePlot.setSectionPaint(2, Color.orange);
      piePlot.setSectionPaint(1, Color.YELLOW);
      piePlot.setSectionPaint(0, Color.GREEN);
      piePlot.setSectionPaint(4, Color.gray);
      piePlot.setSectionPaint(5, Color.blue);
      
      return chart;
   }
        
   /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            //On définit les couleurs générales de l'interface graphique
            UIManager.put( "control", new Color( 55, 55, 55) );
            UIManager.put( "info", new Color( 55, 55, 55) );
            UIManager.put( "nimbusBase", new Color( 18, 30, 49) );
            UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
            UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128) );
            UIManager.put( "nimbusFocus", new Color(115,164,209) );
            UIManager.put( "nimbusGreen", new Color(176,179,50) );
            UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
            UIManager.put( "nimbusLightBackground", new Color( 18, 30, 49) );
            UIManager.put( "nimbusOrange", new Color(191,98,4) );
            UIManager.put( "nimbusRed", new Color(169,46,34) );
            UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
            UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
            UIManager.put( "text", new Color( 230, 230, 230) );
            
            
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FPagePrincipale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FPagePrincipale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FPagePrincipale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FPagePrincipale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FPagePrincipale maPage = new FPagePrincipale();
                maPage.setVisible(true);                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bChargerScenario;
    private javax.swing.JButton bFermerScenarioCourant;
    private javax.swing.JButton bLancerSimulation;
    private javax.swing.JButton bLancerSimulation1;
    private javax.swing.JButton bLancerSimulation2;
    private javax.swing.JButton bPasserUneGeneration;
    private javax.swing.JButton bPlayPauseSim;
    private javax.swing.JButton bResetSim;
    private javax.swing.JButton bSauverScenario;
    private javax.swing.ButtonGroup bgPinceauOuEnflam;
    private javax.swing.ButtonGroup bgTerrain;
    private javax.swing.JComboBox<String> cbBiomePresets;
    private javax.swing.JComboBox<String> cbBiomeUniforme;
    private javax.swing.JComboBox<String> cbForceVent;
    private javax.swing.JComboBox<String> cbForceVentSim;
    private javax.swing.JComboBox<String> cbHumidite;
    private javax.swing.JComboBox<String> cbOrientaVent;
    private javax.swing.JComboBox<String> cbOrientaVentSim;
    private javax.swing.JComboBox<String> cbPinceau;
    private javax.swing.JComboBox<String> cbScenarioSwitcher;
    private javax.swing.JCheckBox ckbEnflammerCase;
    private javax.swing.JCheckBox ckbPinceau;
    private javax.swing.JDesktopPane dpPourGrille;
    private javax.swing.JFileChooser fcChoixChargSauv;
    private javax.swing.JFileChooser fcChoixDossSauv;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JLabel lCasesBruChaud;
    private javax.swing.JLabel lCasesBruFroid;
    private javax.swing.JLabel lCasesCendre;
    private javax.swing.JLabel lCasesEnflammees;
    private javax.swing.JLabel lCasesIntactes;
    private javax.swing.JLabel lLabelHorloge;
    private javax.swing.JPanel lSidePanel;
    private javax.swing.JLabel lVitessePropa;
    private javax.swing.JPanel pChargerCarte;
    private javax.swing.JPanel pPanelComposition;
    private javax.swing.JPanel pSauverCarte;
    private javax.swing.JPanel pSideBar;
    private javax.swing.JPanel pSimuler;
    private javax.swing.JRadioButton rbBiomePreset;
    private javax.swing.JRadioButton rbBiomeUniforme;
    private javax.swing.JSlider sVitesseFeu;
    private javax.swing.JSlider slTaillePinceau;
    private javax.swing.JSplitPane spSeparateur;
    private javax.swing.JTextField tfLargGrille;
    private javax.swing.JTextField tfLongGrille;
    private javax.swing.JTextField tfNomSauvegarde;
    private javax.swing.JTabbedPane tpTabs;
    // End of variables declaration//GEN-END:variables
    private PanelAffichageGrille pPanelGrille;
}
