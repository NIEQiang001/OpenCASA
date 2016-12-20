package spermAnalysis;

import java.awt.geom.Line2D;
import java.util.List;
import java.util.ListIterator;

public class Motility {

	
	/******************************************************/
	/**
	 * @param track - a track
	 * @param avgTrack - 
	 * @return ALH (mean and max) (um/second)
	 */	
	public static float[] alh(List track,List avgTrack,int wSize,int microPerPixel){
		
		int length = avgTrack.size();
		float alh[] = new float[2];
		float alhMax = 0;
		float alhMean = 0;
		for (int i=0;i<length;i++){
			Spermatozoon origSpermatozoon = (Spermatozoon)track.get(i+wSize/2-1);
			Spermatozoon avgSpermatozoon = (Spermatozoon)avgTrack.get(i);
			float distance = origSpermatozoon.distance(avgSpermatozoon);
			alhMean+=distance;
			if(distance>alhMax)
				alhMax =  distance;
		}
		//Mean value
		alhMean=alhMean/length;
		//convert pixels to micrometers
		alh[0]=alhMean*(float)microPerPixel;
		alh[1]=alhMax*(float)microPerPixel;
		
		return alh;
	}
	
	/******************************************************/
	/**
	 * @param track - a track
	 * @param avgTrack - 
	 * @return BCF (Hz)
	 */	
	public static float bcf(List track,List avgTrack,int bcf_shift, int wSize, int frameRate){
		
		int length = avgTrack.size();
		int intersections=0;
		// bcf_shift equal to 1 is not enougth to catch all beat-cross
		for (int i=bcf_shift;i<length;i=i+1+bcf_shift){
			Spermatozoon origP0 = (Spermatozoon)track.get(i-bcf_shift+wSize/2-1);
			Spermatozoon origP1 = (Spermatozoon)track.get(i+wSize/2-1);
			Spermatozoon avgP0 = (Spermatozoon)avgTrack.get(i-bcf_shift);
			Spermatozoon avgP1 = (Spermatozoon)avgTrack.get(i);
			Line2D origLine = new Line2D.Float();
			origLine.setLine(origP0.x,origP0.y,origP1.x,origP1.y);
			Line2D avgLine = new Line2D.Float();
			avgLine.setLine(avgP0.x,avgP0.y,avgP1.x,avgP1.y);
			
			boolean intersection = origLine.intersectsLine(avgLine);
			if(intersection)
				intersections++;
		}
		float bcf_value = (float)intersections*frameRate/(float)length;
		
		return bcf_value;
	}
	/******************************************************/
	/**
	 * @param track - a track
	 * @return MAD - (degrees)
	 */	
	public static float mad(List track){
		
		int length = track.size();
		ListIterator jT = track.listIterator();
		Spermatozoon oldSpermatozoon = (Spermatozoon) jT.next();
		float totalDegrees = 0;
		for (int i=1;i<length;i++){
			Spermatozoon newSpermatozoon = (Spermatozoon)track.get(i);
			float diffX = newSpermatozoon.x-oldSpermatozoon.x;
			float diffY = newSpermatozoon.y-oldSpermatozoon.y;
			double angle = (2*Math.PI+Math.atan2(diffY,diffX))%(2*Math.PI);
			totalDegrees+=angle;
			oldSpermatozoon = newSpermatozoon;
		}	
		//mean angle
		float meanAngle = totalDegrees/(length-1);
		return meanAngle;
	}	
	/******************************************************/
	/**
	 * @param track - a track
	 * @return VCL (um/second)
	 */	
	public static float vcl(List track,int microPerPixel,int frameRate){
		
		int length = track.size();
		ListIterator jT = track.listIterator();
		Spermatozoon oldSpermatozoon = (Spermatozoon) jT.next();
		float distance = 0;
		for (;jT.hasNext();){ 
			Spermatozoon newSpermatozoon = (Spermatozoon) jT.next();
			distance += newSpermatozoon.distance(oldSpermatozoon);
			oldSpermatozoon = newSpermatozoon;
		}	
		//convert pixels to micrometers
		distance = distance*(float)microPerPixel;
	    // Seconds
		float elapsedTime = (length-1)/frameRate;
		//return um/second
		return distance/elapsedTime;
	}
	
	/******************************************************/
	/**
	 * @param track - a track
	 * @return VSL (um/second)
	 */	
	public static float vsl(List track,int microPerPixel,int frameRate){
		int length = track.size();
		Spermatozoon first = (Spermatozoon)track.get(1);
		Spermatozoon last = (Spermatozoon)track.get(length-1);
		//Distance (pixels)
		float distance = last.distance(first);
		//convert pixels to micrometers
		distance = distance*(float)microPerPixel;
	    // Seconds
		float elapsedTime = (length-1)/frameRate;
		//return um/second
		return distance/elapsedTime;
	}
	
}