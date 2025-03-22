package projetASD3 ;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import  java.io.File;
import  java.io.FileWriter;
import java.util.ArrayList;



public class Quadtree {
	
    private	Quadtree fils[] ; // tableau pour stocker les fils
    private int lum ;  // valeur de la luminosité pour chaque noeud :valeur du pixel si il n'y a pas de fils
 	private int w, h; // dimmension de l'image
    private	Boolean estFeuille;
 	private int lumiMax;
 	private int nbNoeud ;
 	private String commentaires; // les commentaires de l'images
 	private int nbNoeudInitial;
 	

	
    /////////////////////////////////////////////////////////////////
    //// cree le quadtree ici !!!
	/*
 	public Quadtree(String path) {
		this.lum = 0;
		this.fils = new Quadtree[4] ;
	}
   */	
	
 	//////////////////////////////////////////////////////////////////
	//test si tout les fils on la même valeurs
      private Boolean filsTousEgaux() {
	      return fils[0].lum == fils[1].lum && fils[0].lum == fils[2].lum && fils[0].lum == fils[3].lum;
      }



    ////////////////////////////////////////////////////////////////////////// 
     private int[][] getMatrice(Scanner sc) {
	         int matrice[][] = new int[w][h]; // creation de la matrice

 	            for (int i=0; i<w; i++) { // parcours sur la hauteur
	              	for (int j=0; j<h; j++) { // parcours sur la largeur
			              matrice[i][j] = sc.nextInt();
		           }
	            }

	             return matrice;
    }
  //////////////////////////////////////////////////////////////////////////////////
     
     
     private void toMatrice(int[][] finalMatrice, int x, int y) {
 		if (this.estFeuille) {
 			for (int iy=0; iy < this.h; iy++) {
 				for (int ix=0; ix < this.w; ix++) {
 					finalMatrice[y + iy][x + ix] = this.lum;
 				}
 			}
 		} else {
 			this.fils[0].toMatrice(finalMatrice, x, y);
 			this.fils[1].toMatrice(finalMatrice, x + w/2, y);
 			this.fils[2].toMatrice(finalMatrice, x + w/2, y + h/2);
 			this.fils[3].toMatrice(finalMatrice, x, y+h/2);
 		}
 	}

 	
     
     /////////////////////////////////////////////////////////////////////////
     /* 
 	cette fonction génère un quadtree sur une surface donné
 	cette surface est défini par un point : x et y
 	ainsi que la largeur : w et la hauteur h
 	*/
 	private void genQuadTree (int[][] lumiMatrice, int x, int y) {
 		if (w == 1 && h == 1) { // un seul pixel, c'est donc une feuille
 			lum = lumiMatrice[y][x];
 			estFeuille = true;
 		} else { // ce n'est pas une feuille
 			// on cree les 4 fils
 			fils = new Quadtree[4];
 			for (int i=0; i<4; i++) {fils[i] = new Quadtree(w/2, h/2);}

 			// on coupe a chaque fois
 			fils[0].genQuadTree(lumiMatrice, x, y);
 			fils[1].genQuadTree(lumiMatrice, x + w/2, y);
 			fils[2].genQuadTree(lumiMatrice, x + w/2, y + h/2);
 			fils[3].genQuadTree(lumiMatrice, x, y+h/2);

 			// ensuite on rassemble si les 4 fils s'il contienne la même valeur
 			if (estBrindille() && filsTousEgaux()) {
 				estFeuille = true;
 				lum = fils[0].lum;
 				for (int i=0; i<4; i++) {fils[i] = null;}
 			}
 			
 		}
 	}
 	
	//////////////////////////////////////////////////////////////////////
	//fonction qui prends une image en paramètre et en construit le QuadTree
	public Quadtree(String path) {
		File file; 
		Scanner sc;
		int lumiMatrice[][]; // matrice contenant les valeurs des pixel (ici niveau de gris)

		try {
			// chargement du fichier et du scanner 
			file = new File(path);
			sc = new Scanner(file);

			// magic number ici P2 (mais on le sait déjà)
			sc.nextLine();

			// gestion des commentaires
			commentaires = sc.nextLine();
			
			// gestion des metadonnées
			w = sc.nextInt();
			h = sc.nextInt();
			lumiMax = sc.nextInt();

			// lecture des valeurs des pixels
			lumiMatrice = getMatrice(sc);

			// creation du quadTree
			estFeuille = false;
			genQuadTree(lumiMatrice, 0, 0);
		} catch (FileNotFoundException e) { // si l'image n'est pas trouvé on renvoit l'erreur
			System.out.println("L'image n'a pas été trouvé");
		}
	}
	/////////////////////////////////////////////////////////////////////////////

	public Quadtree(int newW, int newH) {
		estFeuille = false;
		w = newW;
		h = newH;
	}

	
	
	///////////////////////////////////////////////////////////////////
	//Les getters et les setters pour la luminosité
	
	public int getLuminosity() {
		return lum ;
	}
	
	public void setLuminosity(int luminosity) {
		this.lum = luminosity ;
	}
	
	
	//getter pour les fils
	
	public Quadtree[] getFils() {
		return fils ;
	}
	
	
	/////////////////////////////////////////////////////////////////
	
	// méthode pour compresser une brindille 
	public  int calculMoyenneBrindille() {
		int gama=0 ;
		int sum = 0 ;
		//double moyenLum = 0.0;
		
		if(fils!= null) {
			// calculer la somme des luminosités
		  for(int i=0; i<4 ; i++) {
			  
			  if(fils[i] != null) {
				  
				  sum += fils[i].getLuminosity() ;
				 
 			  }
			 
		  }  
		  // calcule la moyenne logarithmique de la luminosté
		  gama = Math.round((float)Math.exp((1/4)*Math.log(0.1+sum))) ;
		  
	 }
		System.out.println(gama) ;
		return  gama ;
 
	}
	/////////////////////////////////////////////////////////////////////////////////
	
	// Méthode pour appliquer la compression Lambda à l'ensemble du quadtree
	/**
	Fonction qui réalise la compression lambda
*/
 public void compressLambda() {
    if (!estFeuille) {
    	if (estBrindille()) {
    		// Compression de la brindille actuelle
			lum = calculMoyenneBrindille();
			estFeuille = true;
			for (int i = 0; i < 4; i++) {
				this.fils[i] = null;
				}
			
    	} else {
            for (int i = 0; i < 4; i++) {
                if (fils[i] != null && !fils[i].estFeuille) {
                    fils[i].compressLambda(); // Compression récursive des sous-quadtree
            	}
            }
            
			if (estBrindille() && filsTousEgaux()) {
				lum = this.fils[0].lum;
				estFeuille = true;
				for (int i = 0; i < 4; i++) {this.fils[i] = null;}
           }
        }
	}
}

	/////////////////////////////////////////////////////////////////////////////
    // a revoir
 // Fonction pour calculer l'écart maximum ε pour une brindille P
    
    private double calculerEcartMaxEpsilon(Quadtree P) {
        double epsilon = 0.0;

        if (P != null) {
            for (int i = 0; i < 4; i++) {
                if (P.fils[i] != null) {
                    double ecart = Math.abs(P.calculMoyenneBrindille() - P.fils[i].getLuminosity());
                    epsilon = Math.max(epsilon, ecart);
                }
            }
        }

        return epsilon;
    }
    
    
	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//test si le quadTree est une brindille (tous les fils sont des feuilles)
          private  Boolean estBrindille() {
	           return fils[0].estFeuille && fils[1].estFeuille && fils[2].estFeuille && fils[3].estFeuille;
          }

	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//Fonction qui converti un Quadtree en texte
	public String toString() { // converti le quadtree en chaine parentésé
		String chaine; // chaine de retour
		
		if(estFeuille) { 
			return "n=" + w + " (" + lum + ")"; 
		} else {
			chaine = "n=" + w + " (";
			for (int i=0; i<3; i++) {chaine += fils[i].toString() + ", ";}
			chaine += fils[3].toString();
			chaine += ")";
			return chaine;
		}
	}
	//////////////////////////////////////////////////////////////////////////
	/*
	Fonction qui converti un Quadtree en texte
	fonction qui, a partir du Quadtree, reconstruit la matrice des pixel
   */
       private int[][] toMatrice() {
	             int matrice[][] = new int[w][h];
	
	            return matrice;
       }

   ///////////////////////////////////////////////////////////////////////////////
   /*
          Cette fonction prends une matrice et la transforme en chaine de caractère
          afin de pouvoir ecrire dans un fichier.
   */
       public String matriceToString(int[][] matrice) {
               String chaine = "";

                           return chaine;
       }


    
	////////////////////////////////////////////////////////////////////////////
       /**
   	 * Cette methode prends un chemin vers un fichier
   	 * et enregistre l'image du quadtree dans le fichier
   	*/
   	public void toPGM(String path_to_file) { 
   		int[][] matrice;
   		int[][] finalMatrice=new int[h][w];
   		String chaineDePixel; 
   		
   		try {
   			// creation d'un fichier 
   			FileWriter file_writer = new FileWriter(path_to_file);

   			// ecriture du début 
   			file_writer.write("P2\n"); // Nuance de gris
   			file_writer.write(commentaires + "\n"); // restitution des commentaires
   			file_writer.write(w + " " + h + "\n"); // dimension de l'image
   			file_writer.write(lumiMax + "\n"); //luminiosité maximal
   			
   			// restitution de la matrice
   			matrice = this.toMatrice();
   			chaineDePixel = this.matriceToString(matrice);
   			
   			this.toMatrice(finalMatrice, 0, 0);
   			chaineDePixel = this.matriceToString(finalMatrice);

   			file_writer.write(chaineDePixel);
   			
   			file_writer.close();
   			
   		} catch (IOException e) {
   			System.out.println("le fichier : " + path_to_file + ", n'a pas pu être crée !");
   		}
   		
   	}


	//////////////////////////////////////////////////////////////////////////////////////
	
	//public int NbNoeud(Quadtree P) {return 0 ;}
	
	///////////////////////////////////////////////////////////////////
	
	// 1/4(nbNoeud -(rho/100 *nbNoeud))
	public int nbBrindilleASupprimer(int rho) {
				return ( (nbNoeud - (int)Math.floor((rho/100)*nbNoeud))/4) ; // la fonction ceil permet de recuperer l'entier qui suit un décimal
	                                                                    // la fonction floor permet de recuperer l'entier qui précede un decimal
	}
	////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * fonction qui fait la compression rho
	 * */
	
	public int compressRho() {
		double rho = 0 ;	
		Quadtree P = null;
		
		//ArrayList<Double> list = new ArrayList() ;
		
		rho = calculerEcartMaxEpsilon(P) ;
		
	//	list.add(rho) ;		
		
		
		
		return 0;
	}
	

   //////////////////////////////////////////////////////////////////////////////////////	
	public static void main(String[] arg) {
		int a ;
		
		Quadtree  monImage  =  new  Quadtree ( "camera.pgm" );

		System.out.println ( monImage.toString ());
		
		monImage . toPGM ( "mdr.pgm" );
        
		Quadtree myImage = new Quadtree("camera.pgm");

		System.out.println(myImage.toString());
		
		System.out.println(myImage.nbNoeudInitial);

		System.out.println();

		
	}
	
}	










