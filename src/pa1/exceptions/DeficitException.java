package pa1.exceptions;

import pa1.Player;

public class DeficitException extends Exception {
    private final int deficit;
    private final int deficitTimeSpan;
    private final String playerName;

    /**
     * constructor
     *
     * @param player the player who enters into deficit status
     */
    public DeficitException(Player player) {
        this.deficit = player.getMoney();
        this.playerName = player.getName();
        this.deficitTimeSpan = player.getDeficitTimeSpan();
    }

    /**
     * getMessage
     *
     * @return the formatted message string
     */
    @Override
    public String getMessage() {
        return String.format("Player %s has been in deficit for %d turns, current deficit is %d, " +
                        "keeping in deficit status will lead to bankruptcy",
                playerName, deficitTimeSpan, deficit);
    }
}
