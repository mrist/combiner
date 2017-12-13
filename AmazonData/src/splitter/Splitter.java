package splitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import data.SRLReview;

public class Splitter {

	public static void main(String[] args) {
		File folder = new File("./jsons/");
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> list = new ArrayList<String>();
		for (File file : listOfFiles) {
			if (file.toString().endsWith(".json")) {
				SRLReview[] reviews = new SRLReview[0];
				BufferedReader reader;
				try {
					reader = new BufferedReader(new FileReader(file));
					Gson gson = new GsonBuilder().create();
					reviews = gson.fromJson(reader, SRLReview[].class);

				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (SRLReview review : reviews) {
					if (list.contains(review.getReviewerID())) {
						System.out.println(review.getReviewerID());
					}
					list.add(review.getReviewerID());
					BufferedWriter bw = null;
					try {
						bw = new BufferedWriter(
								new FileWriter("./input/" + review.getReviewerID() + "_" + review.getASIN() + ".txt"));
						bw.write(review.getReviewText());

					} catch (Exception e) {

					} finally {
						try {
							bw.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
