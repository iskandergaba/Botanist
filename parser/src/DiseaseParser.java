package PlantParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;

public class DiseaseParser {

    //for parsing plant disease information
    private static final char DELIMETER = ',';
    private static final String THREE_TABS = "\t\t\t";
    private static final int NUM_DISEASES = 32;
    private static final int FAMILY_NAME_INDEX = 0;
    private static final String INPUT_FILENAME = "plantdiseases.csv";
    private static final String OUTPUT_FILENAME = "diseasesOut.json";
    private static final String DISEASE_REFS_FILE = "diseaseUrls.csv";
    
    private File input;
    private File output;
    private BufferedReader br;
    private BufferedWriter bw;
    private Map<String, List<String>> familyDiseaseMap;
    private Map<String, String> diseaseUrls;
    /**
     * Create a parser for diseases file
     * @param inputFileName - plant disease csv
     * @param outputFileName - firebase .json
     * @throws IOException if a read/write error occurs
     */
    public DiseaseParser(String inputFileName, String outputFileName) throws IOException {
        input = new File(inputFileName);
        output = new File(outputFileName);
        br = new BufferedReader(new FileReader(input));
        bw = new BufferedWriter(new FileWriter(output));
        familyDiseaseMap = new HashMap<String, List<String>>();
        diseaseUrls = parseDiseaseUrls(DISEASE_REFS_FILE);
    }

    /**
    * parseDiseaseUrls() - parses input csv into hashMap of diseases and associated urls 
    * @throws IOException if an error occurs
    */
    public Map<String, String> parseDiseaseUrls(String diseaseRefsFilename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(diseaseRefsFilename));
        String curr = reader.readLine();
        Map<String, String> urls = new HashMap<String, String>();
        while (curr != null) {
            Object[] objectArray = Parser.splitString(curr, DELIMETER).toArray();
            String[] fields = Arrays.copyOf(objectArray, objectArray.length, String[].class);
            String disease = Parser.capitalize(fields[0]);
            urls.put(disease, fields[1]);
            curr = reader.readLine();
        }
        reader.close();
        return urls;
    }

    /**
     * parseInputFile() - parses input csv file into hashMap
     * @throws IOException if an error occurs
     */
    public void parseInputFile() throws IOException {
        String currFamily = br.readLine();
        while (currFamily != null) {
            if (!currFamily.equals("")) {
                Object[] objectArray = Parser.splitString(currFamily, DELIMETER).toArray();
                String[] fields = Arrays.copyOf(objectArray, objectArray.length, String[].class);
                String family = Parser.capitalize(fields[FAMILY_NAME_INDEX]);
                List<String> l = Arrays.asList(fields).subList(FAMILY_NAME_INDEX + 1, fields.length);
                List<String> diseaseList = new LinkedList<String>();
                for (int i = FAMILY_NAME_INDEX + 1; i < l.size(); i++) {
                    if (!l.get(i).equals("")) {
                        diseaseList.add(l.get(i));
                    }
                }
                familyDiseaseMap.put(family, diseaseList);
            }
            currFamily = br.readLine();
        }
        br.close();
    }

    private void writeLine(String s) throws IOException {
        bw.write(s);
        bw.newLine();
    }

        /**
     * parseInputFile() - parses input csv file into hashMap
     * @throws IOException if an error occurs
     */
    public void writeToOutputFile() throws IOException {
        writeLine("\t\"Diseases\": {");
        int count = 0;
        for (String s : familyDiseaseMap.keySet()) {
            List<String> diseases = familyDiseaseMap.get(s);
            bw.write(THREE_TABS + "\"" + s + "\" : [");
            int diseaseCount = 0;
            for (String d : diseases) {
                if (!d.trim().equals("")) {
                    if (diseaseCount < diseases.size() - 1) {
                        bw.write("\"" + Parser.capitalize(d) + "\",");
                    } else {
                        bw.write("\"" + Parser.capitalize(d) + "\"");
                    }
                    diseaseCount++;  
                }
                              
            }
            count++;
            if (count < familyDiseaseMap.size()) {
                writeLine("],");
            } else {
                writeLine("]");
            }
        }
        writeLine("\t},");
        writeLine("\t\"DiseaseUrls\": {");
        count = 0;
        for (String s : diseaseUrls.keySet()) {
            String url = diseaseUrls.get(s);
            if (count < diseaseUrls.size()) {
                writeLine(THREE_TABS + "\"" + s + "\" : \"" + url + "\",");
            } else {
                writeLine(THREE_TABS + "\"" + s + "\" : \"" + url + "\"");
            }
            count++;
        }
        writeLine("\t}");
        bw.flush();
        bw.close();
    }

    public static void main(String[] args) {
        try {
            DiseaseParser p = new DiseaseParser(INPUT_FILENAME, OUTPUT_FILENAME);
            p.parseInputFile();
            p.writeToOutputFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}