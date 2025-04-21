package dev.akyawen.boshyrpc.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RC4 {
	private byte[] S = new byte[256];
	private int x = 0;
	private int y = 0;

	public RC4(byte[] key) {
		init(key);
	}

	private void init(byte[] key) {
		int keyLength = key.length;

		for (int i = 0; i < 256; i++) {
			S[i] = (byte) i;
		}

		int j = 0;
		for (int i = 0; i < 256; i++) {
			j = (j + S[i] + key[i % keyLength]) & 0xFF;
			swap(S, i, j);
		}
	}

	private byte getNextByte() {
		x = (x + 1) & 0xFF;
		y = (y + S[x]) & 0xFF;

		swap(S, x, y);
		return S[(S[x] + S[y]) & 0xFF];
	}

	public byte[] crypt(byte[] data) {
		byte[] result = new byte[data.length];

		for (int i = 0; i < data.length; i++) {
			result[i] = (byte) (data[i] ^ getNextByte());
		}

		return result;
	}

	private void swap(byte[] array, int i, int j) {
		byte temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	public String processFile(Path inputFilePath) throws IOException {
		byte[] fileData = Files.readAllBytes(inputFilePath);
		byte[] processedData = crypt(fileData);
		return new String(processedData);
	}
}
