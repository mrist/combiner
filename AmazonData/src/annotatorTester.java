import com.google.gson.Gson;

import data.Review;
import io.JsonReviewReader;
import utils.Evaluation;
import utils.ResultStats;

public class annotatorTester {

	public static void main(String[] args) {

		Gson gson = new Gson();
		JsonReviewReader reader = new JsonReviewReader(gson);
		Review[] rev1 = reader.getSingleDependencyReviewFile("./testingDeps/keyboard.json");
		Review[] rev2 = reader.getSingleDependencyReviewFile("./testingDeps/keyboard_bilawal.json");
		Evaluation eval = new Evaluation();
		ResultStats r = eval.testingReviewRaters(rev1, rev2);
		System.out.println("Recall:    " + r.getRecall());
		System.out.println("Precision: " + r.getPrecision());
		System.out.println("F1:        " + r.getF1Score());
		System.out.println(r.getResult());
		System.out.println(r.getTotal());
	}

}
