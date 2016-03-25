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
	private File file;
	private String meeting, year;
	private static String path = "/home/robintiman/Documents/Propaganda/";
	private ArrayList<String> lines;

	public Builder(int m, String year) {
		this.year = year;
		lines = new ArrayList<String>();
		// Appends "0" to the meeting number if value is below 10"
		if (m < 10) {
			meeting = "0" + m;
		} else {
			meeting = Integer.toString(m);
		}

		createMeetingFile();
		getSummoning();
		parseSummoning();
	}

	/**
	 * Creates the meeting file
	 */
	private void createMeetingFile() {
		Path file = Paths.get(path + "Mötesanteckningar/protokoll_S" + meeting + "_" + year);
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
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
		} catch (Exception e) {
			System.err.println("Path to file has been changed. Please update path");
			e.printStackTrace();
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
			System.out.println(parsedText);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
