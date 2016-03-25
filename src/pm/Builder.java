package pm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * @author Robin Timan
 *
 */
public class Builder {
	private String meeting, year;
	private static String path = "/home/robintiman/Documents/Propaganda/";
	private ArrayList<String> lines;

	public Builder(int m, String year) {
		this.year = year;
		lines = new ArrayList<String>();
		lines.add("Närvaro: \n");
		// Appends "0" to the meeting number if value is below 10"
		if (m < 10) {
			meeting = "0" + m;
		} else {
			meeting = Integer.toString(m);
		}

		getSummoning();
		parseSummoning();
		createMeetingFile();
	}

	/**
	 * Creates the meeting file
	 */
	private void createMeetingFile() {
		Path file = Paths.get(path + "Mötesanteckningar/S" + meeting);
		try {
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW);
		} catch (Exception e) {
			System.err.println("Notes from this meeting already exists \n");
		}
	}

	/**
	 * Gets the summoning from dsek.se
	 */
	private void getSummoning() {
		URL website = null;
		try {
			website = new URL("https://www.dsek.se/arkiv/moteshandlingar/data/2016/Kallelse_S08_2016.pdf");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(path + "/Kallelser/Kallelse_S" + meeting + "_" + year + ".pdf");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (Exception e) {
			System.err.println("URL to file has been changed. Please use the option for updating URL to do so \n");
		}
	}

	/**
	 * Using PDFBox from Apache to parse the summoning
	 */
	private void parseSummoning() {
		PDFTextStripper pdfStripper = null;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		File file = new File(path + "/Kallelser/Kallelse_S" + meeting + "_" + year + ".pdf");
		try {
			PDFParser parser = new PDFParser(new FileInputStream(file));
			parser.parse();
			cosDoc = parser.getDocument();
			pdfStripper = new PDFTextStripper();
			pdDoc = new PDDocument(cosDoc);
			String parsedText = pdfStripper.getText(pdDoc);
			parseSummoningString(parsedText);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used for parsing the parsedText returned by PDFBox. Will find and append
	 * the agenda to the lines ArrayList
	 * 
	 * @param pdf
	 */
	private void parseSummoningString(String pdf) {
		String[] sum = pdf.split("§");
		for (int i = 1; i < sum.length; i++) {
			String toAdd = "";
			if (i != 1) {
				if (sum[i].contains("OFMA")) {
					sum[i] = sum[i].substring(0, sum[i].indexOf("\n"));
					toAdd = sum[i] + "\n";
					lines.add(toAdd);
					return;
				}
				String[] temp = sum[i].split(" ");
				temp[temp.length - 1] = "\n";
				for (String k : temp) {
					toAdd += k + " ";
				} 
			} else {
				toAdd = sum[i];
			}
			lines.add(toAdd);
		}
	}
}
