/**
 * 
 * NO LONGER IN USE
 */

import java.io.IOException;

@Deprecated
public class RunTesseract {
	public static void main(String args[]) {
		
		for (int i=1; i<=1880; i++){
			try{
				System.out.println(i);
				String[] x = new String[] {"tesseract", "/Users/talliemassachi/TestData/RealData/" + i + ".png", "/Users/talliemassachi/TestData/RealData/" + i};
				Process proc = new ProcessBuilder(x).start();
				System.out.println("out");
				proc.destroy();
			} catch (IOException e){
				System.out.println("Error");
			}
		}
	}
}
