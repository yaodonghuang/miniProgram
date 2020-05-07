package com.akuida.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMpegTest {

	private String ffmpegEXE;

	public FFMpegTest(String ffmpegEXE) {
		super();
		this.ffmpegEXE = ffmpegEXE;
	}

	public void convertor(String inputPath, String outPath) throws IOException {
		List<String> commamd = new ArrayList<>();
		commamd.add(ffmpegEXE);
		commamd.add("-i");
		commamd.add(inputPath);
		commamd.add(outPath);
		commamd.forEach(c -> {
			System.out.print(c);
		});
		ProcessBuilder builder = new ProcessBuilder(commamd);
		Process process = builder.start();
		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(inputStreamReader);
		while (br.readLine() != null) {
		}

		if (br != null) {
			br.close();
		}
		if (inputStreamReader != null) {
			inputStreamReader.close();
		}
		if (errorStream != null) {
			errorStream.close();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FFMpegTest ffmpeg = new FFMpegTest("E:\\ffmpeg\\bin\\ffmpeg.exe");
		try {
			ffmpeg.convertor("D:\\123.mp4", "D:\\\\测试视频1.avi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
