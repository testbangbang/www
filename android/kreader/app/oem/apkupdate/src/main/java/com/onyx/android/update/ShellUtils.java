package com.onyx.android.update;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by huxiaomao on 17/10/21.
 */

public class ShellUtils {
    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";
    public static final String COMMAND_NEW_LINE = "\r\n";

    public ShellUtils() {
    }

    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    public static ShellUtils.CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true, true);
    }

    public static ShellUtils.CommandResult execCommand(boolean isRoot, String... commands) {
        return execCommand(commands, isRoot, true, true);
    }

    public static ShellUtils.CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null?null:(String[])commands.toArray(new String[0]), isRoot, true, true);
    }

    public static ShellUtils.CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true, true);
    }

    public static ShellUtils.CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg, true);
    }

    public static ShellUtils.CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null?null:(String[])commands.toArray(new String[0]), isRoot, isNeedResultMsg, true);
    }

    public static ShellUtils.CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg, boolean destroy) {
        int result = -1;
        if(commands != null && commands.length != 0) {
            Process process = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = null;
            StringBuilder errorMsg = null;
            DataOutputStream os = null;

            try {
                process = Runtime.getRuntime().exec(isRoot?"su":"sh");
                os = new DataOutputStream(process.getOutputStream());
                String[] e = commands;
                int var12 = commands.length;

                for(int var13 = 0; var13 < var12; ++var13) {
                    String command = e[var13];
                    if(command != null) {
                        os.write(command.getBytes());
                        os.writeBytes("\n");
                        os.flush();
                    }
                }

                os.writeBytes("exit\n");
                os.flush();
                if(isNeedResultMsg) {
                    successMsg = new StringBuilder();
                    errorMsg = new StringBuilder();
                    successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    String var28;
                    while((var28 = successResult.readLine()) != null) {
                        successMsg.append(var28).append("\r\n");
                    }

                    while((var28 = errorResult.readLine()) != null) {
                        errorMsg.append(var28).append("\r\n");
                    }
                }

                result = process.waitFor();
            } catch (IOException var25) {
                var25.printStackTrace();
            } catch (Exception var26) {
                var26.printStackTrace();
            } finally {
                try {
                    if(os != null) {
                        os.close();
                    }

                    if(successResult != null) {
                        successResult.close();
                    }

                    if(errorResult != null) {
                        errorResult.close();
                    }
                } catch (IOException var24) {
                    var24.printStackTrace();
                }

                if(process != null && destroy) {
                    destroyProcess(process);
                }

            }

            return new ShellUtils.CommandResult(result, successMsg == null?null:successMsg.toString(), errorMsg == null?null:errorMsg.toString());
        } else {
            return new ShellUtils.CommandResult(result, (String)null, (String)null);
        }
    }

    private static void destroyProcess(Process process) {
        try {
            process.exitValue();
        } catch (IllegalThreadStateException var2) {
            process.destroy();
        }

    }

    public static class CommandResult {
        public int result;
        public String successMsg;
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }
}
