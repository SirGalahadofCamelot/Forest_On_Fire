package forest_on_fire;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import forest_on_fire.Biome.TypeTerrain;
import forest_on_fire.Vent.Force;
import forest_on_fire.Vent.Orientation;
import forest_on_fire.random_terrain_gen.TerrainPreset;


public class App {
    public static void main(String[] args) throws Exception {

        /*
        //TESTS DIVERS (Voir plus bas pour les tests relatifs au rapport intermédiaire)

        System.out.println("Test des objets:");

        //TEST DES CASES
        Case zerozero = new CaseVegetation(0, 0, Etat.INTACTE);
        Case zeroun = new caseVide(0, 1);

        //TEST DU SCENARIO
        Scenario casDeFigure1 = new Scenario(0.9, 100, 100, new Vent(Vent.Force.MODERE, Vent.Orientation.NORD));
        System.out.println("Puissance du vent pour le casDeFigure1: "+casDeFigure1.getVent().getForce().getPuissance());

        //TEST DU BIOME
        Biome pelouse = new Biome(new ArrayList<>(Arrays.asList(zerozero,zeroun)), Biome.TypeTerrain.CONTINUE);
        System.out.println("Densité de la végétation pour le biome: "+pelouse.getTerrain().getDensite());
        */
        /*
        //TEST DU VENT
        Vent vent1 = new Vent(Force.FORT, Orientation.EST);
        double[][] matriceVent = vent1.getMatriceVent();
        for(int i=0;i<matriceVent.length;i++){
            for(int j=0;j<matriceVent[0].length;j++){
                System.out.print(matriceVent[i][j]+"\t");
            }
            System.out.println();
        }*/

        
        //CREATION DU SCENARIO
        Scanner sc = new Scanner(System.in);

        //System.out.print("Entrez la taille de la grille carrée: ");
        //int taille = sc.nextInt(); sc.nextLine(); //Le nextLine() est là pour purger la console des retours à la ligne.

        System.out.print("Entrez l'humidité (0,1, 0,35, 0,6 ou 0,9): ");
        double humidite = sc.nextDouble(); sc.nextLine();
        System.out.print("Entrez la force du vent (0=nul, 1=modéré, 2=fort, 3=violent): ");
        int choixForce = sc.nextInt(); sc.nextLine();       
        Force force = Force.NUL;   //Valeur par défaut de la force du vent     
        switch(choixForce){
            case 0: force = Force.NUL;break;
            case 1: force = Force.MODERE;break;
            case 2: force = Force.FORT;break;
            case 3: force = Force.VIOLENT; break;
        }
        System.out.print("Entrez la direction du vent (0=NORD, 1=SUD, 2=EST, 3=OUEST): ");
        int choixDirection = sc.nextInt(); sc.nextLine();
        Vent.Orientation orientation = Vent.Orientation.NORD;  //Valeur par défaut de l'orientation du vent   
        switch(choixDirection){
            case 0: orientation = Vent.Orientation.NORD;break;
            case 1: orientation = Vent.Orientation.SUD;break;
            case 2: orientation = Vent.Orientation.EST;break;
            case 3: orientation = Vent.Orientation.OUEST; break;
        }
        /*
        //Choix d'un biome unique
        System.out.print("Entrez le type de terrain (0=CLAIRSEMEE, 1=ESPACEE, 2=TOUFFUE, 3=CONTINUE, 4=VILLE, 5=VILLAGE): ");
        int choixVegetation = sc.nextInt(); sc.nextLine();
        TypeTerrain terrain = TypeTerrain.CLAIRSEMEE;  //Valeur par défaut du terrain   
        switch(choixVegetation){
            case 0: terrain = TypeTerrain.CLAIRSEMEE;break;
            case 1: terrain = TypeTerrain.ESPACEE;break;
            case 2: terrain = TypeTerrain.TOUFFUE;break;
            case 3: terrain = TypeTerrain.CONTINUE; break;
            case 4: terrain = TypeTerrain.VILLE; break;
            case 5: terrain = TypeTerrain.VILLAGE; break;
        }
        //Creation d'un scenario à biome unique
        Scenario casDeFigure2 = new Scenario(humidite, 10, 10, new Vent(force, orientation), new Biome(new HashSet<>(), terrain));//taille, taille
        
        */

        
        //Choix d'un preset
        System.out.print("Entrez le type de preset (0=TERRAIN_CLASSIQUE, 1=FORET, 2=ILE): ");
        int choixPreset = sc.nextInt(); sc.nextLine();
        TerrainPreset terrain = TerrainPreset.TERRAIN_CLASSIQUE;  //Valeur par défaut du terrain   
        switch(choixPreset){
            case 0: terrain = TerrainPreset.TERRAIN_CLASSIQUE;break;
            case 1: terrain = TerrainPreset.FORET;break;
            case 2: terrain = TerrainPreset.ILE;break;
        }            
        //Creation d'un scenario à preset (multiples biomes)
        Scenario casDeFigure2 = new Scenario(humidite, 70, 130, new Vent(force, orientation), new ArrayList<Biome>());//taille, taille
        


        //System.out.println("Puissance du vent pour le casDeFigure2: "+casDeFigure2.getVent().getForce().getPuissance());
        //casDeFigure2.afficherGrille();
        System.out.println("La simulation est prête: "+casDeFigure2.isReadyForSim());
        casDeFigure2.genererCarteAleatoire(terrain);
        //casDeFigure2.genererLaCarte();
        //casDeFigure2.afficherGrille();
        System.out.println("La simulation est prête: "+casDeFigure2.isReadyForSim());
        //casDeFigure2.getVent().afficherMatrice();
        casDeFigure2.initListesInflamm();//On initie les listes de cases de la sim2, et on chosit 2 départs de feu
        

        casDeFigure2.sauverScenario("testSauvegarde");
        Scenario casDeFigure3 = new Scenario("testSauvegarde");
        //casDeFigure3.afficherGrille();        
        System.out.println("La simulation est prête: "+casDeFigure3.isReadyForSim());
        //casDeFigure3.getVent().afficherMatrice();

        //TEST DE LA PROPAGATION DU FEU
        
        casDeFigure3.initListesInflamm(new HashSet<>());//On initile les listes des cases de la sim3, et on indique aucune case enflammée (car elles ont déjà été choisies)
        //casDeFigure3.afficherGrille();
        //casDeFigure3.afficherGrilleCouleurs();        
        casDeFigure3.simulerIncendie(200);

        sc.close();
        
        
        /*
        //TESTS EFFECTUES DANS LE RAPPORT INTERMEDIAIRE

        //I) Generation de la carte
        //1. Création de l'objet Vent
        //On fabrique l'objet vent, modéré et venant de l'est
        Vent monVent = new Vent(Force.MODERE, Orientation.EST);
        //On affiche sa matrice
        monVent.afficherMatrice();

        //2. Creation de l'objet Scenario
        Scenario monScenario = new Scenario(0.9, 10, 10, monVent, new Biome(TypeTerrain.TOUFFUE));
        //On affiche la grille du scénario
        monScenario.afficherGrille();

        //3. Rendre la carte prête pour la simulation avec la méthode genererLaCarte()
        //On génère la carte
        monScenario.genererLaCarte();
        //On affiche la grille du scénario
        monScenario.afficherGrille();

        //4. Sauvegarder un scénario avec la méthode sauverScenario()
        //On sauve le scénario 
        monScenario.sauverScenario("scenarioSauve");

        //5. Charger un scénario à partir d’un fichier texte avec un constructeur de Scenario
        //On charge le scénario sauvé précédemment dans le fichier "scenarioSauve" dans un nouvel objet
        Scenario monAutreScenario = new Scenario("scenarioSauve");
        System.out.println("Grille du scénario chargé:");
        monAutreScenario.afficherGrille();

        //6.Placer les départs de feu avec la méthode initListesInflamm()
        //On indique les départs de feu et on initie les listes de cases enflammées
        monAutreScenario.initListesInflamm();
        //On affiche la grille du scénario
        monAutreScenario.afficherGrille();

        //II) Simulation de l'incendie
        //On lance la simulation de l'incendie, pour un maximum de 100 générations
        monAutreScenario.simulerIncendie(100);*/
    }
}
