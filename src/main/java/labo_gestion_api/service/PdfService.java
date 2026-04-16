package labo_gestion_api.service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import labo_gestion_api.model.Commande;
import labo_gestion_api.model.ProduitCommande;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    public static ByteArrayInputStream generateCommandePdf(Commande commande) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // ✅ إنشاء PDF writer
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // ✅ Load font
            PdfFont font = PdfFontFactory.createFont();
            PdfFont boldFont = PdfFontFactory.createFont();

            // ✅ Add title
            Paragraph title = new Paragraph("Détails de la Commande")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // ✅ Add spacing
            document.add(new Paragraph(" "));

            // ✅ Add Commande details
            document.add(new Paragraph("Désignation: " + commande.getDesignation()).setFont(boldFont));
            document.add(new Paragraph("Date: " + commande.getDate()).setFont(boldFont));
            document.add(new Paragraph("Observation: " + commande.getObservation()).setFont(boldFont));
            document.add(new Paragraph("Numéro: " + commande.getNumero()).setFont(boldFont));

            document.add(new Paragraph(" "));

            // ✅ Add Fournisseur details (with null checks)
            if (commande.getFournisseur() != null) {
                document.add(new Paragraph("Fournisseur:").setFont(boldFont));
                document.add(new Paragraph("Nom: " + commande.getFournisseur().getNom()).setFont(boldFont));
                document.add(new Paragraph("Adresse: " + commande.getFournisseur().getAdresse()).setFont(boldFont));
                document.add(new Paragraph("Email: " + commande.getFournisseur().getEmail()).setFont(boldFont));
                document.add(new Paragraph("Numéro de téléphone: " + commande.getFournisseur().getNmrTel()).setFont(boldFont));
            }

            document.add(new Paragraph(" "));

            // ✅ Add User details (avec les bons getters)
            if (commande.getUser() != null) {
                document.add(new Paragraph("Utilisateur:").setFont(boldFont));
                document.add(new Paragraph("Nom: " + commande.getUser().getLastname()).setFont(boldFont));
                document.add(new Paragraph("Prénom: " + commande.getUser().getFirstname()).setFont(boldFont));
                document.add(new Paragraph("Email: " + commande.getUser().getEmail()).setFont(boldFont));
            }

            document.add(new Paragraph(" "));

            // ✅ Add Products table
            document.add(new Paragraph("Produits:").setFont(boldFont));

            // ✅ Create table with 3 columns
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 2}));
            table.useAllAvailableWidth();

            // ✅ Add headers
            table.addHeaderCell(new Cell().add(new Paragraph("Produit").setFont(boldFont)));
            table.addHeaderCell(new Cell().add(new Paragraph("Référence").setFont(boldFont)));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantité Ajoutée").setFont(boldFont)));

            // ✅ Add products
            if (commande.getProduits() != null) {
                for (ProduitCommande produit : commande.getProduits()) {
                    if (produit.getProduit() != null) {
                        table.addCell(new Cell().add(new Paragraph(produit.getProduit().getDesignation()).setFont(font)));
                        table.addCell(new Cell().add(new Paragraph(produit.getProduit().getReference()).setFont(font)));
                        table.addCell(new Cell().add(new Paragraph(String.valueOf(produit.getQuantiteAjoutee())).setFont(font)));
                    }
                }
            }

            document.add(table);

            // ✅ Close document
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error generating PDF: " + e.getMessage(), e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}