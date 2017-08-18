/**
 * NO LONGER IN USE
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
@Deprecated
public class FindNonEmptyText {
	
	public static void main(String[] args) throws FileNotFoundException{
		File file;
		file = new File("/Users/talliemassachi/TestData/RealData/redact.txt");
		Scanner scan = new Scanner(file);
		
		while (scan.hasNextLine()){
			String y = scan.nextLine();
			if(y.length()>0){

				y = y.substring(50);
				
				int loc = y.indexOf('.');
				
				y = y.substring(0, loc);
			}
			
			String[] x = new String[] {"cp", "/Users/talliemassachi/TestData/RealData/Checked/" + y + ".png", "/Users/talliemassachi/TestData/RealData/FalseNegativeImages/"};
			try {
				Process proc = new ProcessBuilder(x).start();
				proc.destroy();
			} catch (IOException e) {
				System.out.println("failed to copy");
			}
			
		}
		scan.close();
	}

	/*public static void main(String[] args){
		Scanner scan;
		for (int i=1; i<=4977; i++){
			
			try {
				File file = new File("/Users/talliemassachi/TestData/RealData/EmptyText/" + i + ".txt");
				scan = new Scanner(file);
				System.out.println(i);
				
				
				if(scan.hasNext()){
					String[] x = new String[] {"mv", "/Users/talliemassachi/TestData/RealData/EmptyText/" + i + ".txt", "/Users/talliemassachi/TestData/RealData/TextFound/"};
					try {
						Process proc = new ProcessBuilder(x).start();
					} catch (IOException e) {
						System.out.println("failed to move");
					}
				}
				
				while(scan.hasNextLine()){
					scan.nextLine();
					if(scan.hasNext()){
						String[] x = new String[] {"mv", "/Users/talliemassachi/TestData/RealData/EmptyText/" + i + ".txt", "/Users/talliemassachi/TestData/RealData/TextFound/"};
						try {
							Process proc = new ProcessBuilder(x).start();
						} catch (IOException e) {
							System.out.println("failed to move");
						}
						break;
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("file not found");
			}
			
		}
	}
	*/
}
