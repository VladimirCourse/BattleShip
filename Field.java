import java.lang.Math;
import java.util.ArrayList;
import java.io.Serializable;

//класс поля
public class Field implements Serializable{
	//размер
	private int width;
	private int height;
	//клетки
	private Cell [][] cells;

	public Field(int width, int height){
		this.width = width;
		this.height = height;
		cells = new Cell[height][width];
		for (int i = 0; i < width; i++){
			cells[i] = new Cell[width];
		}
		flushField();
	}

	public void flushField(){
		for (int i = 0; i < height; i++){
			for (int k = 0; k < width; k++){
				cells[i][k] = new Cell();
			}
		}
	}

	public void setCellType(int x, int y, CellType cellType){
		cells[y][x].setType(cellType);
	}

	public CellType getCellType(int x, int y){
		return cells[y][x].getType();
	}
	//добавить корабль на клетку
	private void addShip(int x, int y, Ship ship){
		Cell cell = cells[y][x];
		cell.setType(CellType.SHIP);
		cell.setShip(ship);
	}
	//занять клетки корабем по его размеру
	public void addShip(Ship ship){
		if (ship.getDirection() == Direction.DOWN){
			int y = ship.getY();
			for (int i = y; i < y + ship.getSize(); i++){
				addShip(ship.getX(), i, ship);
			}
		}else{
			int x = ship.getX();
			for (int i = x; i < x + ship.getSize(); i++){
				addShip(i, ship.getY(), ship);
			}
		}
	}

	public Ship getShip(int x, int y){
		return cells[y][x].getShip();
	}
	//поиск контура корабля
	private ArrayList <Cell> outline(Ship ship){
		ArrayList <Cell> res = new ArrayList <Cell>();
		//левый верхний угол
		int bx = Math.max(0, ship.getX()-1);
		int by = Math.max(0, ship.getY()-1);
		int ex = ship.getX();
		int ey = ship.getY();
		//по размеру и направлению находим правый нижний 
		if (ship.getDirection() == Direction.DOWN){
			ey += ship.getSize();
			ex++;
		}else{
			ex += ship.getSize();
			ey++;
		}
		//нижний правый угол
		ex = Math.min(width - 1, ex);
		ey = Math.min(height - 1, ey);
		for (int i = bx; i <= ex; i++){
			for (int k = by; k <= ey; k++){
				res.add(cells[k][i]);
			}
		}
		return res;
	}
	//пометить корабль
	private void markVerticalShip(Ship ship){
		int y = ship.getY();
		for (int i = y; i < y + ship.getSize(); i++){
			cells[i][ship.getX()].setType(CellType.KILLED);
		}
	}
	//пометить корабль
	private void markHorizontalShip(Ship ship){
		int x = ship.getX();
		for (int i = x; i < x + ship.getSize(); i++){
			cells[ship.getY()][i].setType(CellType.KILLED);
		}
	}
	//при убийстве, помечаем клетки, где он был
	public void onShipKill(Ship ship){
		if (ship.getDirection() == Direction.DOWN){
			markVerticalShip(ship);
		}else{
			markHorizontalShip(ship);
		}
		for (Cell cell : outline(ship)){
			if (cell.getType() == CellType.EMPTY){
				cell.setType(CellType.SHIP_NEAR);
			}
		}
	}
	//сталкивается ли корабль с кем-то
	public boolean isCollide(Ship ship){
		for (Cell cell : outline(ship)){
			if (cell.getType() == CellType.SHIP){
				return true;
			}
		}
		return false;
	}
}