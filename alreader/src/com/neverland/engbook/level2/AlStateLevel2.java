package com.neverland.engbook.level2;

public class AlStateLevel2 {
	public int					state_parser = 0;
	public boolean				text_present;
	public boolean				letter_present;
	public int 					start_position;
	public int 					start_position_par;

	public boolean 				state_skipped_flag = false;
	public boolean 				state_special_flag0 = false;
	public boolean				state_code_flag = false;

	public boolean 				insertFromTag = false;
	public boolean				isOpened;	
}
