package eu.unicredit.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.github.shyiko.mysql.binlog.io.ByteArrayInputStream;
import com.github.shyiko.mysql.binlog.io.ByteArrayOutputStream;

public class Utils {



	public static Object deserialize(Object obj){

		ObjectInputStream ois=null;
		InputStream is=null;
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
			){
			oos.writeObject(obj);
			oos.flush();
			is = new ByteArrayInputStream(baos.toByteArray());
			ois=new ObjectInputStream(is);
			Object res =ois.readObject();
			return res;
//			return  (res!=null?res:"");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			is=null;
			try {
				ois.close();
				ois=null;
			} catch (IOException e) {
			}
		}
		return null;
	}


}
