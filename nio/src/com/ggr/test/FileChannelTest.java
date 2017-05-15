package com.ggr.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Created by GuiRunning on 2017/5/15.
 */
public class FileChannelTest {
    public static void main(String[] args) throws FileNotFoundException {

        RandomAccessFile file = new RandomAccessFile("a.txt","rw");

        FileChannel fileChannel = file.getChannel();//获取文件通道

        ByteBuffer buffer = ByteBuffer.allocate(1024);//缓存大小

        Charset charset = Charset.forName("utf-8");//编解码

        int i=-1;
        try {
             while((i=fileChannel.read(buffer))!=-1){

                 System.out.println("Read " + i);

                 buffer.flip();//首先读取数据到Buffer，然后反转Buffer,接着再从Buffer中读取数据。

                 while(buffer.hasRemaining()){

                     System.out.print(charset.decode(buffer));

                 }
                 buffer.clear();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
