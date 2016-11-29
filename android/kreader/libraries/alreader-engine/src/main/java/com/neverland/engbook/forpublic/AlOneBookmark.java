package com.neverland.engbook.forpublic;

public class AlOneBookmark {
	public int					id;
	public String				text = null;
	public String				name = null;
	public int					pos_start = -1;
	public int					pos_end = -1;

	public EngBookMyType.TAL_BOOKMARK_COLOR	color = EngBookMyType.TAL_BOOKMARK_COLOR.NONE;
	public EngBookMyType.TAL_BOOKMARK_TYPE	tp = EngBookMyType.TAL_BOOKMARK_TYPE.BOOKMARK;
}
