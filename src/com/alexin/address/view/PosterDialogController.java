package com.alexin.address.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PosterDialogController 
{
	@FXML
	private TextField keyField;
	@FXML
	private TextField heightField;
	
	private Stage dialogStage;
	private int height;
	private String apiKey;
	private boolean status = false;
	
	@FXML
    private void initialize() 
    {
		
    }
	
	@FXML
	private void handleConfirmed()
	{
		height = Integer.parseInt(heightField.getText());
		apiKey = keyField.getText();
		
		if(apiKey != null && height >= 100)
		{
			status = true;
			dialogStage.close();
		}
	}
	
	@FXML
    private void handleCancel() 
	{
		status = false;
        dialogStage.close();
    }
	
	public boolean getStatus()
	{
		return status;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public String getApiKey()
	{
		return apiKey;
	}
	
	public void setDialogStage(Stage dialogStage) 
	{
        this.dialogStage = dialogStage;
    }
}
