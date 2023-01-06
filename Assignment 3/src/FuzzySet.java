import java.util.ArrayList;

public class FuzzySet {
    String name;
    String shape;
    Double centroid;
    ArrayList<Double> Points;
    public FuzzySet(String name, String shape,ArrayList<Double> points ) {
        this.name = name;
        this.shape = shape;
        this.Points = points;
        centroid = 0.0;
        for (int i=0; i<Points.size();i++){
            centroid+=points.get(i);
        }
        centroid /= points.size();
    }
    public Double Equation(Double input, Double x1,Double x2,Double y1,Double y2 ){
        Double slope = (y2-y1)/(x2-x1);
        Double intercept = y1 - (slope*x1);
        return (slope * input) + intercept;
    }

    public Double Intersection(Double input){
        for (int i =0; i< Points.size()-1 ; i++){
            if (input >= Points.get(i) && input <= Points.get(i+1)){
                Double y1 = 1.0, y2 =1.0;
                if (i==0){
                    y1 = 0.0;
                }
                if (i+1 == Points.size()-1){
                    y2 = 0.0;
                }
                return Equation(input,Points.get(i), Points.get(i+1), y1, y2);
            }
        }
        return 0.0;
    }

    public Boolean inRange(Double crispValue){
        for(int i =0; i< Points.size()-1; i++){
            if(crispValue <= Points.get(i+1) && crispValue >= Points.get(i))
                return true;
        }
        return false;
    }
}
