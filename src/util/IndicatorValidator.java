package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import core.MethLib;

public class IndicatorValidator extends MethLib{

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        
        //local variables
        String[] indicatorNormalization = new String[2];
        String indicatorValue = new String();
        String indicatorType = new String();
        String normalize = new String();
        boolean indicatorValid = false;
        String again = new String();
        
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"/src/config/testbed.properties");
        config.load(fis);
        
        Scanner input = new Scanner(System.in);
        again = "y";
        
        //continue running the utility until user answers n
        while(again.contentEquals("y")){
            
            System.out.println("Enter an indicator value: ");
            indicatorValue = input.nextLine();
        
            System.out.println("Enter an indicator type: ");
            indicatorType = input.nextLine();

            System.out.println("Normalize? (y/n)");
            normalize  = input.nextLine();
        
            config.setProperty("normailizationEnabled", normalize);
        
            //normalize the indicator
            indicatorNormalization = MethLib.normalizeIndicator(indicatorValue, indicatorType);
        
            if (!indicatorNormalization[0].contentEquals(indicatorValue)){
            
                System.out.println("Indicator value \'"+indicatorValue+"\' normalized to \'"+indicatorNormalization[0]+"\'.");
                indicatorValue = indicatorNormalization[0];
            
            }

            if (!indicatorNormalization[1].contentEquals(indicatorType)){
            
                System.out.println("Indicator type \'"+indicatorType+"\' normalized to \'"+indicatorNormalization[1]+"\'.");
                indicatorType = indicatorNormalization[1];
            
            }

            //validate indicator
            indicatorValid = MethLib.validateIndicator(indicatorValue, indicatorType);
        
            if (!indicatorValid){
            
                System.out.println("Indicator \'"+indicatorValue+"\' is not a valid "+indicatorType+".");
            
            }else{
            
                System.out.println("Indicator \'"+indicatorValue+"\' is a valid "+indicatorType+".");
            
            }
        
            //check if the user would like to continue using the utility
            System.out.println("Validate another? (y/n)");
            again  = input.nextLine();
            
        }
        
        input.close();
        System.out.println("Bye!");
        
    }
    
}
