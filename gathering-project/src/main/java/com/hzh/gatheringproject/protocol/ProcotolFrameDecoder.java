package com.hzh.gatheringproject.protocol;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 对LengthFieldBasedFrameDecoder进行封装，使得解码器在外边进行初始化
 */
public class ProcotolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProcotolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProcotolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
