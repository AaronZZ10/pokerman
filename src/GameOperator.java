import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GameOperator {

    private static final PlayerList players = new PlayerList();
    private static int pot = 0;
    private static final Scanner scanner = new Scanner(System.in);
    private static int highBet = 0;
    private static int smallBlindAmount = 1;
    private static int bigBlindAmount = 2;
    private static int numberOfChips = 1000;

    public GameOperator() {
    }

    public void playGame() {
        System.out.println("Welcome! I am Daniel, and it's my pleasure to serve as your banker");
        System.out.println("How many players?");
        int numberOfPlayers = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < numberOfPlayers; i++) {
            System.out.println("What is the name of " + formatOrdinal(i+1) + " player?");
            players.addPlayer(new Player(scanner.nextLine(), numberOfChips));
        }
        System.out.println("Do you want to customize the chips of small big and big blind and the number of" +
                "chips of each player (default: 1, 2, 1000)? (y/n)");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            System.out.println("Amount of chips for Small Blind: ");
            smallBlindAmount = Integer.parseInt(scanner.nextLine());
            System.out.println("Amount of chips for big blind: ");
            bigBlindAmount = Integer.parseInt(scanner.nextLine());
            System.out.println("Amount of chips of each player: ");
            numberOfChips = Integer.parseInt(scanner.nextLine());
        }

        Random rand = new Random();
        int smallBlind = rand.nextInt(numberOfPlayers);
        int bigBlind = (smallBlind + 1) % numberOfPlayers;

        players.get(smallBlind).bet(smallBlindAmount);
        players.get(bigBlind).bet(bigBlindAmount);
        pot += (smallBlindAmount+bigBlindAmount);
        highBet += bigBlindAmount;

        while (true) {
            boolean allFolds = false;

            players.print();
            System.out.println("---------------------------------------");
            System.out.println(formatRound(0) + " round");
            System.out.println("---------------------------------------");
            players.resetCall();
            int playerIndex = bigBlind % numberOfPlayers;
            while (true) {
                playerIndex = (playerIndex + 1) % numberOfPlayers;
                if (players.allCalledOrFolded()) break;
                Player player = players.get(playerIndex);
                if (whetherToSkip(player)) continue;
                if(whetherToBreak(player)) break;
                try{operate(getAction(player), player);} catch (Exception e) {
                    System.out.println("Please try again");
                    System.err.println(e.getMessage());
                    System.out.println("---------------------------------------");
                    playerIndex = (playerIndex - 1) % numberOfPlayers;
                }
                if(players.getActivePlayersNumber() == 1){
                    Player winner = players.getActivePlayer();
                    winner.win(pot);
                    allFolds = true;
                    System.out.println(winner.getName() + " won the game as the last player on the table!");
                    break;
                }

            }

            if(!allFolds&&!players.allButOneNotFoldedOrAllin()) {
                for (int round = 1; round < 4; round++) {
                    if (allFolds) break;
                    System.out.println(formatRound(round)+ " round");
                    System.out.println("---------------------------------------");
                    players.resetCall();
                    playerIndex = smallBlind - 1;
                    while (true) {
                        playerIndex = (playerIndex + 1) % numberOfPlayers;
                        if (players.allCalledOrFolded()) break;
                        Player player = players.get(playerIndex);
                        if(whetherToBreak(player)) break;
                        if (whetherToSkip(player)) continue;
                        try{operate(getAction(player), player);} catch (Exception e) {
                            System.out.println("Please try again");
                            System.err.println(e.getMessage());
                            System.out.println("---------------------------------------");
                            playerIndex = (playerIndex - 1) % numberOfPlayers;
                        }
                        if (players.getActivePlayersNumber() == 1) {
                            Player winner = players.getActivePlayer();
                            winner.win(pot);
                            allFolds = true;
                            System.out.println(winner.getName() + " won the game as the last player on the table!");
                            break;
                        }
                    }
                }
            }
            if(!allFolds){
                ArrayList<String> activePlayers = players.getActivePlayers();
                System.out.println("Who won the game? [" + String.join(",",activePlayers) + "]");
                String name = scanner.nextLine();
                while(!activePlayers.contains(name)){
                    System.err.println("Name isn't correct, please try again!");
                    System.out.println("Who won the game? [" + String.join(",",activePlayers) + "]");
                    name = scanner.nextLine();
                }
                System.out.println(name + ", congratulations!");
                Player winner = players.getPlayerByName(name);
                if(winner.isAllIn()){
                    winner.win(players.calculateSidePot(winner.getBet()));
                } else {
                    winner.win(pot);
                }
            }

            players.reset();
            players.print();


            boolean hasToEnd = players.onlyOneLeft(bigBlindAmount);
            String nextGame = "n";
            if(!hasToEnd) {
                System.out.println("Do you want to play another game? (y/n)");
                nextGame = scanner.nextLine().toLowerCase();
            }
            if (hasToEnd||(!nextGame.equals("y") && !nextGame.startsWith("ye"))){
                System.out.println("---------------------------------------");
                System.out.println("Settling payments");
                players.settlePayments(numberOfChips);
                System.out.println("Goodbye");
                System.out.println("---------------------------------------");
                System.exit(0);
            }



            pot = (smallBlindAmount + bigBlindAmount);
            do smallBlind = (smallBlind + 1) % numberOfPlayers; while(players.get(smallBlind).isBankrupt(smallBlindAmount));
            do bigBlind = (bigBlind + 1) % numberOfPlayers; while(players.get(bigBlind).isBankrupt(bigBlindAmount)&&bigBlind!=smallBlind);
            players.get(smallBlind).bet(smallBlindAmount);
            players.get(bigBlind).bet(bigBlindAmount);
            highBet = bigBlindAmount;
        }
    }

    public void operate(String command, Player player) throws PokerException {
        String[] commands = command.split(" ",2);
        String action = commands[0];
        switch (action){
            case "call", "ca", "cal", "cl", "c" -> {
                System.out.println(highBet);
                if (highBet>(player.getBet()+player.getChips())){
                    throw new PokerException("You must fold or all-in");
                }
                int betAmount = highBet - player.getBet();
                player.bet(betAmount);
                pot+=betAmount;
                player.setHasCalled(true);
                if(betAmount==0){
                    System.out.println(player.getName() + " checked");
                } else {
                    System.out.println(player.getName() + " called with " + betAmount + " chips");
                }
                System.out.println("---------------------------------------");
            }

            case "fold", "f","fd", "fo","fld" -> {
                player.fold();
                System.out.println(player.getName() + " folded");
                System.out.println("---------------------------------------");
            }

            case "raise", "r", "ra","rai", "rs" -> {
                if(players.allOpponentsAllInOrFolded(player)) throw new PokerException("You can't raise when everyone folded or went all-in");
                int raiseAmount;
                try{
                    raiseAmount = Integer.parseInt(commands[1]);
                } catch (Exception e) {
                    System.out.println("How many chips do you want to raise?");
                    raiseAmount = Integer.parseInt(scanner.nextLine());
                }
                if (raiseAmount <= 0) throw new PokerException("You must raise a positive number");

                highBet += raiseAmount;
                int betAmount = highBet - player.getBet();
                if(betAmount > player.getChips()){
                    throw new PokerException("The player doesn't have enough chips");
                }

                players.resetCall();
                player.setHasCalled(true);

                player.bet(betAmount);
                pot += betAmount;
                System.out.println(player.getName() + " went raise by " + raiseAmount + " with " + betAmount + " chips");
                System.out.println("---------------------------------------");
            }

            case "check","ch","ck","chk" -> {
                if(player.getBet() == highBet){
                    player.setHasCalled(true);
                } else{
                    throw new PokerException("The player can't go check");
                }
                System.out.println(player.getName() + " went check");
                System.out.println("---------------------------------------");
            }

            case "all-in", "ai", "all", "al", "a" -> {
                if(players.allOpponentsAllInOrFolded(player)&&player.getTotal()>highBet){
                    throw new PokerException("You must fold or call");
                }
                player.setAllIn(true);
                player.setHasCalled(true);
                int betAmount = player.getChips();
                player.bet(betAmount);
                if(player.getBet() > highBet){
                    highBet = player.getBet();
                    players.resetCall();
                    player.setHasCalled(true);
                }
                pot += betAmount;
                if(player.getBet()>highBet) highBet = player.getBet();
                System.out.println(player.getName() + " went all-in with " + betAmount + " chips");
                System.out.println("---------------------------------------");
            }

            default -> throw new PokerException("Unexpected value: " + action);
        }
    }

    private static String getAction(Player player) {
        System.out.println("It's " + player.getName() + "'s turn. Chips: " + player.getChips()
        + ", Bet: "+ player.getBet()+", Pot: " + pot);
        System.out.println("What does " + player.getName() + " want to do?");
        System.out.println("Options: " + getOptions(player));
        return scanner.nextLine().toLowerCase();
    }

    private static String getOptions(Player player) {
        if(highBet == player.getTotal()){
            return "[Fold, Call, All-in]";
        }if (highBet > player.getTotal()){
            return "[Fold, All-in]";
        } else if(players.allOpponentsAllInOrFolded(player)){
            if(player.getTotal()>highBet){
                return "[Fold, Call]";
            } else {
                return "[Fold, Call, All-in]";
            }
        } else {
            return "[Fold, Check, Raise, All-in]";
        }
    }

    public static String formatOrdinal(int number) {
        if (number % 100 >= 11 && number % 100 <= 13) {
            return number + "th";
        }

        return switch (number % 10) {
            case 1 -> number + "st";
            case 2 -> number + "nd";
            case 3 -> number + "rd";
            default -> number + "th";
        };
    }

    public static boolean whetherToSkip(Player player) {
        return player.hasFolded()||player.isAllIn()||player.getChips()==0;
    }

    public static boolean whetherToBreak(Player player) {
        return ((player.getBet()>=highBet)&&players.allOpponentsAllInOrFolded(player));
    }

    public static String formatRound(int number) {
        return switch(number+1){
            case 1 -> "Pre-flop";
            case 2 -> "Flop";
            case 3 -> "Turn";
            case 4 -> "River";
            default -> throw new IllegalStateException("Unexpected value: " + number);
        };
    }



}