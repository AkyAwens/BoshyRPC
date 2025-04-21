package dev.akyawen.boshyrpc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef.DWORD;

public class MemoryReader {
	interface Kernel32Ext extends Kernel32 {
        Kernel32Ext INSTANCE = Native.load("kernel32", Kernel32Ext.class);

        boolean ReadProcessMemory(
            HANDLE hProcess,
            long lpBaseAddress,
            Memory lpBuffer,
            int nSize,
            IntByReference lpNumberOfBytesRead
        );

        HANDLE OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);

        boolean CloseHandle(HANDLE hObject);
    }
	
    private static final int PROCESS_VM_READ = 0x0010;

    
    public static String getProcessPath(int processId) throws RuntimeException {
        HANDLE processHandle = Kernel32.INSTANCE.OpenProcess(
                0x0400 | 0x0010, // PROCESS_QUERY_INFORMATION | PROCESS_VM_READ
                false,
                processId
        );

        if (processHandle == null) {
            throw new RuntimeException("Failed to open process with PID: " + processId);
        }

        try {
            char[] path = new char[1024];
            int length = Psapi.INSTANCE.GetModuleFileNameExW(
                    processHandle,
                    null, 
                    path,
                    path.length
            );

            if (length == 0) {
                throw new RuntimeException("Failed to get process path for PID: " + processId);
            }

            return new String(path, 0, length);
        } finally {
            Kernel32.INSTANCE.CloseHandle(processHandle);
        }
    }
    
    public static Memory readMemory(HANDLE process, long address, int readSize) {
        Memory output = new Memory(readSize);
        IntByReference bytesRead = new IntByReference();

        if (!Kernel32Ext.INSTANCE.ReadProcessMemory(process, address, output, readSize, bytesRead)) {
            int error = Kernel32Ext.INSTANCE.GetLastError();
            switch (error) {
                case 0x12B:
                    System.err.println("Failed to read the specified address: 0x" + Long.toHexString(address));
                    break;
                default:
                    System.err.println("Failed to read the process: Error code " + error);
                    break;
            }
            return null;
        }

        if (bytesRead.getValue() != readSize) {
            System.err.println("Partial read: Expected " + readSize + " bytes, read " + bytesRead.getValue());
            return null;
        }

        return output;
    }
    
    public static long getModuleBaseAddress(int pid, String moduleName) {
        WinNT.HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new DWORD(pid));
        if (snapshot == WinNT.INVALID_HANDLE_VALUE) {
            return 0;
        }

        Tlhelp32.MODULEENTRY32W moduleEntry = new Tlhelp32.MODULEENTRY32W();
        while (Kernel32.INSTANCE.Module32NextW(snapshot, moduleEntry)) {
            if (Native.toString(moduleEntry.szModule).equalsIgnoreCase(moduleName)) {
                Kernel32.INSTANCE.CloseHandle(snapshot);
                return Pointer.nativeValue(moduleEntry.modBaseAddr);
            }
        }
        Kernel32.INSTANCE.CloseHandle(snapshot);
        return 0;
    }
    
    public static long readPointerChain(int pid, long baseAddress, long... offsets) {
        WinNT.HANDLE processHandle = Kernel32Ext.INSTANCE.OpenProcess(PROCESS_VM_READ, false, pid);
        if (processHandle == null) {
            System.err.println("Cannot open process");
            return -1;
        }

        try {
            long address = baseAddress;
            for (int i = 0; i < offsets.length; i++) {
                Memory pointerMem = readMemory(processHandle, address, 4);
                if (pointerMem == null) return -1;
                address = Integer.toUnsignedLong(pointerMem.getInt(0));
                address += offsets[i];
            }
            return address;
        } finally {
            Kernel32Ext.INSTANCE.CloseHandle(processHandle);
        }
    }
    
    public static int getProcessId(String processName) {
		String taskListCommand = System.getenv("windir") + "\\system32\\" + "tasklist.exe";
	    try {
	        final Process p = Runtime.getRuntime().exec(taskListCommand);
	        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String line;
	        try {
	            while ((line = input.readLine()) != null) {
	                if (line.contains(processName)) {
	                    int PID = Integer.parseInt((line.split("\\s+"))[5]);
	                    return PID;
	                }
	            }
	            input.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        } catch (IOException e) {
	        e.printStackTrace();
	    }
		return 0;
	}

    public static int readMemoryInt(int pid, long address) {
        // Open the process with read access
        WinNT.HANDLE processHandle = Kernel32Ext.INSTANCE.OpenProcess(
            PROCESS_VM_READ,
            false,
            pid
        );

        if (processHandle == null) {
            System.err.println("Failed to open process with PID: " + pid + ", Error code: " + Kernel32Ext.INSTANCE.GetLastError());
            return -1;
        }

        try {
            Memory result = readMemory(processHandle, address, 4);
            if (result == null) {
                return -1;
            }
            return result.getInt(0);
        } finally {
            // Always close the process handle
            Kernel32Ext.INSTANCE.CloseHandle(processHandle);
        }
    }

}
