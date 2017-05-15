package com.ggr.service;

import com.ggr.ServerConfigUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;

/**
 * Created by GuiRunning on 2017/5/15.
 */
public class ClientServer {

    private Charset charset = Charset.forName("utf-8");//编码格式

    private Selector selector = null;//监听器

    private SocketChannel socketChannel=null;


    public void init(){
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.bind(new InetSocketAddress(30000));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            new ClientThread().start();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String s=null;
            while((s=br.readLine())!=null){
                socketChannel.write(charset.encode(s));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientThread extends Thread {
        @Override
        public void run() {
            try {
                while (selector.select() > 0) {

                    //一次处理所有已选择的SelectionKey
                    for (SelectionKey sk : selector.selectedKeys()) {

                        selector.selectedKeys().remove(sk);//移除已处理的SelectionKey
                        //如果有需要都的信息,就打印到控制台
                        if (sk.isReadable()) {

                            SocketChannel socketChannel = (SocketChannel) sk.channel(); //获取通道

                            ByteBuffer b = ByteBuffer.allocate(1024);//开辟大小

                            String content = null;

                            while (socketChannel.read(b) != -1) {

                                b.flip();//清空

                                content += charset.decode(b);
                            }
                            System.out.println("通道内容" + content);//打印通道内容

                            sk.interestOps(SelectionKey.OP_READ);//准备下一次读

                            //如果通道内容不为空,就广播到其他通道上去
                            if (content.length() > 0) {
                                for (SelectionKey key : selector.selectedKeys()) {
                                    Channel channel = key.channel();
                                    if (channel instanceof SocketChannel) {
                                        SocketChannel target = (SocketChannel) channel;
                                        target.write(charset.encode(content));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

    }
}

public static void main(String[] args){
    new ClientServer().init();
}
}



