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
     * ���ַ���д���ļ�
     *
     * @param filePath �ļ�����·��
     * @param input �ַ���
     * @throws IOException �쳣
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
     * ��ȡ�ı��ļ�����
     */
    private static String readFile(String filePath) throws Exception {
        StringBuffer sb = new StringBuffer();
        readToBuffer(sb, filePath);
        return sb.toString();
    }
    /**
     * ���ı��ļ��е����ݶ��뵽buffer��
     *
     */
    private static void readToBuffer(StringBuffer buffer, String filePath) throws Exception {
        InputStream is = new FileInputStream(filePath);
        String line; // ��������ÿ�ж�ȡ������
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // ��ȡ��һ��
        while (line != null) { // ��� line Ϊ��˵��������
            buffer.append(line); // ��������������ӵ� buffer ��
            buffer.append("\n"); // ��ӻ��з�
//            line = reader.readLine(); // ��ȡ��һ��
        }
        // ֻ��¼���һ��
//        buffer = new StringBuffer(line);
        reader.close();
        is.close();
    }
}
