import java.util.Random;
import java.util.Scanner;

public class GameOperator {

    private static int numOfPlayers;
    private static PlayerList players = new PlayerList();
    private static int pot = 0;
    private static int smallBlind;
    private static int bigBlind;
    private static Scanner scanner = new Scanner(System.in);
    private static int highBet = 0;
    private static boolean[] fold = new boolean[5];

    public GameOperator() {
    }

    public void playGame() {
        System.out.println("Welcome! How many players?");
        int numberOfPlayers = scanner.nextInt();
        int numberOfChips = 1000;
        for (int i = 0; i < numberOfPlayers; i++) {
            System.out.println("What is the name of player" + (i + 1) + "?");
            players.addPlayer(new Player(scanner.next(), numberOfChips));
        }


        smallBlind = 0;
        bigBlind = (smallBlind + 1)%numberOfPlayers;

        players.get(smallBlind).bet(1);
        players.get(bigBlind).bet(2);
        pot+=3;
        highBet+=2;

        while (true) {

            players.print();
            System.out.println("---------------------------------------");
            System.out.println("Round 1");
            System.out.println("---------------------------------------");
            players.resetCall();
            int playerIndex = (bigBlind + 1) % numberOfPlayers;
            while (true) {
                if (players.allCalledOrFolded()) break;
                Player player = players.get(playerIndex);
                if (player.hasFolded()) continue;
                operate(getAction(playerIndex), player);
                playerIndex = (playerIndex + 1) % numberOfPlayers;
            }


            for (int round = 1; round < 4; round++) {
                System.out.println("Round " + (round + 1));
                System.out.println("---------------------------------------");
                players.resetCall();
                playerIndex = smallBlind - 1;
                while (true) {
                    playerIndex = (playerIndex + 1) % numberOfPlayers;
                    if (players.allCalledOrFolded()) break;
                    Player player = players.get(playerIndex);
                    if (player.hasFolded()) continue;
                    operate(getAction(playerIndex), player);
                }
            }

            System.out.println("Who win?");
            String name = scanner.next();
            System.out.println("Congratulations! " + name);
            players.getPlayerByName(name).win(pot);
            players.print();
            players.reset();
            pot = 3;
            smallBlind = (smallBlind + 1) % numberOfPlayers;
            bigBlind = (bigBlind + 1) % numberOfPlayers;
            players.get(smallBlind).bet(1);
            players.get(bigBlind).bet(2);
            highBet = 2;

            System.out.println("Quit? (y/n)");
            if (scanner.next().toLowerCase().equals("y")) {
                System.exit(0);
            }
        }
    }

    public void operate(String command, Player player){
        String action = command.split(" ",2)[0].toLowerCase();
        switch (action){
            case "call", "ca", "cal", "cl" -> {
                int betAmount = highBet - player.getBet();
                player.bet(betAmount);
                pot+=betAmount;
                player.setHasCalled(true);
                System.out.println(player.getName() + " called with " + betAmount + " chips");
                System.out.println("---------------------------------------");
            }

            case "fold", "f" -> {
                player.fold();
                System.out.println(player.getName() + " folded");
                System.out.println("---------------------------------------");
            }

            case "raise", "r" -> {
                players.resetCall();
                player.setHasCalled(true);
                int raiseAmount = scanner.nextInt();
                highBet += raiseAmount;
                int betAmount = highBet - player.getBet();
                player.bet(betAmount);
                pot += betAmount;
                System.out.println(player.getName() + " raised by " + raiseAmount + " with " + betAmount + " chips");
                System.out.println("---------------------------------------");
            }

            case "check","ch","chck","ck","chk" -> {
                if(player.getBet() == highBet){
                    player.setHasCalled(true);
                } else{
                    throw new IllegalStateException("The player can't check");
                }
                System.out.println(player.getName() + " called");
                System.out.println("---------------------------------------");
            }

            default -> throw new IllegalStateException("Unexpected value: " + action);
        }
    }

    private static String getAction(int j) {
        System.out.println("It's " + players.get(j).getName() + "'s turn. Chips: " + players.get(j).getChips()
        + ". Pot: " + pot);
        System.out.println("What does " + players.get(j).getName() + " want to do?");
        String action = scanner.next();
        return action;
    }

}