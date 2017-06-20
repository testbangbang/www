package com.neverland.engbook.forpublic;

import java.util.ArrayList;

/**
 * свойства книги
 */
public class AlBookProperties {
	public String					title = null;
	public ArrayList<String>		authors = null;
	public ArrayList<String>        series = null;
    public ArrayList<String>        genres = null;
    // при получении свойств книги в scanMetaData - следующие два поля не заполняются
    public ArrayList<AlOneContent>	content = null;
	public boolean 					isTextFormat = true;

    public int						size = 0;
	// данные по обложке заполняются только в scanMetaData
	//public int						coverImageDataSize = 0;
	public byte[]					coverImageData = null;

}
