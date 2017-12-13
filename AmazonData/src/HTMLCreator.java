import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import data.SRLReview;

public class HTMLCreator {

	public static void main(String[] args) throws IOException {

		JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream("./kindle100.json"), "UTF-8"));
		ArrayList<SRLReview> reviews = new ArrayList<SRLReview>();
		reader.beginArray();
		Gson gson = new GsonBuilder().create();
		while (reader.hasNext()) {
			SRLReview review = gson.fromJson(reader, SRLReview.class);
			reviews.add(review);

		}

		for (SRLReview review : reviews) {
			try {
				File input = new File("./defaultHTML.html");
				Document doc = Jsoup.parse(input, "UTF-8");
				Element productName = doc.select("div#ProductName").get(0);
				Element reviewText = doc.select("div#reviewText").get(0);
				productName.append(review.getReviewerID());
				reviewText.append(review.getReviewText());

				FileWriter fw = new FileWriter("./HTMLSites/" + review.getReviewerID() + ".html");
				fw.write(doc.outerHtml());
				fw.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			//System.out.println(review);
		}
	}

}
