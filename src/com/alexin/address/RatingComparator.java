package com.alexin.address;

import java.util.Comparator;

public class RatingComparator implements Comparator<MovieEntry>
{

	public int compare(MovieEntry mv1, MovieEntry mv2) 
	{
		if(mv2.getimdbRating() - mv1.getimdbRating() == 0)
			return mv2.getimdbVotes() - mv1.getimdbVotes();
		else if (mv2.getimdbRating() > mv1.getimdbRating())
			return 1;
		else 
			return -1;
	}
	
	public String toString() 
	{
		return "Rating";
	}
}
