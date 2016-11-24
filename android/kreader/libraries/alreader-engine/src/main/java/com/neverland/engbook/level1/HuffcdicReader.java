package com.neverland.engbook.level1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class HuffcdicReader {
	
	private final DictUnpackResult dict1[] = new DictUnpackResult[256];
	private final long mincode0[] = new long[33];
	private final long maxcode0[] = new long[33];
	private final ArrayList<Slice> dictionary = new ArrayList<>();
	
	private class DictUnpackResult {
		int codelen;
		int term;
		long maxcode;
	}
	
	private class Slice {
		byte[] slice;
		int flag;
	}
	
	
	private DictUnpackResult dict1_unpack(int v) {
		DictUnpackResult a = new DictUnpackResult();
		a.codelen = v & 0x1f;
		a.term = v & 0x80;
		a.maxcode = ((long)v) >> 8L;
		a.maxcode = ((a.maxcode + 1L) << (32L - a.codelen)) - 1L;		
		return a;
	}
	
	public boolean loadHuff(AlFiles a, int start_pos) {
		int off1, off2, i;
		
		a.read_pos = start_pos;
		off1 = (int) (a.getRevDWord());		
		off2 = (int) (a.getRevDWord());
		if (off1 == 0x48554646 && off2 == 0x00000018) {			
			off1 = (int) (a.getRevDWord());
			off2 = (int) (a.getRevDWord());
			
			a.read_pos = start_pos + off1;
			for(i = 0; i < 256; i++)
				dict1[i] = dict1_unpack((int)(a.getRevDWord()));
					
			a.read_pos = start_pos + off2;
			for(i = 0; i < 32 ; i++) { // 32 is 64/2. In the original code, dict2Off and dict2Even are merged into one list
				mincode0[i] = (((long)a.getRevDWord()) << (32L - i - 1));
				maxcode0[i] = ((((long)a.getRevDWord()) + 1L) << (32L - i - 1)) - 1L;
			}
						
			dictionary.clear();
			return true;
		}
		return false;
	}

	public boolean loadCdic(AlFiles a, int start_pos) {
		int phrases, bits;
		a.read_pos = start_pos;
		phrases = (int) (a.getRevDWord());
		bits = (int) (a.getRevDWord());
		if (phrases == 0x43444943 && bits == 0x00000010) {		
			phrases = (int) (a.getRevDWord());
			bits = (int) (a.getRevDWord());
			
			int n = Math.min(1 << bits, phrases - dictionary.size());
			
			a.read_pos = start_pos + 0x10;
			for(int i = 0; i < n; i++) {
				int offset = a.getRevWord();
				int savedPosition = a.read_pos;
				
				a.read_pos = start_pos + offset + 16;
				int blen = a.getRevWord();
								
				byte[] slice = new byte[blen & 0x7fff];
				for (int j = 0; j < (blen & 0x7fff); j++)
					slice[j] = a.getByte(start_pos + offset + 18 + j);
				
				Slice sliceObject  = new Slice();
				sliceObject.slice = slice;
				sliceObject.flag = blen & 0x8000;
				dictionary.add(sliceObject);
				
				a.read_pos = savedPosition;
			}
			
			return true;
		}
		return false;		
	}
	
	private final static long getNextLong(final byte[] data, final int maxLen, final int pos) {
		long res = 0;
		
		for (int i = 0; i < 8; i++) {
			res <<= 8;
			if (pos + i < maxLen)
				res |= (long)data[pos + i] & 0xff;
		}
		
		return res;
	}
	
	public int calcSizeBlock(byte[] data, int len, int depth) {
		int result = 0;
		
		int bitsleft = len * 8, pos = 0, n = 32;
		long x = getNextLong(data, len, 0);		
		
		while (true) {
			if (n <= 0) {
				pos += 4;
				x = getNextLong(data, len, pos);
				n += 32;
			}
			
			final long code = (x >> n) & 0x00000000ffffffffL;
			DictUnpackResult unpackedDict = dict1[(int) (code >> 24)];								
			int codelen = unpackedDict.codelen, term = unpackedDict.term;
			long maxcode = unpackedDict.maxcode;
			
			if (term == 0) {
				while (code < mincode0[codelen - 1])
					codelen++;
				maxcode = maxcode0[codelen - 1];
			}

			n -= codelen;
			bitsleft -= codelen;
			if (bitsleft < 0)
				break;

			long r = ((maxcode - code) >> (32L - codelen));
						
			Slice slice = dictionary.get((int) r);
			if (slice != null) {
				if(slice.flag == 0) {
					dictionary.set((int) r, null);
					
					slice.slice = unpack(slice.slice, slice.slice.length, depth + 1);
					
					slice.flag = 1;
					dictionary.set((int) r, slice);
				}
				
				result += slice.slice.length;				
			}			
		}
				
		return result;
	}
	
	public byte[] unpack(byte[] data, int len, int depth) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		
		int bitsleft = len * 8, pos = 0, n = 32;
		long x = getNextLong(data, len, 0);		
		
		while (true) {
			if (n <= 0) {
				pos += 4;
				x = getNextLong(data, len, pos);
				n += 32;
			}
			
			final long code = (x >> n) & 0xffffffffL;
			DictUnpackResult unpackedDict = dict1[(int) (code >> 24)];								
			int codelen = unpackedDict.codelen, term = unpackedDict.term;
			long maxcode = unpackedDict.maxcode;
			
			if (term == 0) {
				while (code < mincode0[codelen - 1])
					codelen++;
				maxcode = maxcode0[codelen - 1];
			}

			n -= codelen;
			bitsleft -= codelen;
			if (bitsleft < 0)
				break;

			long r = ((maxcode - code) >> (32L - codelen));
						
			Slice slice = dictionary.get((int) r);
			if (slice != null) {
				if(slice.flag == 0) {
					dictionary.set((int) r, null);
					
					slice.slice = unpack(slice.slice, slice.slice.length, depth + 1);
					
					slice.flag = 1;
					dictionary.set((int) r, slice);
				}
				
				try {
					result.write(slice.slice);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
				
		return result.toByteArray();
	}
	
	public final static int calcSizeOfTrailingDataEntry(byte[] ptr, int psize) {
	    int bitsSaved = 0;
		int res = 0;
	    while (true) {
	        int oneByte = ptr[psize - 1] & 0xff;
	        res |= (oneByte & 0x7F) << bitsSaved;
	        bitsSaved += 7;
	        psize -= 1;
	        if (((oneByte & 0x80) != 0) || (bitsSaved >= 28) || (psize == 0))
	            return res;
		}
	} 

	public int calcTrailingDataEntries(byte[] data, int psize, int huffman_extra) {
		int num = 0;
		
		int flags = huffman_extra >> 1;
		while (flags != 0) {
	    	if ((flags & 1) != 0) {
				if (num < psize) {
	        		num += calcSizeOfTrailingDataEntry(data, psize - num);
				}
			}
	    	flags >>= 1;
		}
		
		if ((huffman_extra & 0x01) != 0 && (psize - num - 1) > 0) {
			flags = (data[psize - num - 1] & 0x03) + 1;
			num += flags;
		}
		return num; 		
	}
}
