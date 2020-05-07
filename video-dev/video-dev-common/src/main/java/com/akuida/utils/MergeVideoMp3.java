package com.akuida.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

	private String ffmpegEXE;

	public MergeVideoMp3(String ffmpegEXE) {
		super();
		this.ffmpegEXE = ffmpegEXE;
	}

	/**
	 * 视频音频合并方法
	 * 
	 * @param inputVideoPath 视频位置
	 * @param imputMusicPath 音乐位置
	 * @param seconds        视频秒数
	 * @param outPath        输出位置
	 * @throws IOException
	 */
	public void convertor(String inputVideoPath, String imputMusicPath, double seconds, String outPath)
			throws IOException {
		// ffmpeg.exe -i 123.mp4 -i 测试音乐1.mp3 -t 51 -y newVideo.mp4
		List<String> commamd = new ArrayList<>();
		commamd.add(ffmpegEXE);
		commamd.add("-i");
		commamd.add(inputVideoPath);
		commamd.add("-i");
		commamd.add(imputMusicPath);
		commamd.add("-t");
		commamd.add(String.valueOf(seconds));
		commamd.add("-y");
		commamd.add(outPath);
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

	/**
	 * 截取封面图
	 * 
	 * @param inputVideoPath 视频位置
	 * @param coverOutPath   输出图片位置
	 * @throws IOException
	 */
	public void getCover(String inputVideoPath, String coverOutPath) throws IOException {
		// ffmpeg.exe -ss 00:00:01 -y -i inputVideoPath -vframes 1 coverOutPath
		List<String> commamd = new ArrayList<>();
		commamd.add(ffmpegEXE);
		commamd.add("-ss");
		commamd.add("00:00:01");
		commamd.add("-y");
		commamd.add("-i");
		commamd.add(inputVideoPath);
		commamd.add("-vframes");
		commamd.add("1");
		commamd.add(coverOutPath);
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
		MergeVideoMp3 ffmpeg = new MergeVideoMp3("E:\\ffmpeg\\bin\\ffmpeg.exe");
		try {
			ffmpeg.convertor("E:\\ffmpeg\\bin\\123.mp4", "E:\\ffmpeg\\bin\\测试音乐1.mp3", 51, "D:\\合成的视频.avi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
