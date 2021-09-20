package GoogleSheets;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public class CalculateTable {
	 private static Sheets sheetsService;
	 private static String SPREADSHEET_ID;

    public CalculateTable(Sheets sheetsService, String SPREADSHEET_ID) {
        this.sheetsService = sheetsService;
        this.SPREADSHEET_ID = SPREADSHEET_ID;
    }
	
    @SuppressWarnings("removal")
	public void updateData() throws IOException, GeneralSecurityException {
    	
        String range = "challenge!A4:F27";
        Integer column = 3;
        Integer totalClasses = 60;
        Double  averageAbsences = totalClasses * 0.25; //25%
       
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        List<List<Object>> values = response.getValues();
        
        Integer qtAbsences = 0;
        Double test01 = 0.0;
        Double test02 = 0.0;
        Double test03 = 0.0;
        Double average = 0.0;
        Double naf = 0.0;
        String status = "";
        if(values == null || values.isEmpty()){
            System.out.println("No data found.");
        } else {
            for (List<?> row : values ) {
            	qtAbsences = new Integer (row.get(2).toString());
                test01 = new Double(row.get(3).toString());
                test02 = new Double(row.get(4).toString());
                test03 = new Double(row.get(5).toString());
                
                average = (test01 + test02 + test03) / 3.0;
                 
                if(qtAbsences > averageAbsences) {
                	status = "Reprovado por Falta";
                    
                } else {
                    if(average < 50.0) {
                    	status = "Reprovado por Nota";
                    } else if (50.0 <= average && average < 70.0) {
                    	status = "Exame Final";
                    } else if (average >= 70.0) {
                    	status = "Aprovado";
                    }
                }
                
                column += 1;
                updateTable(status, "G", column);
                
                if(status.equals("Exame Final")) {
                	//5 <= (m  naf)/2
                	
                } else {
                	naf = 0.0;
                	DecimalFormat df = new DecimalFormat();
                    df.applyPattern("#,##0.00");
                    
                	updateTable(df.format(naf), "H", column);
                }	
            }
        }
    }
    
    public static void updateTable(String value, String letter, Integer column) throws IOException, GeneralSecurityException {
    	
    	String range = letter + column;
        ValueRange body = new ValueRange()
        		.setValues(Arrays.asList(
        				Arrays.asList(value)
        				));
        
        UpdateValuesResponse result = sheetsService.spreadsheets().values()
        		.update(SPREADSHEET_ID, range, body)
        		.setValueInputOption("RAW")
        		.setIncludeValuesInResponse(true)
        		.execute();
    } 
}
