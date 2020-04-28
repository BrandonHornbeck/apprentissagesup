import java.io.*;

import static java.lang.Integer.parseInt;

public class FiltreAntiSpam {
    static int TAILLE_DICO = 853; //1000 - 147 mots de 3 lettres ou moins


    /**
     * charge le dictionnaire de données
     * @return le dictionnaire
     * @throws IOException
     */
    public static String[] charger_dictionnaire() throws IOException {
        String[] dic = new String[TAILLE_DICO];
        InputStream flux = new FileInputStream("./dictionnaire1000en.txt");
        InputStreamReader reader = new InputStreamReader(flux);
        BufferedReader buffer = new BufferedReader(reader);
        String line;
        int i = 0;

        while ((line = buffer.readLine()) != null){
            if(line.length() > 3){
                dic[i] = line;
                i++;
            }

        }

        return dic;
    }

    /**
     *
     * @param dico dictionnaire de mots courants
     * @param path chemin vers le fichier
     * @return un vecteur binaire
     * @throws IOException
     */
    public static int[] lire_message(String[] dico, String path) throws IOException {
        int[] x = new int[TAILLE_DICO];
        for (int i = 0; i < TAILLE_DICO; i++){
            x[i] = 0;
        }
        InputStream flux = new FileInputStream(path);
        InputStreamReader reader = new InputStreamReader(flux);
        BufferedReader buffer = new BufferedReader(reader);
        String line;
        String[] words;

        while ((line = buffer.readLine()) != null){
            for (int i = 0; i < TAILLE_DICO; i++){
                if(x[i] == 0){
                    if(line.toUpperCase().contains(dico[i]) && dico[i].length() > 3){
                        x[i] = 1;
                    }
                }
            }

        }
        return x;
    }

    public static void main(String args[]) {
        if (args.length >= 4){
            try {
                int epsilon = 1;
                String[] dic = charger_dictionnaire();
                int[] x;

                int mSpam = Integer.parseInt(args[1]);
                int mHam = Integer.parseInt(args[2]);
                double[] bSpam = new double[TAILLE_DICO];
                double[] bHam = new double[TAILLE_DICO];
                for (int i = 0; i < TAILLE_DICO; i++){
                    bSpam[i] = 0;
                    bHam[i] = 0;
                }
                //apprentissage
                File dossierSpams = new File("./baseapp/spam");
                File dossierHams = new File("./baseapp/ham");
                File[] listeSpams = dossierSpams.listFiles();
                File[] listeHams = dossierHams.listFiles();
                for (int i = 0; i < mSpam; i++) {
                    if (listeSpams[i].isFile()) {
                        x = lire_message(dic, "./baseapp/spam/" + listeSpams[i].getName());
                        for (int j = 0; j < TAILLE_DICO; j++) {
                            bSpam[j] += x[j];
                        }
                    }
                }
                for (int i = 0; i < mHam; i++) {
                    if (listeHams[i].isFile()) {
                        x = lire_message(dic, "./baseapp/ham/" + listeHams[i].getName());
                        for (int j = 0; j < TAILLE_DICO; j++) {
                            bHam[j] += x[j];
                        }
                    }
                }
                for (int i = 0; i < TAILLE_DICO; i++) {
                    bSpam[i] = (bSpam[i] +  epsilon) / (mSpam + (2 * epsilon)); //calcul final des bSpam et bHam
                    bHam[i] = (bHam[i] +  epsilon) / (mHam + (2 * epsilon));

                }
                //tests
                //on récupère les m'
                mSpam = Integer.parseInt(args[3]);
                mHam = Integer.parseInt(args[4]);

                dossierSpams = new File(args[0]+"/spam");
                dossierHams = new File(args[0]+"/ham");
                listeSpams = dossierSpams.listFiles();
                listeHams = dossierHams.listFiles();
                double pSpam = ((double)mSpam) / (mSpam + mHam); //P(Y = SPAM)
                double pHam = 1 - pSpam; //P(Y=HAM)

                int nbErreursSpams = 0;
                int nbErreursHams = 0;
                for (int i = 0; i < mSpam; i++) {
                    if (listeSpams[i].isFile()) {
                        double pPosterioriSpam = 1, pPosterioriHam = 1 ; // proba a posteriori
                        double produitSPam = 1, produitHam = 1; //produit des (bjSpam ^ xj)(1-bjSpam ^ 1-xj)

                        x = lire_message(dic, args[0]+"/spam/" + listeSpams[i].getName());
                        //produit des (bjSpam ^ xj)(1-bjSpam ^ 1-xj)
                        for (int j = 0; j < TAILLE_DICO; j++) {
                            //le calcul suivant équivaut a
                            double bj = 1 - bSpam[j] ;
                            if(x[j] > 0) bj = bSpam[j];


                            produitSPam *= bj; // pour la formule a posteriori

                            bj = 1 - bHam[j];
                            if(x[j] > 0) bj = bHam[j];
                            produitHam *= bj; //pour la formule de proba a posteriori

                        }
                        double pX = produitHam*pHam + produitSPam*pSpam; //p(X=x)
                        //p(X=x) = p(X = x | Y = HAM)p(Y=HAM) + p(X = x | Y = SPAM)p(Y=SPAM)
                        pPosterioriSpam *= pSpam*produitSPam/pX;
                        pPosterioriHam *= pHam*produitHam/pX;

                        System.out.println("Spam numéro "+i+" : P(Y = SPAM | X=x) = "+ pPosterioriSpam + ", P(Y=HAM| X = x) ) "+ pPosterioriHam);
                        if (pPosterioriSpam > pPosterioriHam){
                            System.out.println("-----> identifié comme SPAM");
                        } else{
                            nbErreursSpams++;
                            System.out.println("-----> identifié comme HAM     ******erreur******");
                        }

                    }
                }
                for (int i = 0; i < mHam; i++) {
                    if (listeSpams[i].isFile()) {
                        double pPosterioriSpam = 1, pPosterioriHam = 1 ; // proba a posteriori
                        double produitSPam = 1, produitHam = 1; //produit des (bjSpam ^ xj)(1-bjSpam ^ 1-xj)

                        x = lire_message(dic, args[0]+"/ham/" + listeHams[i].getName());
                        //produit des (bjSpam ^ xj)(1-bjSpam ^ 1-xj)
                        for (int j = 0; j < TAILLE_DICO; j++) {
                            //le calcul suivant équivaut a
                            double bj = 1 - bSpam[j] ;
                            if(x[j] > 0) bj = bSpam[j];


                            produitSPam *= bj; // pour la formule a posteriori

                            bj = 1 - bHam[j];
                            if(x[j] > 0) bj = bHam[j];
                            produitHam *= bj; //pour la formule de proba a posteriori

                        }
                        double pX = produitHam*pHam + produitSPam*pSpam; //p(X=x)
                        //p(X=x) = p(X = x | Y = HAM)p(Y=HAM) + p(X = x | Y = SPAM)p(Y=SPAM)
                        pPosterioriSpam *= pSpam*produitSPam/pX;
                        pPosterioriHam *= pHam*produitHam/pX;

                        System.out.println("Ham numéro "+i+" : P(Y = SPAM | X=x) = "+ pPosterioriSpam + ", P(Y=HAM| X = x) ) "+ pPosterioriHam);
                        if (pPosterioriSpam > pPosterioriHam){
                            System.out.println("-----> identifié comme SPAM     ******erreur******");
                            nbErreursHams++;
                        } else{

                            System.out.println("-----> identifié comme HAM");
                        }

                    }
                }

                System.out.println("pourcentage d'erreur SPAM: "+((double)nbErreursSpams/mSpam)*100);
                System.out.println("pourcentage d'erreur HAM: "+((double)nbErreursHams/mHam)*100);






            } catch (IOException e) {
                e.printStackTrace();
            }

        } else{
            System.out.println(args.length);
            System.out.println("trop peu d'arguments");
        }
    }
}
