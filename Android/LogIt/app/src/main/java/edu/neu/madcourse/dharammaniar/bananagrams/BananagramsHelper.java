package edu.neu.madcourse.dharammaniar.bananagrams;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Dharam on 10/21/2014.
 */
public class BananagramsHelper {

    public List<String> initializeBunch() {
        List<String> bunch = new ArrayList<String>();
        for (int i = 0; i < 18; i++) {
            bunch.add("E");
        }
        for (int i = 0; i < 13; i++) {
            bunch.add("A");
        }
        for (int i = 0; i < 12; i++) {
            bunch.add("I");
        }
        for (int i = 0; i < 11; i++) {
            bunch.add("O");
        }
        for (int i = 0; i < 9; i++) {
            bunch.add("R");
            bunch.add("T");
        }
        for (int i = 0; i < 8; i++) {
            bunch.add("N");
        }
        for (int i = 0; i < 6; i++) {
            bunch.add("D");
            bunch.add("S");
            bunch.add("U");
        }
        for (int i = 0; i < 5; i++) {
            bunch.add("L");
        }
        for (int i = 0; i < 4; i++) {
            bunch.add("G");
        }
        for (int i = 0; i < 3; i++) {
            bunch.add("B");
            bunch.add("C");
            bunch.add("F");
            bunch.add("H");
            bunch.add("M");
            bunch.add("P");
            bunch.add("V");
            bunch.add("W");
            bunch.add("Y");
        }
        for (int i = 0; i < 2; i++) {
            bunch.add("J");
            bunch.add("K");
            bunch.add("Q");
            bunch.add("X");
            bunch.add("Z");
        }

        return bunch;

    }

    public String[] selectLetters(List<String> bunch, Random randomGenerator) {
        String[] tiles = new String[10];
        for (int i = 0; i < 10; i++) {
            int index = randomGenerator.nextInt(bunch.size());
            tiles[i] = bunch.get(index);
            bunch.remove(index);
        }
        return tiles;
    }

    public void saveUserTiles(String[] usertiles, SharedPreferences.Editor editor) {
        String usertilesString = "";
        for (int i = 0; i < usertiles.length; i++) {
            if (i < usertiles.length - 1) {
                usertilesString = usertilesString + usertiles[i] + "_";
            } else {
                usertilesString = usertilesString + usertiles[i];
            }
        }
        editor.putString("UserTiles", usertilesString);
        editor.commit();
    }

    public String[] peel(String[] usertiles, List<String> bunch, Random randomGenerator) {
        if (bunch.size() > 1) {
            int index = randomGenerator.nextInt(bunch.size());
            for (int i = 0; i < usertiles.length; i++) {
                if (usertiles[i].equals(" ")) {
                    usertiles[i] = bunch.get(index);
                    bunch.remove(index);
                    break;
                }
            }
            return usertiles;
        } else {
            return usertiles;
        }
    }

    public String[] dump(String[] usertiles, List<String> bunch, Random randomGenerator) {
        int dumpCount = 0;
        while (bunch.size() > 1 && dumpCount < 3) {
            int index = randomGenerator.nextInt(bunch.size());
            for (int i = 0; i < usertiles.length; i++) {
                if (usertiles[i].equals(" ")) {
                    usertiles[i] = bunch.get(index);
                    bunch.remove(index);
                    break;
                }
            }
            dumpCount++;
        }
        return usertiles;
    }


    public int calculatePoints(List<String> formedWords) {
        int points = 0;
        for (int i = 0; i < formedWords.size(); i++) {
            System.out.println("Calculating for " + formedWords.get(i));
            for (int j = 0; j < formedWords.get(i).length(); j++) {
                switch (formedWords.get(i).charAt(j)) {
                    case 'a':
                    case 'e':
                    case 'i':
                    case 'o':
                        points = points + 1;
                        break;
                    case 'n':
                    case 'r':
                    case 't':
                        points = points + 2;
                        break;
                    case 'd':
                    case 'l':
                    case 's':
                    case 'u':
                        points = points + 3;
                        break;
                    case 'b':
                    case 'c':
                    case 'f':
                    case 'g':
                    case 'h':
                    case 'm':
                    case 'p':
                    case 'v':
                    case 'w':
                    case 'y':
                        points = points + 4;
                        break;
                    case 'j':
                    case 'k':
                    case 'q':
                    case 'x':
                    case 'z':
                        points = points + 5;
                        break;
                }

            }
        }
        return points;
    }
}
