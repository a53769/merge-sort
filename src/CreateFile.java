import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class CreateFile {

    private static final int RECORD_NUMBER =10000000;
    private static final int FILES_NUMBER = 20;
    private static final int SUB_RECORD_NUMBER = 500000; //RECORD_NUMBER/FILES_NUMBER
    private static final int RECORD_SIZE = 100;
    private static final int RANDOM_SCOPE = 1000000;
    private static final int INTLENGTH = 6;

    public static String generateZeroString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append('0');
        }
        return sb.toString();
    }

    public static String toFixdLengthString(long num) {
        StringBuffer sb = new StringBuffer();
        String strNum = String.valueOf(num);
        if (INTLENGTH - strNum.length() >= 0) {
            sb.append(generateZeroString(INTLENGTH - strNum.length()));
        } else {
            throw new RuntimeException("将数字" + num + "转化为长度为" + INTLENGTH
                    + "的字符串发生异常！");
        }
        sb.append(strNum);
        return sb.toString();
    }

    public static void StringBuffer() throws IOException{
        File file=new File("file0.txt");
        if(!file.exists())
            file.createNewFile();
        FileOutputStream out=new FileOutputStream(file,true);

        for(int i=0;i<FILES_NUMBER;i++){
            StringBuffer sb=new StringBuffer();
            for(int j=0;j<SUB_RECORD_NUMBER;j++){
                Random random = new Random();
                int A = Math.abs(random.nextInt())%RANDOM_SCOPE;
                sb.append(toFixdLengthString(A)+":--------------------------------------------------------------------------------------------\n");
            }
            out.write(sb.toString().getBytes("utf-8"));
        }
        out.close();
    }

    public static void main(String[] args) throws IOException {
        long startTime ;
        long endTime ;
        startTime = System.currentTimeMillis();
        StringBuffer();
        endTime = System.currentTimeMillis();
        System.out.println(">>>>>>>>>Generate file0 time: "+(endTime-startTime)+" ms");


    }
}
