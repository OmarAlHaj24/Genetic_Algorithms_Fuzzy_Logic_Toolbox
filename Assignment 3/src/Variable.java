import java.util.HashMap;
public class Variable {
    String name;
    String type;
    HashMap<String, FuzzySet> sets;
    Double low;
    Double high;
    public Variable(String name, String type,Double low, Double high){
        this.name = name ;
        this.type = type;
        this.low = low;
        this.high = high;
        sets = new HashMap<>();
    }
    public void AddSet(FuzzySet set){
        sets.put(set.name, set);
    }
    public HashMap<String,Double> CalcMembership (Double input){
        HashMap<String,Double> map = new HashMap<>();
        for (String setName: sets.keySet()){
            Double membership = sets.get(setName).Intersection(input);
            map.put(setName, membership);
        }
        return map;
    }
    public Double GetCentroid (String name){
        return sets.get(name).centroid;
    }
}