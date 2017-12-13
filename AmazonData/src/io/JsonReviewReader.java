package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.maltparser.core.helper.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import data.Review;
import data.SRLReview;

public class JsonReviewReader {
	private static String	wordFolder			= "./words/";
	private static String	dependencyFolder	= "./dep/";
	private Gson			gson;

	public JsonReviewReader(Gson gson) {
		this.gson = gson;
	}

	public Review[] readReviews(File file) {
		Review[] reviews = new Review[0];
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			this.gson = new GsonBuilder().create();
			reviews = this.gson.fromJson(reader, Review[].class);

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return reviews;
	}

	public HashMap<String, SRLReview> getLabelledReviews() {
		// ArrayList<LabelledReview> reviews = new ArrayList<LabelledReview>();
		HashMap<String, SRLReview> reviews = new HashMap<String, SRLReview>();
		File folder = new File(wordFolder);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			SRLReview rev = this.labelledReview(file);
			reviews.put(rev.getID(), rev);
		}
		return reviews;
	}

	public SRLReview labelledReview(File file) {
		SRLReview review = new SRLReview();
		try {
			JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			review = this.gson.fromJson(reader, SRLReview.class);
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return review;
	}

	public HashMap<String, Review> getDependencyReviews() {
		// ArrayList<LabelledReview> reviews = new ArrayList<LabelledReview>();
		HashMap<String, Review> reviews = new HashMap<String, Review>();
		File folder = new File(dependencyFolder);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.getName().endsWith(".json")) {
				Review[] revs = this.dependencyReview(file);
				if (revs != null) {
					// rev.initialize();
					for (Review rev : revs) {
						reviews.put(rev.getID(), rev);
					}
				}
			}

		}
		return reviews;
	}

	public Review[] getSingleDependencyReviewFile(String fileName) {
		Review[] reviews = new Review[0];
		if (fileName.endsWith(".json")) {
			reviews = this.dependencyReview(new File(fileName));
		}
		return reviews;
	}

	public Review[] dependencyReview(File file) {
		Review[] review = null;
		try {
			JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			review = this.gson.fromJson(reader, Review[].class);
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return review;
	}
}
