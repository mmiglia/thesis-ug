package com.thesisug.ui.accessibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;



public class Morse {
	
	// >>>>>>>>> da conformare alla TAG delle altre classi
	private static final String TAG = "classeMorse";
	
	private static long dotDuration = 100; // duration of a dot in milliseconds
	private static long[] initialRest = {0}; // rest before morse vibration pattern starts
	private static long nextNotificationInstant = 0L;
	
	// HashMap is used as dictionary type as it allows access
	// in constant time
	private static HashMap<String,String> morseCode;
	
	public static long[] getMorseVibrationPattern(String message) {
		// if this is the first time method is called initialize
		// letter-to-code dictionary
		if (morseCode == null) initDictionary();
		// cleans message to encode and makes it upper-case as morse
		// is not case-sensitive (so we choose upper-case)
		String cleanMsg = removeMultipleWhiteSpacesAndUpperCase(message);
		// creates the data structure where vibration patterns for gaps
		// and letters are assembled
		ArrayList<long[]> vibrationPatternChunks = new ArrayList<long[]>();
		// Android's vibration patterns always begin with a the time the phone
		// should rest before executing the first vibration in the pattern
		vibrationPatternChunks.add(initialRest);
		Log.v(TAG, "Aggiunto initial rest");
		// this variable keeps track of how many places the long[] to be
		// returned will have to avoid re-reading a third time
		int totalEncMsgLenght = 1; // this length is initially 1 as
									// as initial rest was added
		// characters in cleanMsg are looked at one after the other
		// and if a non-whitespace character is met if a non-WS
		// character was before it then a gap is put in front of
		// character dash-dot sequence, otherwise (whitespace before)
		// no gap is added. If a whitespace is met then the gap is put.
		// In any case the nature of current character is reported for
		// next iteration and totalEncMsgLength is properly updated
		boolean previousWasNonWhiteSpace = false;
		for (int i = 0 ; i < cleanMsg.length() ; i++) {
			char c = cleanMsg.charAt(i);
			Log.v(TAG, "Letto " + c);
			switch (c) {
			case ' ':
				vibrationPatternChunks.add(getMediumGap());
				Log.v(TAG, "Messo un mediumGap");
				previousWasNonWhiteSpace = false;
				totalEncMsgLenght++;
				Log.v(TAG, "Lunghezza pattern = " + totalEncMsgLenght);
				break;
			default:
				if (previousWasNonWhiteSpace) {
					vibrationPatternChunks.add(getShortGap());
					Log.v(TAG, "Messo uno shortGap");
					totalEncMsgLenght++;
					Log.v(TAG, "Lunghezza pattern = " + totalEncMsgLenght);
				}
				// characters is converted to its String version
				// so it can be retrieved in the HashMap
				String code = morseCode.get(Character.toString(c));
				// characters not in the dictionary are encoded as X's
				if ( code == null ) code = morseCode.get("X");
				long[] vPattern = codeToVibratePattern(code);
				vibrationPatternChunks.add(vPattern);
				Log.v(TAG, "Aggiunto codice per " + c + " lungo " + vPattern.length);
				previousWasNonWhiteSpace = true;
				totalEncMsgLenght += vPattern.length;
				Log.v(TAG, "Lunghezza pattern = " + totalEncMsgLenght);
				break;
			}
		}
		// adds a final gap to separate multiple notifications in a row
		vibrationPatternChunks.add(getGap(14));
		totalEncMsgLenght++;
		// chunks sticked in a single long[] are returned
		return chunksToPattern(vibrationPatternChunks, totalEncMsgLenght);
	}
	
	private static String removeMultipleWhiteSpacesAndUpperCase(String message) {
		// multiple whitespace characters are removed by detecting them with
		// a regular expression and substituting them with a single white
		// space. This substitution seemed appropriate for the purpose of
		// morse coding
		Pattern pattern = Pattern.compile("\\s+");
	    Matcher matcher = pattern.matcher(message);
	    matcher.find();
	    return matcher.replaceAll(" ").toUpperCase();
	}
	
	// given long[] chunks of a vibration pattern and their overall length it
	// assembles them in a single long[] (which is what Android understands)
	private static long[] chunksToPattern(ArrayList<long[]> chunks, int totalLength) {
		long[] vPattern = new long[totalLength];
		long totalPatternDuration = 0L;
		int j = 0;
		for (long[] chunk : chunks) {
			for (int i = 0 ; i < chunk.length ; i++) {
					vPattern[j] = chunk[i]*dotDuration;
					Log.v(TAG, "pattern["+j+"] (" + (j%2 == 0 ? "fermo" : "vibra") + ") -> " + vPattern[j]);
					totalPatternDuration += vPattern[j];
					j++;
			}
		}
		//vPattern[0] = getWaitBeforeStartTime(totalPatternDuration);
		return vPattern;
	}
	
	// get time a vibrating notification needs to rest before
	// starting such that it does not mess up with preceding ones
	private static long getWaitBeforeStartTime(long patternDuration) {
		long now = System.currentTimeMillis();
		long waitBeforeStartTime;
		if (now <= nextNotificationInstant) {
			waitBeforeStartTime = nextNotificationInstant - now;
			nextNotificationInstant += patternDuration;
		} else {
			waitBeforeStartTime = 0;
			nextNotificationInstant = now + patternDuration;
		}
		return waitBeforeStartTime;
	}
	
	// gives vibration pattern for the dash-dot sequence of a single letter
	// WITHOUT INTER-LETTER OR INTER-WORD RESTS according to Android's convention:
	// entries specify alternately vibration and rest timings
	private static long[] codeToVibratePattern(String singleCharacterCode) {
		// allocate long array which is going to be filled with
		// Android device vibration pattern for a single letter
		// array has a place for vibration timing for each dot
		// or dash (singleCharacterCode.length() places) and a
		// place for rest timing for each inter-element (dot or
		// dash) time (singleCharacterCode.length() - 1 places)
		long [] vibratePattern = new long[singleCharacterCode.length() + singleCharacterCode.length() - 1];
		
		// for each element set appropriate vibration timing and
		// if the element is not the last one add a 1tu ( = time
		// unit ) rest for inter-element gap
		for (int i = 0, j = 0; i < singleCharacterCode.length() ; i++) {
			switch (singleCharacterCode.charAt(i)) {
			case '.': vibratePattern[j] = 1; j++; break;
			case '-': vibratePattern[j] = 3; j++; break;
			}
			if ( i < singleCharacterCode.length() - 1) {
				vibratePattern[j] = 1;
				j++;
			}
		}
		return vibratePattern;
	}
	
	// test method
	/*public static long[] test(long[] letter) {
		ArrayList<long[]> vibrationChunks = new ArrayList<long[]>();
		long[] initialRest = new long[1];
		initialRest[0] = 3;
		int totalLength = 0;
		vibrationChunks.add(initialRest);
		totalLength += initialRest.length;
		vibrationChunks.add(letter);
		totalLength += letter.length;
		long[] vibrationPattern = new long[totalLength];
		int j = 0;
		for ( int i = 0 ; i < vibrationChunks.size() ; i++ ) {
			for ( int k = 0 ; k < vibrationChunks.get(i).length ; k++ ) {
				vibrationPattern[j] = vibrationChunks.get(i)[k]*dotDuration;
				j++;
			}
		}
		return vibrationPattern;
	}*/
	
	private static long[] getShortGap() {
		return getGap(3);
	}
	
	private static long[] getMediumGap() {
		return getGap(7);
	}
	
	private static long[] getGap(long lengthInTu) {
		long[] newGap = new long[1];
		newGap[0] = lengthInTu;
		return newGap;
	}
	
	private static void initDictionary() {
		morseCode = new HashMap<String, String>();
		// ITU morse code
		
		// letters
		morseCode.put("A", ".-");
		morseCode.put("B", "-...");
		morseCode.put("C", "-.-.");
		morseCode.put("D", "-..");
		morseCode.put("E", ".");
		morseCode.put("F", "..-.");
		morseCode.put("G", "--.");
		morseCode.put("H", "....");
		morseCode.put("I", "..");
		morseCode.put("J", ".---");
		morseCode.put("K", "-.-");
		morseCode.put("L", ".-..");
		morseCode.put("M", "--");
		morseCode.put("N", "-.");
		morseCode.put("O", "---");
		morseCode.put("P", ".--.");
		morseCode.put("Q", "--.-");
		morseCode.put("R", ".-.");
		morseCode.put("S", "...");
		morseCode.put("T", "-");
		morseCode.put("U", "..-");
		morseCode.put("V", "...-");
		morseCode.put("W", ".--");
		morseCode.put("X", "-..-");
		morseCode.put("Y", "-.--");
		morseCode.put("Z", "--..");
		
		// figures
		morseCode.put("1", ".----");
		morseCode.put("2", "..---");
		morseCode.put("3", "...--");
		morseCode.put("4", "....-");
		morseCode.put("5", ".....");
		morseCode.put("6", "-....");
		morseCode.put("7", "--...");
		morseCode.put("8", "---..");
		morseCode.put("9", "----.");
		morseCode.put("0", "-----");
		// end of transmission character "0(alt)" -> "-" ?
		//morseCode.put("\0", "-")
	}

}
