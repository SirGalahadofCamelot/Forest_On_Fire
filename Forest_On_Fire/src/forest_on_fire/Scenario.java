package forest_on_fire;

import java.io.BufferedReader;
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


public class Scenario {

    private double humidite;
    private int tailleGrilleX, tailleGrilleY, horloge;
    private Vent vent;//Valeur du vent du scénario
    private Case[][] grille;//Grille de cases de la simulation 
    private ArrayList<Biome> biomes;
    private HashSet<CaseInflammable> casesInflammables, casesTransFeu;//Liste des cases inflammables, et des cases qui peuvent transmettre le feu.
    
    //Constructeur avec en paramètre une liste de biomes, et la hauteur/largeur de la grille
    //Comme la grille n'existe pas encore, on la remplit avec des cases de type Case
    //Ce constructeur est plutot pour une nouvelle simulation
    public Scenario(double humidite, int tailleGrilleX, int tailleGrilleY, Vent vent, ArrayList<Biome> valBiomes) {
        this.humidite = humidite;
        this.tailleGrilleX = tailleGrilleX;
        this.tailleGrilleY = tailleGrilleY;
        this.horloge = 0;
        this.vent = vent;
        this.grille = new Case[tailleGrilleX][tailleGrilleY];
        for(int i=0; i<tailleGrilleX; i++){
            for(int j=0; j<tailleGrilleY; j++){
                this.grille[i][j]=new Case(i, j, false);
            }
        }
        this.biomes = valBiomes;
    }
    //Constructeur avec en paramètre une liste de biomes et un tableau de cases (qui représente la grille)
    //Ce constructeur est plutot pour une simulation déjà "prête", car la grille est un paramètre.
    public Scenario(double humidite, Case[][] grille, Vent vent, ArrayList<Biome> valBiomes) {
        this.humidite = humidite;
        this.tailleGrilleX = grille.length;
        this.tailleGrilleY = grille[0].length;
        this.horloge = 0;
        this.vent = vent;
        this.grille = grille;
        this.biomes = valBiomes;
    }
    //Constructeur avec en paramètre un unique biome, et la hauteur/largeur de la grille
    //Ce constructeur est plutot pour une nouvelle simulation, qui représente un seul biome
    public Scenario(double humidite, int tailleGrilleX, int tailleGrilleY, Vent vent, Biome biome){
        this(humidite, tailleGrilleX, tailleGrilleY, vent, new ArrayList<Biome>(Arrays.asList(biome)));
    }
    //Ce constructeur sert à récupérer un scénario à partir d'une sauvegarde fichier
    public Scenario(String nomFichier){
        try{this.chargerScenario(nomFichier);}
        catch(IOException ex){System.out.println("Erreur de chargement du fichier");}
        this.horloge=0;
    }
    /*
    //Constructeur avec en paramètre un type de biome, et la hauteur/largeur de la grille
    public Scenario(double humidite, int tailleGrilleX, int tailleGrilleY, Vent vent, TypeTerrain terrain){
        Case[][] grilleIntermediaire = new Case[tailleGrilleX][tailleGrilleY];
        Scenario(humidite, grilleIntermediaire, vent, new ArrayList<Biome>(Arrays.asList(new Biome(grilleIntermediaire, terrain))), Biome.TypeTerrain.CONTINUE)))
    }*/
    public void addBiome(Biome nvBiome){
        this.biomes.add(nvBiome);
    }
    public void genererLaCarte(){
        boolean ready = true;
        if(!this.isReadyForSim()){//Si la carte n'est pas prete pour une simulation, on synchronise les biomes avec la grille
            if(this.biomes.size()==0) {System.out.println("AUNCUN BIOME DANS LE SCENARIO: Veuillez ajouter au moins un biome");ready=false;}

            else if(this.biomes.size()==1){//Si il y a un seul biome, on admet que toute la carte sera de ce biome. Toutes les cases vierges de la grilles sont données au biome.
                for(int i=0;i<this.tailleGrilleX;i++){
                    for(Case caseJ:this.grille[i]){
                        this.biomes.get(0).ajouterCase(caseJ);//On met toutes les cases vierges de la grille dans la liste de cases du biome
                    }
                }
                this.biomes.get(0).generation();//Les cases vierges donnees au biome sont converties selon la densite de vegetation du biome
                this.rangerCasesBiomesDansGrille();
            }
            else{//Si il y a plus que 1 biome dans le scenario

            }
        }
        System.out.println((ready) ? "Carte prête pour simulation!" : "Carte non prête pour la simulation.");
    }

    public boolean isReadyForSim(){//Si ne serait-ce qu'une seule case n'est pas valide pour la simulation, le scenario n'est pas pret
        for(int i=0; i<this.tailleGrilleX;i++){
            for(Case caseI: this.grille[i]){
                if(!caseI.validePourSim()) return false;
            }
        }
        return true;
    }

    public void rangerCasesBiomesDansGrille(){
        for(Biome biomeCourant:this.biomes){//Pour chaque biome du scénario
            for(Case caseI:biomeCourant.getCases()){
                this.grille[caseI.getCoordX()][caseI.getCoordY()] = caseI;//On place les cases du biome dans les cellules de la grille du scénario corresspondantes
            }
        }
    }

    public double getHumidite() {
        return humidite;
    }

    public void setHumidite(double humidite) {
        this.humidite = humidite;
    }

    public Vent getVent() {
        return vent;
    }

    public void setVent(Vent vent) {
        this.vent = vent;
    }
    public Case getCaseGrille(int X, int Y) {
        if(X<this.tailleGrilleX && Y<this.tailleGrilleY) return grille[X][Y];
        else {
            System.out.println("Indice de grille trop élevé!");
            return null;
        }
    }
    public void afficherGrilleDebug(){
        for(int i=0;i<this.tailleGrilleX;i++){
            for(int j=0;j<this.tailleGrilleY;j++){
                System.out.print(this.grille[i][j]);
            }
            System.out.println();
        }
    }
    public void afficherGrille(){
        for(int i=0;i<this.tailleGrilleX;i++){
            for(int j=0;j<this.tailleGrilleY;j++){
                System.out.print(this.grille[i][j].myID());
            }
            System.out.println();
        }
    }
    public void sauverScenario(String nomFichier){
        try{
            FileWriter fich = new FileWriter(System.getProperty("user.dir")+"\\"+nomFichier+".txt");
            
            //On écrit la grille dans le fichier texte
            for(int i=0;i<this.tailleGrilleX;i++){
                for(int j=0;j<this.tailleGrilleY;j++){
                    fich.write(this.grille[i][j].myID());
                }
                fich.write(System.lineSeparator());
            }
            fich.write("FIN DE GRILLE"+System.lineSeparator());
            //On écrit les caractéristiques du vent
            fich.write(this.vent.getOrientation()+System.lineSeparator());
            fich.write(this.vent.getForce()+System.lineSeparator());

            //On écrit le pourcentage d'huimidité
            fich.write(this.humidite+System.lineSeparator());

            fich.close();
        }
        catch(IOException ex){System.out.println("Le scénario n'a pas pu être sauvegardé");}
    }
    public void chargerScenario(String nomFichier) throws IOException{
        try{
            FileReader fich = new FileReader(System.getProperty("user.dir")+"\\"+nomFichier+".txt");
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
            
            fich.close();
        }
        finally{}
    }
    public void initListesInflamm(HashSet<Case> caseFeuDepart){
        /*
        Cette méthode initialise les listes de cases intactes et enflammées du scénario
        Le paramètre en entrée est une liste des cases que l'on souhaite être les départs de feu
        */

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
            if(this.grille[randX][randY] instanceof CaseInflammable && !listeCaseFeu.contains(this.grille[randX][randY])){//Il faut que la case choisie au hasard soit dans de type vegetation et pas déjà choisie
                listeCaseFeu.add(this.grille[randX][randY]);
                cmptDepFeu++;
            }
        }
        System.out.println("Cases de départ de feu: ");
        listeCaseFeu.forEach((caseFeu) -> System.out.print(caseFeu));
        System.out.println();
        this.initListesInflamm(listeCaseFeu);//On initalise les listes avec les cases de départ de feu choisies
    }
    public void simulerIncendie(int nbDeGenerations){

        if(!this.casesInflammables.equals(null) && !this.casesTransFeu.equals(null)){//On s'assure que les listes des cases inflammables/enflammées sont initialisées

        SplittableRandom rand = new SplittableRandom();
        HashSet<CaseInflammable> casesBrulentPlus= new HashSet<>();//Liste qui contient les cases qui ne propagent plus le feu (BruleFroid)
        HashSet<CaseInflammable> casesCendre;//Liste qui contient les cases qui ne propagent plus le feu (cendre)
        HashSet<CaseInflammable> casesPlusIntactes;         
        ArrayList<Double> listeProbas;  
            
            while(this.horloge<nbDeGenerations){//On répète cette boucle tant que la limite de la simulation n'a pas été atteinte
                try{                
                casesCendre= new HashSet<>();//Liste qui contient les cases qui ne propagent plus le feu (cendre)
                casesPlusIntactes= new HashSet<>();

                for(CaseInflammable caseBruPlus : casesBrulentPlus){//Pour chaque case qui ne brule plus (BruleFroid)
                    if(caseBruPlus.getEtatCase()==Etat.BRULEFROID && caseBruPlus.getCompteurInflammation()>=caseBruPlus.dureeVieEtat()){//Si il faut passer à l'etat de cendre
                        caseBruPlus.changerDEtat();//On procède au changement d'état
                        casesCendre.add(caseBruPlus);//On va retirer la cases en cendre de la liste car elle ne changera plus                       
                    } 
                    else caseBruPlus.indenterCompteurInflammation();//On indente le compteur d'inflammation de 1
                }
                casesBrulentPlus.removeAll(casesCendre);//On retire les cases en cendre car elles n'ont plus besoin d'etre indentées


                
                for(CaseInflammable caseEnflammee:this.casesTransFeu){//Pour toutes les cases qui peuvent transmettre le feu

                    if(caseEnflammee.getEtatCase()==Etat.BRULECHAUD && rand.nextDouble()<= 0.4){//Si la probabilité de passer à l'etat froid est validée, on change                            
                            caseEnflammee.changerDEtat();//On procède au changement d'état
                            casesBrulentPlus.add(caseEnflammee);//On ajoute cette case aux cases qui ne propagent plus le feu
                        } 
                    else if(caseEnflammee.getEtatCase()==Etat.ENFLAMME && caseEnflammee.getCompteurInflammation()>=caseEnflammee.dureeVieEtat()){//Si la durée de vie est atteinte
                        caseEnflammee.changerDEtat();//La case passe de enflammee à brule chaud
                    }                     
                    else if(caseEnflammee.getEtatCase().peutPasserFeu()){//La case peut encore transmettre le feu et ne vient pas de changer d'etat
                        distribuerCoefFeu(caseEnflammee);//On ajoute les coefs de proba d'inflammation aux voisins concernés
                        caseEnflammee.indenterCompteurInflammation();//On indente le compteur d'inflammation de 1
                    }  
                }
                casesTransFeu.removeAll(casesBrulentPlus);

                int cmptProba;//Nombre de probabilités que la case prenne feu                
                for(CaseInflammable caseFeuPoss:casesInflammables){//Pour chaque case suceptible de bruler
                    listeProbas = caseFeuPoss.getProbaInflamm();//Liste des probabilités que la case brule
                    if(!listeProbas.isEmpty()){//On s'assure que la case est en danger de d'enflammer (des voisins lui ont donné des probas)
                        cmptProba=0;
                        Collections.reverse(listeProbas);//On trie la liste des probas en ordre décroissant
                        while(!caseFeuPoss.getEtatCase().peutPasserFeu() && cmptProba<listeProbas.size()){//Tant que la case est ni enflammée, ni à court de probas
                            if(rand.nextDouble()<=listeProbas.get(cmptProba)){//Si la probabilité de la prise de feu est validée
                                caseFeuPoss.changerDEtat();//La case passe en etat suivant (enflammee)
                                casesTransFeu.add(caseFeuPoss);//La case est rajoutée à la liste des cases qui transmettent le feu
                                casesPlusIntactes.add(caseFeuPoss);//La case qui vient de prendre feu sera retirée de la liste des intactes
                            }
                            cmptProba++;
                        }                        
                        listeProbas.clear();                        
                    } 
                    
                }
                casesInflammables.removeAll(casesPlusIntactes);
                this.horloge++;

                System.out.print("\033[H\033[2J");  
                System.out.flush();  
                System.out.println("ETAT DU SCENARIO A t="+this.horloge);
                this.afficherGrille();
                TimeUnit.MILLISECONDS.sleep(90);
                }
                catch(InterruptedException e){e.printStackTrace();}
            }
            

        }
        else System.out.println("VEUILLEZ INITIALISER LES LISTES DE CASES INFLAMMABLES/ENFLAMMEES AVEC LA METHODE initListesInflamm");
    }
    public void distribuerCoefFeu(CaseInflammable caseFeu){
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
    public double calculProba(double coef, CaseInflammable emetteur){//Methode qui donne le coef de proba correspondant selon le vent (=parametre coef) et l'etat de l"émetteur
        if(emetteur.getEtatCase()==Etat.BRULECHAUD){
            return 0.5*(1+2*this.vent.getForce().getPuissance())*coef*this.humidite;
        }
        else if(emetteur.getEtatCase()==Etat.ENFLAMME){
            return coef*this.humidite;
        }
        else return 0;//Il est impossible que ce cas arrive
    }
}
