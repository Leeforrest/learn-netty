package timeclient;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * ճ��
 * ʹ���ֲ������⣺�������紫��Ĳ���packet�����Ƕ����Ƶ�����{@link TimeClientHandler}��������û�취����ȷ�����ܵ���
 * 4���ֽڣ�{@link TimeServerHandler}�з���ʱָ������4�ֽڣ������ݣ������漰��ճ��
 *
 * author : Forrest
 * date : 2017/7/27.
 */
public class TimeClientHandlerDefragSolution1 extends ChannelHandlerAdapter {
    private ByteBuf buf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        //ChannelHandler�������ں��������Handlerʱ����, SocketChannel.pipline().addLast()ʱ����
        buf = ctx.alloc().buffer(4); // (1)
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        //ChannelHandler�������ں�����ɾ��Handlerʱ����
        buf.release(); // (1) ����˼buf release������client���̵���ֹ
        buf = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf) msg;
        buf.writeBytes(m); // (2)//�ۻ��յ����ֽ�
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
