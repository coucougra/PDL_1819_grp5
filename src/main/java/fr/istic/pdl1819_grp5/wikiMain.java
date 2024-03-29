package fr.istic.pdl1819_grp5;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class wikiMain {


    public static void main(String[] args) throws IOException {


        if(args.length<2){
            System.err.println("Usage : wiki -<inputFile> -<OuputDirectory>\n" +
                    "-inputFile : list of wikipedia's article title \n" +
                    "-ouputDirectory : destination directory\n");
            System.exit(0);
        }

        File urlsFile = new File(args[0]);

        if(!urlsFile.exists() && !urlsFile.isDirectory()){
            System.err.println("input file not found");
            System.exit(0);
        }

        File directory = new File(args[1]);

        if(!directory.exists() || !directory.isDirectory()){
            System.err.println("Bad destination path");
            System.exit(0);
        }



        File htmlDir = new File(directory.getAbsoluteFile()+""+File.separator+"html");
        File wikitextDir = new File(directory.getAbsoluteFile()+""+File.separator+"wikitext");
        String url;
        String csvFileName;
        htmlDir.mkdir();
        wikitextDir.mkdir();

        WikipediaMatrix wiki = new WikipediaMatrix();

        // Html extraction
        wiki.setUrlsMatrix(getListofUrls(urlsFile));
        wiki.setExtractType(ExtractType.HTML);
        System.out.println("Extracting via html...");
        Set<UrlMatrix> urlMatrixSet = wiki.getConvertResult();
        //save files
        for (UrlMatrix urlMatrix : urlMatrixSet){
            int i=0;
            url=urlMatrix.getLink();

            Set<FileMatrix> fileMatrices = urlMatrix.getFileMatrix();
            for (FileMatrix f : fileMatrices){
                csvFileName=mkCSVFileName(url.substring(url.lastIndexOf("/")+1,url.length()),i);
                f.saveCsv(htmlDir.getAbsolutePath()+File.separator+csvFileName);
                i++;
            }

        }

        // Wikitext extraction
        wiki.setUrlsMatrix(getListofUrls(urlsFile));
        wiki.setExtractType(ExtractType.WIKITEXT);
        System.out.println("Extracting via wikitext...");
        urlMatrixSet = wiki.getConvertResult();
        //save files
        for (UrlMatrix urlMatrix : urlMatrixSet){
            Set<FileMatrix> fileMatrices = urlMatrix.getFileMatrix();
            int i=0;
            url=urlMatrix.getLink();

            for (FileMatrix f : fileMatrices){
                csvFileName=mkCSVFileName(url.substring(url.lastIndexOf("/")+1,url.length()),i);
                f.saveCsv(wikitextDir.getAbsolutePath()+File.separator+csvFileName);
                i++;
            }

        }

    }

    private static Set<UrlMatrix> getListofUrls(File inputFile) throws IOException {
        Set<UrlMatrix> urlsMatrix = new HashSet<UrlMatrix>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(inputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String BASE_WIKIPEDIA_URL = "https://en.wikipedia.org/wiki/";
        String url;
        String wurl;
        while ((url = br.readLine()) != null) {
            wurl=BASE_WIKIPEDIA_URL+url;
            urlsMatrix.add(new UrlMatrix(wurl));
        }

        return urlsMatrix;


    }
    private static String mkCSVFileName(String url, int n) {
        return url.trim() + "-" + n + ".csv";
    }
}
