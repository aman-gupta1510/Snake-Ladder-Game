package Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import Model.*;
// import Service.*;

public class SnakeAndLadderService {
    private SnakeAndLadderBoard snakeAndLadderBoard;
    private int initialNumberOfPlayers;
    private Queue<Player> players; // Comment: Keeping players in game service as they are specific to this game and not the board. Keeping pieces in the board instead.
    private boolean isGameCompleted;

    private int noOfDices; //Optional Rule 1
    private boolean shouldGameContinueTillLastPlayer; //Optional Rule 3
    private boolean shouldAllowMultipleDiceRollOnSix; //Optional Rule 4

    private static final int DEFAULT_BOARD_SIZE = 100; //The board will have 100 cells numbered from 1 to 100.
    private static final int DEFAULT_NO_OF_DICES = 1;

    public SnakeAndLadderService(int boardSize) {
        this.snakeAndLadderBoard = new SnakeAndLadderBoard(boardSize);
        this.players = new LinkedList<Player>();
        this.noOfDices = SnakeAndLadderService.DEFAULT_NO_OF_DICES;
    }

    public SnakeAndLadderService(){
        this(SnakeAndLadderService.DEFAULT_BOARD_SIZE);
    }

    // Setters
    public void setNoOfDices(int noOfDices) {
        this.noOfDices = noOfDices;
    }

    public void setShouldGameContinueTillLastPlayer(boolean shouldGameContinueTillLastPlayer) {
        this.shouldGameContinueTillLastPlayer = shouldGameContinueTillLastPlayer;
    }

    public void setShouldAllowMultipleDiceRollOnSix(boolean shouldAllowMultipleDiceRollOnSix) {
        this.shouldAllowMultipleDiceRollOnSix = shouldAllowMultipleDiceRollOnSix;
    }

    // Initialize Board
    public void setPlayers(List<Player> players) {
        this.players = new LinkedList<Player>();
        this.initialNumberOfPlayers = players.size();
        Map<String,Integer> playerPieces = new HashMap<>();
        for(Player player : players){
            this.players.add(player);
            playerPieces.put(player.getId(),0);
        }
        snakeAndLadderBoard.setPlayerPieces(playerPieces);
    }

    public void setSnakes(List<Snake> snakes){
        
        snakeAndLadderBoard.setSnakes(snakes);
    }

    public void setLadders(List<Ladder> ladders) {
        snakeAndLadderBoard.setLadders(ladders);
    }

    // Game Logic
    private int getNewPositionAfterGoingThroughSnakesAndLadders(int newPosition) {
        int previousPosition;
        do{
            previousPosition = newPosition;
            for(Snake snake:snakeAndLadderBoard.getSnakes()){
                if(snake.getStart()==newPosition){
                    newPosition = snake.getEnd();
                }
            }
            for(Ladder ladder:snakeAndLadderBoard.getLadders()){
                if(ladder.getStart()==newPosition){
                    newPosition = ladder.getEnd();
                }
            }
        }
        while(previousPosition!=newPosition);
        return previousPosition;
    }
    private void movePlayer(Player player,int positions) {
        int oldPosition = snakeAndLadderBoard.getPlayerPieces().get(player.getId());
        int newPosition = oldPosition + positions;
        int boardSize = snakeAndLadderBoard.getSize();
        if(newPosition>boardSize){
            newPosition = oldPosition;
        }
        else{
            newPosition = getNewPositionAfterGoingThroughSnakesAndLadders(newPosition);
        }
        snakeAndLadderBoard.getPlayerPieces().put(player.getId(),newPosition);
        System.out.println(player.getName() + " rolled a " + positions + " and moved from " + oldPosition +" to " + newPosition);
    }

    private int getTotalValueAfterDiceRolls() {
        // Can use noOfDices and setShouldAllowMultipleDiceRollOnSix here to get total value (Optional requirements)
        return DiceService.roll();
    }

    private boolean hasPlayerWon(Player player) {
        int playerPosition = snakeAndLadderBoard.getPlayerPieces().get(player.getId());
        int winningPosition = snakeAndLadderBoard.getSize();
        return playerPosition == winningPosition;
    }

    private boolean isGameCompleted() {
        int currentNumberOfPlayers = players.size();
        if(currentNumberOfPlayers<initialNumberOfPlayers) return true;
        else return false;
    }
    
    public void startGame() {
        while(!isGameCompleted()){
            int totalDiceValue = getTotalValueAfterDiceRolls();
            Player currentPlayer = players.poll();
            movePlayer(currentPlayer, totalDiceValue);
            if (hasPlayerWon(currentPlayer)) {
                System.out.println(currentPlayer.getName() + " wins the game");
                snakeAndLadderBoard.getPlayerPieces().remove(currentPlayer.getId());
            } else {
                players.add(currentPlayer);
            }
        }
    }

}
