import java.util.Scanner;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.io.Serializable;
//класс человека
class Human extends Player implements Serializable{

	public Human(){		
		
	}
	//попытка поставить корабль
	private boolean tryAddShip(int x, int y, int size, Direction dir){
		//проверка выхода за границы
		if (dir == Direction.RIGHT){
			if (x + size > WIDTH){
				return false;
			}
		}else{
			if (y + size > HEIGHT){
				return false;
			}
		}
		Ship ship = new Ship(x, y, size, dir);
		//проверка, столкнулся ли корабль с другим
		if (myField.isCollide(ship)){
			return false;
		}
		myField.addShip(ship);
		return true;
	}
	//проверка координат
	private boolean parseCoords(String coords){
		boolean err = false;
		int y = 0;
		try{
        	y = Integer.parseInt(coords.substring(1, coords.length())) - 1;
   	 	}catch(NumberFormatException e){
       	 	err = true;
    	}
		if ((coords.charAt(0) < 'A' || coords.charAt(0) > 'J') || (y < 0 || y > HEIGHT - 1)) {
			err = true;
		}
		return err;
	}
	//устанока кораблей
	protected void placeShips(){
		Scanner scanner = new Scanner(System.in);
		for (int i = 0; i < MAX_SHIP_SIZE; i++){
			for (int k = 0; k < i + 1; k++){
				System.out.print("\033[H\033[2J");			//очистка экрана в линуксе
				printMyField();
				int size = MAX_SHIP_SIZE - i;
				System.out.println("\nPlease, place the number " + (k + 1) + " of your " + size + "-deck ships");
				System.out.println("Enter 0 for vertical or 1 for horizontal displacemant: ");
				String disp = scanner.nextLine();
				System.out.println("Enter the coordinates of head: ");
				String coords = scanner.nextLine();
				if (parseCoords(coords) == true){
					System.out.println("You enter wrong position, enter again!");
					scanner.nextLine();
					k--;
					continue;
				}
				Point p = strToPoint(coords);
				Direction dir;
				dir = (disp.equals("0")) ? Direction.DOWN : Direction.RIGHT;
				if (tryAddShip(p.getX(), p.getY(), size, dir) == false){
					System.out.println("You enter wrong position, enter again!");
					scanner.nextLine();
					k--;
				}
			}
		}
	}
	//вести корабли с консоли
	private void inputCoords(){
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter coordinastes: ");
		String coords = scanner.nextLine();
		//проверка координат
		if (parseCoords(coords) == true){
			System.out.println("You enter wrong position, enter again!");
			scanner.nextLine();
		}
		Point p = strToPoint(coords);
		lastX = p.getX();
		lastY = p.getY();
	}
	
	protected void onMiss(){
		inputCoords();
	}

	protected void onWound(){
		inputCoords();
	}

	protected void onKill(){
		inputCoords();
	}
}