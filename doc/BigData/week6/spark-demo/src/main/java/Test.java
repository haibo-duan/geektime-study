import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Test {

    public static void main(String[] args) throws Exception{
        File file = new File("D:\\test\\index.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String line = null;
        while ((line = bufferedReader.readLine())!= null) {
            System.out.println(line);
            String [] array1 = line.split("\\.");
            System.out.println(array1[1]);
            String result1 = array1[1].replaceAll("\"", "");
            System.out.println(result1);
        }
    }

}
