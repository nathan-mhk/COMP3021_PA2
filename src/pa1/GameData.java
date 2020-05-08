package pa1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameData {
    private List<Player> players;

    public List<Player> getPlayers() {
        return players;
    }

    /**
     * loadGameData from players.txt
     */
    public void loadGameData(String filename) throws IOException {
        //  the following reads the players.txt file
        //  1. The first line includes the number of players
        //  2. Then there is an empty line
        //  3. Then the next line includes the detailed information of  a player
        //     including player name, player hospitalName and initial
        //     amount of money (all these fields are separated by one space each).
        //  4. then another empty line
        //  5. then another line of player information
        //  6. and so on so forth

        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            int numPlayers = Integer.parseInt(in.readLine()); // number of players read
            in.readLine(); // read the empty line

            players = new ArrayList<>(); // create the ArrayList for holding all the player objects
            for (int i = 0; i < numPlayers; i++) {
                String[] tokens = in.readLine().split(" ");
                String playerName = tokens[0];
                String hospitalName = tokens[1];
                int money = Integer.parseInt(tokens[2]);

                Player player = new Player(playerName, hospitalName, money);
                players.add(player);
                in.readLine();
            }
        }
    }
}
