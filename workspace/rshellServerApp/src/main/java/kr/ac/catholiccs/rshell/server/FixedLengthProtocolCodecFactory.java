package kr.ac.catholiccs.rshell.server;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

public class FixedLengthProtocolCodecFactory implements ProtocolCodecFactory {

    private final Charset charset;
    private final int messageLength;

    public FixedLengthProtocolCodecFactory(Charset charset, int messageLength) {
        this.charset = charset;
        this.messageLength = messageLength;
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return new Encoder(charset, messageLength);
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return new Decoder(charset, messageLength);
    }

    private static class Encoder implements ProtocolEncoder {

        private final Charset charset;
        private final int messageLength;

        public Encoder(Charset charset, int messageLength) {
            this.charset = charset;
            this.messageLength = messageLength;
        }

        @Override
        public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
            if (!(message instanceof String)) {
                throw new IllegalArgumentException("Only String messages are supported");
            }

            String strMessage = (String) message;
            byte[] messageBytes = strMessage.getBytes(charset);
            String lengthString = String.format("%0"+messageLength+"d", messageBytes.length);

            IoBuffer buffer = IoBuffer.allocate(messageBytes.length + lengthString.length());
            buffer.put(lengthString.getBytes(charset));
            buffer.put(messageBytes);
            buffer.flip();
            out.write(buffer);
        }

        @Override
        public void dispose(IoSession session) throws Exception {
            // Nothing to dispose
        }
    }

    private static class Decoder implements ProtocolDecoder {

        private final Charset charset;
        private final int messageLength;

        public Decoder(Charset charset, int messageLength) {
            this.charset = charset;
            this.messageLength = messageLength;
        }

        @Override
        public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
            // 첫 6자리를 읽어 전문의 길이를 가져옴
            byte[] lengthBytes = new byte[6];
            in.get(lengthBytes);
            int length = Integer.parseInt(new String(lengthBytes, charset));

            // 남은 데이터가 전문의 길이보다 짧으면 더 이상 처리하지 않음
            if (length > in.remaining()) {
                return;
            }

            // 전문의 길이만큼 데이터를 읽어서 ProtocolDecoderOutput에 추가
            byte[] messageBytes = new byte[length];
            in.get(messageBytes);
            byte[] crlf = new byte[in.remaining()];
            in.get(crlf); // 전문의 길이로 지정된 범위의 데이터는 버림
            String message = new String(messageBytes, charset);
            out.write(message);
        }

        @Override
        public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
            // Nothing to finish
        }

        @Override
        public void dispose(IoSession session) throws Exception {
            // Nothing to dispose
        }
    }
}

