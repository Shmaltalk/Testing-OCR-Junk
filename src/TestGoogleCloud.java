/**
 * Runs Google Cloud OCR OR Tesseract on all files named in /Users/talliemassachi/TestData/imgs.txt
 * Uses Google Could on all img files, runs Tesseract on all tiffs
 */

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.grpc.ApiException;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
//import com.google.api.services.vision.v1.model.AnnotateImageRequest;
//import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.Status;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.tokenize.TokenizerME;

import redis.clients.jedis.JedisPool;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Block;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.cloud.vision.v1.Word;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.cloud.vision.v1.Page;
import com.google.cloud.vision.v1.Paragraph;
import com.google.cloud.vision.v1.Symbol;
import com.google.protobuf.ByteString;

import io.grpc.StatusRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestGoogleCloud {
	
	private static int numImgs = 0;
	private static long totalTime = 0;
	
	private static int numTiffs = 0;
	private static long tiffTime = 0;
	private static long tessMin = 0;
	private static long tessMax = 0;
	
	private static int numPdfs = 0;
	private static long pdfConvertTime = 0;
	private static long pdfConvertMin = 0;
	private static long pdfConvertMax = 0;
	
	private static int numDocs = 0;
	
	private static long imgTime = 0;
	private static long imgMin = 0;
	private static long imgMax = 0;
	
	public static void main(String[] args) throws FileNotFoundException{
		start("/Users/talliemassachi/TestData/Round3/quicktest");

		//printTimes();
	}
	
	
	public static void start(String filename) throws FileNotFoundException{
		
		System.out.println("checking folder: "+filename);
		File folder = new File(filename);
		File[] listOfFiles = folder.listFiles();
		String imgPath = null;
		String imgName = null;
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile() && !listOfFiles[i].getPath().equalsIgnoreCase("txt") && !listOfFiles[i].getName().substring(0, 1).equals(".")) {
	        imgPath = listOfFiles[i].getPath();
	        imgName = FilenameUtils.removeExtension(imgPath);
	        System.out.println(imgPath);

	        Go(imgPath, imgName);
	      } else if (listOfFiles[i].isDirectory()) {
	        System.out.println("Directory " + listOfFiles[i].getName());
	      }
	      

	    }
		
		
	}
	
	public static void printTimes(){
		System.out.printf("There were a total of %d Images processed. \n It took %d milliseconds to process them all, for an average time of %d milli per image.\n", numImgs, totalTime, (totalTime/numImgs));
		System.out.printf("There were %d PDFs, all of which were converted to tiff. \n It took a total of %d milliseconds to convert all pdfs, for an average of %d milliseconds per image.\n", numPdfs, pdfConvertTime, (pdfConvertTime/numPdfs));
		System.out.printf("It took a minimum of %d milli to convert a pdf, and a maximum of %d milli.\n", pdfConvertMin, pdfConvertMax);
		System.out.printf("There were %d tiffs processed in %d milliseconds. \n It took a minimum of %d and a maximum of %d milli to OCR a tiff.\n", numTiffs, tiffTime, tessMin, tessMax);
		System.out.printf("There were %d docs, which were not processed.\n", numDocs);
		System.out.printf("It took %d milliseconds to process every non-pdf, non-tiff image. For an average of %d milli per image. \n This took a minimum of %d millis and a maximum of %d millis.\n", imgTime, (imgTime/(numImgs-numTiffs-numDocs)),imgMin, imgMax);
	
	}
	
	public static void Go(String imgFile, String outFile){
		String type = FilenameUtils.getExtension(imgFile);
		System.out.println("Type = " + type);
		//Tesseract for tiffs
		if(type.equals("tiff")){
			System.out.println("TIFF SENT TO TESSERACT");
			
			numImgs++;

			long tiffStart = System.currentTimeMillis();
			numTiffs++;
			TesseractDetect(imgFile, outFile);
			long diff = System.currentTimeMillis() - tiffStart;
			tiffTime += diff;
			totalTime += diff;
			
			if(diff < tessMin || tessMin==0) {
				tessMin = diff;
			} else if(diff > tessMax) {
				tessMax = diff;
			}
			
		//PDF and word docs dont work
		} else if(type.equals("pdf")) {
			System.out.println("CONVERT PDF TO TIFF");
			createTiff(imgFile, outFile);
			numPdfs++;
			return;
			
		} else if(type.equals("doc") || type.equals("docx")){
			System.out.println("CANT READ DOCS");
			numImgs++;
			numDocs++;
			PrintStream out;
			try {
				out = new PrintStream(outFile +".txt");
				out.print("CANT READ DOCS");
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
			
		//everything else through Google Cloud
		} else {
			try{
				System.out.println("IMG SENT TO GOOGLE");
				numImgs++;
				detectText(imgFile, outFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	//changes a pdf to a tiff file using image magick in the command line
	public static void createTiff(String imgFile, String outFile){
		long tiffStart = System.currentTimeMillis();
		try {
			String outDir = outFile;
			new File(outDir).mkdir();
			String tiffOut = outDir + "/copy.jpg";
			//System.out.println(imgFile);
			//System.out.println(tiffOut);
			String[] x = new String[] {"/usr/local/bin/magick", "convert", imgFile, "-alpha", "remove", "-resize", "175%", tiffOut};
			ProcessBuilder pb = new ProcessBuilder(x);
			pb.environment().put("PATH", pb.environment().get("PATH")+":/usr/local/bin");
			Process proc = pb.start();
			/* Debugging issues
			System.out.println(pb.environment().toString());
			BufferedReader br = new BufferedReader( new InputStreamReader(proc.getErrorStream() ));
	        String line = null;  
	        while((line=br.readLine())!=null){  
	            System.out.println(line);  
	         }
	         */
			proc.waitFor();
			long diff = System.currentTimeMillis() - tiffStart;
			pdfConvertTime += diff;
			totalTime += diff;
			
			if(diff < pdfConvertMin || pdfConvertMin==0) {
				pdfConvertMin = diff;
			} else if(diff > pdfConvertMax) {
				pdfConvertMax = diff;
			}
			
			start(outDir);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	//Tesseract OCR for tiffs
	public static void TesseractDetect(String filepath, String out){
		try{
			String[] x = new String[] {"/usr/local/bin/tesseract", filepath, out};
			Process proc = new ProcessBuilder(x).start();
			proc.waitFor();
		} catch (IOException | InterruptedException e){
			e.printStackTrace();
			System.out.println("Error in tesseract");
		}
		
		
	}
	
	//Google Cloud OCR
	public static void detectText(String filePath, String outFile) throws FileNotFoundException{
		long cloudTime = System.currentTimeMillis();
		PrintStream out = new PrintStream(outFile + ".txt");
		
		List<AnnotateImageRequest> requests = new ArrayList<>();

		try{
			ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
			
	
			Image img = Image.newBuilder().setContent(imgBytes).build();
			Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
			AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
			requests.add(request);
	
			BatchAnnotateImagesResponse response = ImageAnnotatorClient.create().batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();
	
			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
					out.printf("Error: %s\n", res.getError().getMessage());
					return;
				}
				
				// For full list of available annotations, see
				// http://g.co/cloud/vision/docs
				int i = 1;
				/*for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
					out.printf(i + ": " + "%s\n", annotation.getDescription());
					i++;
					// out.printf("Position : %s\n", annotation.getBoundingPoly());
				}
				*/
				
				out.printf("%s\n", res.getTextAnnotations(0).getDescription());

				//out.printf("NUMBER TWO \n%s\n", res.getTextAnnotations(3).getDescription());
			}

			out.close();
		//if the file is larger than google cloud will handle, send to tesseract.
		} catch (ApiException e){
			//TesseractDetect(filePath, outFile);
			System.out.println("File too large. Sent to tesseract.");
			e.printStackTrace();
		} catch (IOException f){
			f.printStackTrace();
		}
		
		long diff = System.currentTimeMillis() - cloudTime;
		imgTime += diff;
		totalTime += diff;
		
		if(diff < imgMin || imgMin==0) {
			imgMin = diff;
		} else if(diff > imgMax) {
			imgMax = diff;
		}
		
	}
	
	
}