package com.neverland.engbook.level2;

public class AlStateLevel2 {
	static	final long	PAR_DESCRIPTION1 =		0x0010000000000000L; // description 		// head         // first        // relations
	static	final long	PAR_DESCRIPTION2 =		0x0020000000000000L; // title-info		//              // content      // lang
	static	final long	PAR_DESCRIPTION3 =		0x0040000000000000L; // document-info	//              // toc          // sequence
	static	final long	PAR_DESCRIPTION4 =		0x0080000000000000L; // publich-info		//              // cover		// periodical classification written custom-info
	static	final long	PAR_DESCRIPTIONMASK =	0x00f0000000000000L;
	//static	const int64_t	PAR_COVER =				0x0000010000000000L;
	static	final long	PAR_NOTE =				0x0000040000000000L;

	public int					state_parser = 0;
	//public boolean				text_present;
	//public boolean				letter_present;
	public int 					start_position;
	public int 					start_position_par;
	//public int 					start_position_par_level;
	//public long					start_position_par_iType;

	//public int 					start_position_tag;
	public int 					skipped_save;
	public int 					skipped_flag = 0;
	public boolean 				state_special_flag = false;
	public boolean				state_code_flag = false;

	public boolean 				insertFromTag = false;

	public boolean				isNoteSection = false;
	public long					description = 0;
	public int					section_count = 0;

	public boolean  			vector_image = false;
	public int 					content_start = 0;

	public int					image_start = -1;
	public int					image_stop = -1;
	public int					image_type = 0;
	public String				image_name;

	/*public boolean				isOpened;

	public int					skip_count = 0;*/

	public AlStateLevel2() {
		image_name = null;
	}

	public void incSkipped() {
		skipped_flag++;
	}

	public void decSkipped() {
		if (skipped_flag > 0)
			skipped_flag--;
	}

	public void clearSkipped() {
		skipped_save = skipped_flag;
		skipped_flag = 0;
	}

	public void restoreSkipped() {
		skipped_flag = skipped_save;
	}
}
