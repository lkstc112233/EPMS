package action;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.*;

public class IOHelper {
	
	
	
	static public ByteArrayOutputStream ZIP(Map<String,OutputStream> files) throws IOException {
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		try(ZipOutputStream zos=new ZipOutputStream(out);){
			for(Entry<String,OutputStream> entry:files.entrySet()) {
				ZipEntry zipFile=new ZipEntry(entry.getKey());
				zos.putNextEntry(zipFile);
				byte[] data=((ByteArrayOutputStream)entry.getValue()).toByteArray();
				zos.write(data);
			}
			zos.flush();
			zos.closeEntry();
		} catch (IOException e) {
			throw e;
		}
		return out;
	}
	static public ByteArrayInputStream ByteArrayOutStream2InputStream(ByteArrayOutputStream out){//实际上获取的输出流，使用getter获取的downloadAttachment
		if(out==null) return null;
		System.out.println(">> TableOperationAction:downloadAttachment > ");
		byte[] data=out.toByteArray();
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ByteArrayInputStream in=new ByteArrayInputStream(data);
		return in;
	}
}
