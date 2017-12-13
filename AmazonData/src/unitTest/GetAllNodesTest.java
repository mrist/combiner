package unitTest;

import java.util.ArrayList;

import org.junit.Test;

import data.Phrase;
import data.PhrasePatternNode;
import data.WordNode;
import junit.framework.TestCase;

public class GetAllNodesTest extends TestCase {
	Phrase		aPhrase	= new Phrase();
	WordNode	a1		= new WordNode("A1");
	WordNode	a2		= new WordNode("A2");
	WordNode	a3		= new WordNode("A3");
	WordNode	a4		= new WordNode("A4");
	WordNode	b1		= new WordNode("A1");
	WordNode	b2		= new WordNode("A2");
	WordNode	b3		= new WordNode("A3");
	WordNode	b4		= new WordNode("A4");
	WordNode	b5		= new WordNode("A4");
	WordNode	c1		= new WordNode("A1");
	WordNode	c2		= new WordNode("B2");
	WordNode	c3		= new WordNode("A3");
	WordNode	c4		= new WordNode("A4");
	WordNode	c5		= new WordNode("A2");

	private void initialize() {
		a3.addSucessors(a4);
		a1.addSucessors(a3);
		a1.addSucessors(a2);
		a1.addSucessors(a1);
		a4.setPredcessor(a3);
		a3.setPredcessor(a1);
		a2.setPredcessor(a1);
		a1.setPredcessor(a1);
		aPhrase.addWord(a1);
		aPhrase.addWord(a2);
		aPhrase.addWord(a3);
		aPhrase.addWord(a4);

		b4.setPredcessor(b3);
		b3.setPredcessor(b1);
		b2.setPredcessor(b1);
		b1.setPredcessor(b1);
		b3.addSucessors(b4);
		b1.addSucessors(b5);
		b1.addSucessors(b3);
		b1.addSucessors(b2);
		b1.addSucessors(b1);

		c4.setPredcessor(c3);
		c5.setPredcessor(c1);
		c3.setPredcessor(c1);
		c2.setPredcessor(c1);
		c1.setPredcessor(c1);
		c3.addSucessors(c4);
		c1.addSucessors(c5);
		c1.addSucessors(c3);
		c1.addSucessors(c2);
		c1.addSucessors(c1);
	}

	@Test
	public void testSame() {
		initialize();
		PhrasePatternNode tn = new PhrasePatternNode(a1);
		tn.initializeWordTree(a1, a1, aPhrase);
		ArrayList<WordNode> as = new ArrayList<WordNode>();
		as.add(a1);
		as.add(a2);
		as.add(a3);
		as.add(a4);
		System.out.println(a1.getAllNodes(tn));
		System.out.println(as);
		tn.print();
		assertTrue(a1.getAllNodes(tn).containsAll(as));

	}

	@Test
	public void testDifferentTrees() {
		initialize();
		PhrasePatternNode tn = new PhrasePatternNode(a1);
		tn.initializeWordTree(a1, a1, aPhrase);
		ArrayList<WordNode> bs = new ArrayList<WordNode>();
		bs.add(b1);
		bs.add(b2);
		bs.add(b3);
		bs.add(b4);
		assertTrue(b1.getAllNodes(tn).containsAll(bs));

	}

	@Test
	public void testDifferent() {
		initialize();
		PhrasePatternNode tn = new PhrasePatternNode(a1);
		tn.initializeWordTree(a1, a1, aPhrase);
		ArrayList<WordNode> cs = new ArrayList<WordNode>();
		cs.add(c1);
		cs.add(c3);
		cs.add(c4);
		cs.add(c5);
		assertTrue(c1.getAllNodes(tn).containsAll(cs));

	}

}
