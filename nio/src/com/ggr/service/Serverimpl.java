package com.ggr.service;

import com.ggr.ServerConfigUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;

/**
 * Created by GuiRunning on 2017/5/15.
 */
public class Serverimpl implements Server {

    private boolean flag=true;//服务器启动停止控制符

    @Override
    public void start() {

        Selector selector = null;//用于检测所有channel的监听器

        ServerSocketChannel server=null;//服务器实例

        ServerSocket sc=null;

        try {

            selector = Selector.open();//获得selector实例

            server= ServerSocketChannel.open();//开启服务端口

            sc = server.socket();//获取对外socket服务

            Charset charset = Charset.forName("utf-8");

            sc.bind(new InetSocketAddress("127.0.0.1",Integer.parseInt(ServerConfigUtil.getConfig("port"))));//绑定socket服务端口,默认ip为本地ip地址

            server.configureBlocking(false);//采用非堵塞的方式工作

            server.register(selector, SelectionKey.OP_ACCEPT);//注册selector

            while(selector.select()>0){

                //一次处理所有已选择的SelectionKey
                for (SelectionKey sk:selector.selectedKeys()) {

                    selector.selectedKeys().remove(sk);//移除已处理的SelectionKey

                    //如果是刚连接上来的，包含连接请求
                    if(sk.isAcceptable()){

                        SocketChannel socketChannel = server.accept(); //接受连接请求

                        socketChannel.configureBlocking(false); //设置非堵塞

                        socketChannel.register(selector,SelectionKey.OP_READ); //注册

                        sk.interestOps(SelectionKey.OP_ACCEPT);//将状态设置为等待接收信息状态

                    }

                    //如果有需要都的信息,就打印到控制台
                    if(sk.isReadable()){

                            SocketChannel socketChannel = (SocketChannel)sk.channel(); //获取通道

                            ByteBuffer b = ByteBuffer.allocate(1024);//开辟大小

                            String content = null;

                            while(socketChannel.read(b)!=-1){

                                b.flip();//清空

                                content+=charset.decode(b);
                            }
                            System.out.println("通道内容"+content);//打印通道内容

                            sk.interestOps(SelectionKey.OP_READ);//准备下一次读

                        //如果通道内容不为空,就广播到其他通道上去
                        if(content.length()>0){
                            for (SelectionKey key:selector.selectedKeys()) {
                                Channel channel = key.channel();
                                if(channel instanceof SocketChannel){
                                    SocketChannel target = (SocketChannel) channel;
                                    target.write(charset.encode(content));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;

    }

    public static void main(String[] args){
        new Serverimpl().start();
    }
}
