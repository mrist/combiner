package splitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import data.Review;

public class JSONExtractor {

	public static void main(String[] args) throws IOException {
		//File file = new File("D:/Data/Amazon/complete.json");

		JsonReader reader = new JsonReader(
				new InputStreamReader(new FileInputStream("D:/Data/Amazon/Electronics_5.json"), "UTF-8"));
		reader.setLenient(true);
		Gson gson = new GsonBuilder().create();
		int counter = 0;
		ArrayList<Review> reviews = new ArrayList<Review>();

		try {

			while (reader.hasNext()) {
				Review review = gson.fromJson(reader, Review.class);
				if (review.getAsin().equals("B005EOWBHC")) {
					reviews.add(review);
					System.out.println(counter + ": " + review.getReviewText());
					counter++;
				}
				if (counter >= 100) {
					break;
				}

			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JsonWriter writer = new JsonWriter(new FileWriter(new File("test.json")));
		writer.setIndent("  ");
		for (Review review : reviews) {
			gson.toJson(review, Review.class, writer);
		}
		writer.close();

	}
}
