import java.util.*;

public class PlayerList {
    private final ArrayList<Player> players;

    public Player get(int index) {
        return players.get(index);
    }

    public boolean allCalledOrFolded(){
        for(Player p : players){
            if(!p.hasFolded()&& p.isCalled() &&!p.isAllIn()&&!(p.getChips()<=0)){
                return false;
            }
        }
        return true;
    }

    public void resetCall(){
        for(Player p : players){
            p.setHasCalled(false);
        }
    }

    public void reset(){
        for(Player p : players){
            p.reset();
            p.setAllIn(false);
        }
        resetCall();
    }

    public PlayerList() {
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Player getPlayerByName(String name){
        return players.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    public void print() {
        for (Player player : players) {
            System.out.println(player);
        }
    }

    public int getActivePlayersNumber(){
        int count = 0;
        for (Player player : players) {
            if (!player.hasFolded()&&!(player.isBankrupt(0))) {
                count++;
            }
        }
        return count;
    }

    public Player getActivePlayer() {
        for (Player player : players) {
            if (!player.hasFolded()) {
                return player;
            }
        }
        return null;
    }

    public ArrayList<String> getActivePlayers(){
        ArrayList<String> playerNames = new ArrayList<>();
        for (Player player : players) {
            if (!player.hasFolded()&&!player.isBankrupt(0)) {
                playerNames.add(player.getName());
            }
        }
        return playerNames;
    }

    public boolean allButOneNotFoldedOrAllin(){
        int count = 0;
        for (Player player : players) {
            if(!player.hasFolded()&& player.isCalled() &&!player.isAllIn()) {
                return false;
            }
            if (!player.hasFolded()&&!player.isAllIn()) {
                count++;
            }
        }
        return count <= 1;
    }

    public int calculateSidePot(int amount){
        int sidePot = 0;
        for (Player player : players) {
            if(player.getBet() > amount){
                sidePot += amount;

                player.win(player.getBet()-amount);
            } else {
                sidePot += player.getBet();
            }
        }
        return sidePot;
    }

    public void settlePayments(int initialChips){
        Map<Player, Integer> playerBalances = new HashMap<>();
        for (Player player : players) {
            playerBalances.put(player, player.getChips()-initialChips);
        }

        List<Player> payers = new ArrayList<>();
        List<Player> payees = new ArrayList<>();

        for (Player player : players) {
            if (player.getChips() < initialChips) {
                payers.add(player);
            } else if (player.getChips() > initialChips) {
                payees.add(player);
            }
        }

        for(Player payer : payers) {
            int amountOwed = -playerBalances.get(payer);

            for (Player payee : payees) {
                int amountWon = playerBalances.get(payee);

                if(amountWon > 0 && amountOwed > 0){
                    int payment = Math.min(amountOwed, amountWon);

                    System.out.println(payer.getName() + " owes " + payment + " chips to "
                        + payee.getName());

                    playerBalances.put(payer, playerBalances.get(payer) + payment);
                    playerBalances.put(payee, playerBalances.get(payee) - payment);
                    amountOwed -= payment;
                }
            }
        }
    }

    public boolean onlyOneLeft(int amount){
        int count = 0;
        for (Player player : players) {
            if(!player.isBankrupt(amount)) count++;
        }
        return count <= 1;
    }

    public boolean allOpponentsAllInOrFolded(Player player){
        for (Player p : players) {
            if(p == player) continue;
            if(!p.hasFolded()&&!p.isAllIn()&&!p.isBankrupt(0)) return false;
        }
        return true;
    }
}
