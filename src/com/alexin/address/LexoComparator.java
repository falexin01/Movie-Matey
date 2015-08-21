package com.alexin.address;

import java.util.Comparator;

public class LexoComparator implements Comparator<MovieEntry>
{

	public int compare(MovieEntry mv1, MovieEntry mv2) 
	{
		return mv1.getTitle().compareTo(mv2.getTitle());
	}

	public String toString() 
	{
		return "Title";
	}
	
}
