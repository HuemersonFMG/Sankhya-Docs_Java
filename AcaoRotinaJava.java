import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;

public class App implements AcaoRotinaJava {

    @Override
    public void doAction (ContextoAcao contexto) throws Exception {

        if (contexto.getLinhas ().length == 0)
            contexto.mostraErro ("Selecione, ao menos, um registro!");

        List <File> files = new ArrayList <> ();

        for (Registro registro : contexto.getLinhas ()) {
            File file = App.gerar (registro);
        }

        File compactedFile = App.compactar (notas);
        byte [] characters = Files.readAllBytes (compactedFile.toPath ());
        String base64Content = Base64.getEncoder ().encodeToString (characters);

        String html = ""
            + "<script> "
                + "(function () { "
                    + "const date = new Date (); "
                    + "const timestamp = `${ "
                        + "String ( date.getFullYear ()  ).padStart( 2, '0' ) }-${ "
                        + "String ( date.getMonth () + 1 ).padStart( 2, '0' ) }-${ "
                        + "String ( date.getDate ()      ).padStart( 2, '0' ) }`; "
                    + "const link = document.createElement( 'a' ); "
                    + "link.href = 'data:application/octet-stream;base64," + base64Content + "'; "
                    + "link.download = `${ timestamp }.zip`; "
                    + "link.target = '_blank'; "
                    + "document.body.appendChild (link); "
                    + "link.click (); "
                    + "document.body.removeChild (link); "
                + "}) (); "
            + "</script> ";

        contexto.setMensagemRetorno (html);

    }
    
    public static File gerar (Registro registro) throws IOException, MGEModelException {

        // Busque o conteudo que deseja e transforme ele como um arquivo
        return // new File...
    
    }

    public static File compactar (List <File> arquivos) throws IOException {

        Calendar calendario = Calendar.getInstance ();
        calendario.setTime (new Date ());
        String ano = String.format ("%04d", calendario.get ( Calendar.YEAR ));
        String mes = String.format ("%02d", calendario.get ( Calendar.MONTH ) + 1);
        String dia = String.format ("%02d", calendario.get ( Calendar.DAY_OF_MONTH ));

        // Nome do arquivo ZIP
        String nomeArquivo = String.format ("ZIP_%s-%s-%s", ano, mes, dia);

        // Crie o arquivo ZIP arquivo temporario aqui
        File zip = File.createTempFile ( nomeArquivo, "tmp" );

        // Compactacao dos arquivos
        try (ZipOutputStream zipOutputStream = new ZipOutputStream (new FileOutputStream (zip))) {

            for (File arquivo : arquivos) {
                zipOutputStream.putNextEntry (new ZipEntry (arquivo.getName ()));
                zipOutputStream.write (Files.readAllBytes (arquivo.toPath ()));
                zipOutputStream.closeEntry ();
            }

        }

        return zip;
    }

}