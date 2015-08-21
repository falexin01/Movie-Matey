package com.alexin.address.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Actor 
{
	private StringProperty name;
    private IntegerProperty numMovies;
    
    public Actor(String name, int numMovies)
    {
    	this.name = new SimpleStringProperty(name);
    	this.numMovies = new SimpleIntegerProperty(numMovies);
    }
    
    public String getName() {
		return name.get();
	}
	
	public StringProperty nameProperty() {
		return name;
	}

	public int getNumMovies() {
		return numMovies.get();
	}
	
	public IntegerProperty numMoviesProperty() {
		return numMovies;
	}
}
