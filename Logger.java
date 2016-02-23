import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//класс логгера
class Logger{
	
	private String filePath;
	private File file;
	private FileWriter fw;
	private BufferedWriter bw;
	private String message; //последнее сообщение
	//тип ответа
	private final String answers [] = {
		"missing",
		"wound ship",
		"killed the ship",
		":("
	};

	public Logger(String filePath){
		this.filePath = filePath;
		try{
			file = new File(filePath);
			if (file.exists() == false) {
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		}catch (IOException e){
			System.out.println("Error in reading or writing the file!");
			e.printStackTrace();
		}
	}
	//запись лога
	public void log(String player1, String attack, String player2, AnswerType ans){
		try {
			message = player1 + " аttack the cell number " + attack + " of player " + player2 + " and " + answers[ans.ordinal()] + "\n";
			bw.write(message);
		}catch (IOException e){
			System.out.println("Error in reading or writing the file!");
			e.printStackTrace();
		}
	}
	//закончили писать
	public void endLog(){
		try{
			bw.close();
		}catch (IOException e){
			System.out.println("Error in reading or writing the file!");
			e.printStackTrace();
		}
	}

	public String getLastMessage(){
		return message;
	}
}