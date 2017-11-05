import java.io.*;


public class SecondPhase {

    private static final int FILES_NUMBER = 20;
    private static final int BUFFER_NUMBER = 21;  //21个缓冲块，其中20个输入缓冲块，1个输出缓冲块
    private static final int BUFFER_RECORD_NUMBER = 40; //一个缓冲块4096B，可以存放4096/100个记录
                                                                //--->修改第二阶段算法时，缓冲块大小为1M,1M/100=10485, 取为10000

    private static String buffers[][] = new String[BUFFER_NUMBER][BUFFER_RECORD_NUMBER];

    private static void writeToDiskFromOutputBuffer(FileOutputStream out, String[] buffer, int outputBufferIndex) throws IOException {
        StringBuffer sb = new StringBuffer();
        for(int j=0;j<outputBufferIndex;j++){
            sb.append(buffer[j]+"\n");
        }
        out.write(sb.toString().getBytes("utf-8"));

    }

    private static void writeToDiskFromTheLastInputBuffer(FileOutputStream out, String[] buffer, int i) throws IOException {
        StringBuffer sb = new StringBuffer();
        for(int j=i;j<BUFFER_RECORD_NUMBER;j++){
            sb.append(buffer[j]+"\n");
        }
        out.write(sb.toString().getBytes("utf-8"));
    }

    private static void readToInputBuffer(int i, BufferedReader br) throws IOException {
        String temp = null;
        for (int k = 0; k < BUFFER_RECORD_NUMBER ; k++) {//从文件输入流br[i]中读取（第i个文件中的）一个block的记录到输入缓冲区buffer[i]
            temp = br.readLine();
            if (temp != null) {
                buffers[i][k] = temp;
            }else{
                buffers[i] = null;
                break;
            }

        }
    }

    private static void BlockReader() throws IOException {
        File file=new File("file1.txt");//输出文件
        if(!file.exists())
            file.createNewFile();
        FileOutputStream out = new FileOutputStream(file,true);//一个输出流
        BufferedReader[] br = new BufferedReader[BUFFER_NUMBER-1];//20个 输入流

        //初始化输入缓存块
        for(int i=0; i< FILES_NUMBER; i++) {
            br[i] = new BufferedReader(new FileReader(new File("f" + i  + ".txt")));
            String temp = null;
            for (int k = 0; k < BUFFER_RECORD_NUMBER; k++) {//从文件输入流br[i]中读取（第i个文件中的）一个block的记录到输入缓冲区buffer[i]
                temp = br[i].readLine();
                buffers[i][k] = temp;
            }
        }

        int minIndex = 0;	//记录20个缓冲输入块的第一个元素中最小的记录的索引
        int index[] = new int[20]; //用20个整数记录20个缓冲块当前元素的下标，初始值为0
        int outputBufferIndex = 0; //记录输出缓冲区的第一个为空的索引
        int availableBufferNum = 20;

        while(true){
            minIndex = 0;
            while(buffers[minIndex] == null){	//从第一个不为null的缓冲块开始
                minIndex++;
            }
            for(int i  = minIndex; i < FILES_NUMBER && availableBufferNum>1; i++){
                if(index[i] == -1){ //index[i]=-1时，说明对应的文件已读完，不考虑此缓冲块
                    continue;
                }

                if (index[i] >= BUFFER_RECORD_NUMBER) {    //缓冲块读完，重新从文件读入后面的数据
                    readToInputBuffer(i, br[i]);
                    index[i] = 0;
                    if (buffers[i] == null) { //对应的文件已读完
                        availableBufferNum--;
                        index[i] = -1;
                        System.out.println("文件aa" + i + "结束" + availableBufferNum);
                        if (i == minIndex){
                            // TODO: 2017/11/3 将最小指针指向不为空的其他缓冲块
                            for (int t=0;t<19;t++){
                                if (index[t]!=-1){
                                    minIndex = t;
                                    break;
                                }
                            }
                        }
                        continue;
                    }
                }

                if (RecordToInt(buffers[i][index[i]].substring(0, 6)) <= RecordToInt(buffers[minIndex][index[minIndex]].substring(0, 6))) {
                    minIndex = i;
                }

            }

            if(availableBufferNum == 1){ //如果只剩最后一个输入缓冲块，
                writeToDiskFromOutputBuffer(out,buffers[20],outputBufferIndex);	//先将输出缓冲块中的数据写到磁盘File文件中
                buffers[20]=null;  //回收输出缓冲区内从空间
                writeToDiskFromTheLastInputBuffer(out,buffers[minIndex],index[minIndex]);  //再把最后一个输入缓冲块的剩余数据(从index[minIndex]处开始)写到磁盘
                break;
            }

            buffers[20][outputBufferIndex++]=buffers[minIndex][index[minIndex]++];
            if(outputBufferIndex>=BUFFER_RECORD_NUMBER){	//输出缓冲区满时，将输出缓冲区中的数据写到磁盘File文件中
                writeToDiskFromOutputBuffer(out,buffers[20],outputBufferIndex);
                outputBufferIndex=0;
            }
        }
        out.close();//输出完成关闭文件
        System.out.println("生成排序后文件成功"+availableBufferNum);
    }

    private static int RecordToInt(String s) {
        return  Integer.parseInt(s);
    }


    public static void main(String[] args) throws IOException {
        long startTime ;
        long endTime ;

        startTime = System.currentTimeMillis();
        BlockReader();
        endTime = System.currentTimeMillis();
        System.out.println(">>>>>>>>>Merge(change block to cylinder) sort time: "+(endTime-startTime)+" ms");
    }


}
