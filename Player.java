import java.lang.Math;
import java.util.ArrayList;
import java.io.Serializable;
//класс игрока
public abstract class Player implements Serializable {

	protected final int WIDTH = 10;
	protected final int HEIGHT = 10;
	protected final int MAX_SHIP_SIZE = 4;
	protected final int BIG_SHIP_COUNT = 6;
	protected final int SMALL_SHIP_COUNT = 4;

	protected Field myField;
	protected Field enemyField;
	protected AnswerType answer;
	//последнее попадание и куда стрелять
	protected int lastX;	
	protected int lastY;
	//кол-во кораблей каждого типа и общее количество кораблей
	protected int [] shipCount;
	protected int allShips = SMALL_SHIP_COUNT + BIG_SHIP_COUNT;

	public Player(){
		myField = new Field(WIDTH, HEIGHT);
		enemyField = new Field(WIDTH, HEIGHT);
		shipCount = new int[MAX_SHIP_SIZE];
		for (int i = 0; i < MAX_SHIP_SIZE; i++){
			shipCount[i] = MAX_SHIP_SIZE - i;
		}
		placeShips();
	}
	//поиск направления корабля при убийстве
	public Direction findShipDirection(){
		if (lastX > 0){
			if (enemyField.getCellType(lastX - 1, lastY) == CellType.WOUND){
				return Direction.RIGHT;
			}
		}
		if (lastX < WIDTH - 1){
			if (enemyField.getCellType(lastX + 1, lastY) == CellType.WOUND){
				return Direction.RIGHT;
			}
		}
		if (lastY > 0){
			if (enemyField.getCellType(lastX, lastY - 1) == CellType.WOUND){
				return Direction.DOWN;
			}
		}
		if (lastY < WIDTH - 1){
			if (enemyField.getCellType(lastX, lastY + 1) == CellType.WOUND){
				return Direction.DOWN;
			}
		}
		return null;
	}
	//методы, которые надо переопределить в человеке или боте
	protected abstract void onMiss();
	protected abstract void onWound();
	protected abstract void onKill();
	protected abstract void placeShips();

	private boolean isCellWounded(int x, int y){
		return enemyField.getCellType(x, y) == CellType.WOUND;
	}
	
	private int getBeginX(int x, int y){
		while (x >= 0){
			if (!isCellWounded(x, y)){
				break;
			}
			x--;
		}
		return Math.min(x+1, WIDTH - 1);
	}

	private int getBeginY(int x, int y){
		while (y >= 0){
			if (!isCellWounded(x, y)){
				break;
			}
			y--;
		}
		return Math.min(y+1, HEIGHT - 1);
	}

	private int getSizeX(int x, int y){
		int size = 0;
		while (x < WIDTH){
			if (!isCellWounded(x, y)){
				break;
			}
			size++;
			x++;
		}
		return size;
	}

	private int getSizeY(int x, int y){
		int size = 0;
		while (y < HEIGHT){
			if (!isCellWounded(x, y)){
				break;
			}
			size++;
			y++;
		}
		return size;
	}
	//добавление убитого корабля на поле врага
	private void addKilledShip(){
		int shipX = lastX;
		int shipY = lastY;
		int size = 0;
		//определеяем направление корабля и положение
		Direction direction = findShipDirection();
		if (direction == Direction.RIGHT){
			shipX = getBeginX(shipX, shipY);
			size = getSizeX(shipX, shipY);
		}else{
			shipY = getBeginY(shipX, shipY);
			size = getSizeY(shipX, shipY);
		}
		Ship ship = new Ship(shipX, shipY, size, direction);
		shipCount[size - 1]--;
		enemyField.addShip(ship);
		enemyField.onShipKill(ship);
	}
	//перевод координат
	protected Point strToPoint(String coords){
		int x = coords.charAt(0) - 'A';
		int y = Integer.parseInt(coords.substring(1, coords.length())) - 1;
		return new Point(x, y);
	}
	//метод выстрела
	public String shoot(){	
		if (answer == null){
			lastX = 0;
			lastY = 0;
		}else{
			switch(answer){
				case M:
					enemyField.setCellType(lastX, lastY, CellType.MISS);
					onMiss();
					break;
				case W:
					enemyField.setCellType(lastX, lastY, CellType.WOUND);
					onWound();
					break;
				case K:
					enemyField.setCellType(lastX, lastY, CellType.WOUND);
					addKilledShip();
					onKill();
					onMiss();
					break;
			}
		}
		char [] cAns = {(char)(lastX + 'A')};
		String ans = String.valueOf(cAns) + String.valueOf(lastY+1);
		return ans;
	} 
	//получение выстрела
	public AnswerType applyShot(String coords){
		Point p = strToPoint(coords);
		int x = p.getX();
		int y = p.getY();
		switch(myField.getCellType(x, y)){
			case EMPTY:				//промах врага
				myField.setCellType(x, y, CellType.MISS);
				return AnswerType.M;
			case SHIP:				//попадание врага
				Ship ship = myField.getShip(x, y);
				ship.hit();
				if (ship.getHealth() > 0){		//ранил
					myField.setCellType(x, y, CellType.WOUND);
					return AnswerType.W;
				}else{							//убил
					myField.onShipKill(ship);
					myField.setCellType(x, y, CellType.KILLED);
					allShips--;
					if (allShips == 0){
						return AnswerType.GAME_END;
					}
					return AnswerType.K;
				}
			default:
				return AnswerType.M;
			}
	}
	//получить ответ
	public void applyAnswer(AnswerType answer){
		this.answer = answer;
	}
	//вывод поля
	private void printField(Field field){
		System.out.println("  ABCDEFGHIJ");
		String symb = "#MWKS.";				//# - пустая клетка, М - промах, W - ранен, K - убит, S - корабль, . - ореол
		String num;
		for (int i = 0; i < HEIGHT; i++){
			num = (i != 9) ? " " : "";		//добавить для красиового вывода
			System.out.print(num + (i+1));
			for (int k = 0; k < WIDTH; k++){
				int j = field.getCellType(k, i).ordinal();
				System.out.print(symb.charAt(j));	
			}
			System.out.println();
		}
	}

	public void printMyField(){
		printField(myField);
	}

	public void printEnemyField(){
		printField(enemyField);
	}
}
