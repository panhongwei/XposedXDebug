package com.example.xposedxdebug;

import java.lang.reflect.Method;

import android.util.Log;
import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.os.Process;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static final int DEBUG_ENABLE_DEBUGGER = 0x1;
    private XC_MethodHook debugAppsHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param)
                throws Throwable {
          //  Log.i("hookcipher","-- beforeHookedMethod :" + param.args[1]);
            int id = 5;
            int flags = (Integer) param.args[id];
            if ((flags & DEBUG_ENABLE_DEBUGGER) == 0) {
                flags |= DEBUG_ENABLE_DEBUGGER;
            }
            param.args[id] = flags;
            if (BuildConfig.DEBUG) {
            	// Log.i("hookcipher","-- app debugable flags to 1 :" + param.args[1]);
            }
        }
    };

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
    	XposedHelpers.findAndHookMethod(Debug.class, "startMethodTracingDdms", int.class, int.class, boolean.class, int.class,new XC_MethodHook() {
    		@Override
	        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
    			int size=(Integer)param.args[0];
    			if(size==8388608){
    				param.args[0]=size*10;
    			}
    		}
    	});
//        if (BuildConfig.DEBUG) {
//            XposedBridge.log("-- handle package: " + loadPackageParam.packageName + " process: " + loadPackageParam.processName);
//        }
//
//        if (loadPackageParam.appInfo == null ||
//                (loadPackageParam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
//            XposedBridge.log("-- appInfo: " + loadPackageParam.appInfo);
//            return;
//        }
//
//        XposedBridge.log("-- start hook, appInfo: " + loadPackageParam.appInfo);
//
//        Method start = findMethodExact(Process.class, "start",
//                String.class,
//                String.class,
//                Integer.TYPE,
//                Integer.TYPE,
//                int[].class,
//                Integer.TYPE,
//                Integer.TYPE,
//                Integer.TYPE,
//                String.class,
//                String.class,
//                String.class,
//                String.class,
//                String[].class);
//
//        XposedBridge.log("-- find method, method: " + start.toString());
//
//        XposedBridge.hookMethod(start, debugAppsHook);

    }

    @Override
    public void initZygote(final IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
//    	/og.i("hookcipher","============================");
    	Method[] methods=Process.class.getDeclaredMethods();
//    	for(Method m:methods){
//    		Log.i("hookcipher",m.getName());
//    	}
        XposedBridge.hookAllMethods(Process.class, "start", debugAppsHook);
    }
}
