package com.thesisug.caching;

public class Area 
{
	public float lat;
	public float lng;
	public float rad;
	public int checkResult;
	public Area(float latitude, float longitude, float radius)
	{
		lat=latitude;
		lng=longitude;
		rad=radius;
		checkResult=-1;
	}
	public Area(float latitude, float longitude, float radius,int result)
	{
		lat=latitude;
		lng=longitude;
		rad=radius;
		checkResult=result;
	}
}
