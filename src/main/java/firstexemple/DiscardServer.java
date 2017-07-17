package firstexemple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
            serverBootstrap.group(boss)
                    //NioServerSocketChannel��ʱֻ�Ӵ������������ʵ����һ��Channel�����յ���������
                    .channel(NioServerSocketChannel.class)
                    //���handler��������������Ϣ
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    //����Channel����
                    //option����Խ��ܿͻ������ӵ�channel,
                                    .option(ChannelOption.SO_BACKLOG, 128)
                    //childOption��server���ܿͻ������Ӻ�ע�ᵽworker��channel
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
