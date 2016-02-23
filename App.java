import java.util.Scanner;

//основной класс приложения
public class App {

	public static void main(String[] args){

		System.out.println("Enter 0 to watch bot match, 1 to play with bot");
		Scanner scanner = new Scanner(System.in);
		String param = scanner.nextLine();
		//создаем игру и игроков
		Game game = Game.getInstance();
		if (param.equals("0")){
			Bot bot = new Bot();
			Bot bot2 = new Bot();
			game.setPlayers(bot, bot2);
		}else{
			Human human = new Human();
			Bot bot = new Bot();
			game.setPlayers(human, bot);
		}
		game.play();
	}

}