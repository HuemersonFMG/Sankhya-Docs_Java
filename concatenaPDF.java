package mesclando;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class ConcatenatePDF {

    private Collection<byte[]> pdfFiles = new ArrayList<>();
    private boolean numeration = false;

    public ConcatenatePDF() {
    }

    public Collection<byte[]> getPdfFiles() {
        return pdfFiles;
    }

    public boolean isNumeration() {
        return numeration;
    }

    public void setNumeration(boolean numeration) {
        this.numeration = numeration;
    }

    public void addPdfFile(byte[] pdf) {
        Objects.requireNonNull(pdf, "Não é possível adicionar um PDF nulo.");
        this.pdfFiles.add(pdf);
    }

    public int getSize() {
        return this.pdfFiles.size();
    }

    public ConcatenatePDF(Collection<byte[]> pdfFiles) {
        Objects.requireNonNull(pdfFiles, "A coleção de PDFs não pode ser nula.");
        this.pdfFiles = pdfFiles;
    }

    public void setPdfFiles(Collection<byte[]> pdfFiles) {
        Objects.requireNonNull(pdfFiles, "A coleção de PDFs não pode ser nula.");
        this.pdfFiles = pdfFiles;
    }

    public ByteArrayOutputStream run() throws DocumentException, IOException {
        ByteArrayOutputStream pdfConcatenated = new ByteArrayOutputStream();
        int f = 0;
        Document document = null;
        PdfContentByte pdfContentByte = null;
        PdfWriter pdfWriter = null;
        for (byte[] bytes : this.pdfFiles) {
            PdfReader reader = new PdfReader(bytes);
            reader.consolidateNamedDestinations();
            if (f == 0) {
                document = new Document(reader.getPageSizeWithRotation(1));
                pdfWriter = PdfWriter.getInstance(document, pdfConcatenated);
                document.open();
                if (isNumeration()) {
                    HeaderFooter header = new HeaderFooter(new Phrase("Fls.: "), true);
                    header.setAlignment(HeaderFooter.ALIGN_RIGHT);
                    header.setBorder(HeaderFooter.NO_BORDER);
                    document.resetHeader();
                    document.setHeader(header);
                }
                pdfContentByte = pdfWriter.getDirectContent();
            }
            int numPages = reader.getNumberOfPages();
            for (int i = 1; i <= numPages; i++) {
                document.setPageSize(reader.getPageSizeWithRotation(i));
                document.newPage();
                PdfImportedPage page = pdfWriter.getImportedPage(reader, i);
                int rotation = reader.getPageRotation(i);
                if (rotation == 90 || rotation == 270) {
                    pdfContentByte.addTemplate(page, 0, -1f, 1f, 0, 0, reader.getPageSizeWithRotation(i).getHeight());
                } else {
                    pdfContentByte.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
                }
            }
            f++;
        }
        if (document != null)
            document.close();
        return pdfConcatenated;
    }
}