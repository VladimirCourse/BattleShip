import java.lang.Math;
import java.util.Stack;
import java.io.Serializable;

//класс бота
class Bot extends Player implements Serializable{

	private final int SIDE_COUNT = 4;		//количество сторон в квадтрате
	private final int SIZE_GEN = 3;			//максимально возможное расстояние между кораблями
	//первое попадание по кораблю
	private int hittedX;					
	private int hittedY;
	private boolean movingForward = true;	//идем вправо или вниз
	private boolean foundedDir = false;		//определили положение корабля
	private boolean wounded = false;		//ранен корабль?
	//макс. размер уничтоженного корабля
	private int maxDestroyed = MAX_SHIP_SIZE;	
	//стек, куда стрелять (верх, низ, справа, слева)
	private Stack <Point> shootTo;

	public Bot(){
		super();
		shootTo = new Stack <Point>();
	}
	//генерация рандомного числа
	private int randNum(int max){
		return (int)(Math.random()*max);
	}

	//рандомная расстановка кораблей. нумерация сторон по часовой стрелке
	//в правую и левую стороны ставятся только по одному кораблю и только в середину
	//в верхнюю и нижнюю ставятся по 2 корабля, возможно по краям

	private void placeBigShips(){
		int xTop = 0, xBottom = 0; 			//позиция последнего корабля на верхней и нижней стороне
		int [] shipOnSide = new int[SIDE_COUNT];		//кол-во кораблей на стене
		int shipSize = MAX_SHIP_SIZE;				
		int shipCount = 0;
		int side = 0;
		for (int i = 0; i < BIG_SHIP_COUNT; i++){		//ставим все корабли, кроме однопалубных
			do{
				side = randNum(SIDE_COUNT);
			}while(shipOnSide[side] == side%2 + 1);		//генерируем, пока не найдем свободную сторону
			
			switch(side){
				case 0:
				myField.addShip(new Ship(0, randNum(SIZE_GEN) + SIZE_GEN - 1, shipSize, Direction.DOWN));
				break;
				case 1:
				xTop += randNum(SIZE_GEN -1);	
				myField.addShip(new Ship(xTop, 0, shipSize, Direction.RIGHT));
					xTop += shipSize + 1; 				//последняя координата = x + size + 1 для следующего корабля
					break;
					case 2:
					myField.addShip(new Ship(WIDTH - 1, randNum(SIZE_GEN) + SIZE_GEN - 1, shipSize, Direction.DOWN));
					break;
					case 3:
					xBottom += randNum(SIZE_GEN - 1);
					myField.addShip(new Ship(xBottom, HEIGHT - 1, shipSize, Direction.RIGHT));
					xBottom += shipSize + 1;
					break;
				}
				shipOnSide[side]++;
				shipCount++;
				if (shipCount == (MAX_SHIP_SIZE + 1) - shipSize){
					shipSize--;
					shipCount = 0;		
				}
			}
		}
		//расставляем однопалубные
		private void placeSmallShips(){
			for (int i = 0; i < SMALL_SHIP_COUNT; i++){
				Ship ship = new Ship(0, 0, 1, Direction.RIGHT);
				do{
					int x = 1 + randNum(WIDTH - 2);
					int y = 1 + randNum(HEIGHT - 2);
					ship.setX(x);
					ship.setY(y);
				}while(myField.isCollide(ship));		//рандомно ищем свободное место
				myField.addShip(ship);
			}
		}

		protected void placeShips(){
			placeBigShips();
			placeSmallShips();
		}
		//ищем, куда стрелять по диагонали, размер шага - макс. размер вражеского живого корабля - 1
		//прмерно так
		/*
		*#########
		####*#####
		##*#######
		#*########
		*####*####
		####*#####
		*/
		private void findShootingCoords(){
			if (lastX < WIDTH-1 && lastY > 0){
				lastX++;
				lastY--;
			}else{
				boolean found = false;
				for (lastX = 0; lastX < WIDTH; lastX++){
					int shipCoord = 0;			//шаг диагонали
					for (lastY = 0; lastY < HEIGHT; lastY++){
						if (enemyField.getCellType(lastX, lastY) == CellType.EMPTY){
							if (shipCoord == maxDestroyed-1){
								found = true;
								break;
							}
							shipCoord++;		
						}else{
							shipCoord = 0;
						}
					}
					if (found) return;
				}
				lastX = 0;
				lastY = 0;
				maxDestroyed--;

			}
		}
		//найти, горизонтально или вертикально стоит корабль
		private void calculateShipDirection(){
			if (foundedDir == false){
				Point p = shootTo.pop();
				lastX = p.getX();
				lastY = p.getY();
			}else{
				onWound();
			}
		}

		protected void onMiss(){
			if(wounded == false){
				findShootingCoords();
			}else{
				calculateShipDirection();
			}

		}
		//могу ли стрелять вправо или влево
		private boolean canMoveRight(int x){
			return x < WIDTH - 1 && x >= 0 && enemyField.getCellType(x, lastY) == CellType.EMPTY;
		}
		//могу ли стрелять вверх или вниз
		private boolean canMoveDown(int y){
			return y < HEIGHT - 1 && y >= 0 && enemyField.getCellType(lastX, y) == CellType.EMPTY;
		}
		//как только попали, обстреливаем по кресту чтобы найти направление его
		/*
		########
		###*####
		##*W*###
		###*####
		########
		*/
		private void crossFire(){
			hittedX = lastX;
			hittedY = lastY;
			//добавление точек слева, справа, вверху, внизу
			if (lastX > 0) shootTo.push(new Point(lastX - 1, lastY));
			if (lastY > 0) shootTo.push(new Point(lastX, lastY - 1));
			if (lastX < WIDTH - 1) shootTo.push(new Point(lastX + 1, lastY));
			if (lastY < HEIGHT - 1) shootTo.push(new Point(lastX, lastY + 1));
			Point p = shootTo.pop();
			lastX = p.getX();
			lastY = p.getY();
		}
		//двигаться вправо или влево
		private void moveHorizontal(){
			//пока пустое место, двигаемся вправо, иначе влево
			if (movingForward){
				if (canMoveRight(lastX + 1)){
					lastX++;
				}else{
					movingForward = false;
					lastX = hittedX - 1;
				}
			}else{
				lastX--;
			}
		}
		//двигаться вверх или вниз
		private void moveVertical(){
			//пока пустое место, двигаемся вниз, иначе вверх
			if (movingForward){
				if (canMoveDown(lastY + 1)){
					lastY++;
				}else{
					movingForward = false;
					lastY = hittedY - 1;
				}
			}else{
				lastY--;
			}
		}

		protected void onWound(){
			//как только ранили, стреляем по кресту
			if (wounded == false){
				crossFire();
				wounded = true;
			}else{
				//если не определили направление корабля, но попали 2 раза, определяем его направление
				if (foundedDir == false){
					int tmp = 0;
					//горизонтально стоит
					if (lastY == hittedY){
						tmp = hittedX;
						hittedX = Math.min(lastX, hittedX);
						lastX = Math.max(lastX, tmp);
						moveHorizontal();
					}else{			//вертикально
						tmp = hittedY;
						hittedY = Math.min(lastY, hittedY);
						lastY = Math.max(lastY, tmp);
						moveVertical();
					}
					foundedDir = true;
				}else{
					if (lastY == hittedY){
						moveHorizontal();
					}else{
						moveVertical();
					}
				}
			}
		}

		protected void onKill(){
			foundedDir = false;
			wounded = false;
			lastX = 0;
			lastY = 0;
			movingForward = true;
			shootTo.clear();
			//поиск максимального размера живого корабля соперника
			for (int i = 0; i < MAX_SHIP_SIZE; i++){
				if (shipCount[i] > 0){
					maxDestroyed = i + 1;
				}
			}
		}

	}