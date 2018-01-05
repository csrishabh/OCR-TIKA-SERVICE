/*package com.mrll.javelin.tikaparser.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import com.abbyy.FREngine.IReadStream;
import com.abbyy.FREngine.Ref;

public class AbbyyInputStream implements IReadStream {

	InputStream stream;
	byte[] bytes;
	
	public AbbyyInputStream(MultipartFile imageFile) throws IOException {
		super();
		this.bytes = imageFile.getBytes();
		this.stream = imageFile.getInputStream();
	}

	@Override
	public void Close() {
		// TODO Auto-generated method stub
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int Read(Ref<byte[]> data, int arg1) {
		
		// TODO Auto-generated method stub
		
		data = new Ref<byte[]>(bytes);
		return 0;
	}

}
*/