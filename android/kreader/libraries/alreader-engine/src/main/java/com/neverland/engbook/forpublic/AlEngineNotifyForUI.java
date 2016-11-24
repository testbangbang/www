package com.neverland.engbook.forpublic;

import android.content.Context;

/**
 * параметры, которые необходимы библиотеке для организации обмена с основным окном программы
 */
public class AlEngineNotifyForUI {
	/**
	 * класс Activity, реализующий интерфейс EngBookListener
	 */
	public EngBookListener		hWND;
	/**
	 * класс Application, для возможности загрузки ресурсов программы (доступ к Assets)
	 */
	public Context appInstance;
}	
