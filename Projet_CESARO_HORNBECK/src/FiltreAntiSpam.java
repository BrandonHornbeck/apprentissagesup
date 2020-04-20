import java.io.*;

import static java.lang.Integer.parseInt;

public class FiltreAntiSpam {
    static int TAILLE_DICO = 1000;


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
            dic[i] = line;
            i++;
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
                    if(line.toUpperCase().contains(dico[i])){
                        x[i] = 1;
                    }
                }
            }

        }
        return x;
    }

    public static void main(String args[]) {
        if (args.length >= 2) {
            try {
                String[] dic = charger_dictionnaire();
                int[] x;

                int mSpam = Integer.parseInt(args[0]);
                int mHam = Integer.parseInt(args[1]);
                double[] bSpam = new double[TAILLE_DICO];
                double[] bHam = new double[TAILLE_DICO];
                for (int i = 0; i < TAILLE_DICO; i++) {
                    bSpam[i] = 0;
                    bHam[i] = 0;
                }
                //String[] nomsSpams = new String[mSpam];
                //String[] nomsHams = new String[mHam];
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
                    bSpam[i] = bSpam[i] / mSpam;
                    bHam[i] = bHam[i] / mHam;
                    System.out.println(bSpam[i]);
                    System.out.println(bHam[i]);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else{
            System.out.println(args.length);
            System.out.println("trop peu d'arguments");
        }
    }
}
