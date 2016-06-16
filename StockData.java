package final_project;

import week6.FileIO;
import java.util.*;
import java.io.*;

public class StockData {

	//if the amount invested is bigger than you get 0% for the assignment.
	public static int LIMIT = 200000;

	public static void main(String[] args) throws Exception {

		FileIO io = new FileIO();
		String[] original = io.load(
				"C:\\Users\\Tomek\\OneDrive\\Documents\\College\\Year 2\\Semester 2\\CS211 Algorithms and Data Structures 2\\Final Project\\StockData.txt");

		int numrows = original.length;
		// Split the first line (names of companies) between the tabs. If you
		// notice in the file, each company is seperate with a TAB.
		// '\t' gets rid of the tab and puts each company in its own position.
		// So we're basically checking how many columns there are.
		int numcols = original[0].split("\t").length;

		double[][] array = new double[numrows][numcols];

		// Because position 0 in the rows is the DATE ... COMPANY1 ... COMPANY2
		// ... etc.

		for (int i = 1; i < numrows; i++) {

			// Because the first column is filled with dates. So we start to
			// fill out the data starting from position 1.
			for (int j = 1; j < numcols; j++) {
				array[i][j] = Double.parseDouble(original[i].split("\t")[j]);
			}
		}

		// Create an array that will store the dates.
		String dates[] = new String[numrows];

		for (int i = 1; i < numrows; i++) {
			dates[i] = original[i].substring(0, 10);
		}

		// get an array of companies names
		String companies[] = original[0].split("\\t");


		//************************************************************************************************
		//************* CALCULATING DRAWDOWN - NO NEEDED FOR THE FINAL PROJECT - ONLY WASTES TIME ********

		/*		
		// can we find drawdown higher than this?
		double drawdown = 0;
		String startdate = "";
		String finishdate = "";
		String company = "";

		// repeat for all companies
		for (int j = 1; j < numcols; j++) {
			// start current price at 100%
			double current = 100;
			// start peak is 100%
			double peak = 100;
			// start trought is 0%
			double trough = 0;

			// store the start date, finish date and record date
			String localstartdate = "";
			String localfinishdate = "";
			String recorddate = "";

			// go through each day - data is backwards
			for (int i = numrows - 1; i > 0; i--) {
				// change the price for today
				current = current + (current * (array[i][j] / 100));
				// if it's a record high update
				if (current > peak) {
					peak = current;
					// keep track of the date
					recorddate = original[i].split("\t")[0];
					// otherwise, are we lower than ever before below the
					// current peak?
				} else if (1 - current / peak > trough) {
					// keep track of this super low
					trough = 1 - current / peak;

					localstartdate = recorddate;
					localfinishdate = original[i].split("\t")[0];
				}
			}

			// now we've found the drawdown for this company - is it bigger
			// than the other companies?
			if (trough > drawdown) {

				drawdown = trough;
				startdate = localstartdate;
				finishdate = localfinishdate;
				// remember the company
				company = original[0].split("\t")[j];
			}
		}

		// print out the overall results
		System.out.println("The company with the highest drawdown was " + company + " which suffered a drawdown of "
				+ String.format("%.1f", drawdown * 100) + "% between the dates of " + startdate + " and " + finishdate);



		 ****************************** CALCULATING DRAWDOWN ENDS HERE  *************
		 ****************************************************************************/

		///////////////////////////////////
		///////////////////////////////////
		//PROJECT CALCULATIONS BEGIN HERE//
		///////////////////////////////////
		///////////////////////////////////


		// get an array of the stock price for each company from the FILE IO class
		String stockPriceLoad[] = io.load(
				"C:\\Users\\Tomek\\OneDrive\\Documents\\College\\Year 2\\Semester 2\\CS211 Algorithms and Data Structures 2\\Final Project\\StockPrice.txt");

		//Put them into an array of strings but with split
		String stockPriceArr[] = stockPriceLoad[0].split("\\t");

		//Now parse the strings to a final StockPrice array of ints
		int stockPrice[] = new int[numcols];
		for (int i = 1; i < numcols; i++) {
			stockPrice[i] = Integer.parseInt(stockPriceArr[i]);
		}

		//the change column. The volatility will be calculated from this array by taking the standard deviation of it.
		double change[] = new double[numrows];

		///////////////////////////////////////////////////////////////////////
		///// ALGORITHM TO PRODUCE A STRING WITH QUANTITY OF EACH COMPANY//////
		///////////////////////////////////////////////////////////////////////

		//volatility value
		double vol = 100.0, tempVol = 0.0;

		//quantity of each company
		int qty[] = new int[numcols], tempQty[] = new int[numcols];

		//invested
		int invested = 0;

		/////////////////
		// ALGORITHM 1
		// Vol: 1.008
		// Eur: 242900
		/////////////////

		while(getInvested(stockPrice, qty) < LIMIT || vol > 1.01){

			for(int i = 1; i < numcols; i++){
				qty[i]++;



				tempVol = getVol(array, stockPrice, qty);

				if(tempVol < vol){
					vol = tempVol;
					print(vol, qty, getInvested(stockPrice, qty));

				}
				else {
					qty[i] = 0;
				}
			}
		}

		//Above gets it down to 1.008 //
		//Below code is just an attempt to decrease the volatility even more.
		while(true){
			for(int i = 1; i < numcols; i++){
				qty[i] = randomNum(0, 20);
				
				if(getVol(array, stockPrice, qty) < vol){
					vol = getVol(array,stockPrice,qty);
					print(vol, qty, getInvested(stockPrice,qty));
				}
				else {
					qty[i] = 0;
				}
			}
		}




		/////////////////
		// ALGORITHM 2
		// Vol: 1.568
		// Eur: 959979
		/////////////////
		// Boolean array tells you which companies decrease your volatility after you buy 1 unit of it. Company 1 is taken to be a default one.
		// Then whenever the boolean is true, you add 1 unit of that company

		/*
		boolean isLower[] = new boolean[numcols];
		isLower[1] = true;
		qty[1] = 1;
		vol = getVol(array, stockPrice, qty);
		Arrays.fill(isLower, false);

		for(int i = 2; i < numcols; i++){
			qty[i]++;
			if(getVol(array, stockPrice, qty) < vol){
				isLower[i] = true;
			}
			qty[i]--;
		}

		while(vol > 1.0){
			for(int i = 1; i < numcols; i++){
				if(isLower[i] == true){
					qty[i]++;
				}


				if(getVol(array, stockPrice, qty) < vol){
					vol = getVol(array, stockPrice, qty);
					print(vol, qty, getInvested(stockPrice, qty));
				}
			}


		}
		 */

	}

	//************* Method: print just the string of numbers and volatility ******************//
	public static void print(double vol, int qty[], int invested) throws Exception{
		File strings = new File("C:\\Users\\Tomek\\OneDrive\\Documents\\College\\Year 2\\Semester 2\\CS211 Algorithms and Data Structures 2\\Final Project\\strings.txt");
		FileWriter stringWriter = new FileWriter(strings, true);

		int numcols = qty.length;

		stringWriter.append(Math.round((vol + 0.003) * 1000d) / 1000d + "\r\n" + invested + "\r\n");
		System.out.println(Math.round((vol + 0.003) * 1000d) / 1000d + "\n" + invested);

		for(int j = 1; j < numcols; j++){
			stringWriter.append(qty[j] + "\t");
			System.out.print(qty[j] + "\t");
		}
		stringWriter.append("\r\n");
		System.out.println();

		stringWriter.close();

	}


	//********************** Method: print FINAL results out to the screen and to the file ***************************//
	public static void printAll(double vol, int qty[], int invested) throws Exception{
		//This allows me to write the outputs to a file called output.txt. Instead of remembering each output I can just always go back and
		//read which string sequence gave me the lowest volatility
		File output = new File("C:\\Users\\Tomek\\OneDrive\\Documents\\College\\Year 2\\Semester 2\\CS211 Algorithms and Data Structures 2\\Final Project\\output.txt");

		//true: append the file (update it)
		//false: overwrite the file (delete what's in it and put new stuff in it
		FileWriter outputWriter = new FileWriter(output, true);

		int numcols = qty.length;

		//Printing out the results to the console but also updating the output.txt file with the results so that I can always go back to it and check
		System.out.println("Volatility " + Math.round((vol + 0.003) * 1000d) / 1000d);
		System.out.println("Total invested: " + invested);
		System.out.println("Investment limit: " + LIMIT);
		System.out.println("String of numbers:");

		outputWriter.append("Volatility: " + Math.round((vol + 0.003) * 1000d) / 1000d + "\r\n");
		outputWriter.append("Total invested: " + invested + "\r\n");
		outputWriter.append("Investment limit: " + LIMIT + "\r\n");
		outputWriter.append("String of numbers:\r\n");

		for(int i = 1; i < numcols; i++){
			System.out.print(qty[i] + "\t");
			outputWriter.append(qty[i] + "\t");
		}

		System.out.println("\n\n***************************************************************\n\n");
		outputWriter.append("\r\n\r\n***********************************************************\r\n\r\n");
		outputWriter.close();


	}

	//*************************** METHOD: get the total sum of money invested so far ********************//
	public static int getInvested(int stockPrice[], int qty[]){
		int invested = 0;
		int numcols = stockPrice.length;

		for(int i = 1; i < numcols; i++){
			invested = invested + (stockPrice[i] * qty[i]);
		}

		return invested;
	}

	// ****************** METHOD: Calculate volatility ******************** //
	public static double getVol(double array[][], int stockPrice[], int qty[]){
		//Need the change table for the given quantity
		double change[] = getChange(array, stockPrice, qty);

		//Volatility is just the standard deviation of the 'change' table
		double vol = stDev(change);
		return vol;
	}

	// ******************* Method: CALCULATING THE "CHANGE%" TABLE **********************//
	//
	// DESCRIPTION
	//
	// Accepts the following parameters:
	// 1: numrows (NEVER CHANGES)
	// 2: numcols (NEVER CHANGES)
	// 3: empty "change" array initialised in the main method (NEVER CHANGES)
	// 4: the 2D array with all the constant values (initialised at the start of the program) (NEVER CHANGES)
	// 5: stockPrice array (NEVER CHANGES)
	// 6: quantity array: this will be different everytime we call this method.
	public static double[] getChange(double array[][], int stockPrice[], int qty[]){
		int numrows = array.length;
		int numcols = array[1].length;
		double change[] = new double[numrows];


		for(int i = 1; i < numrows; i++){

			//sum: sum of products
			// sumStock: sum of the whole stock money
			double sum = 0, sumStock = 0;

			//go through columns
			for(int j = 1; j < numcols; j++){
				//product: stock value * stock price * quantity of that stock
				double product = array[i][j] * stockPrice[j] * qty[j];
				//summing up the products
				sum = sum + product;
				//summing up all the stock prices that we invested in
				sumStock = sumStock + stockPrice[j] * qty[j];
			}

			//change is the sum / sumStock
			change[i] = (sum / sumStock);
		}

		return change;
	}

	// ******************* Method: calculate Standard Deviation **********************//
	public static double stDev(double values[]){
		//call the getMean method
		double mean = getMean(values);

		for(int i = 0; i < values.length; i++){
			values[i] = Math.pow(values[i] - mean, 2);
		}

		double stdev = Math.sqrt(getMean(values));
		return stdev;		
	}

	// ******************* Method: calculate the mean of an array of numbers **********************//
	public static double getMean(double values[]){
		double mean = 0;
		for(int i = 0; i < values.length; i++){
			mean = mean + values[i];
		}

		mean = mean/values.length;

		return mean;
	}

	// ******************* Method: generate a random number between min and max **********************//
	public static int randomNum(int min, int max){
		int range = (max - min) + 1;     
		return (int)(Math.random() * range) + min;
	}
}