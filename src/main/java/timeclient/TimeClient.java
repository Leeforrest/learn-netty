package timeclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * author : Forrest
 * date : 2017/7/26.
 */
public class TimeClient {
    public static void main(String[] args) {
        String host = "";
        int port = 8080;
        EventLoopGroup workgroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workgroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            socketChannel.pipeline().addLast(new TimeClientHandler());//未使用粘包的handler
                            socketChannel.pipeline().addLast(new TimeClientHandlerDefragSolution1());//使用粘包解决方案1
                        }
                    });
            ChannelFuture future = b.connect(host, port).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e) {
            workgroup.shutdownGracefully();
        }
    }
}
