package boq.utils.net;

import static boq.utils.net.StreamHelper.readVLI;
import static boq.utils.net.StreamHelper.writeVLI;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class BlockHelper {

    public interface BlockReader<T> {
        public T read(InputStream stream) throws IOException;
    }

    public static abstract class DataBlockReader<T> implements BlockReader<T> {
        @Override
        public final T read(InputStream stream) throws IOException {
            DataInput input = new DataInputStream(stream);
            return read(input);
        }

        public abstract T read(DataInput input) throws IOException;
    }

    public static <T> T readBlock(DataInput input, BlockReader<T> reader) throws IOException {
        boolean compressed = input.readBoolean();
        return readBlock(input, compressed, reader);
    }

    public static <T> T readBlock(DataInput input, boolean compressed, BlockReader<T> reader) throws IOException {
        int size = readVLI(input);
        if (size <= 0)
            return null;

        byte[] bytes = new byte[size];
        input.readFully(bytes);

        InputStream buffer = new ByteArrayInputStream(bytes);
        InputStream tmp = buffer;
        if (compressed)
            tmp = new GZIPInputStream(tmp);

        T result = reader.read(tmp);
        tmp.close();
        return result;
    }

    public interface BlockWriter {
        public void write(OutputStream stream) throws IOException;
    }

    public static abstract class DataBlockWriter implements BlockWriter {
        @Override
        public final void write(OutputStream stream) throws IOException {
            DataOutput output = new DataOutputStream(stream);
            write(output);
        }

        public abstract void write(DataOutput output) throws IOException;
    }

    public static void writeBlock(DataOutput output, BlockWriter input) throws IOException {
        writeBlock(output, false, input);
    }

    public static void writeBlock(DataOutput output, boolean compressed, BlockWriter input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        OutputStream tmp = buffer;
        if (compressed)
            tmp = new GZIPOutputStream(tmp);
        input.write(tmp);
        tmp.close();
        byte[] bytes = buffer.toByteArray();
        writeVLI(output, bytes.length);
        output.write(bytes);
    }

    public static void writeNullBlock(DataOutput output) throws IOException {
        writeVLI(output, 0);
    }
}
