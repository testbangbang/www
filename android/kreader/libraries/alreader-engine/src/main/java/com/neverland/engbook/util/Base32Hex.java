package com.neverland.engbook.util;

public class Base32Hex {
	public static long decode2int(String src, boolean verifyPresent) {
		long res = 0;
		long val/* = 0*/;
		long shift = 0; 
		int last = verifyPresent ? src.length() - 2 : src.length() - 1;
		for (int i = last; i >=0 ;  i--) {
			char ch = src.charAt(i);
			switch (ch) {
			case '0': 			val = 0x00; break;
			case '1': 			val = 0x01; break;
			case '2': 			val = 0x02; break;
			case '3': 			val = 0x03; break;
			case '4': 			val = 0x04; break;
			case '5': 			val = 0x05; break;
			case '6': 			val = 0x06; break;
			case '7': 			val = 0x07; break;
            case '8': 			val = 0x08; break;
            case '9': 			val = 0x09; break;
            case 'a': case 'A': val = 0x0A; break;
            case 'b': case 'B': val = 0x0B; break;
            case 'c': case 'C': val = 0x0C; break;
            case 'd': case 'D': val = 0x0D; break;
            case 'e': case 'E': val = 0x0E; break;
            case 'f': case 'F': val = 0x0F; break;
            case 'g': case 'G': val = 0x10; break;
            case 'h': case 'H': val = 0x11; break;
            case 'i': case 'I': val = 0x12; break;
            case 'j': case 'J': val = 0x13; break;
            case 'k': case 'K': val = 0x14; break;
            case 'l': case 'L': val = 0x15; break;
            case 'm': case 'M': val = 0x16; break;
            case 'n': case 'N': val = 0x17; break;
            case 'o': case 'O': val = 0x18; break;
            case 'p': case 'P': val = 0x19; break;
            case 'q': case 'Q': val = 0x1A; break;
            case 'r': case 'R': val = 0x1B; break;
            case 's': case 'S': val = 0x1C; break;
            case 't': case 'T': val = 0x1D; break;
            case 'u': case 'U': val = 0x1E; break;
            case 'v': case 'V': val = 0x1F; break;
            default: return 0;           	
			}
			
			if (shift > 0)
				val <<= shift;
			shift += 5;
			
			res |= val;
		}
		
		return res;
	}
}
