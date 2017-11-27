package com.neverland.engbook.forpublic;

import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_ID;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;



/**
 * интерфейс для связи библиотеки и нужной Activity
 */
public interface EngBookListener {
    //Handler         handler = new Handler();

	/**
	 *
	 * @param id - id выполненой операции
	 * @param res - результат выполненой операции
	 */
	public void	engBookGetMessage(TAL_NOTIFY_ID id, TAL_NOTIFY_RESULT res);

}
