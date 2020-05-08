package pa1;

import java.util.Scanner;

import pa1.departments.Department;
import pa1.doctors.Doctor;
import pa1.exceptions.DeficitException;
import pa2.Main;

public class GameEngine {
    public int turns;
    private Scanner userInputScanner = new Scanner(System.in);
    public GameData gameData = new GameData();

    /**
     * Test if game is over.
     *
     * @return true if the game is over
     */

    public boolean isGameOver() {
        if (turns > 20) return true;
        for (Player player: gameData.getPlayers()) {
            if (player.hasBankrupted()) return true;
        }
        return false;
    }

    public int getTurns()
    {
        return turns;
    }
    /**
     * Get the winner for the game.
     *
     * @return the winning player in the game
     */
    public Player getWinner() {

        if (!isGameOver()) return null;
        if (turns > 20) {
            Player winner = null;
            for (Player player: gameData.getPlayers()) {
                if (winner == null) {
                    winner = player;
                } else {
                    if (winner.getCuredPatientCount() < player.getCuredPatientCount()) {
                        winner = player;
                    }
                }
            }
            return winner;
        } else {
            Player winner = null;
            for (Player player: gameData.getPlayers()) {
                if (winner == null)
                    winner = player;
                else
                {
                    if(winner.getMoney() < player.getMoney()){
                        winner = player;
                    }
                }

            }
            return winner;
        }
    }





   private void printPlayerInfo(Player player) {
        System.out.print("\n\n");
        System.out.println(player);
        System.out.print("\n");
        String r = "\n" + player.toString() + "\n";
        Main.printResult.add(r);
        for (Department dept: player.getDepartments()) {
            System.out.println(dept);
            String result = dept.toString();
            Main.printResult.add(result);
        }
        System.out.print("\n");
    }




    /*private void printStatistics() {
        System.out.printf("*** Game statistics\n", turns);
        for (Player player: gameData.getPlayers()) {
            System.out.printf("*** %s\n", player);
        }
        System.out.println();
    }*/


}
