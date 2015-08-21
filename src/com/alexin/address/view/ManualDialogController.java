package com.alexin.address.view;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ManualDialogController 
{
	private Stage dialogStage;
	
	@FXML
    private void initialize() 
    {
		
    }
	
	public void setDialogStage(Stage dialogStage) 
	{
        this.dialogStage = dialogStage;
    }

}
