import java.io.*;

public class FirstPhase {

    private static final int SUB_RECORD_NUMBER = 500000; //RECORD_NUMBER/FILES_NUMBER

    private static final String[] record = new String[SUB_RECORD_NUMBER];

    private static void MemoryFile(int count) throws IOException {
        File file = new File("f" + count + ".txt");
        if(!file.exists())
            file.createNewFile();
        FileOutputStream out = new FileOutputStream(file,true);
        StringBuffer sb = new StringBuffer();
        for (int i =0 ; i<SUB_RECORD_NUMBER;i++){
            sb.append(record[i]+"\n");
        }
        out.write(sb.toString().getBytes("utf-8"));
        out.close();
//        System.out.println("生成第" + count + "个子文件成功");
    }

    //读入内存
    public static void BufferedReader(String path) throws IOException {
        File file = new File(path);
        if(!file.exists()||file.isDirectory())
            throw new FileNotFoundException();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String temp = null;
        temp = br.readLine();
        int count = 0;
        while(temp != null){
            int i = 0;
            while (i<SUB_RECORD_NUMBER) {
                record[i] = temp;
                temp = br.readLine();
                i++;
            }
            quickSort(record,0,SUB_RECORD_NUMBER-1);
            MemoryFile(count);
            count++;
        }
    }

    //快速排序，用于给每个子文件排序
    public static void quickSort(String[] a, int left, int right){
        if(left>=right){
            return ;
        }
        int i = left;
        int j = right;
        String key = a[i];
        while(i<j){
            while(i<j && RecordToInt(a[j].substring(0,6))>= RecordToInt(key.substring(0,6))){
                j--;
            }
            if(i<j){
                a[i]=a[j];
                i++;
            }
            while(i<j && RecordToInt(a[i].substring(0,6))<=RecordToInt(key.substring(0,6))){
                i++;
            }
            if(i<j){
                a[j]=a[i];
                j--;
            }
        }
        a[i]=key;
        quickSort(a, left, i-1);
        quickSort(a,i+1,right);

    }

    private static int RecordToInt(String s) {
        return  Integer.parseInt(s);
    }

    public static void main(String[] args) throws IOException {

        long startTime ;
        long endTime ;
        startTime = System.currentTimeMillis();
        BufferedReader("file0.txt");
        endTime = System.currentTimeMillis();
        System.out.println(">>>>>>>>>Generate 20 files and sort time: "+(endTime-startTime)+" ms");

    }
}
