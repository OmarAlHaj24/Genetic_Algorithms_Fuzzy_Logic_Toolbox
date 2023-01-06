import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class GUI {
    public static Scanner input;
    public static File inFile;
    public static int line;
    public static FuzzySystem fuzzySystem;
    public static Scanner in = new Scanner(System.in);
    public static Variable variable;
    public static PrintStream outFile;
    public static PrintStream console = System.out;
    public static FuzzySet set;
    public static ArrayList<JTextField> varFields;
    public static ArrayList<String> varLabels;
    public static JPanel panel3;
    public static void inputVariables() throws Exception {
        line++;
        String x = input.nextLine();
        while (!x.equals("x")) {
            String[] str = x.split(" ", 0);
            if (!str[1].equals("IN") && !str[1].equals("OUT")) {
                JOptionPane.showMessageDialog(null, "Error: " + x + " (Line " + line + ")\n" + "Please enter a valid Variable!");
                throw new Exception("Error: " + x + " (Line " + line + ")\n" + "Please enter a valid Variable!");
            }
            String l = str[2].substring(1, str[2].length() - 1);
            String r = str[3].substring(0, str[3].length() - 1);
            variable = new Variable(str[0], str[1], Double.parseDouble(l), Double.parseDouble(r));
            fuzzySystem.AddVariable(variable);
            line++;
            x = input.nextLine();
        }
    }
    public static void inputFuzzySet(String varName) throws Exception {
        if (!fuzzySystem.variables.containsKey(varName)) {
            JOptionPane.showMessageDialog(null, "Error: " + varName + " (Line " + line + ")\n" + "There is no variable in the system with the given name!");
            throw new Exception("Error: " + varName + " (Line " + line + ")\n" + "There is no variable in the system with the given name!");
        }
        line++;
        String x = input.nextLine();
        while (!x.equals("x")) {
            String[] str = x.split(" ", 0);
            String name = str[0];
            String type = str[1];
            variable = fuzzySystem.variables.get(varName);
            ArrayList<Double> values = new ArrayList<Double>();
            if (type.equals("TRI")) {
                if (str.length != 5) {
                    JOptionPane.showMessageDialog(null,"Error: " + x + " (Line " + line + ")\n" + "Please enter three points!" );
                    throw new Exception("Error: " + x + " (Line " + line + ")\n" + "Please enter three points!" );
                }
                values.add(Double.parseDouble(str[2]));
                values.add(Double.parseDouble(str[3]));
                values.add(Double.parseDouble(str[4]));
            } else {
                if (str.length != 6) {
                    JOptionPane.showMessageDialog(null,"Error: " + x + " (Line " + line + ")\n" + "Please enter four points!" );
                    throw new Exception("Error: " + x + " (Line " + line + ")\n" + "Please enter four points!" );
                }
                values.add(Double.parseDouble(str[2]));
                values.add(Double.parseDouble(str[3]));
                values.add(Double.parseDouble(str[4]));
                values.add(Double.parseDouble(str[5]));
            }
            set = new FuzzySet(name, type, values);
            variable.AddSet(set);
            line++;
            x = input.nextLine();
        }
    }
    public static void runSystem() throws Exception {
        HashMap<String, Double> inputs = new HashMap<>();
        for (int i =0; i<varFields.size(); i++) {
            Variable var1 = fuzzySystem.variables.get(varLabels.get(i));
            if ((var1.type).equals("IN")) {
                line++;
                Double value = Double.parseDouble(varFields.get(i).getText());
                if (var1.low > value || var1.high < value) {
                    JOptionPane.showMessageDialog(null,"Error: " + value + " (Line " + line + ")\n" + "Your input must be within the variable's specified range!" );
                    throw new Exception("Error: " + value + " (Line " + line + ")\n" + "Your input must be within the variable's specified range!" );
                }
                inputs.put(varLabels.get(i), value);
            }
        }
        ArrayList<DefuzzifiedOutput> results = fuzzySystem.runSystem(inputs);
        System.setOut(outFile);
        if (results.size() < fuzzySystem.numberOfOutVariables) {
            JOptionPane.showMessageDialog(null, "Not all the variables are predicted probably because of wrong rules");
            System.out.println("Not all the variables are predicted probably because of wrong rules");
        }
        for (int i = 0; i < results.size(); i++) {
            JOptionPane.showMessageDialog(null, "The predicted " + results.get(i).varName + " is " + results.get(i).predictedSet + " (" + results.get(i).crispValue + ")");
            System.out.print("The predicted " + results.get(i).varName + " is " + results.get(i).predictedSet);
            System.out.println(" (" + results.get(i).crispValue + ")");
        }
        System.setOut(console);
    }
    public static void inputRules() {
        line++;
        String ruleStr = input.nextLine();
        while (!ruleStr.equals("x")) {
            Rule rule = new Rule(ruleStr);
            fuzzySystem.AddRule(rule);
            line++;
            ruleStr = input.nextLine();
        }
    }
    public static void ReadFromFile(String inPath, String outPath) throws Exception {
        try {
            inFile = new File(inPath);
            input = new Scanner(inFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid path for the input file");
        }
        outFile = new PrintStream(new File(outPath));
        String name = input.nextLine();
        String description = input.nextLine();
        System.out.println(name);
        System.out.println(description);
        line += 2;
        fuzzySystem = new FuzzySystem(name, description);
        while (true) {
            line++;
            String choice = input.nextLine();
            if (choice.equals("1")) {
                try {
                    inputVariables();
                } catch (Exception E) {
                    System.setOut(outFile);
                    JOptionPane.showMessageDialog(null, E.toString());
                    System.out.println(E.getMessage());
                    System.setOut(console);
                    throw new Exception();
                }
            } else if (choice.equals("2")) {
                line++;
                String varName = input.nextLine();
                try {
                    inputFuzzySet(varName);
                } catch (Exception E) {
                    System.setOut(outFile);
                    JOptionPane.showMessageDialog(null, E.toString());
                    System.out.println(E.getMessage());
                    System.setOut(console);
                    throw new Exception();
                }
            } else if (choice.equals("3")) {
                try {
                    inputRules();
                } catch (Exception E) {
                    System.setOut(outFile);
                    JOptionPane.showMessageDialog(null, "Error: in (Line \" + line + \")");
                    JOptionPane.showMessageDialog(null, "Please enter a valid Rule!");
                    System.out.println(E.getMessage());
                    System.setOut(console);
                    throw new Exception();

                }
            }
//            else if (choice.equals("4")) {
//                try {
//                    if (!fuzzySystem.isInitialized()){
//                        throw new Exception(" (Line " + line + ")\n" + "Please add the rules and variables first");
//                    }
//
//                    runSystem();
//                } catch (Exception E) {
//                    System.setOut(outFile);
//                    JOptionPane.showMessageDialog(null,E.getMessage());
//                    System.out.println(E.getMessage());
//                    System.setOut(console);
//                    throw new Exception();
//                }
//            }
           else if (choice.equals("Close")) {
                break;
            }
        }
        input.close();

    }

    // /home/alaa/IdeaProjects/fuzzyLogic/src/input.txt
    // /home/alaa/IdeaProjects/fuzzyLogic/src/output.txt

    public static void main(String args[]) {
        JFrame frame = new JFrame("Fuzzy Logic");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.getContentPane();
        panel.setLayout(null);
        panel.setBackground(Color.white);

        //-----------------------------

        JFrame frame3 = new JFrame("Fuzzy Logic");
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame3.setSize(600, 400);
        frame3.setLocationRelativeTo(null);
        frame3.getContentPane();

        panel3 = new JPanel();
        panel3.setBackground(Color.white);
        panel3.setLayout(null);

        JLabel label4 = new JLabel("Enter the Crisp Values ");
        label4.setBounds(90,52,200,30);

        JButton button4 = new JButton("Submit");
        button4.setBounds(120,52,150,30);
        button4.setForeground(Color.BLACK);
        button4.setBackground(Color.WHITE);
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    runSystem();
                } catch (Exception E) {
                    System.setOut(outFile);
                    //JOptionPane.showMessageDialog(null,E.getMessage());
                    System.out.println(E.getMessage());
                    System.setOut(console);
                }
            }

        });

        JButton button5 = new JButton("Quit");
        button5.setBounds(300,52,150,30);
        button5.setForeground(Color.BLACK);
        button5.setBackground(Color.WHITE);
        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame3.dispose();
            }

        });

        //----------------------------

        JFrame frame2 = new JFrame("Fuzzy Logic");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setSize(600, 400);
        frame2.setLocationRelativeTo(null);
        frame2.getContentPane();


        JPanel panel2 = new JPanel();
        panel2.setLayout(null);
        panel2.setBackground(Color.white);

        JLabel label1 = new JLabel("Enter the path to input file");
        label1.setBounds(90,80, 400,50);

        JTextField field1 = new JTextField(30);
        field1.setBounds(90,120,400,40);

        JLabel label2 = new JLabel("Enter the path to output file");
        label2.setBounds(90,170, 400,50);

        JTextField field2 = new JTextField(30);
        field2.setBounds(90,210,400,40);

        JButton button = new JButton("Submit");
        button.setBounds(235,300,100,30);
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputFile = field1.getText();
                String outputFile = field2.getText();
                try {
                    ReadFromFile(inputFile,outputFile);
                    frame2.dispose();

                    int i = 92;
                    panel3 = new JPanel();
                    panel3.setBackground(Color.white);
                    panel3.setLayout(null);
                    varFields = new ArrayList<>();
                    varLabels = new ArrayList<>();
                    for(String key:fuzzySystem.variables.keySet()){
                        if(fuzzySystem.variables.get(key).type.equals("IN")){
                            JLabel l = new JLabel(key);
                            l.setBounds(90,i,100,30);

                            JTextField f = new JTextField(30);
                            f.setBounds(180,i,200,30);
                            f.setForeground(Color.BLACK);
//                            Border border = BorderFactory.createLineBorder(Color.BLACK);
//                            f.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                            i+=40;
                            panel3.add(l);
                            panel3.add(f);
                            varFields.add(f);
                            varLabels.add(key);
                        }

                    }
                    button4.setBounds(120,i,150,30);
                    button5.setBounds(300,i,150,30);
                    panel3.add(label4);
                    panel3.add(button4);
                    panel3.add(button5);

                    frame3.add(panel3);
                    frame3.setVisible(true);

                } catch (Exception ex) {
                    frame2.dispose();
                    throw new RuntimeException(ex);
                }

            }

        });

        //----------------------------

        JButton button1 = new JButton("Create System");
        button1.setBounds(120,52,150,30);
        button1.setForeground(Color.BLACK);
        button1.setBackground(Color.WHITE);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame2.add(panel2);
                frame2.setVisible(true);

            }
        });

        JButton button2 = new JButton("Quit");
        button2.setBounds(300,52,150,30);
        button2.setForeground(Color.BLACK);
        button2.setBackground(Color.WHITE);
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        frame.setVisible(true);
        frame.add(panel);

        panel2.add(label1);
        panel2.add(field1);
        panel2.add(label2);
        panel2.add(field2);
        panel2.add(button);


        panel.add(button1);
        panel.add(button2);

    }
}
