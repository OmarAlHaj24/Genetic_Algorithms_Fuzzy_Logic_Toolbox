import java.util.ArrayList;
import java.util.HashMap;
public class FuzzySystem {
    String name;
    String description;
    ArrayList<Rule> rules;
    HashMap<String,Variable> variables;
    int numberOfOutVariables;
    public FuzzySystem(String name, String description){
        this.name = name;
        rules = new ArrayList<>();
        variables = new HashMap<>();
        this.description = description;
        numberOfOutVariables = 0;
    }
    public HashMap<String, HashMap<String,Double>> Fuzzification (HashMap<String, Double> input){
        HashMap<String, HashMap<String,Double>> map = new HashMap<>();
        for(String key:variables.keySet()){
            if(variables.get(key).type.equals("IN")){
                HashMap<String, Double> memberships = variables.get(key).CalcMembership(input.get(key));
                map.put(key, memberships);
            }
        }
        return map;
    }

    public void AddRule (Rule rule){
        rules.add(rule);
    }

    public void AddVariable (Variable variable){
        variables.put(variable.name,variable);
        if(variable.type.equals("OUT"))
            numberOfOutVariables++;
    }

    public HashMap<String, HashMap<String, Double>>  Inference(HashMap<String, HashMap<String,Double>> membership){
        HashMap<String, HashMap<String, Double>> results = new HashMap<>();

        for(int i = 0; i<rules.size(); i++){
            HashMap<String, HashMap<String, Double>> temp = rules.get(i).Evaluate(membership);
            for(String key:temp.keySet()){
                if (!results.containsKey(key)){
                    results.put(key,new HashMap<String,Double>());
                }
                HashMap<String, Double> map = temp.get(key);
                for(String setName:map.keySet()){
                    if(results.get(key).containsKey(setName)){
                        Double num1 = results.get(key).get(setName);
                        Double num2 = map.get(setName);
                        results.get(key).put(setName, Math.max(num1, num2));
                    }
                    else{
                        results.get(key).put(setName, map.get(setName));
                    }
                }

            }
        }
        return results;
    }
    public ArrayList<DefuzzifiedOutput>  Deffuzificztion (HashMap<String, HashMap<String, Double>> results){
        ArrayList<DefuzzifiedOutput> finalResult = new ArrayList<>();
        for (String key : results.keySet()) {
            if(!variables.containsKey(key) || variables.get(key).type.equals("IN"))
                continue;
            HashMap<String, Double> temp = results.get(key);
            Double sum = 0.0, weights = 0.0;
            Variable variable = variables.get(key);
            for (String setKey : temp.keySet()) {
                Double centroid = variable.GetCentroid(setKey);
                sum += centroid * temp.get(setKey);
                weights += temp.get(setKey);
            }
            Double crispValue = sum / weights;

            Double maxWeight = -Double.MAX_VALUE;
            String set="";

            for (String setKey : variable.sets.keySet()) {
                if(variable.sets.get(setKey).inRange(crispValue)){
                    Double weight = temp.get(setKey);
                    if(weight > maxWeight){
                        maxWeight = weight;
                        set = setKey;
                    }
                }
            }
            finalResult.add(new DefuzzifiedOutput(key, crispValue, set));
        }

        return finalResult;
    }
    public ArrayList<DefuzzifiedOutput> runSystem(HashMap<String, Double> input){
        ArrayList<DefuzzifiedOutput> finalResults = new ArrayList<>();
        HashMap<String, HashMap<String,Double>> membership = Fuzzification(input);
        Boolean isNotFinished = true;
        while(isNotFinished) {
            isNotFinished = false;
            HashMap<String, HashMap<String, Double>> inferenceResults = this.Inference(membership);

            int numOut = 0;
            for(String var:variables.keySet()){
                if(variables.get(var).type.equals("OUT")){
                    if(inferenceResults.containsKey(var)) {
                        numOut++;
                    }
                }
            }

            for(String var:inferenceResults.keySet()){
                HashMap<String, Double> temp = inferenceResults.get(var);
                for(String set:temp.keySet()) {
                    if(!membership.containsKey(var)){
                        membership.put(var, new HashMap<String, Double>());
                        membership.get(var).put(set, temp.get(set));
                        isNotFinished = true;

                    }else{
                        HashMap<String, Double> currMem = membership.get(var);
                        Double value = temp.get(set);
                        if(currMem.containsKey(set)){
                            Double curValue = currMem.get(set);
                            if(value > curValue){
                                isNotFinished = true;
                                curValue = value;
                                membership.get(var).put(set, curValue);
                            }
                        }else{
                            isNotFinished = true;
                            membership.get(var).put(set, value);
                        }
                    }

                }
            }

            if(numOut == numberOfOutVariables)
                break;
        }
        finalResults  = Deffuzificztion(membership);
        return finalResults;
    }

    public Boolean isInitialized(){
        if(variables.size()==0)
            return false;
        if(rules.size()==0)
            return false;
        for(String var:variables.keySet()){
            if(variables.get(var).sets.size()==0)
                return false;
        }
        return true;
    }

}
