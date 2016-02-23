import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.util.Scanner;
//класс игры
class Game {

	private Player [] players;	
	private Logger logger;
	private Scanner scanner;
	private String message;			//последнее сообщение лога
	private boolean gameEnded = false;	//закончилась игра?
	private boolean finish = false;		//промотать игру с ботами?
	private static Game instance;
	public static Game getInstance(){		//синглтон ленивый
		if (instance == null){
			instance = new Game();
		}
		return instance;
	}

	public void setPlayers(Player player1, Player player2){
		players[0] = player1;
		players[1] = player2;
	}

	private Game(){
		players = new Player[2];
		logger = new Logger("log.txt");
		scanner = new Scanner(System.in);
	}
	//вывести поля
	private void showFields(){
		System.out.println("P1 field:");
		players[0].printMyField();
		System.out.println("P2 field:");
		if (players[0].getClass().getName().equals("Human")){
			players[0].printEnemyField();
		}else{
			players[1].printMyField();
		}
	}
	//меню игры
	private void showMenu(){
		System.out.print("\033[H\033[2J");
		System.out.println("Input /save to save the game.\n" + 
			"Input /load to load the game.\n" + 
			"Input /showsea to show the map.\n" + 
			"Input /finish to go to the end of the game." +
			"Input /exit to exit the game.\n" +
			"Input any key to continue.");
		System.out.println(logger.getLastMessage());
		String command = scanner.nextLine();
		switch(command){
			case "/save":
				saveGame("game.save");
				scanner.next();
				break;
			case "/load":
				loadGame("game.save");
				scanner.next();
				break;
			case "/showsea":
				showFields();
				scanner.next();
				break;
			case "/finish":
				finish = true;
				scanner.next();
				break;
		}
	}
	//действие одного игрока по отношению к другому
	private void action(Player player1, Player player2, String name1, String name2){
		AnswerType ans;
		String shot;
		//стреляет пока попадает
		do{
			shot = player1.shoot();
			ans = player2.applyShot(shot);
			player1.applyAnswer(ans);
			logger.log(name1, shot, name2, ans);
			if (ans == AnswerType.GAME_END){
				gameEnded = true;
				break;
			}
			//если не нужно промтывать игру до конца, показываем меню
			if (finish == false) showMenu();
		}while(ans != AnswerType.M);
	}
	//игра закончилась?
	private boolean checkEnd(){
		if (gameEnded == true){
			logger.endLog();
			showFields();
		}
		return gameEnded;
	}
	//основной цикл игры
	public void play(){
		while(true){
			action(players[0], players[1], "P1", "P2");
			if (checkEnd()) return;
			action(players[1], players[0], "P2", "P1");
			if (checkEnd()) return;
		}
	}
	//сохранить игру, серилаизация игроков
	public void saveGame(String fileName){
		boolean ok = true;
		try{
			FileOutputStream fos = new FileOutputStream(fileName);
  			ObjectOutputStream oos = new ObjectOutputStream(fos);
  			oos.writeObject(players);
  			oos.flush();
  			oos.close();
  		}catch (IOException e){
  			ok = false;
  			System.out.println("Error in saving game");
  		}
  		if (ok){
  			System.out.println("Game saved!");
  		}
	}
	//загрузить игру, десериализация игроков
	public void loadGame(String fileName){
		boolean ok = true;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = new FileInputStream(fileName);
  			ois = new ObjectInputStream(fis);
  			try{
  				players = (Player[])ois.readObject();
  			}catch (ClassNotFoundException e){
  				System.out.println("Error in loading game");
  			}
  			ois.close();
  		}catch (IOException e){
  			ok = false;
  			System.out.println("Error in loading game");
  		}
  		if (ok){
  			System.out.println("Game loaded!");
  		}
	}
}