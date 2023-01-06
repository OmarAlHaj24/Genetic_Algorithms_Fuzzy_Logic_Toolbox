import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import java.util.HashMap;

public class Runner {
    public static FuzzySystem fuzzySystem;
    public static Variable variable;
    public static FuzzySet set;
    public static PrintStream outFile;
    public static PrintStream console = System.out;
    public static Scanner input;
    public static Scanner in = new Scanner(System.in);
    public static File inFile;
    public static int line;

    public static void inputVariables() throws Exception {
        line++;
        String x = input.nextLine();
        while (!x.equals("x")){
            String[] str = x.split(" ", 0);
            if(!str[1].equals("IN") && !str[1].equals("OUT")){
                throw new Exception("Error: " +x +" (Line " +line+")\n"+"Please enter a valid Variable!");
            }
            String l = str[2].substring(1, str[2].length()-1);
            String r = str[3].substring(0, str[3].length()-1);
            variable = new Variable(str[0],str[1],Double.parseDouble(l),Double.parseDouble(r));
            fuzzySystem.AddVariable(variable);
            line++;
            x = input.nextLine();
        }
    }

    public static void inputFuzzySet(String varName) throws Exception {
        if (!fuzzySystem.variables.containsKey(varName)){
            throw new Exception("Error: " +varName +" (Line " +line+")\n"+"There is no variable in the system with the given name!");
        }
        line++;
        String x = input.nextLine();
        while (!x.equals("x")){
            String[] str = x.split(" ", 0);
            String name = str[0];
            String type = str[1];
            variable = fuzzySystem.variables.get(varName);
            ArrayList<Double> values = new ArrayList<Double>();
            if (type.equals("TRI")){
                if(str.length != 5){
                    throw new Exception("Error: " +x +" (Line " +line+")\n"+"Please enter three points!");
                }
                values.add(Double.parseDouble(str[2]));
                values.add(Double.parseDouble(str[3]));
                values.add(Double.parseDouble(str[4]));
            }else {
                if(str.length != 6){
                    throw new Exception("Error: " +x +" (Line " +line+")\n"+"Please enter four points!");
                }
                values.add(Double.parseDouble(str[2]));
                values.add(Double.parseDouble(str[3]));
                values.add(Double.parseDouble(str[4]));
                values.add(Double.parseDouble(str[5]));
            }
            set = new FuzzySet(name,type,values);
            variable.AddSet(set);
            line++;
            x = input.nextLine();
        }
    }

    public static void inputRules(){
        line++;
        String ruleStr = input.nextLine();
        while(!ruleStr.equals("x")){
            Rule rule = new Rule(ruleStr);
            fuzzySystem.AddRule(rule);
            line++;
            ruleStr = input.nextLine();
        }
    }

    public static void runSystem() throws Exception {
        HashMap<String, Double> inputs = new HashMap<>();
        for(String var:fuzzySystem.variables.keySet()){
            Variable var1 = fuzzySystem.variables.get(var);
            if((var1.type).equals("IN")) {
                line++;
                Double value = Double.parseDouble(input.nextLine());

                if(var1.low > value || var1.high < value){
                    throw new Exception("Error: " + value +" (Line " +line+")\n"+"Your input must be within the variable's specified range!");
                }
                inputs.put(var, value);
            }
        }
        ArrayList<DefuzzifiedOutput> results = fuzzySystem.runSystem(inputs);
        System.setOut(outFile);
        if(results.size() < fuzzySystem.numberOfOutVariables){
            System.out.println("Not all the variables are predicted probably because of wrong rules");
        }

        for (int i=0; i<results.size(); i++){
            System.out.print("The predicted " + results.get(i).varName + " is " + results.get(i).predictedSet);
            System.out.println(" (" + results.get(i).crispValue + ")");
        }
        System.setOut(console);

    }

    public static void run() throws FileNotFoundException
    {
        //Input and output from and to files
        outFile = new PrintStream(new File("output.txt"));
        //
        while(true){
            System.out.println("1- Create a new fuzzy system");
            System.out.println("2- Quit");
            String choice = in.nextLine();
            try{
                if(!choice.equals("1") && !choice.equals("2")){
                    throw new Exception("Please enter a valid input!");
                }
            }catch(Exception E){
                System.setOut(outFile);
                System.out.println(E);
                System.setOut(console);
            }
            if (choice.equals("1")){
                line = 0;
                System.out.print("Enter the path to the input file: ");
                String inPath = in.nextLine();
                System.out.print("Enter the path to the output file: ");
                String outPath = in.nextLine();
                try {
                    inFile = new File(inPath);
                    input = new Scanner(inFile);
                }catch(Exception e){
                    System.out.println("Please enter a valid path for the input file");
                    continue;
                }

                outFile = new PrintStream(new File(outPath));
                String name = input.nextLine();
                String description = input.nextLine();
                line+=2;
                fuzzySystem = new FuzzySystem(name, description);
                while(true){
                    line++;
                    choice = input.nextLine();
                    if (choice.equals("1")){
//                        System.out.println("Enter the variable's name, type (IN/OUT) and range ([lower, upper]):");
//                        System.out.println("(Press x to finish)");
//                        System.out.println("--------------------------------------------------------------------");
                        try{
                            inputVariables();
                        }catch(Exception E){
                            System.setOut(outFile);
                            System.out.println(E);
                            System.setOut(console);
                            break;
                        }
                    }
                    else if (choice.equals("2")){
                        line++;
                        String varName = input.nextLine();
                        try{
                            inputFuzzySet(varName);
                        }catch(Exception E){
                            System.setOut(outFile);
                            System.out.println(E.getMessage());
                            System.setOut(console);
                            break;
                        }
                    }
                    else if(choice.equals("3")){
                        try{
                            inputRules();
                        }catch(Exception E){
                            System.setOut(outFile);
                            System.out.println("Error: in (Line " +line+")");
                            System.out.println("Please enter a valid Rule!");
                            System.setOut(console);
                            break;

                        }
                    }
                    else if(choice.equals("4")){
                        try {
                            if (!fuzzySystem.isInitialized())
                                throw new Exception("Error: " + choice +" (Line " +line+")\n"+"Please add the rules and variables first");
                            runSystem();
                        }catch(Exception e){
                            System.setOut(outFile);
                            System.out.println(e.getMessage());
                            System.setOut(console);
                            break;
                        }


                    }else if(choice.equals("Close")){
                        break;
                    }
                }
                input.close();
            }else{
                break;
            }
        }

    }
}
