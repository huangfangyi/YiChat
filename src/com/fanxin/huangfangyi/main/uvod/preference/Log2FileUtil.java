package com.fanxin.huangfangyi.main.uvod.preference;

import android.os.Environment;
import android.util.Log;

import com.ucloud.common.logger.L;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Log2FileUtil log = Log2FileUtil.getInstance();
//log.startLog();//开始保存log
//log.stopLog();//停止保存log

public class Log2FileUtil {

	private static final String TAG = "Log";
	// LogWrite
	private String LOG_PATH_SDCARD_DIR; // log file path in sdcard

	private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd_HH-mm-ss");// log
																				// name

	private Process process;

	private static Log2FileUtil mLogDemo = null;

	private Log2FileUtil() {
		init();
	}

	public static Log2FileUtil getInstance() {
		if (mLogDemo == null) {
			mLogDemo = new Log2FileUtil();
		}

		return mLogDemo;
	}

	public void startLog() {
		createLog();

	}

	public void stopLog() {
		if (process != null) {
			process.destroy();
		}
	}
	
	
	//TBD: not work now... why??
	public void clearLog() {
		List<String> commandList = new ArrayList<String>();
		commandList.add("rm");
		commandList.add("-f");
		commandList.add(LOG_PATH_SDCARD_DIR + "/ucloud*.log");
		
		try {
			process = Runtime.getRuntime().exec(
					commandList.toArray(new String[commandList.size()]));
		} catch (Exception e) {
			Log.e(TAG,e.getMessage(), e);
		}
	}

	private void init() {

		LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
		createLogDir();
		Log.i(TAG, "Log onCreate");
	}

	public void setLogCacheDir(String dir) {
		LOG_PATH_SDCARD_DIR = dir;
	}

	/**
	 * write the log
	 */
	public void createLog() {
		// TODOWriteLog

		List<String> commandList = new ArrayList<String>();
		commandList.add("logcat");
		commandList.add("-f");
		commandList.add(getLogPath());
		commandList.add("-v");
		commandList.add("time");
//		commandList.add("-r");
//		commandList.add("2048"); 
//		commandList.add("-n");
//		commandList.add("1");		
		
		try {
			process = Runtime.getRuntime().exec(
					commandList.toArray(new String[commandList.size()]));
		} catch (Exception e) {
			Log.e(TAG,e.getMessage(), e);
		}
	}

	/**
	 * the path of the log file
	 * 
	 * @return
	 */
	public String getLogPath() {
		createLogDir();
		String logFileName = "ucloud-" + sdf.format(new Date()) + ".log";// name

		L.i(TAG, "Log stored in SDcard, the path is:" + LOG_PATH_SDCARD_DIR + File.separator + logFileName);
		return LOG_PATH_SDCARD_DIR + File.separator + logFileName;

	}

	/**
	 * make the dir
	 */
	private void createLogDir() {
		File file;
		boolean mkOk;

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			file = new File(LOG_PATH_SDCARD_DIR);
			if (!file.isDirectory()) {
				mkOk = file.mkdirs();
				if (!mkOk) {
					return;
				}
			}
		}
	}

}
