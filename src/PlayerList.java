import java.util.ArrayList;
import java.util.Collections;

public class PlayerList {
    private ArrayList<Player> players;

    public Player get(int index) {
        return players.get(index);
    }

    public void resetBets(){
        for(Player p : players){
            p.setBet(0);
        }
    }
    public boolean allCalledOrFolded(){
        for(Player p : players){
            if(!p.hasFolded()&&!p.hasCalled()){
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
        }
        resetCall();
    }

    public PlayerList() {
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void shufflePlayers() {
        Collections.shuffle(players);
    }

    public int getIndexOfPlayer(Player player) {
        return players.indexOf(player);
    }

    public Player getPlayerByName(String name){
        return players.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    public void print() {
        for (Player player : players) {
            System.out.println(player);
        }
    }
}
