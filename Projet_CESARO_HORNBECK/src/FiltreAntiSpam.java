import java.io.*;

public class FiltreAntiSpam {
    static int TAILLE_DICO = 1000;


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

    public static void main(String args[]){
        try {
            String[] dic = charger_dictionnaire();
            int[] x = lire_message(dic, "./baseapp/spam/0.txt");
            for (int i = 0; i < TAILLE_DICO; i++){
                System.out.println(dic[i] + " : " + x[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
