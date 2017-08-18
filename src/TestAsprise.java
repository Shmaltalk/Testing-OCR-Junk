import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.asprise.ocr.Ocr;

public class TestAsprise {
	
	public static void main(String[] args){
		
		//args = new String[1];
		//args[0] = "/Users/talliemassachi/TestData/Round2/imgs/test1F.png";
		Ocr.setUp(); // one time setup
		Ocr ocr = new Ocr(); // create a new OCR engine
		ocr.startEngine("eng", Ocr.SPEED_SLOW); // English
		String s = ocr.recognize(new File[] {new File(args[0])}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_PLAINTEXT);
		
		String name = args[0].substring(0, args[0].indexOf('.'));
		
		try (PrintWriter out = new PrintWriter(name + ".txt")){
		    out.println(s);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		
		System.out.println("Result: " + s);
		// ocr more images here ...
		ocr.stopEngine();
		
	}
	
}
