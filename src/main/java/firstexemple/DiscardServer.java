package firstexemple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import timeclient.TimeServerHandler;

/**
 * author : Forrest
 * date : 2017/7/13.
 */
public class DiscardServer {

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() {
        //�Ұ�NioEventLoopGroup���Ϊһ���̳߳أ����ܸ�ǿ����̳߳�
        //netty server��Ҫһ���߳�ר����accept�ͻ�������,boss���Ǹ������
        EventLoopGroup boss = new NioEventLoopGroup();
        //worker����˼����Ǹɻ�ģ���Ҫ�Ǵ��䣬��boss����һ�����ӣ����Ͱ�����ע���worker, worker֮��͸����ȡ�����ϵ����ݣ���Ȼ��ֹ�Ƕ�����
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //bootstrap��һ�������࣬�����ǽ���server
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    //NioServerSocketChannel��ʱֻ�Ӵ������������ʵ����һ��Channel�����յ���������
                    .channel(NioServerSocketChannel.class)
                    //���handler��������������Ϣ
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new DiscardServerHandler());
                            /**���Ұ�{@link TimeServerHandler} (�ڽ�������ʱֱ�ӷ��ظ��ͻ���ʱ��Ȼ��Ͽ����ӣ������
                            ���� {@link DiscardServer}, ������{@link timeclient.TimeClient}
                            ��server��������˲�䣬���Ӿͱ��ر��ˣ��ƺ���û�п���DiscardServerHandler�ĸ���
                             */
//                                    .addLast(new TimeServerHandler());

                        }
                    })
                    //����Channel����
                    //option����Խ��ܿͻ������ӵ�channel,
                                    .option(ChannelOption.SO_BACKLOG, 128)
                    //childOption��server���ܿͻ������Ӻ�ע�ᵽworker��channel
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            //ChannelFuture.channel()��������ָ����NioServerSocketChannel��closeFuture()��������CloseFuture
            //sync()�������������������߳�
            //�����sync�������sync����һ���£���ʱ��Դ�벻��̫��ȫ�����������������
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //��һ��ʱ����û�н��ܵ��µ�����͹ر�����,Ĭ����2s,����ڼ����µ���������������¼���
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    //ͨ��command : telnet localhost 8080�Ϳ��Բ�����
    public static void main(String[] args) {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new DiscardServer(port).run();

    }
}
