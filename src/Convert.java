
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Convert {
	
	public static void main(String[] args) {
		String fileName ="/Users/talliemassachi/TestData/Round5/FindViolation.txt";
		int min = 119;
		int max = getImgs(fileName, min);
		System.out.println("images created");
		/*
		runTesseract(min, max);
		System.out.println("Tesseract run");
		
		//FindNonEmptyText(min, max);
		System.out.println("done");
		*/
		
	}
	
	
	public static int getImgs(String fileName, int min){

		int i = min;
		try{
			File file = new File(fileName);
			Scanner scan = new Scanner(file);
			
			String imgURL = "";
			while(scan.hasNextLine()){
				try {
					imgURL=scan.nextLine();
					String type = FilenameUtils.getName(imgURL);
					File saveFile = new File("/Users/talliemassachi/TestData/Round5/" + type);
					FileUtils.copyURLToFile(new URL(imgURL), saveFile);
					System.out.println("img " + i + " created");
					i++;
				} catch(IOException ioe) {
					System.out.println("error in image " + i);
				}
			}
			
			scan.close();
			
			return i;
		} catch(FileNotFoundException e){
			System.out.println("file not found");
			
		}
		return 0;
	}
	
	@Deprecated
	public static void saveImage(String imageUrl, String destinationFile) throws IOException {
		URL url = new URL(imageUrl);
		BufferedImage img = null;
		
		try{
			img = ImageIO.read(url);
		} catch (IOException e){
			System.out.println("That file does not exist");
		}
		
		
		if (img != null){
			try {
			    File outputfile = new File(destinationFile);
			    ImageIO.write(img, "png", outputfile);
			} catch (IOException e) {
			    System.out.println("Issues writing the image");
			}
		}
		
	}
	
	public static void runTesseract(int minFile, int maxFile) {
		
		for (int i=minFile; i<=maxFile; i++){
			try{
				System.out.println(i);
				String[] x = new String[] {"tesseract", "/Users/talliemassachi/TestData/RealData/" + i + ".png", "/Users/talliemassachi/TestData/RealData/" + i};
				Process proc = new ProcessBuilder(x).start();
				proc.destroy();
				System.out.println("tesseract called on img " + i);
			} catch (IOException e){
				System.out.println("Error");
			}
		}
	}
	
	public static void FindNonEmptyText(int minFile, int maxFile){
		Scanner scan;
		for (int i=minFile; i<=maxFile; i++){
			
			try {
				File file = new File("/Users/talliemassachi/TestData/RealData/" + i + ".txt");
				scan = new Scanner(file);
				if(scan.hasNext()){
					String[] x = new String[] {"mv", "/Users/talliemassachi/TestData/RealData/" + i + ".txt", "/Users/talliemassachi/TestData/RealData/TextFound/"};
					System.out.println("img " + i + " has text");
					try {
						Process proc = new ProcessBuilder(x).start();
						proc.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if(scan.hasNextLine()){
					do{
						scan.nextLine();
						if(scan.hasNext()){
							String[] x = new String[] {"mv", "/Users/talliemassachi/TestData/RealData/" + i + ".txt", "/Users/talliemassachi/TestData/RealData/TextFound/"};
							System.out.println("img " + i + " has text");
							try {
								Process proc = new ProcessBuilder(x).start();
								proc.destroy();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} while (scan.hasNextLine());
					
					String[] x = new String[] {"mv", "/Users/talliemassachi/TestData/RealData/" + i + ".txt", "/Users/talliemassachi/TestData/RealData/EmptyText/"};
					System.out.println("img " + i + " has no text");
					try {
						Process proc = new ProcessBuilder(x).start();
						proc.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}else{
					String[] x = new String[] {"mv", "/Users/talliemassachi/TestData/RealData/" + i + ".txt", "/Users/talliemassachi/TestData/RealData/EmptyText/"};
					System.out.println("img " + i + " has no text");
					try {
						Process proc = new ProcessBuilder(x).start();
						proc.destroy();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
}
