package com.neverland.engbook.forpublic;

import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_ID;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;

public interface EngBookListener {
	public void	engBookGetMessage(TAL_NOTIFY_ID id, TAL_NOTIFY_RESULT result);
}
