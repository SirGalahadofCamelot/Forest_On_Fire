package forest_on_fire;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;

import forest_on_fire.Vent.Force;
import forest_on_fire.Vent.Orientation;
import forest_on_fire.random_terrain_gen.RandTerrainGen;
import forest_on_fire.random_terrain_gen.TerrainPreset;


public class Scenario {
    /**
     * La classe scenario représente la partie centrale de la simulation d'incendie.
     * L'objet scenario possède:
     * -la grille de case, qui représente le terrain (Case[][])
     * -l'humidité globale du scenario (double)
     * -le vent(Vent)
     * -une liste des ses biomes (ArrayList<Biome>)
     * -une liste des cases inflammables, et des cases qui transmettent le feu (HashSet<CaseInflammable>)
     * - un objet statistiques qui permet de regrouper les infos sur l'évolution du feu
     */
    
    public class Statistiques {
        
        private int nbCases=0, vitesseFeu=0, nbCasesIntactes=0, nbCasesEnflammees=0, nbCasesBrulChaud=0, nbCasesBrulFroid=0, nbCasesCendre=0, nbCasesNonInfl=0;
        
        public int getNbCases(){return nbCases;}
        
        public int getVitesseFeu() {return vitesseFeu;}

        public int getNbCasesIntactes() {return nbCasesIntactes;}
        
        public int getNbCasesEnflammees() {return nbCasesEnflammees;}

        public int getNbCasesBrulChaud() {return nbCasesBrulChaud;}

        public int getNbCasesBrulFroid() {return nbCasesBrulFroid;}

        public int getNbCasesCendre() {return nbCasesCendre;}
        
        public int getNbCasesNonInfl() {
            nbCasesNonInfl = nbCases-nbCasesIntactes-nbCasesEnflammees-nbCasesBrulChaud-nbCasesBrulFroid-nbCasesCendre;//Les cases non inflammables sont les cases restantes
            return nbCasesNonInfl;}
    }

    private String nom;
    private double humidite;
    private int tailleGrilleX, tailleGrilleY, horloge=0;
    private Vent vent;//Valeur du vent du scénario
    private Case[][] grille;//Grille de cases de la simulation 
    private ArrayList<Biome> biomes;
    private HashSet<CaseInflammable> casesInflammables, casesTransFeu;//Liste des cases inflammables, et des cases qui peuvent transmettre le feu.
    private HashSet<CaseInflammable> casesCendre= new HashSet<>();//Liste qui contient les cases qui ne propagent plus le feu (cendre)
    private HashSet<CaseInflammable> casesPlusIntactes= new HashSet<>();//Liste qui contient les cases qui passent de l'état intact à enflammé
    private HashSet<CaseInflammable> casesBrulentPlus= new HashSet<>();//Liste qui contient les cases qui ne propagent plus le feu (BruleFroid)
    private static File emplacementSauvDefaut = new File(System.getProperty("user.dir")+File.separator+"Sauvegardes_scenarios");//Emplacement de sauvegarde de scénarios par défaut
    private Statistiques stats;
    
    //Constructeur avec en paramètre une liste de biomes, et la hauteur/largeur de la grille
    //Comme la grille n'existe pas encore, on la remplit avec des cases de type Case
    //Ce constructeur est plutot pour une nouvelle simulation
    public Scenario(double humidite, int tailleGrilleX, int tailleGrilleY, Vent vent, ArrayList<Biome> valBiomes) {
        this.humidite = humidite;
        this.tailleGrilleX = tailleGrilleX;
        this.tailleGrilleY = tailleGrilleY;        
        this.vent = vent;
        this.grille = new Case[tailleGrilleX][tailleGrilleY];
        for(int i=0; i<tailleGrilleX; i++){
            for(int j=0; j<tailleGrilleY; j++){
                this.grille[i][j]=new Case(i, j, false);
            }
        }
        this.biomes = valBiomes;
        if(!Scenario.emplacementSauvDefaut.exists()) Scenario.emplacementSauvDefaut.mkdir();//On fabrique le dossier de sauvegarde par défaut s'il est inexistant
    }
    //Constructeur avec en paramètre une liste de biomes et un tableau de cases (qui représente la grille)
    //Ce constructeur est plutot pour une simulation déjà "prête", car la grille est un paramètre.
    public Scenario(double humidite, Case[][] grille, Vent vent, ArrayList<Biome> valBiomes) {
        this.humidite = humidite;
        this.tailleGrilleX = grille.length;
        this.tailleGrilleY = grille[0].length;        
        this.vent = vent;
        this.grille = grille;
        this.biomes = valBiomes;
        if(!Scenario.emplacementSauvDefaut.exists()) Scenario.emplacementSauvDefaut.mkdir();//On fabrique le dossier de sauvegarde par défaut s'il est inexistant
    }
    //Constructeur avec en paramètre un unique biome, et la hauteur/largeur de la grille
    //Ce constructeur est plutot pour une nouvelle simulation, qui représente un seul biome
    public Scenario(double humidite, int tailleGrilleX, int tailleGrilleY, Vent vent, Biome biome){
        this(humidite, tailleGrilleX, tailleGrilleY, vent, new ArrayList<Biome>(Arrays.asList(biome)));
    }
     //Ce constructeur sert à récupérer un scénario (emplacement fichier en "File") à partir d'une sauvegarde fichier
    public Scenario(File emplacementSauv) throws IOException{
        this.chargerScenario(emplacementSauv);                
        if(!Scenario.emplacementSauvDefaut.exists()) Scenario.emplacementSauvDefaut.mkdir();//On fabrique le dossier de sauvegarde par défaut s'il est inexistant
    }
    
    //Ce constructeur sert à récupérer un scénario à partir d'une sauvegarde fichier (titre en "String") dans le dossier de sauvegarde par défaut
    public Scenario(String nomFichier){
        try{this.chargerScenario(nomFichier);}
        catch(IOException ex){System.out.println("Erreur de chargement du fichier");}        
        if(!Scenario.emplacementSauvDefaut.exists()) Scenario.emplacementSauvDefaut.mkdir();//On fabrique le dossier de sauvegarde par défaut s'il est inexistant
    }

    //Constructeur de copie de Scénario
    public Scenario(Scenario s) {
        this.nom = s.nom;
        this.humidite = s.humidite;
        this.tailleGrilleX = s.tailleGrilleX;
        this.tailleGrilleY = s.tailleGrilleY;
        this.vent = s.vent;
        this.grille = new Case[tailleGrilleX][tailleGrilleY];
        //Pour que les copies soient bien différentes, on fabirque également une copie de la grille
        for(int i = 0; i < s.grille.length; i++) { 
            for(int j=0;j<s.grille[i].length;j++){
                if(s.grille[i][j] instanceof CaseEau) this.grille[i][j] = new CaseEau((CaseEau) s.grille[i][j]);
                else if(s.grille[i][j] instanceof CaseVide) this.grille[i][j] = new CaseVide((CaseVide) s.grille[i][j]);
                else if(s.grille[i][j] instanceof CaseVegetation) this.grille[i][j] = new CaseVegetation((CaseVegetation) s.grille[i][j]);
                else if(s.grille[i][j] instanceof CaseMaison) this.grille[i][j] = new CaseMaison((CaseMaison) s.grille[i][j]);
            }            
        }        
    }
    
    
    public void addBiome(Biome nvBiome){
        this.biomes.add(nvBiome);
    }
    public void addBiome(int index, Biome nvBiome){
        this.biomes.add(index, nvBiome);
    }
    //Methode qui "génère" le contenu du terrain de chaque biome, créant ainsi la carte de la simulation
    //Aucune entrée, aucune sortie
    public void genererLaCarte(){
        boolean ready = true;
        if(!this.isReadyForSim()){//Si la carte n'est pas prete pour une simulation, on synchronise les biomes avec la grille
            if(this.biomes.isEmpty()) {System.out.println("AUNCUN BIOME DANS LE SCENARIO: Veuillez ajouter au moins un biome");ready=false;}

            else if(this.biomes.size()==1){//Si il y a un seul biome, on admet que toute la carte sera de ce biome. Toutes les cases vierges de la grilles sont données au biome.
                for(int i=0;i<this.tailleGrilleX;i++){
                    for(Case caseJ:this.grille[i]){
                        this.biomes.get(0).ajouterCase(caseJ);//On met toutes les cases vierges de la grille dans la liste de cases du biome
                    }
                }
                this.biomes.get(0).generation();//Les cases vierges donnees au biome sont converties selon la densite de vegetation du biome
                this.rangerCasesBiomesDansGrille();//Les cases générées pour chaque biome sont placées dans la grille
            }
            else{//Si il y a plus que 1 biome dans le scenario
                this.biomes.forEach((Biome currentBiome) -> currentBiome.generation());//On genere chaque biome avec sa densité
                this.rangerCasesBiomesDansGrille();//Les cases générées pour chaque biome sont placées dans la grille
            }
        }
        System.out.println((ready) ? "Carte prête pour simulation!" : "Carte non prête pour la simulation.");
    }
    public void genererCarteAleatoire(TerrainPreset preset){
        RandTerrainGen rand = new RandTerrainGen(preset, this);
        rand.generateRandomMap();
        this.genererLaCarte();
    }

    public boolean isReadyForSim(){//Si ne serait-ce qu'une seule case n'est pas valide pour la simulation, le scenario n'est pas pret
        for(int i=0; i<this.tailleGrilleX;i++){
            for(Case caseI: this.grille[i]){
                if(!caseI.validePourSim()) return false;
            }
        }
        return true;
    }

    private void rangerCasesBiomesDansGrille(){
        for(Biome biomeCourant:this.biomes){//Pour chaque biome du scénario
            for(Case caseI:biomeCourant.getCases()){
                this.grille[caseI.getCoordX()][caseI.getCoordY()] = caseI;//On place les cases du biome dans les cellules de la grille du scénario correspondantes
            }
        }
    }
    public void setCaseDansGrille(Case caseAjout){
        try{this.grille[caseAjout.getCoordX()][caseAjout.getCoordY()]=caseAjout;}//On essaie d'ajouter la case à son emplcement dans la grille
        catch(Exception e){System.out.println("ERREUR: La case ne peut pas être ajoutée au scenario");}//Si la case ne peut pas être rangée, on attrape l'erreur
    }

    public double getHumidite() {
        return humidite;
    }

    public void setHumidite(double humidite) {
        this.humidite = humidite;
    }
    

    public int getTailleGrilleX() {
        return tailleGrilleX;
    }
    
    public int getTailleGrilleY() {
        return tailleGrilleY;
    }
    
    public Vent getVent() {
        return vent;
    }

    public void setVent(Vent vent) {
        this.vent = vent;
    }    

    public static File getEmplacementSauvDefaut() {
        return emplacementSauvDefaut;
    }

    public static void setEmplacementSauvDefaut(File emplacementSauvDefaut) {
        Scenario.emplacementSauvDefaut = emplacementSauvDefaut;
    }
    
    public ArrayList<Biome> getBiomes() {
        return biomes;
    }
    public void viderBiomes(){
        this.biomes.clear();
    }

    public Statistiques getStats() {
        return stats;
    }
    
    //Methode qui pour une paire de coordonnées renvoit la case de la grille correspondante
    public Case getCaseGrille(int X, int Y) {
        if(X<this.tailleGrilleX && Y<this.tailleGrilleY) return grille[X][Y];
        else {
            System.out.println("Indice de grille trop élevé!");
            return null;
        }
    }
    //Différentes methodes d'affichages console:

    //Affichage des infos complètes de chaque case de la grille
    public void afficherGrilleDebug(){
        for(int i=0;i<this.tailleGrilleX;i++){
            for(int j=0;j<this.tailleGrilleY;j++){
                System.out.print(this.grille[i][j]);
            }
            System.out.println();
        }
    }
    //Affichage des caractères ASCII représentatifs de chaque case de la grille
    public void afficherGrille(){
        for(int i=0;i<this.tailleGrilleX;i++){
            for(int j=0;j<this.tailleGrilleY;j++){
                System.out.print(this.grille[i][j].myID());
            }
            System.out.println();
        }
    }
    //Affichage de cases colorés (code ANSI) pour chaque case de la grille (ATTENTION: Fonctionne seulements avec certains IDE, comme VSCode)
    public void afficherGrilleCouleurs(){
        for(int i=0;i<this.tailleGrilleX;i++){
            for(int j=0;j<this.tailleGrilleY;j++){
                System.out.print(this.grille[i][j].getCouleurCase()+"  "+"\u001B[0m");
            }
            System.out.print("\n");
        }
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    public int getHorloge(){
        return this.horloge;
    }

    public boolean sauverScenario(String nomFichier, File emplacementDossier){
        this.nom = nomFichier;//Le nom du scénario est celui de la sauvegarde fichier
        try{
            FileWriter fich = new FileWriter(emplacementDossier.getAbsolutePath()+File.separator+nomFichier+".txt");
            CaseInflammable caseInfla;
            String coordCasesInfla="";
            //On écrit la grille dans le fichier texte
            for(int i=0;i<this.tailleGrilleX;i++){
                for(int j=0;j<this.tailleGrilleY;j++){

                    //Si on trouve une case inflammable
                    if(this.grille[i][j] instanceof CaseInflammable){
                        caseInfla = (CaseInflammable) this.grille[i][j];
                        //On enregistre si ID par défaut (non enflammé) dans le fichier texte
                        fich.write(caseInfla.getDefaultID());
                        if(!caseInfla.getEtatCase().equals(Etat.INTACTE)){//Si la case est enflammée (son état n'est plus intact)
                        //On sauvegarde les coord de la case enflammée pour les écrire au fichier
                        coordCasesInfla+=caseInfla.getCoordX()+System.lineSeparator()+caseInfla.getCoordY()+System.lineSeparator();
                        }
                    }
                    //On écrit l'ID des cases non inflammables
                    else fich.write(this.grille[i][j].myID());
                }
                fich.write(System.lineSeparator());
            }
            fich.write("FIN DE GRILLE"+System.lineSeparator());
            //On écrit les caractéristiques du vent
            fich.write(this.vent.getOrientation()+System.lineSeparator());
            fich.write(this.vent.getForce()+System.lineSeparator());

            //On écrit le pourcentage d'huimidité
            fich.write(this.humidite+System.lineSeparator());
            
            //On écrit les coordonnées des cases enflammées dans le fichier texte
            fich.write(coordCasesInfla);

            fich.close();
            return true;
        }
        catch(IOException ex){System.out.println("Le scénario n'a pas pu être sauvegardé");return false;}
    }

    public boolean sauverScenario(String nomFichier){ 
        //Si on souhaite sauver un scenario dans le fichier de sauvegarde par défaut, on a juste besoin d'appeler sauverScenario avec le nomFichier choisi
        return sauverScenario(nomFichier, Scenario.emplacementSauvDefaut);
    }

    public void chargerScenario(File fichierSauvegarde) throws IOException{
        try{
            FileReader fich = new FileReader(fichierSauvegarde.getAbsolutePath());
            BufferedReader br = new BufferedReader(fich);        
            String ligne = br.readLine();//On récupère la 1ère ligne de la grille
            char[] listeLigne;//Ligne de symboles qui sera analysée
            int i=0;//Compteur de la ligne
            int j=0;//Compteur de la colonne
            ArrayList<ArrayList<Case>> grilleTemp = new ArrayList<ArrayList<Case>>();//Grille temporaire de remlissage
            while(!ligne.equals("FIN DE GRILLE")){//Tant que la fin de la grille n'est pas atteinte, on lit les lignes.
                grilleTemp.add(new ArrayList<Case>());//On rajoute une ligne à la grille temporaire (redimensonnée au fil de la lecture du fichier)
                listeLigne = ligne.toCharArray();
                for(j=0;j<ligne.length();j++){//On va mettre la case correspondant à son symbole dans la grille du scénario. j est le compteur de la colonne.
                    if(listeLigne[j]=='-') grilleTemp.get(i).add(new CaseVegetation(i, j, Etat.INTACTE));
                    else if(listeLigne[j]=='M') grilleTemp.get(i).add(new CaseMaison(i, j, Etat.INTACTE));
                    else if(listeLigne[j]=='_') grilleTemp.get(i).add(new CaseVide(i, j));
                    else if(listeLigne[j]=='X') grilleTemp.get(i).add(new Case(i,j,false));
                    else if(listeLigne[j]=='~') grilleTemp.get(i).add(new CaseEau(i, j));
                    else System.out.println("Symbole no reconnu dans la lecture de la grille sauvegardée, à la case "+i+", "+j);
                }
                ligne = br.readLine();
                i++;
            }            
            this.tailleGrilleX=i;//On donne les valeurs de tailles au scénario
            this.tailleGrilleY=j;
            this.grille = new Case[i][j];//On initialise la grille du scénario selon la taille de la grille chargée du fichier
            for(int k=0;k<grilleTemp.size();k++){//On copie la grille temporaire dans la grille du scénario.
                for(int l=0;l<grilleTemp.get(k).size();l++){
                    this.grille[k][l] = grilleTemp.get(k).get(l);
                }                
            } 
            //On convertit les force et orientation de strings vers leurs valeurs en enums           
            Orientation orientation = Orientation.valueOf(br.readLine());
            Force force = Force.valueOf(br.readLine());                        
            this.vent = new Vent(force, orientation);
            this.humidite = Double.parseDouble(br.readLine()); 
            
            ligne = br.readLine();
            int coordXInf, coordYInf;
            CaseInflammable caseInf;
            //On enflamme les cases de la grille selon les coordonnées des cases enflammées indiquées dans la sauvegarde
            while(ligne!=null){
                coordXInf = Integer.parseInt(ligne);//On récupère les coord X et Y de la case
                coordYInf = Integer.parseInt(br.readLine());
                caseInf = (CaseInflammable)this.grille[coordXInf][coordYInf];
                caseInf.setEtatCase(Etat.ENFLAMME);//La case du tableau est enflammée
                ligne= br.readLine();
            }
            fich.close();
            this.nom = fichierSauvegarde.getName().replace(".txt", "");//Le nom du scenario est celui de la sauvegarde fichier
        }
        finally{}
    }

    public void chargerScenario(String nomFichier) throws IOException{//Charger un scenario à partir d'un simple nom de fichier
        File sauvegarde = new File(Scenario.emplacementSauvDefaut.getAbsolutePath(), nomFichier+".txt");        
        if(!sauvegarde.exists()) throw new IOException();//On s'assure que le fichier existe bien (si ce n'est pas le cas, on renvoit une erreur)
        else chargerScenario(sauvegarde);//Sinon on charge le scénario

    }

    public void initListesInflamm(HashSet<Case> caseFeuDepart){
        /*
        Cette méthode initialise les listes de cases intactes et enflammées du scénario
        Le paramètre en entrée est une liste des cases que l'on souhaite être les départs de feu
        */
        stats = new Statistiques();//On génère l'objet de statistiques pour la simulation       
        stats.nbCases = this.tailleGrilleX*this.tailleGrilleY;//Le nombre de cases de la grille

        for(Case caseEnFeu:caseFeuDepart){//Pour chaque case qui est un départ de feu
            CaseInflammable caseEnFeuVege = (CaseInflammable)caseEnFeu;//La case doit être de type CaseVegetation
            caseEnFeuVege.setEtatCase(Etat.ENFLAMME);//On s'assure que les cases sont bien enflammées
        }
        
        this.casesInflammables = new HashSet<>();
        this.casesTransFeu = new HashSet<>();

        for(int i=0;i<this.tailleGrilleX;i++){//On parcourt la grille de cases
            for(int j=0;j<this.tailleGrilleY;j++){
                Case caseCourante = this.grille[i][j];

                if(caseCourante.isInflammable()){//On vérifie si la case est inflammable
                    CaseInflammable caseCouranteInfl = (CaseInflammable)caseCourante;//Si elle est inflammable, elle est de type CaseInflammable et a un état 
                    if(caseCouranteInfl.getEtatCase()==Etat.INTACTE) this.casesInflammables.add(caseCouranteInfl);//On ajoute les cases inflammables intactes à la liste
                    else if(caseCouranteInfl.getEtatCase()==Etat.ENFLAMME) this.casesTransFeu.add(caseCouranteInfl);//On ajoute les cases enflammées à la liste
                }
            }
        }
        stats.nbCasesIntactes=this.casesInflammables.size();
        stats.nbCasesEnflammees=this.casesTransFeu.size();
    }
    public void initListesInflamm(){
        /*
        Cette méthode initialise les listes de cases intactes et enflammées du scénario
        Les deux cases qui représentent le départ de feu sont choisies au hasard
        */
        SplittableRandom rand = new SplittableRandom();
        int cmptDepFeu = 0;
        int randX, randY;
        HashSet<Case> listeCaseFeu = new HashSet<>();

        while(cmptDepFeu<2){
            randX = rand.nextInt(this.tailleGrilleX);
            randY = rand.nextInt(this.tailleGrilleY);
            if(this.grille[randX][randY] instanceof CaseInflammable && !listeCaseFeu.contains(this.grille[randX][randY])){//Il faut que la case choisie au hasard soit dans de type inflammable et pas déjà choisie
                listeCaseFeu.add(this.grille[randX][randY]);
                cmptDepFeu++;
            }
        }
        System.out.println("Cases de départ de feu: ");
        listeCaseFeu.forEach((caseFeu) -> System.out.println(caseFeu));
        System.out.println();
        this.initListesInflamm(listeCaseFeu);//On initalise les listes avec les cases de départ de feu choisies
    }
    public void simulerIncendie(int nbDeGenerations){
        /**
         * Méthode qui va faire évoluer l'incendie en faisant passer des génération, 
         * jusqu'à ce que la limite de générations soit atteinte ou que le feu s'est arrêté
         */
        if(!this.casesInflammables.equals(null) && !this.casesTransFeu.equals(null)){//On s'assure que les listes des cases inflammables/enflammées sont initialisées       
            
            //On répète la boucle suivante tant que la limite de la simulation n'a pas été atteinte, et qu'il reste des cases en feu
            while(this.horloge<nbDeGenerations && !isFireFinished()){
                this.passerUneGeneration();//On passe à la génération suivante
            }
        }
        else System.out.println("VEUILLEZ INITIALISER LES LISTES DE CASES INFLAMMABLES/ENFLAMMEES AVEC LA METHODE initListesInflamm");
    }
    public boolean isFireFinished(){
        return (this.casesTransFeu.isEmpty() && casesBrulentPlus.isEmpty());//REMETTRE ||
    }

    public void passerUneGeneration(){
        /**
         * Cette méthode effectue les opérations nécessaires pour faire évoluer la propagation du feu d'une génération
         * On étudie tour à tour les listes: des cases BruleFroid, des cases qui transmettent le feu, et des cases intactes inflammables
         */
        SplittableRandom rand = new SplittableRandom();             
        ArrayList<Double> listeProbas;  
        try{
            this.casesCendre.clear();//Liste qui contient les cases qui ne propagent plus le feu (cendre)
            this.casesPlusIntactes.clear();

            //Pour chaque case qui ne brule plus (BruleFroid)
            for(CaseInflammable caseBruPlus : this.casesBrulentPlus){
                if(caseBruPlus.getEtatCase()==Etat.BRULEFROID && caseBruPlus.getCompteurInflammation()>=caseBruPlus.dureeVieEtat()-1){//Si il faut passer à l'etat de cendre
                    caseBruPlus.changerDEtat();//On procède au changement d'état
                    casesCendre.add(caseBruPlus);//On va retirer la cases en cendre de la liste car elle ne changera plus  
                    stats.nbCasesBrulFroid--;
                    stats.nbCasesCendre++;
                } 
                else caseBruPlus.indenterCompteurInflammation();//On indente le compteur d'inflammation de 1
            }
            casesBrulentPlus.removeAll(casesCendre);//On retire les cases en cendre car elles n'ont plus besoin d'etre indentées

            //Pour toutes les cases qui peuvent transmettre le feu
            for(CaseInflammable caseEnflammee:this.casesTransFeu){

                if(caseEnflammee.getEtatCase()==Etat.BRULECHAUD && rand.nextDouble()<= 0.4){//Si la probabilité de passer à l'etat froid est validée, on change                            
                        caseEnflammee.changerDEtat();//On procède au changement d'état
                        stats.nbCasesBrulFroid++;
                        stats.nbCasesBrulChaud--;
                        casesBrulentPlus.add(caseEnflammee);//On ajoute cette case aux cases qui ne propagent plus le feu
                    } 
                else if(caseEnflammee.getEtatCase()==Etat.ENFLAMME && caseEnflammee.getCompteurInflammation()>=caseEnflammee.dureeVieEtat()-1){//Si la durée de vie est atteinte
                    caseEnflammee.changerDEtat();//La case passe de enflammee à brule chaud
                    stats.nbCasesBrulChaud++;
                    stats.nbCasesEnflammees--;
                }                     
                if(caseEnflammee.getEtatCase().peutPasserFeu()){//La case peut encore transmettre le feu
                    distribuerCoefFeu(caseEnflammee);//On ajoute les coefs de proba d'inflammation aux voisins concernés
                    caseEnflammee.indenterCompteurInflammation();//On indente le compteur d'inflammation de 1
                }  
            }
            casesTransFeu.removeAll(casesBrulentPlus);

            int cmptProba;//Nombre de probabilités que la case prenne feu                
            //Pour chaque case suceptible de bruler
            stats.vitesseFeu=0;
            for(CaseInflammable caseFeuPoss:casesInflammables){
                listeProbas = caseFeuPoss.getProbaInflamm();//Liste des probabilités que la case brule
                if(!listeProbas.isEmpty()){//On s'assure que la case est en danger de d'enflammer (des voisins lui ont donné des probas)
                    cmptProba=0;
                    Collections.reverse(listeProbas);//On trie la liste des probas en ordre décroissant
                    while(!caseFeuPoss.getEtatCase().peutPasserFeu() && cmptProba<listeProbas.size()){//Tant que la case est ni enflammée, ni à court de probas
                        if(rand.nextDouble()<=listeProbas.get(cmptProba)){//Si la probabilité de la prise de feu est validée
                            caseFeuPoss.changerDEtat();//La case passe en etat suivant (enflammee)
                            casesTransFeu.add(caseFeuPoss);//La case est rajoutée à la liste des cases qui transmettent le feu
                            casesPlusIntactes.add(caseFeuPoss);//La case qui vient de prendre feu sera retirée de la liste des intactes
                            this.stats.nbCasesEnflammees++;
                            this.stats.nbCasesIntactes--;
                            this.stats.vitesseFeu++;
                        }
                        cmptProba++;
                    }                        
                    listeProbas.clear();//On retire les probabilités transmises à la case lors de cette iteration                       
                } 
                
            }
            casesInflammables.removeAll(casesPlusIntactes);//On retire les cases nouvellement enflammées aux cases inflammables intactes
            this.horloge++;

            //Lignes pour un affichage correct en console

            //On efface ce qui est affiché en console (OPTIONNEL)
            //System.out.print("\033[H\033[2J");
            //System.out.flush();
            //System.out.println("ETAT DU SCENARIO A t="+this.horloge);//Repère temporel de l'évolution du scénario
            //Affichage de la grille (différentes options possibles)
            //this.afficherGrille();
            //afficherGrilleDebug();
            //this.afficherGrilleCouleurs(); //(Seulement pour les IDE compatibles avec l'affichage ANSI)
            TimeUnit.MILLISECONDS.sleep(0);//Temps de pause après l'affichage
        }
        catch(InterruptedException e){e.printStackTrace();}//On attrape une erreur d'interruption potentielle
        finally{};
    }
    private void distribuerCoefFeu(CaseInflammable caseFeu){
        /**
         * Cette méthode permet de distribuer les coefficients d'inflammation aux cases intactes autour d'un émetteur enflammé, donné en paramètre
         */
        int coorFeuX = caseFeu.getCoordX();
        int coorFeuY = caseFeu.getCoordY();

        double[][] matriceCoefs = this.vent.getMatriceVent();//Matrice avec les coefs de transmissions selon le vent
        int centreMatrice = matriceCoefs.length/2;
        for(int i=0;i<matriceCoefs.length;i++){//On parcourt la matrice du vent
            for(int j=0; j<matriceCoefs[i].length;j++){
                if(matriceCoefs[i][j]!=0 && matriceCoefs[i][j]!=-1){//On considère le coefficient seulement s'il est ni nul, ni le centre (qui vaut -1)
                    try{
                        Case caseVoisine = this.grille[coorFeuX-centreMatrice+i][coorFeuY-centreMatrice+j];
                        if(this.casesInflammables.contains(caseVoisine)){//Si la case voisine est contenue dans inflammable
                            CaseInflammable casePeutBruler = (CaseInflammable)caseVoisine;//La case voisine doit etre de type caseInflammable
                            casePeutBruler.ajoutProbaInflamm(calculProba(matriceCoefs[i][j], caseFeu));//On ajoute la probabilité d'inflammation à la caseVoisine
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException e){}
                }
                
            }
        }
    }
    public double calculProba(double coef, CaseInflammable emetteur){
        //Methode qui donne le coef de probabilité correspondant selon le vent (=parametre "coef") et l'etat de l'émetteur
        if(emetteur.getEtatCase()==Etat.BRULECHAUD){
            return 0.5*(1+2*this.vent.getForce().getPuissance())*coef*this.humidite;
        }
        else if(emetteur.getEtatCase()==Etat.ENFLAMME){
            return coef*this.humidite;
        }
        else return 0;//Il est impossible que ce cas arrive
    }
}
