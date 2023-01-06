public class DefuzzifiedOutput {
    String varName;
    Double crispValue;
    String predictedSet;

    public DefuzzifiedOutput(String varName, Double crispValue, String predictedSet){
        this.crispValue = crispValue;
        this.predictedSet = predictedSet;
        this.varName = varName;
    }

}
