import java.util.*;
public class Rule {
    ArrayList<HashMap<String,String>> LHS = new ArrayList<>();
    HashMap<String,String> RHS = new HashMap<>();
    public Rule(String rule){
        String[] arrOfStr = rule.split(" => ", 0);
        String lhs = arrOfStr[0];
        String rhs = arrOfStr[1];

        String[] strOr = lhs.split(" or ", 0);
        for (int i=0; i<strOr.length ; i++){
            HashMap<String,String> temp = new HashMap<>();
            String[] strAnd = strOr[i].split(" and ", 0);
            for (int j =0; j<strAnd.length; j++ ){
                String[] strSpace = strAnd[j].split(" ", 0);
                temp.put(strSpace[0],strSpace[1]);
            }
            LHS.add(temp);
        }

        String[] rhsStr = rhs.split(" ", 0);
        RHS.put(rhsStr[0], rhsStr[1]);
        strOr = rhs.split(" or ", 0);
        for (int i=0; i<strOr.length ; i++){
            String[] strAnd = strOr[i].split(" and ", 0);
            for (int j =0; j<strAnd.length; j++ ){
                String[] strSpace = strAnd[j].split(" ", 0);
                RHS.put(strSpace[0],strSpace[1]);
            }
        }
    }

    public HashMap<String, HashMap<String, Double>> Evaluate(HashMap<String, HashMap<String,Double>> memberships){
        Double result = -Double.MAX_VALUE;
        for(int i = 0; i < LHS.size(); i++){
            Double value = Double.MAX_VALUE;
            HashMap<String,String> map = LHS.get(i);
            for(String key:map.keySet()){
                if(!memberships.containsKey(key))
                    return new HashMap<String, HashMap<String, Double>>();
                String setKey = map.get(key);
                value = Math.min(value, memberships.get(key).get(setKey));
            }
            result = Math.max(result, value);
        }
        HashMap<String, HashMap<String, Double>> results = new HashMap<>();
        for(String key:RHS.keySet()){
            HashMap<String, Double> map = new HashMap<>();
            map.put(RHS.get(key), result);
            results.put(key, map);
        }
        return results;
    }
}
