import java.io.Serializable;

//класс корабля
class Ship implements Serializable{
	//размер
	private final int size;		
	//положение
	private int x;
	private int y;			
	private final Direction direction;		//направление
	private int health;

	public Ship(int x, int y, int size, Direction direction){
		this.x = x;
		this.y = y;
		this.size = size;
		this.health = size;
		this.direction = direction;
	}

	public void setX(int x){
		this.x = x;
	}

	public void setY(int y){
		this.y = y;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public void hit(){
		health--;
	}

	public int getSize(){
		return size;
	}

	public int getHealth(){
		return health;
	}

	public Direction getDirection(){
		return direction;
	}
}