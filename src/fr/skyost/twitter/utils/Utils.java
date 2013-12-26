package fr.skyost.twitter.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.skyost.twitter.Curricutum;

public class Utils {
	
	public static void extractFromJAR(final String pathFrom, final File pathTo) throws IOException {
		final InputStream inputStream = Curricutum.class.getResourceAsStream(pathFrom);
		final OutputStream outputStream = new FileOutputStream(pathTo);
		int read = 0;
		byte[] bytes = new byte[1024];
		while((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
		outputStream.close();
	}

}
