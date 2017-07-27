package timeclient;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * 粘包
 * 使用手册解释理解：鉴于网络传输的不是packet，而是二进制的流，{@link TimeClientHandler}的做法是没办法真正确保接受的是
 * 4个字节（{@link TimeServerHandler}中发送时指定的是4字节）的数据，所以涉及到粘包
 *
 * author : Forrest
 * date : 2017/7/27.
 */
public class TimeClientHandlerDefragSolution1 extends ChannelHandlerAdapter {
    private ByteBuf buf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        //ChannelHandler生命周期函数，添加Handler时调用, SocketChannel.pipline().addLast()时调用
        buf = ctx.alloc().buffer(4); // (1)
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        //ChannelHandler生命周期函数，删除Handler时调用
        buf.release(); // (1) 有意思buf release伴随着client进程的终止
        buf = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg;
        buf.writeBytes(m); // (2)//累积收到的字节
        m.release();

        if (buf.readableBytes() >= 4) { // (3)
            long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
