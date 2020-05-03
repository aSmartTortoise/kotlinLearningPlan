public class CosoleParameterInJava {
    public static void main(String[] args) {
        for (String arg : args) {
            String[] splits = arg.split("_");
            for (String split : splits) {
                System.out.print(split + " ");
            }
        }
    }
}
