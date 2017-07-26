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
        //我把NioEventLoopGroup理解为一个线程池，功能更强大的线程池
        //netty server需要一个线程专门来accept客户端连接,boss就是干这个的
        EventLoopGroup boss = new NioEventLoopGroup();
        //worker顾名思义就是干活的，主要是传输，当boss接收一个连接，他就把连接注册给worker, worker之后就负责读取连接上的数据，当然不止是读操作
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //bootstrap是一个辅助类，帮我们建立server
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    //NioServerSocketChannel暂时只接触到这个，它会实例化一个Channel来接收到来的连接
                    .channel(NioServerSocketChannel.class)
                    //添加handler用来处理网络消息
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new DiscardServerHandler());
                            /**当我把{@link TimeServerHandler} (在建立连接时直接返回给客户端时间然后断开连接）加入后，
                            启动 {@link DiscardServer}, 在启动{@link timeclient.TimeClient}
                            与server建立连接瞬间，连接就被关闭了，似乎并没有考虑DiscardServerHandler的感受
                             */
//                                    .addLast(new TimeServerHandler());

                        }
                    })
                    //设置Channel参数
                    //option是针对接受客户端连接的channel,
                                    .option(ChannelOption.SO_BACKLOG, 128)
                    //childOption是server接受客户端连接后，注册到worker的channel
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            //ChannelFuture.channel()返回上文指定的NioServerSocketChannel，closeFuture()方法返回CloseFuture
            //sync()方法阻塞将会阻塞主线程
            //上面的sync和下面的sync不是一回事，暂时看源码不能太完全消化，后续继续添加
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //在一定时间内没有接受到新的请求就关闭连接,默认是2s,如果期间有新的请求过来，会重新计算
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    //通过command : telnet localhost 8080就可以测试了
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
