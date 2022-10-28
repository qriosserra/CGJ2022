package fr.lost_in_dark.game.utils;

import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.system.MemoryUtil.memSlice;

public final class IOUtil {

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            try (
                    InputStream source = IOUtil.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }

        buffer.flip();
        return memSlice(buffer);
    }

    public static String readStreamFile(String resource) {
        StringBuilder builder = new StringBuilder();

        InputStream source = IOUtil.class.getClassLoader().getResourceAsStream(resource);
        if (source == null) {
            File file = new File(resource);
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            return builder.toString();
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

}