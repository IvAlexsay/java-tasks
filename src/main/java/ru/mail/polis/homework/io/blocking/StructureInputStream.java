package ru.mail.polis.homework.io.blocking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Вам нужно реализовать StructureInputStream, который умеет читать данные из файла.
 * Читать поля нужно ручками, с помощью массива байт и методов {@link #read()}, {@link #read(byte[])} и так далее
 * 3 тугрика
 */
public class StructureInputStream extends FileInputStream {

    private Structure[] structures = new Structure[0];

    public StructureInputStream(File fileName) throws FileNotFoundException {
        super(fileName);
    }

    /**
     * Метод должен вернуть следующую прочитанную структуру.
     * Если структур в файле больше нет, то вернуть null
     */
    public Structure readStructure() throws IOException {
        Structure structure = new Structure();
        if (available() == 0) {
            return null;
        }
        readId(structure);
        readName(structure);
        readCoeff(structure);
        readFlags(structure);
        readParam(structure);
        readSubStructures(structure);
        structures = Arrays.copyOf(structures, structures.length + 1);
        structures[structures.length - 1] = structure;
        return structure;
    }

    private long bytesToLong(byte[] bytes) {
        return ((long) bytes[7] << 56)
                | ((long) bytes[6] & 0xff) << 48
                | ((long) bytes[5] & 0xff) << 40
                | ((long) bytes[4] & 0xff) << 32
                | ((long) bytes[3] & 0xff) << 24
                | ((long) bytes[2] & 0xff) << 16
                | ((long) bytes[1] & 0xff) << 8
                | ((long) bytes[0] & 0xff);
    }

    private int bytesToInt(byte[] bytes) {
        return ((int) bytes[3] << 24)
                | ((int) bytes[2] & 0xff) << 16
                | ((int) bytes[1] & 0xff) << 8
                | ((int) bytes[0] & 0xff);
    }

    public Long readLong() throws IOException {
        byte[] buffer = new byte[8];
        read(buffer);
        return bytesToLong(buffer);
    }

    public String readString(int length) throws IOException {
        byte[] buffer = new byte[length];
        read(buffer);
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public int readInt() throws IOException {
        byte[] buffer = new byte[4];
        read(buffer);
        return bytesToInt(buffer);
    }

    private void readId(Structure structure) throws IOException {
        structure.setId(readLong());
    }

    private void readName(Structure structure) throws IOException {
        int nameLength = readInt();
        if (nameLength == -1) {
            structure.setName(null);
            return;
        }
        structure.setName(readString(nameLength));
    }

    private void readCoeff(Structure structure) throws IOException {
        structure.setCoeff(Float.intBitsToFloat(readInt()));
    }

    private void readFlags(Structure structure) throws IOException {
        byte flags = (byte) read();
        structure.setFlag1((flags & 8) >> 3 == 1);
        structure.setFlag2((flags & 4) >> 2 == 1);
        structure.setFlag3((flags & 2) >> 1 == 1);
        structure.setFlag4((flags & 1) == 1);
    }

    private void readParam(Structure structure) throws IOException {
        structure.setParam((byte) read());
    }

    private void readSubStructures(Structure structure) throws IOException {
        int subStructuresCount = readInt();
        if (subStructuresCount == -1) {
            return;
        }
        SubStructure[] subStructures = new SubStructure[subStructuresCount];
        for (int i = 0; i < subStructuresCount; i++) {
            int id = readInt();
            int nameLengthSub = readInt();
            String name = readString(nameLengthSub);
            boolean flag = ((byte) read() == 1);
            double score = Double.longBitsToDouble(readLong());
            subStructures[i] = new SubStructure(id, name, flag, score);
        }
        structure.setSubStructures(subStructures);
    }

    /**
     * Метод должен вернуть все структуры, которые есть в файле.
     * Если файл уже прочитан, но возвращается полный массив.
     */
    public Structure[] readStructures() throws IOException {
        Structure structure;
        do {
            structure = readStructure();
        } while (structure != null);
        return structures;
    }
}
