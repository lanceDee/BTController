package com.example.lance.btcontroller;

/**
 * Created by Lance on 2017/4/17.
 */

public class Constants {
    public static final String CMD_SEND_DATA = "send\r\n";
    public static final String CMD_SAVE_DATA = "save\r\n";
    public static final String CMD_STOP = "stop\r\n";
    public static final String CMD_REQ_FILE_SIZE = "fsize\r\n";
    public static final int RECIEVE_STATE_NONE = 0;
    public static final int RECIEVE_STATE_SUCCESS = 1;
    public static final int RECIEVE_STATE_FAILED = 2;
    public static final int RECIEVE_STATE_CANCELED = 3;
}
