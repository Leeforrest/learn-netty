package firstexemple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.util.TreeMap;

/**
 * author : Forrest
 * date : 2017/7/13.
 */
public class DiscardServerHandler extends ChannelHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf byteBuf = (ByteBuf)msg;
            ctx.write(byteBuf);
            //注意这里多次write(byteBuf)时需要使用retain方法，因为byteBuf的pos, index的原因吧
            //flush方法后会调用byteBuf release
//            byteBuf.retain();
//            ctx.write(byteBuf);
//            byteBuf.retain();
//            ctx.write(byteBuf);
            ctx.flush();

            testAsyncronous(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 话说netty所有操作都是异步的，那么连续向客户端发送连续的数据，客户端接受到的数据应该不一定是连续的，但是可能我的测试环境过于简单，所以客户端的数字是连续的
     *
     * @param ctx
     */
    private void testAsyncronous(ChannelHandlerContext ctx) {
            for (int i = 1; i < 58; i++) {
                ByteBuf byteBuf = ctx.alloc().buffer(4);
                byteBuf.writeInt(i);
                ChannelFuture future = ctx.writeAndFlush(byteBuf);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
