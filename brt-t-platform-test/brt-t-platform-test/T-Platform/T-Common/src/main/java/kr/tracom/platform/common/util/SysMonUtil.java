package kr.tracom.platform.common.util;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;

import java.io.File;

public class SysMonUtil {
	private static JavaSysMon sysMon = new JavaSysMon();

	public static String getCpuLog() {
		return String.format("%.2f %%", getCpuUsage());
	}

	public static String getRamLog() {
		long total = sysMon.physical().getTotalBytes();
		long free = sysMon.physical().getFreeBytes();

		return String.format("%.2f/%.2f GB", toGiga(free), toGiga(total));
	}

	public static String getHddLog() {
		try {
			File root = new File("/");
			long total = root.getTotalSpace();
			long free = root.getUsableSpace();
			long used = total - free;

			return String.format("%.2f/%.2f GB", toGiga(used), toGiga(total));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0.0/0.0";
	}

	public static String getJvmLog() {
		Runtime runtime = Runtime.getRuntime();
		long max = runtime.maxMemory();
		long total = runtime.totalMemory();
		long free = runtime.freeMemory();
		long used = total - free;

		return String.format("%.2f/%.2f/%.2f MB", toMega(used), toMega(total), toMega(max));
	}

	public static double getCpuUsage() {
		CpuTimes cpuTime = sysMon.cpuTimes();

		if (cpuTime == null) {
			return 0.0;
		}
		CpuTimes previous = new CpuTimes(cpuTime.getUserMillis(), cpuTime.getSystemMillis(), cpuTime.getIdleMillis());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

		}

		return sysMon.cpuTimes().getCpuUsage(previous) * 100D;
	}

	public static double getRamUsage() {
		long total = sysMon.physical().getTotalBytes();
		long free = sysMon.physical().getFreeBytes();
		long used = total - free;

		return ((double)used / (double)total) * 100.0;
	}

	public static double getHddUsage() {
		try {
			File root = new File("/");
			long total = root.getTotalSpace();
			long free = root.getUsableSpace();
			long used = total - free;

			return ((double)used / (double)total) * 100.0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public static double getJvmUsage() {
		Runtime runtime = Runtime.getRuntime();
		long total = runtime.totalMemory();
		long free = runtime.freeMemory();
		long used = total - free;

		return ((double)used / (double)total) * 100.0;
	}

	public static String getProcess() {
		StringBuilder sb = new StringBuilder();
		sb.append(ProcessInfo.header() + "\n");
		for (ProcessInfo pi : sysMon.processTable()) {
			sb.append(pi.toString() + "\n");
		}
		return sb.toString();
	}
	
	private static double toMega(long value) {
		return (double) value / 1024.0 / 1024.0;
	}

	private static double toGiga(long value) {
		return (double) value / 1024.0 / 1024.0 / 1024.0;
	}
}
