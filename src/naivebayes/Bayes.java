package naivebayes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Bayes {
	HashMap<String, Word> words = new HashMap<String,Word>();
	BufferedWriter out;
	
	public static void main(String[] args) {
		Bayes run = new Bayes();
		try {
			run.train("test/train.txt");
			run.filter("test/testFile.txt");
		} catch (Exception e) {
			System.out.println("AN ERROR HAS OCCURED");
		}		
	}
	
	//uses a train-file to make a hashmap containing all words, and their probability of being spam
	public void train(String input) throws Exception{
            System.out.println("Training classifier in progress ..........");
             TimeUnit.SECONDS.sleep(10);
		int totalSpamCount = 0;
		int totalHamCount = 0;
		BufferedReader in = new BufferedReader(new FileReader(input));
		String line = in.readLine();
		while (line != null){
			if (!line.equals("")){
				String type = line.split("\t")[0];
				String sms = line.split("\t")[1];				
				for (String word : sms.split(" ")){
					word = word.replaceAll("\\W", "");
					word = word.toLowerCase();
					Word w = null;
					if(words.containsKey(word)){
						w = (Word) words.get(word);
					}
					else {
						w = new Word(word);
						words.put(word,w);
					}
					if(type.equals("ham")){
						w.countHam();
						totalHamCount++;
					}
					else if(type.equals("spam")){
						w.countSpam();
						totalSpamCount++;
					}		
				}
			}
			line = in.readLine();	
		}
		in.close();
		
		for (String key : words.keySet()) {
		    words.get(key).calculateProbability(totalSpamCount, totalHamCount);
		}
                System.out.println("Success!!Classifier Trained!! ");
	}
	
	//Takes the text to be analyzes as input, and produces predictions by form of 'spam' or 'ham'
	public void filter(String inputFile) throws Exception{
            System.out.println("Checking for spam and legit mails, PLEASE WAIT......");
        
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
                 TimeUnit.SECONDS.sleep(10);
		this.out = new BufferedWriter(new FileWriter("test/predictions.txt"));
		String line = in.readLine();
                 
		while (line != null){
			if (!line.equals("")){
				ArrayList<Word> sms = makeWordList(line);
				boolean isSpam = calculateBayes(sms);
                              
				if(isSpam == true) this.out.write(line+" is "+" SPAM!!!!!!!!!!!!!! ");
				else if (isSpam == false) this.out.write(line+" is "+"A LEGIT MAIL");
                                
			}
			this.out.newLine();
			line = in.readLine();
		}
		this.out.close();
		in.close();
                System.out.println("Done!");
	}

	//make an arraylist of all words in an sms, set probability of spam to 0.4 if word is not known
	public ArrayList<Word> makeWordList(String sms){
		ArrayList<Word> wordList = new ArrayList<Word>();
		for (String word : sms.split(" ")){
			word = word.replaceAll("\\W", "");
			word = word.toLowerCase();
			Word w = null;
			if(words.containsKey(word)){
				w = (Word) words.get(word);
			}
			else {
				w = new Word(word);
				w.setProbOfSpam(0.40f);
			}
			wordList.add(w);
		}
		return wordList;
	}
	
	//Applying Bayes rule and calculating probability of ham or spam. Return true if spam, false if ham
	public boolean calculateBayes(ArrayList<Word> sms){
		float probabilityOfPositiveProduct = 1.0f;
		float probabilityOfNegativeProduct = 1.0f;
		for (int i = 0; i < sms.size(); i++) {
			Word word = (Word) sms.get(i);
			probabilityOfPositiveProduct *= word.getProbOfSpam();
			probabilityOfNegativeProduct *= (1.0f - word.getProbOfSpam());
		}
		float probOfSpam = probabilityOfPositiveProduct / (probabilityOfPositiveProduct + probabilityOfNegativeProduct);
		if(probOfSpam > 0.9f) return true;
		else return false;
	}		
}
