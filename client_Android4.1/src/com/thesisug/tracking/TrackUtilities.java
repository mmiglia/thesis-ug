package com.thesisug.tracking;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
/**
 * Some field and methods useful for tracking features.
 * 
 * @author Alberto Servetti
 *
 */
public class TrackUtilities 
{
	/**
	 * Simple date format for date : yyyy-MM-dd. 
	 */
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);;
	/**
	 * Simple date format for time : HH:mm:ss. 
	 */
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss",Locale.ITALY);
	
	/**
	 * Write a string to a private file.
	 * 
	 * @param FILENAME		Name of the file to be written.
	 * @param newcontent	String content to be written.
	 * @param context		Application context.
	 * @return				True if writing succeeds, false otherwise.
	 */
	public static boolean writeToFile(String FILENAME,String newcontent,Context context)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_APPEND);
			fos.write(newcontent.getBytes());
			fos.close();
			return true;
		}
		catch (FileNotFoundException e) 
		{
			
			e.printStackTrace();
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return false;	
	}
}
