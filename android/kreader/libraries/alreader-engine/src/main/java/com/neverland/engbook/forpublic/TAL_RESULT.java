package com.neverland.engbook.forpublic;

/**
 * результат работы большинства методов работы с библиотекой
 TAL_RESULT_OK - все прошло нормально
 TAL_RESULT_ERROR - что-то пошло не так.
 TAL_RESULT_WORK_OK - в настоящий момент не используется, пока зарезервировано
 */
public class TAL_RESULT {
	public static final int OK = 0;
	public static final int WORK_OK = 1;
	public static final int ERROR = -1;
}
