package com.alexin.address.model;

import com.alexin.address.MovieEntry;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Movie 
{
    private StringProperty title;
    private IntegerProperty year;
    private FloatProperty rating;
    private StringProperty released;
    private IntegerProperty runtime;
    private StringProperty rated;
    private StringProperty plot;
    private MovieEntry mvEntry;
    
    public Movie(MovieEntry entry)
    {
    	this.title = new SimpleStringProperty(entry.getTitle());
    	this.year = new SimpleIntegerProperty(entry.getYear());
    	this.rating = new SimpleFloatProperty(entry.getimdbRating());
    	this.released = new SimpleStringProperty(entry.getRelease());
    	this.runtime = new SimpleIntegerProperty(entry.getRuntime());
    	this.rated = new SimpleStringProperty(entry.getRated());
    	this.plot = new SimpleStringProperty(entry.getPlot());
    	mvEntry = entry;
    }
    
    public MovieEntry getEntry()
    {
    	return mvEntry;
    }

	public String getTitle() {
		return title.get();
	}
	
	public StringProperty titleProperty() {
		return title;
	}

	public int getYear() {
		return year.get();
	}
	
	public IntegerProperty yearProperty() {
		return year;
	}
	
	public float getRating() {
		return rating.get();
	}
	
	public FloatProperty ratingProperty() {
		return rating;
	}

	public String getReleased() {
		return released.get();
	}
	
	public StringProperty releasedProperty() {
		return released;
	}

	public int getRuntime() {
		return runtime.get();
	}
	
	public IntegerProperty runtimeProperty() {
		return runtime;
	}

	public String getRated() {
		return rated.get();
	}
	
	public StringProperty ratedProperty() {
		return rated;
	}

	public String getPlot() {
		return plot.get();
	}
	
	public StringProperty plotProperty() {
		return plot;
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public void setYear(int year) {
		this.year.set(year);
	}
	
	public void setRating(float rating) {
		this.rating.set(rating);
	}

	public void setReleased(String released) {
		this.released.set(released);
	}

	public void setRuntime(int runtime) {
		this.runtime.set(runtime);
	}

	public void setRating(String rating) {
		this.rated.set(rating);
	}

	public void setPlot(String plot) {
		this.plot.set(plot);
	}
    
    
}
