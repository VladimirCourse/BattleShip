import java.io.Serializable;
//класс клетки на поле
class Cell implements Serializable{
	//тип клетки (есть ли раненый корабль, пусто, промахнулся и тд.)
	private CellType type;
	//корабль, если есть
	private Ship ship;

	public Cell(){
		type = CellType.EMPTY;
	}

	public void setType(CellType type){
		this.type = type;
	}

	public CellType getType(){
		return type;
	}

	public void setShip(Ship ship){
		this.ship = ship;
	}

	public Ship getShip(){
		return ship;
	}

}