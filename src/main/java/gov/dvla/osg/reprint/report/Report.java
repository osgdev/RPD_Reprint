package gov.dvla.osg.reprint.report;

import static gov.dvla.osg.reprint.models.Session.props;
import static gov.dvla.osg.reprint.utils.ErrorHandler.*;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import gov.dvla.osg.reprint.models.Session;

/**
 * Generate PDF report for stats team.
 *
 */
public class Report {

	/**
	 * Report contains a single header followed by a summary of all reprints. The reportContent
	 * Strings are taken from the ToString() method of each report type.
	 * @param reportContent
	 */
	public static void writePDFreport(ArrayList<String> reportContent) {

		try {
			// filename -> {workingDir}\{filePrefix}.{user}.{timestamp}.pdf
	        LocalDateTime now = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
	        String timestamp = now.format(formatter);
			String fName = props.getProperty("reportWorkingDir") + props.getProperty("FileNamePrefixReport") + Session.userName + "." + timestamp + ".pdf";
			// see if report file aready exists
			File checkFile = new File(fName);
			if (checkFile.exists()) {
				checkFile.delete();
			}
			// pdf document object to write to file
			Document pdfDoc = new Document();

			try (FileOutputStream fos = new FileOutputStream(fName)) {
				// pdf writer to write to the document
				PdfWriter.getInstance(pdfDoc, fos);
				// all text is appended to a single paragraph
				Paragraph p = new Paragraph();
				// generate timestamp
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy @ HH:mm:ss");
				Date date = new Date();
				// add the report headng
				p.add("Document Reprint - Scanned jobs");
				p.add("\n\nScanned on " + df.format(date) + " by " + Session.userName + ":\n\n");
				// add the report content
				for (String str : reportContent) {
					p.add("\n     " + str);
				}
				// write the content to the pdf file
				pdfDoc.open();
				pdfDoc.add(p);
				pdfDoc.close();
			}	
			
			/*** SHOW REPORT DURING TESTING ONLY ****/
			if (Desktop.isDesktopSupported()) {
				File pdfFile = new File(fName);
				Desktop.getDesktop().open(pdfFile);
			}
			/***************************************/
			
		} catch (DocumentException e) {
			ErrorMsg(e.getClass().getSimpleName(), e.getMessage());
		} catch (IOException e) {
			ErrorMsg(e.getClass().getSimpleName(), "The report pdf is open or unavailable!");
		}
	}
}
