public class Player {
    private final String name;
    private int chips;
    private boolean hasFolded;
    private int bet;
    private boolean hasCalled;
    private boolean allIn;

    public boolean isAllIn() {
        return allIn;
    }

    public int getTotal(){
        return chips + bet;
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

    public boolean isCalled(){
        return !hasCalled;
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


    public int getChips() {
        return chips;
    }

    public void bet(int chips){
        if(this.chips - chips < 0){
            throw new RuntimeException("Chips can't be less than 0");
        }
        this.chips -= chips;
        this.bet += chips;
    }

    public void win(int pot){
        this.chips += pot;
    }

    public boolean isBankrupt(int amount){
        return chips+bet-amount <=0;
    }

    @Override
    public String toString() {
        return "Player: " + name + ", Chips: " + chips;
    }
}
