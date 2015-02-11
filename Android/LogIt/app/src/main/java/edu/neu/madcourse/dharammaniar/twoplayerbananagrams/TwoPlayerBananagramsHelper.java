package edu.neu.madcourse.dharammaniar.twoplayerbananagrams;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Dharam on 10/21/2014.
 */
public class TwoPlayerBananagramsHelper {

    public List<String> initializeBunch(Context context) {
        List<String> bunch = new ArrayList<String>();
        String bunchString = getGCMPreferences(context).getString("bunch", "bunch");
        if (!bunchString.equals("bunch")) {
            String bunchSplit[] = bunchString.split("`");
            for (int i = 0; i < bunchSplit.length; i++) {
                bunch.add(bunchSplit[i]);
            }
        } else {

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

    public String saveGameState(String[] boardtiles, String[] usertiles, List<String> bunch,
                                int points, long timeLeft) {
        System.out.println("Received Points = " + points);
        String gameState = "";
        JSONObject gameStateJson = new JSONObject();
        String boardTilesString = "";
        for (int i = 0; i < boardtiles.length; i++) {
            if (i == 0) {
                boardTilesString = boardtiles[i];
            } else {
                boardTilesString = boardTilesString + "`" + boardtiles[i];
            }
        }
        String userTilesString = "";
        for (int i = 0; i < usertiles.length; i++) {
            if (i == 0) {
                userTilesString = usertiles[i];
            } else {
                userTilesString = userTilesString + "`" + usertiles[i];
            }
        }
        String bunchString = "";
        for (int i = 0; i < bunch.size(); i++) {
            if (i == 0) {
                bunchString = bunch.get(i).toString();
            } else {
                bunchString = bunchString + "`" + bunch.get(i).toString();
            }
        }
        try {
            gameStateJson.put("boardTiles", boardTilesString);
            gameStateJson.put("userTiles", userTilesString);
            gameStateJson.put("bunch", bunchString);
            gameStateJson.put("points", points);
            gameStateJson.put("timeLeft", timeLeft);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("Returning " + gameStateJson.toString());
        return gameStateJson.toString();
    }

    public void getGameState(String message, Context context) {

        try {
            JSONObject gameStateJson = new JSONObject(message);
            String boardTilesString = gameStateJson.getString("boardTiles");
            String userTilesString = gameStateJson.getString("userTiles");
            String bunchString = gameStateJson.getString("bunch");
            int points = gameStateJson.getInt("points");
            long timeLeft = gameStateJson.getLong("timeLeft");

            SharedPreferences.Editor editor = getGCMPreferences(context).edit();
            editor.putString("opponentBoardTiles", boardTilesString);
            editor.putString("opponentUserTiles", userTilesString);
            editor.putString("bunch", bunchString);
            editor.putInt("opponentPoints", points);
            editor.putLong("opponentTimeLeft", timeLeft);
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences("GCMPreferences",
                                            Context.MODE_PRIVATE);
    }

    public List<String> getBunch(Context context) {
        List<String> bunch = new ArrayList<String>();
        String bunchString = getGCMPreferences(context).getString("bunch", "bunch");
        String bunchSplit[] = bunchString.split("`");
        for (int i = 0; i < bunchSplit.length; i++) {
            bunch.add(bunchSplit[i]);
        }
        return bunch;
    }

    public String[] shake(String[] usertiles, Random randomGenerator) {
        String newUserTiles[] = new String[10];
        int j = 9;
        for (int i = 0; i < usertiles.length; i++) {
            newUserTiles[i] = usertiles[j];
            j--;
        }
        return newUserTiles;
    }
}
