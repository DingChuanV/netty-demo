package com.uin.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NormalMessageHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf in = (ByteBuf) msg;
    byte[] req = new byte[in.readableBytes()];
    in.readBytes(req); //把数据读到byte数组中
    String body = new String(req, StandardCharsets.UTF_8);
    log.info("服务器端收到消息：" + body);
    //写回数据
    ByteBuf resp = Unpooled.copiedBuffer(("receive message:" + body + " ").getBytes());
    ctx.write(resp);
    //ctx.write表示把消息再发送回客户端，但是仅仅是写到缓冲区，没有发送，flush才会真正写到网络上去
  }

  /**
   * channelReadComplete方法表示消息读完了的处理，writeAndFlush方法表示写入并发送消息
   *
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    //这里的逻辑就是所有的消息读取完毕了，在统一写回到客户端。Unpooled.EMPTY_BUFFER表示空消息，addListener(ChannelFutureListener.CLOSE)表示写完后，就关闭连接
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
  }

  /**
   * exceptionCaught方法就是发生异常的处理
   *
   * @param ctx
   * @param cause
   * @throws Exception
   */

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
