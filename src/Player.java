public class Player {
    private String name;
    private int chips;
    private boolean hasFolded;
    private int bet;
    private boolean hasCalled;
    private boolean allIn;

    public boolean isAllIn() {
        return allIn;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

    public int getBet() {
        return bet;
    }

    public void setHasCalled(boolean hasCalled) {
        this.hasCalled = hasCalled;
    }

    public boolean hasCalled(){
        return hasCalled;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public boolean hasFolded() {
        return hasFolded;
    }

    public void fold(){
        hasFolded = true;
    }


    public void reset(){
        hasFolded = false;
        bet = 0;
    }

    public Player(String name, int chips) {
        this.name = name;
        this.chips = chips;
        this.bet = 0;
        this.hasFolded = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void bet(int chips){
        this.chips -= chips;
        this.bet += chips;
    }

    public void win(int pot){
        this.chips += pot;
    }



    @Override
    public String toString() {
        return "Player: " + name + ", Chips: " + chips;
    }
}
