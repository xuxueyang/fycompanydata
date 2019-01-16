package Utils;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MapBufferUtil {
    public static void encodeMap(Object map,String filePath) throws IOException{
        String listString = JSON.toJSONString(map, true);
        writeFile(filePath, listString);

    }
    public static Object decodeMap(String filePath){

        String listString2= null;
        try {
            listString2 = readFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<String,Object>();
        }
        return JSON.parseObject(listString2);
    }
    /**
     * 将字符串写如文件
     *
     * @param filePath 文件所在路径
     * @param input 字符串
     * @throws IOException 异常
     */
    private static void writeFile(String filePath, String input) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(input);
        out.println();
        fw.close();
        out.close();
    }
    /**
     * 读取文本文件内容
     */
    private static String readFile(String filePath) throws Exception {
        StringBuffer sb = new StringBuffer();
        readToBuffer(sb, filePath);
        return sb.toString();
    }
    /**
     * 将文本文件中的内容读入到buffer中
     *
     */
    private static void readToBuffer(StringBuffer buffer, String filePath) throws Exception {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
//            line = reader.readLine(); // 读取下一行
        }
        // 只记录最后一个
//        buffer = new StringBuffer(line);
        reader.close();
        is.close();
    }
}
