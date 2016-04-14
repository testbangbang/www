package com.neverland.engbook.unicode;

import com.neverland.engbook.util.AlStyles;

public class CP932 {
	public static char getChar(char s1, char s2) {
		char wc = 0x00;
			
		switch (s1) {
		case 0x81: wc = CP932Data80.data_81_40_FC[s2 - 0x40]; break;
		case 0x82: if (s2 >= 0x4f && s2 <= 0xf1) wc = CP932Data80.data_82_4F_F1[s2 - 0x4F]; break;	
		case 0x83: if (s2 >= 0x40 && s2 <= 0xd6) wc = CP932Data80.data_83_40_D6[s2 - 0x40]; break;
		case 0x84: if (s2 >= 0x40 && s2 <= 0xbe) wc = CP932Data80.data_84_40_BE[s2 - 0x40]; break;	
		//case 0x85: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932Data80.data_85_40_FE[s2 - 0x40]; break;
		//case 0x86: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932Data80.data_86_40_FE[s2 - 0x40]; break;	
		case 0x87: if (s2 >= 0x40 && s2 <= 0x9c) wc = CP932Data80.data_87_40_9C[s2 - 0x40]; break;
		case 0x88: if (s2 >= 0x9f && s2 <= 0xfc) wc = CP932Data80.data_88_9F_FC[s2 - 0x9F]; break;	
		case 0x89: wc = CP932Data80.data_89_40_FC[s2 - 0x40]; break;
		case 0x8a: wc = CP932Data80.data_8A_40_FC[s2 - 0x40]; break;	
		case 0x8b: wc = CP932Data80.data_8B_40_FC[s2 - 0x40]; break;	
		case 0x8c: wc = CP932Data80.data_8C_40_FC[s2 - 0x40]; break;
		case 0x8d: wc = CP932Data80.data_8D_40_FC[s2 - 0x40]; break;	
		case 0x8e: wc = CP932Data80.data_8E_40_FC[s2 - 0x40]; break;
		case 0x8f: wc = CP932Data80.data_8F_40_FC[s2 - 0x40]; break;
		case 0x90: wc = CP932Data80.data_90_40_FC[s2 - 0x40]; break;
		case 0x91: wc = CP932Data80.data_91_40_FC[s2 - 0x40]; break;
		case 0x92: wc = CP932Data80.data_92_40_FC[s2 - 0x40]; break;	
		case 0x93: wc = CP932Data80.data_93_40_FC[s2 - 0x40]; break;
		case 0x94: wc = CP932Data80.data_94_40_FC[s2 - 0x40]; break;	
		case 0x95: wc = CP932Data80.data_95_40_FC[s2 - 0x40]; break;
		case 0x96: wc = CP932Data80.data_96_40_FC[s2 - 0x40]; break;	
		case 0x97: wc = CP932Data80.data_97_40_FC[s2 - 0x40]; break;
		case 0x98: wc = CP932Data80.data_98_40_FC[s2 - 0x40]; break;	
		case 0x99: wc = CP932Data80.data_99_40_FC[s2 - 0x40]; break;
		case 0x9a: wc = CP932Data80.data_9A_40_FC[s2 - 0x40]; break;	
		case 0x9b: wc = CP932Data80.data_9B_40_FC[s2 - 0x40]; break;	
		case 0x9c: wc = CP932Data80.data_9C_40_FC[s2 - 0x40]; break;
		case 0x9d: wc = CP932Data80.data_9D_40_FC[s2 - 0x40]; break;	
		case 0x9e: wc = CP932Data80.data_9E_40_FC[s2 - 0x40]; break;
		case 0x9f: wc = CP932Data80.data_9F_40_FC[s2 - 0x40]; break;				
		case 0xe0: wc = CP932DataE0.data_E0_40_FC[s2 - 0x40]; break;
		case 0xe1: wc = CP932DataE0.data_E1_40_FC[s2 - 0x40]; break;
		case 0xe2: wc = CP932DataE0.data_E2_40_FC[s2 - 0x40]; break;	
		case 0xe3: wc = CP932DataE0.data_E3_40_FC[s2 - 0x40]; break;
		case 0xe4: wc = CP932DataE0.data_E4_40_FC[s2 - 0x40]; break;	
		case 0xe5: wc = CP932DataE0.data_E5_40_FC[s2 - 0x40]; break;
		case 0xe6: wc = CP932DataE0.data_E6_40_FC[s2 - 0x40]; break;	
		case 0xe7: wc = CP932DataE0.data_E7_40_FC[s2 - 0x40]; break;
		case 0xe8: wc = CP932DataE0.data_E8_40_FC[s2 - 0x40]; break;	
		case 0xe9: wc = CP932DataE0.data_E9_40_FC[s2 - 0x40]; break;		
		case 0xea: if (s2 >= 0x40 && s2 <= 0xa4) wc = CP932DataE0.data_EA_40_A4[s2 - 0x40]; break;			
		//case 0xeb: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_EB_40_FE[s2 - 0x40]; break;	
		//case 0xec: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_EC_40_FE[s2 - 0x40]; break;
		case 0xed: wc = CP932DataE0.data_ED_40_FC[s2 - 0x40]; break;	
		case 0xee: wc = CP932DataE0.data_EE_40_FC[s2 - 0x40]; break;
		//case 0xef: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_EF_40_FE[s2 - 0x40]; break;
		//case 0xf0: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_F0_40_FE[s2 - 0x40]; break;
		//case 0xf1: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_F1_40_FE[s2 - 0x40]; break;
		//case 0xf2: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_F2_40_FE[s2 - 0x40]; break;	
		//case 0xf3: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_F3_40_FE[s2 - 0x40]; break;
		//case 0xf4: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_F4_40_FE[s2 - 0x40]; break;	
		//case 0xf5: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_F5_40_FE[s2 - 0x40]; break;
		//case 0xf6: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_F6_40_FE[s2 - 0x40]; break;	
		//case 0xf7: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_F7_40_FE[s2 - 0x40]; break;
		//case 0xf8: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_F8_40_FE[s2 - 0x40]; break;	
		//case 0xf9: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_F9_40_FE[s2 - 0x40]; break;
		case 0xfa: wc = CP932DataE0.data_FA_40_FC[s2 - 0x40]; break;	
		case 0xfb: wc = CP932DataE0.data_FB_40_FC[s2 - 0x40]; break;	
		case 0xfc: if (s2 >= 0x40 && s2 <= 0x4b) wc = CP932DataE0.data_FC_40_4B[s2 - 0x40]; break;
		//case 0xfd: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_FD_40_FE[s2 - 0x40]; break;	
		//case 0xfe: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_FE_40_FE[s2 - 0x40]; break;
		//case 0xff: if (s2 >= 0x40 && s2 <= 0xfe) wc = CP932DataE0.data_FF_40_FE[s2 - 0x40]; break;
		}
		
		if ((wc & AlStyles.STYLE_BASE_MASK) == AlStyles.STYLE_BASE0)
			wc = 0x00;
		
		return wc;

	}
}
