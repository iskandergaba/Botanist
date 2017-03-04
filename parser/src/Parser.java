/**
 * Parses input csv file from USDA plants database and converts it to JSON format for use with Firebase database.
 * @author Antonio Muscarella
 */
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
public class Parser
{
    private static final String DELIMETER = ",";
    private static final String THREE_TABS = "\t\t\t";
    private static final int NUM_OTHER_FIELDS = 37;
    private static final int SPECIES_NAME_INDEX = 1;
    //column numbers of the different fields for the plants in the csv file, stored as constants in case the input file changes
    private static final int COMMON_NAME_INDEX = 0;
    private static final int GROUP_NAME_INDEX = 1;
    private static final int DURATION_INDEX = 2;
    private static final int GROWTH_HABIT_INDEX = 3;
    private static final int ACTIVE_GROWTH_PERIOD_INDEX = 4;
    private static final int FLOWER_COLOR_INDEX = 5;
    private static final int FOLIAGE_COLOR_INDEX = 6;
    private static final int FRUIT_COLOR_INDEX = 7;
    private static final int GROWTH_FORM_INDEX = 8;
    private static final int GROWTH_RATE_INDEX = 9;
    private static final int MAX_HEIGHT_20_YRS_INDEX = 10;
    private static final int MATURE_HEIGHT_INDEX = 11;
    private static final int LIFESPAN_INDEX = 12;
    private static final int TOXICITY_INDEX = 13;
    private static final int COARSE_SOILS_INDEX = 14;
    private static final int MEDIUM_SOILS_INDEX = 15;
    private static final int FINE_SOILS_INDEX = 16;
    private static final int ANAEROBIC_TOLERANCE_INDEX = 17;
    private static final int CACO3_TOLERANCE_INDEX = 18;
    private static final int COLD_STATIFICATION_INDEX = 19;
    private static final int HEDGE_TOLERANCE_INDEX = 20;
    private static final int MOISTURE_USE_INDEX = 21;
    private static final int SOIL_PH_MIN_INDEX = 22;
    private static final int SOIL_PH_MAX_INDEX = 23;
    private static final int MIN_PLANTING_DENSITY_INDEX = 24;
    private static final int MAX_PLANTING_DENSITY_INDEX = 25;
    private static final int PRECIPITATION_MIN_INDEX = 26;
    private static final int PRECIPITATION_MAX_INDEX = 27;
    private static final int ROOT_DEPTH_MIN_INDEX = 28;
    private static final int SALINITY_TOLERANCE_INDEX = 29;
    private static final int SHADE_TOLERANCE_INDEX = 30;
    private static final int TEMPERATURE_MIN_INDEX = 31;
    private static final int BLOOM_PERIOD_INDEX = 32;
    private static final int COMMERCIAL_AVAILABILITY_INDEX = 33;
    private static final int PALATABLE_BROWSE_ANIMAL_INDEX = 34;
    private static final int PALATABLE_GRAZE_ANIMAL_INDEX = 35;
    private static final int PALATABLE_HUMAN_INDEX = 36;
    private File input;
    private File output;
    private BufferedReader br;
    private BufferedWriter bw;
    private Map<String, String[]> plantMap;
    /**
     * Create a parser
     * @param inputFileName - plant csv
     * @param outputFileName - firebase .json
     * @throws IOException if a read/write error occurs
     */
    public Parser(String inputFileName, String outputFileName) throws IOException
    {
        input = new File(inputFileName);
        output = new File(outputFileName);
        br = new BufferedReader(new FileReader(input));
        bw = new BufferedWriter(new FileWriter(output));
        plantMap = new HashMap<String, String[]>();
    }

    /**
     * parseInputFile() - parses input csv file into hashMap
     * @throws IOException if an error occurs
     */
    public void parseInputFile() throws IOException
    {
        String currPlant = br.readLine();
        while (currPlant != null)
        {
            if (!currPlant.contains("?"))
            {
                String[] fields = currPlant.split(DELIMETER);
                String species = capitalize(fields[SPECIES_NAME_INDEX]);
                List<String> l = Arrays.asList(fields).subList(SPECIES_NAME_INDEX + 1, fields.length);
                String[] otherFields = new String[l.size()];
                for (int i = 0; i < otherFields.length; i++)
                {
                    otherFields[i] = l.get(i);
                }
                plantMap.put(species, otherFields);
            }
            currPlant = br.readLine();
        }
        br.close();
    }

    /**
     * Write a line to the file
     * @param s - line to write
     * @throws IOException if the file cannot be reached
     */
    private void writeLine(String s) throws IOException
    {
        bw.write(s);
        bw.newLine();
    }

    /**
     * writeToOutputFile() - generates output JSON file format
     * @throws IOException if the file cannot be reached.
     */
    public void writeToOutputFile() throws IOException
    {
        writeLine("{");
        writeLine("\t\"PlantsData\": {");
        boolean isFirst = true;
        for (String s: plantMap.keySet())
        {
            if ((s == null) || s.equals(""))
            {
                continue;
            }
            if (isFirst)
            {
                isFirst = false;
            }
            else
            {
                writeLine("\t\t},");
            }
            //write species as a key
            writeLine("\t\t\"" + s + "\": {");
            //write other fields as values under the species name
            String[] otherFields = plantMap.get(s);
            writeLine(THREE_TABS + "\"common name\": \"" + capitalize(plantMap.get(s)[COMMON_NAME_INDEX]) + "\",");
            writeLine(THREE_TABS + "\"group\": \"" + plantMap.get(s)[GROUP_NAME_INDEX] + "\",");
            writeLine(THREE_TABS + "\"duration\": \"" + plantMap.get(s)[DURATION_INDEX] + "\",");
            writeLine(THREE_TABS + "\"growth habit\": \"" + plantMap.get(s)[GROWTH_HABIT_INDEX] + "\",");
            writeLine(THREE_TABS + "\"active growth period\": \"" + plantMap.get(s)[ACTIVE_GROWTH_PERIOD_INDEX] + "\",");
            writeLine(THREE_TABS + "\"flower color\": \"" + plantMap.get(s)[FLOWER_COLOR_INDEX] + "\",");
            writeLine(THREE_TABS + "\"foliage color\": \"" + plantMap.get(s)[FOLIAGE_COLOR_INDEX] + "\",");
            writeLine(THREE_TABS + "\"fruit color\": \"" + plantMap.get(s)[FRUIT_COLOR_INDEX] + "\",");
            writeLine(THREE_TABS + "\"growth form\": \"" + plantMap.get(s)[GROWTH_FORM_INDEX] + "\",");
            writeLine(THREE_TABS + "\"growth rate\": \"" + plantMap.get(s)[GROWTH_RATE_INDEX] + "\",");
            writeLine(THREE_TABS + "\"max height 20 years\": \"" + plantMap.get(s)[MAX_HEIGHT_20_YRS_INDEX] + "\",");
            writeLine(THREE_TABS + "\"mature height\": \"" + plantMap.get(s)[MATURE_HEIGHT_INDEX] + "\",");
            writeLine(THREE_TABS + "\"lifespan\": \"" + plantMap.get(s)[LIFESPAN_INDEX] + "\",");
            //for toxicity, insert a default "unknown" if field is blank
            if (plantMap.get(s)[TOXICITY_INDEX].equals(""))
            {
                writeLine(THREE_TABS + "\"toxicity\": \"unknown\",");
            }
            else
            {
                writeLine(THREE_TABS + "\"toxicity\": \"" + plantMap.get(s)[TOXICITY_INDEX] + "\",");
            }
            writeLine(THREE_TABS + "\"adapted coarse soils\": \"" + plantMap.get(s)[COARSE_SOILS_INDEX] + "\",");
            writeLine(THREE_TABS + "\"adapted medium soils\": \"" + plantMap.get(s)[MEDIUM_SOILS_INDEX] + "\",");
            writeLine(THREE_TABS + "\"adapted fine soils\": \"" + plantMap.get(s)[FINE_SOILS_INDEX] + "\",");
            writeLine(THREE_TABS + "\"anaerobic tolerance\": \"" + plantMap.get(s)[ANAEROBIC_TOLERANCE_INDEX] + "\",");
            writeLine(THREE_TABS + "\"CaCO3 tolerance\": \"" + plantMap.get(s)[CACO3_TOLERANCE_INDEX] + "\",");
            writeLine(THREE_TABS + "\"cold stratification\": \"" + plantMap.get(s)[COLD_STATIFICATION_INDEX] + "\",");
            writeLine(THREE_TABS + "\"hedge tolerance\": \"" + plantMap.get(s)[HEDGE_TOLERANCE_INDEX] + "\",");
            writeLine(THREE_TABS + "\"moisture use\": \"" + plantMap.get(s)[MOISTURE_USE_INDEX] + "\",");
            //for pH range, check if fields are missing or are both zero, if so put N/A
            if (plantMap.get(s)[SOIL_PH_MIN_INDEX].equals("0") || plantMap.get(s)[SOIL_PH_MAX_INDEX].equals("0") ||  
                plantMap.get(s)[SOIL_PH_MIN_INDEX].equals("NA") || plantMap.get(s)[SOIL_PH_MAX_INDEX].equals("NA"))
            {
                writeLine(THREE_TABS + "\"soil pH range\": \"NA\",");
            }
            else
            {
                writeLine(THREE_TABS + "\"soil pH range\": \"" + plantMap.get(s)[SOIL_PH_MIN_INDEX] + "-" + plantMap.get(s)[SOIL_PH_MAX_INDEX] + "\",");
            }
            writeLine(THREE_TABS + "\"minimum planting density\": \"" + plantMap.get(s)[MIN_PLANTING_DENSITY_INDEX] + "\",");
            writeLine(THREE_TABS + "\"max planting density\": \"" + plantMap.get(s)[MAX_PLANTING_DENSITY_INDEX] + "\",");
            writeLine(THREE_TABS + "\"precipitation minimum\": \"" + plantMap.get(s)[PRECIPITATION_MIN_INDEX] + "\",");
            writeLine(THREE_TABS + "\"precipitation maximum\": \"" + plantMap.get(s)[PRECIPITATION_MAX_INDEX] + "\",");
            writeLine(THREE_TABS + "\"root depth minimum\": \"" + plantMap.get(s)[ROOT_DEPTH_MIN_INDEX] + "\",");
            writeLine(THREE_TABS + "\"salinity tolerance\": \"" + plantMap.get(s)[SALINITY_TOLERANCE_INDEX] + "\",");
            writeLine(THREE_TABS + "\"shade tolerance\": \"" + plantMap.get(s)[SHADE_TOLERANCE_INDEX] + "\",");
            writeLine(THREE_TABS + "\"temperature minimum\": \"" + plantMap.get(s)[TEMPERATURE_MIN_INDEX] + "\",");
            if ((plantMap.get(s).length - 1) < BLOOM_PERIOD_INDEX)
            {
                writeLine(THREE_TABS + "\"bloom period\": \"\",");
            }
            else
            {
                writeLine(THREE_TABS + "\"bloom period\": \"" + plantMap.get(s)[BLOOM_PERIOD_INDEX] + "\",");
            }
            if (plantMap.get(s).length - 1 < COMMERCIAL_AVAILABILITY_INDEX)
            {
                writeLine(THREE_TABS + "\"commercial availability\": \"\",");
            }
            else
            {
                writeLine(THREE_TABS + "\"commercial availability\": \"" + plantMap.get(s)[COMMERCIAL_AVAILABILITY_INDEX] + "\",");
            }
            if (plantMap.get(s).length - 1 < PALATABLE_BROWSE_ANIMAL_INDEX)
            {
                writeLine(THREE_TABS + "\"palatable browse animal\": \"\",");
            }
            else
            {
                writeLine(THREE_TABS + "\"palatable browse animal\": \"" + plantMap.get(s)[PALATABLE_BROWSE_ANIMAL_INDEX] + "\","); 
            }

            if (plantMap.get(s).length - 1 < PALATABLE_GRAZE_ANIMAL_INDEX)
            {
                writeLine(THREE_TABS + "\"palatable graze animal\": \"\",");
            }
            else
            {
                writeLine(THREE_TABS + "\"palatable graze animal\": \"" + plantMap.get(s)[PALATABLE_GRAZE_ANIMAL_INDEX] + "\",");
            }
            if (plantMap.get(s).length - 1 < PALATABLE_HUMAN_INDEX)
            {
                writeLine(THREE_TABS + "\"palatable human\": \"\"");
            }
            else
            {
                writeLine(THREE_TABS + "\"palatable human\": \"" + plantMap.get(s)[PALATABLE_HUMAN_INDEX] + "\"");
            }
        }
        writeLine("\t\t}");
        writeLine("\t},");
        // SpeciesNames
        writeLine("\t\"SpeciesNames\": [");
        int count = 0;
        for (String s: plantMap.keySet())
        {
            //write species as a key
            count++;
            if (count == plantMap.size())
            {
                writeLine("\t\t\"" + s + "\"");
            }
            else
            {
                writeLine("\t\t\"" + s + "\",");
            }
        }
        writeLine("\t],");
        // CommonNames
        writeLine("\t\"CommonNames\": [");
        count = 0;
        for (String s: plantMap.keySet())
        {
            count++;
            String common = plantMap.get(s)[COMMON_NAME_INDEX];
            if (common.equals("") || (common == null))
            {
                continue;
            }
            //write common name as key
            if (count == plantMap.size())
            {
                writeLine("\t\t\"" + capitalize(common) + "\"");
            }
            else
            {
                writeLine("\t\t\"" + capitalize(common) + "\",");
            }
        }
        writeLine("\t]");
        bw.write("}");
        bw.flush();
        bw.close();
    }

    /**
     * Run from command line
     * @param args - command line arguments
     */
    public static void main (String[] args)
    {
        try
        {
            Parser p = new Parser("usdaplants.csv", "out.json");
            p.parseInputFile();
            p.writeToOutputFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Capitalize a string
     * @param line - line to capitalize
     * @return Returns the capitalized line
     */
    public static String capitalize(String line)
    {
        if ((line == null) || line.equals(""))
        {
            return line;
        }
        StringBuilder capped = new StringBuilder().append(Character.toUpperCase(line.charAt(0)));
        for (int i = 1; i < line.length(); i++)
        {
            char c = line.charAt(i);
            if ((c == ' ') && (++i < line.length()))
            {
                capped.append(c).append(Character.toUpperCase(line.charAt(i)));
            }
            else
            {
                capped.append(c);
            }
        }
        return capped.toString();
    }
}