// Entry in PlantsData database child
// @author: Christopher Besser and Antonio Muscarella
package com.scientists.happy.botanist.data;
import java.util.List;
@SuppressWarnings({"WeakerAccess", "unused"})
public class PlantEntry {
    private String commonName, group, duration, growthHabit, endangered, active, afterHarvest;
    private String flowerColor, foliageColor, fruitColor, growthRate, matureHeight, life, toxicity;
    private String coarseSoil, mediumSoil, fineSoil, anaerobic, moisture, pHRange, salinity, shade;
    private String bloomPeriod, availability, seedBegin, seedEnd, seedPersistence, vigor;
    private String pruningReq, wildAnimalPalate, grazeAnimalPalate, humanPalate, fertility;
    private List<String> noxious;
    private String baseHeight, minRain, maxRain, minDepth, minTemp;
    /**
     * Required by Firebase, this useless constructor must remain
     */
    public PlantEntry() {
    }

    /**
     * Create a PlantsData entry wrapper
     * @param commonName - commonly known as
     * @param group - family of plants
     * @param duration - when the plant grows
     * @param growthHabit - how the plant grows
     * @param noxious - the plant's noxious classifications
     * @param endangered - whether the US government classifies this plant as endangered
     * @param active - time of year the plant grows
     * @param afterHarvest - how quickly plant regrows when harvested
     * @param flowerColor - flower color
     * @param foliageColor - leaves color
     * @param fruitColor - fruit color
     * @param growthRate - growth rate
     * @param baseHeight - starting height
     * @param matureHeight - maximum height
     * @param life - life expectancy
     * @param toxicity - toxicity to humans
     * @param coarseSoil - adaptivity to coarse soil
     * @param mediumSoil - adaptivity to medium soil
     * @param fineSoil - adaptivity to fine soil
     * @param anaerobic - anaerobic tolerance
     * @param moisture - moisture usage
     * @param pHRange - pH range the plant lives in
     * @param minRain - minimum precipitation requirement
     * @param maxRain - maximum precipitation requirement
     * @param minDepth - minimum soil depth
     * @param salinity - required soil salinity
     * @param shade - shade tolerance
     * @param minTemp - minimum temperature
     * @param bloomPeriod - when the plant blooms
     * @param availability - can this plant be bought
     * @param seedBegin - when seeds appear
     * @param seedEnd - when seeds stop appearing
     * @param seedPersistence - how long seeds stay with plant
     * @param vigor - how likely seeds are to sprout
     * @param pruningReq - whether this plant needs pruning
     * @param wildAnimalPalate - whether wild animals will eat this plant
     * @param grazeAnimalPalate - whether pets/farm animals will eat the plant
     * @param humanPalate - whether humans will eat the plant
     * @param fertility - what fertilizer the plant needs
     */
    public PlantEntry(String commonName, String group, String duration, String growthHabit,
                      String endangered, String active, String afterHarvest, String flowerColor,
                      String foliageColor, String fruitColor, String growthRate, String baseHeight,
                      String matureHeight, String life, String toxicity, String coarseSoil,
                      String mediumSoil, String fineSoil, String anaerobic, String moisture,
                      String pHRange, String minRain, String maxRain, String minDepth,
                      String salinity, String shade, String minTemp, String bloomPeriod,
                      String availability, String seedBegin, String seedEnd, String seedPersistence,
                      String vigor, String pruningReq, String wildAnimalPalate,
                      String grazeAnimalPalate, String humanPalate, String fertility,
                      List<String> noxious) {
        this.commonName = commonName;
        this.group = group;
        this.duration = duration;
        this.growthHabit = growthHabit;
        this.endangered = endangered;
        this.active = active;
        this.afterHarvest = afterHarvest;
        this.flowerColor = flowerColor;
        this.foliageColor = foliageColor;
        this.fruitColor = fruitColor;
        this.growthRate = growthRate;
        this.baseHeight = baseHeight;
        this.matureHeight = matureHeight;
        this.life = life;
        this.toxicity = toxicity;
        this.coarseSoil = coarseSoil;
        this.mediumSoil = mediumSoil;
        this.fineSoil = fineSoil;
        this.anaerobic = anaerobic;
        this.moisture = moisture;
        this.pHRange = pHRange;
        this.minRain = minRain;
        this.maxRain = maxRain;
        this.minDepth = minDepth;
        this.salinity = salinity;
        this.shade = shade;
        this.minTemp = minTemp;
        this.bloomPeriod = bloomPeriod;
        this.availability = availability;
        this.seedBegin = seedBegin;
        this.seedEnd = seedEnd;
        this.seedPersistence = seedPersistence;
        this.vigor = vigor;
        this.pruningReq = pruningReq;
        this.wildAnimalPalate = wildAnimalPalate;
        this.grazeAnimalPalate = grazeAnimalPalate;
        this.humanPalate = humanPalate;
        this.fertility = fertility;
        this.noxious = noxious;
    }

    /**
     * Retrieve the common name
     * @return Returns the common name
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Set common name
     * @param name - the common name
     */
    public void setCommonName(String name) {
        commonName = name;
    }

    /**
     * Get group
     * @return - Returns group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Set group
     * @param group the plant's group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Get growth duration
     * @return Returns growth duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Set growth duration
     * @param period - the growth duration
     */
    public void setDuration(String period) {
        duration = period;
    }

    /**
     * Get growth habit
     * @return Returns growth habit
     */
    public String getGrowthHabit() {
        return growthHabit;
    }

    /**
     * Set growth habit
     * @param habit - growth habit
     */
    public void setGrowthHabit(String habit) {
        growthHabit = habit;
    }

    /**
     * Get endangered status
     * @return Returns endangered status
     */
    public String getEndangered() {
        return endangered;
    }

    /**
     * Set endangered status
     * @param status - endangered status
     */
    public void setEndangered(String status) {
        endangered = status;
    }

    /**
     * Get active growth period
     * @return Returns active growth period
     */
    public String getActive() {
        return active;
    }

    /**
     * Set active growth period
     * @param period - Active growth period
     */
    public void setActive(String period) {
        active = period;
    }

    /**
     * Get after harvest regrowth rate
     * @return Returns after harvest regrowth rate
     */
    public String getAfterHarvest() {
        return afterHarvest;
    }

    /**
     * Set after harvest regrowth rate
     * @param rate - after harvest regrowth rate
     */
    public void setAfterHarvest(String rate) {
        afterHarvest = rate;
    }

    /**
     * Get flower color
     * @return Returns flower color
     */
    public String getFlowerColor() {
        return flowerColor;
    }

    /**
     * Set flower color
     * @param color - flower color
     */
    public void setFlowerColor(String color) {
        flowerColor = color;
    }

    /**
     * Get foliage color
     * @return Returns foliage color
     */
    public String getFoliageColor() {
        return foliageColor;
    }

    /**
     * Set foliage color
     * @param color - foliage color
     */
    public void setFoliageColor(String color) {
        foliageColor = color;
    }

    /**
     * Get fruit color
     * @return Returns fruit color
     */
    public String getFruitColor() {
        return fruitColor;
    }

    /**
     * Set fruit color
     * @param color - fruit color
     */
    public void setFruitColor(String color) {
        fruitColor = color;
    }

    /**
     * Get growth rate
     * @return Returns growth rate
     */
    public String getGrowthRate() {
        return growthRate;
    }

    /**
     * Set growth rate
     * @param rate - growth rate
     */
    public void setGrowthRate(String rate) {
        growthRate = rate;
    }

    /**
     * Get mature height
     * @return Returns mature height
     */
    public String getMatureHeight() {
        return matureHeight;
    }

    /**
     * Set mature height
     * @param height - mature height
     */
    public void setMatureHeight(String height) {
        matureHeight = height;
    }

    /**
     * Get toxicity
     * @return Returns toxicity
     */
    public String getToxicity() {
        return toxicity;
    }

    /**
     * Set toxicity
     * @param toxic - toxicity
     */
    public void setToxicity(String toxic) {
        toxicity = toxic;
    }

    /**
     * Get expected lifespan
     * @return Returns expected lifespan
     */
    public String getLife() {
        return life;
    }

    /**
     * Set expected lifespan
     * @param lifespan - expected lifespan
     */
    public void setLife(String lifespan) {
        life = lifespan;
    }

    /**
     * Get coarse soil adaptivity
     * @return Returns coarse soil adaptivity
     */
    public String getCoarseSoil() {
        return coarseSoil;
    }

    /**
     * Set course soil adaptivity
     * @param soil - coarse soil adaptivity
     */
    public void setCoarseSoil(String soil) {
        coarseSoil = soil;
    }

    /**
     * Get medium soil adaptivity
     * @return Returns medium soil adaptivity
     */
    public String getMediumSoil() {
        return mediumSoil;
    }

    /**
     * Set medium soil adaptivity
     * @param soil - medium soil adaptivity
     */
    public void setMediumSoil(String soil) {
        mediumSoil = soil;
    }

    /**
     * Get fine soil adaptivity
     * @return Returns fine soil adaptivity
     */
    public String getFineSoil() {
        return fineSoil;
    }

    /**
     * Set fine soil adaptivity
     * @param soil - fine soil adaptivity
     */
    public void setFineSoil(String soil) {
        fineSoil = soil;
    }

    /**
     * Get anaerboic tolerance
     * @return Returns anaerobic tolerance
     */
    public String getAnaerobic() {
        return anaerobic;
    }

    /**
     * Set anaerobic tolerance
     * @param tolerance - anaerobic tolerance
     */
    public void setAnaerobic(String tolerance) {
        anaerobic = tolerance;
    }

    /**
     * Get moisture requirements
     * @return Returns moisture requirements
     */
    public String getMoisture() {
        return moisture;
    }

    /**
     * Set moisture requirements
     * @param moist - moisture requirements
     */
    public void setMoisture(String moist) {
        moisture = moist;
    }

    /**
     * Get soil pH range
     * @return Returns soil pH range
     */
    public String getPHRange() {
        return pHRange;
    }

    /**
     * Set soil pH Range
     * @param pH - soil pH range
     */
    public void setPHRange(String pH) {
        pHRange = pH;
    }

    /**
     * Get soil salinity requirements
     * @return Returns soil salinity requirements
     */
    public String getSalinity() {
        return salinity;
    }

    /**
     * Set soil salinity requirements
     * @param salt - soil salinity requirements
     */
    public void setSalinity(String salt) {
        salinity = salt;
    }

    /**
     * Get shade tolerance
     * @return Returns shade tolerance
     */
    public String getShade() {
        return shade;
    }

    /**
     * Set shade tolerance
     * @param shade - shade tolerance
     */
    public void setShade(String shade) {
        this.shade = shade;
    }

    /**
     * Get availability
     * @return Returns availability
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * Set availability
     * @param buy - can I buy this?
     */
    public void setAvailability(String buy) {
        availability = buy;
    }

    /**
     * Get bloom period
     * @return Returns bloom period
     */
    public String getBloomPeriod() {
        return bloomPeriod;
    }

    /**
     * Set bloom period
     * @param period - bloom period
     */
    public void setBloomPeriod(String period) {
        bloomPeriod = period;
    }

    /**
     * Get seed start period
     * @return Returns seed start period
     */
    public String getSeedBegin() {
        return seedBegin;
    }

    /**
     * Set seed start period
     * @param period - seed start period
     */
    public void setSeedBegin(String period) {
        seedBegin = period;
    }

    /**
     * Get seed end period
     * @return Returns seed disappearance period
     */
    public String getSeedEnd() {
        return seedEnd;
    }

    /**
     * Set seed end period
     * @param period - seed disappearance period
     */
    public void setSeedEnd(String period) {
        seedEnd = period;
    }

    /**
     * Get seed persistence
     * @return Returns seed persistence
     */
    public String getSeedPersistence() {
        return seedPersistence;
    }

    /**
     * Set seed persistence
     * @param seed - seed persistence
     */
    public void setSeedPersistence(String seed) {
        seedPersistence = seed;
    }

    /**
     * Get seed vigor
     * @return Returns seed vigor
     */
    public String getVigor() {
        return vigor;
    }

    /**
     * Set seed vigor
     * @param seed - seed vigor
     */
    public void setVigor(String seed) {
        vigor = seed;
    }

    /**
     * Get pruning requirements
     * @return Returns pruning requirements
     */
    public String getPruningReq() {
        return pruningReq;
    }

    /**
     * Set pruning requirements
     * @param prune - pruning requirements
     */
    public void setPruningReq(String prune) {
        pruningReq = prune;
    }

    /**
     * Get wild animal palatable
     * @return Returns wild animal palatable
     */
    public String getWildAnimalPalate() {
        return wildAnimalPalate;
    }

    /**
     * Set wild animal palatable
     * @param palate - wild animal palatable
     */
    public void setWildAnimalPalate(String palate) {
        wildAnimalPalate = palate;
    }

    /**
     * Get graze animal palatable
     * @return Returns graze animal palatable
     */
    public String getGrazeAnimalPalate() {
        return grazeAnimalPalate;
    }

    /**
     * Set graze animal palatable
     * @param palate - graze animal palatable
     */
    public void setGrazeAnimalPalate(String palate) {
        grazeAnimalPalate = palate;
    }

    /**
     * Get human palatable
     * @return Returns human palatable
     */
    public String getHumanPalate() {
        return humanPalate;
    }

    /**
     * Set human palatable
     * @param palate - human palatable
     */
    public void setHumanPalate(String palate) {
        humanPalate = palate;
    }

    /**
     * Get fertilizer requirements
     * @return Returns fertilizer requirements
     */
    public String getFertility() {
        return fertility;
    }

    /**
     * Set fertilizer requirements
     * @param req - fertilizer requirements
     */
    public void setFertility(String req) {
        fertility = req;
    }

    /**
     * Get noxious statuses
     * @return Returns the noxious statuses
     */
    public List<String> getNoxious() {
        return noxious;
    }

    /**
     * Set noxious statuses
     * @param statuses - noxious statuses
     */
    public void setNoxious(List<String> statuses) {
        noxious = statuses;
    }

    /**
     * Get base height
     * @return Returns base height
     */
    public String getBaseHeight() {
        return baseHeight;
    }

    /**
     * Set base height
     * @param height - base height
     */
    public void setBaseHeight(String height) {
        baseHeight = height;
    }

    /**
     * Get minimum precipitation requirements
     * @return Returns minimum precipitation requirements
     */
    public String getMinRain() {
        return minRain;
    }

    /**
     * Set minimum precipitation requirements
     * @param rain - minimum precipitation requirements
     */
    public void setMinRain(String rain) {
        minRain = rain;
    }

    /**
     * Get maximum precipitation requirement
     * @return Returns maximum precipitation requirement
     */
    public String getMaxRain() {
        return maxRain;
    }

    /**
     * Set maximum precipitation requirements
     * @param rain - maximum precipitation requirements
     */
    public void setMaxRain(String rain) {
        maxRain = rain;
    }

    /**
     * Get minimum soil depth
     * @return Returns minimum soil depth
     */
    public String getMinDepth() {
        return minDepth;
    }

    /**
     * Set minimum soil depth
     * @param depth - minimum soil depth
     */
    public void setMinDepth(String depth) {
        minDepth = depth;
    }

    /**
     * Get minimum temperature
     * @return Returns minimum temperature
     */
    public String getMinTemp() {
        return minTemp;
    }

    /**
     * Set minimum temperature
     * @param temp - minimum temperature
     */
    public void setMinTemp(String temp) {
        minTemp = temp;
    }

    /**
     * check if toxic
     * @return true if toxic, false otherwise
     */
    public boolean isToxic() {
        return toxicity != null && (toxicity.equals("Slight") || toxicity.equals("Moderate") || toxicity.equals("Severe"));
    }

    /**
     * Describe shade tolerance
     * @return Returns the description
     */
    private String inferSunRequirements() {
        if (!hasMeaningfulData(shade)) {
            return "NA";
        }
        else if (shade.equals("Intolerant")) {
            return "• This plant has a low shade tolerance and should receive full sunlight.\n";
        }
        else if (shade.equals("Intermediate")) {
            return "• This plant has a moderate shade tolerance and should receive moderate sunlight.\n";
        }
        else {
            return "• This plant has high shade tolerance and may be placed in partially shaded areas.\n";
        }
    }

    /**
     * Describe soil requirements
     * @return Returns the description
     */
    private String inferSoilRequirements() {
        if (!hasMeaningfulData(fineSoil) && !hasMeaningfulData(mediumSoil) && !hasMeaningfulData(coarseSoil)) {
            return "NA";
        }
        StringBuilder output = new StringBuilder("");
        if (fineSoil.equals("Yes")) {
            output.append("fine");
            if ((coarseSoil.equals("No") || !hasMeaningfulData(coarseSoil)) ^
                    (mediumSoil.equals("No") || !hasMeaningfulData(mediumSoil))) {
                output.append(" and ");
            }
        }
        if (mediumSoil.equals("Yes")) {
            if (fineSoil.equals("Yes") && coarseSoil.equals("Yes")) {
                output.append(",");
            }
            output.append(" medium");
            if ((coarseSoil.equals("Yes"))) {
                output.append(", and");
            }
        }
        if (coarseSoil.equals("Yes")) {
            output.append(" coarse");
        }
        return output.toString();
    }

    /**
     * check if plant data entry is meaningful (not NA, empty, or null)
     * @param s - string to check
     * @return Returns whether s is useful
     */
    private boolean hasMeaningfulData(String s) {
        return !(s == null || s.equals("NA") || s.trim().isEmpty());
    }

    /**
     * generate care-tips
     * @return Returns caretips
     */
    public String generateCareTips() {
        StringBuilder careTips = new StringBuilder("");
        if (hasMeaningfulData(commonName)) {
            careTips.append("• This plant's common name is ").append(commonName).append(".\n");
        }
        if (hasMeaningfulData(group)) {
            careTips.append("• This plant belongs to the ").append(group).append(".\n");
        }
        if (!inferSunRequirements().equals("NA")) {
            careTips.append(inferSunRequirements());
        }
        if (hasMeaningfulData(moisture)) {
            careTips.append("• This plant has ").append(moisture.toLowerCase()).append(" moisture use.\n");
        }
        if (hasMeaningfulData(minTemp)) {
            careTips.append("• This plant will not survive at temperatures below ").append(minTemp).append("°F.\n");
        }
        if (hasMeaningfulData(pruningReq)) {
            careTips.append("• ").append(pruningReq).append(" for this plant\n");
        }
        if (!inferSoilRequirements().equals("NA")) {
            careTips.append("• This plant is adapted to grow in ").append(inferSoilRequirements()).append("soil(s).\n");
        }
        if (hasMeaningfulData(minDepth)) {
            careTips.append("• Be sure that this plant has at least ").append(minDepth).append(" inches of soil to spread its roots.\n");
        }
        if (hasMeaningfulData(duration)) {
            careTips.append("• This plant's duration class is ").append(duration).append(".\n");
        }
        if (hasMeaningfulData(growthHabit)) {
            careTips.append("• This plant's growth habit is ").append(growthHabit).append(".\n");
        }
        if (hasMeaningfulData(life)) {
            careTips.append("• This plant has a ").append(life).append(" lifespan.\n");
        }
        if (hasMeaningfulData(flowerColor)) {
            careTips.append("• This plant has ").append(flowerColor).append(" flowers.\n");
        }
        if (hasMeaningfulData(foliageColor)) {
            careTips.append("• This plant has ").append(foliageColor).append(" foliage.\n");
        }
        if (hasMeaningfulData(fruitColor)) {
            careTips.append("• This plant bears ").append(fruitColor).append(" fruit.\n");
        }
        if (hasMeaningfulData(growthRate)) {
            careTips.append("• This plant's growth rate is ").append(growthRate).append(".\n");
        }
        if (hasMeaningfulData(matureHeight)) {
            careTips.append("• This plant's mature height is ").append(matureHeight).append(" feet.\n");
        }
        if (hasMeaningfulData(pHRange)) {
            careTips.append("• This plant's soil pH should be ").append(pHRange).append(".\n");
        }
        if (hasMeaningfulData(pHRange)) {
            careTips.append("• This plant's has a ").append(salinity).append("tolerance for salty soil.\n");
        }
        if (hasMeaningfulData(bloomPeriod)) {
            careTips.append("• This plant blooms in ").append(bloomPeriod).append(".\n");
        }
        if (hasMeaningfulData(bloomPeriod)) {
            careTips.append("• This plant blooms in ").append(bloomPeriod).append(".\n");
        }
        if (hasMeaningfulData(seedBegin) && hasMeaningfulData(seedEnd)) {
            careTips.append("• This plant produces seeds starting in ").append(seedBegin).append(" and ending in ").append(seedEnd).append(".\n");
        }
        if (hasMeaningfulData(seedPersistence)) {
            careTips.append("• This plant's seeds are ");
            if (seedPersistence.equals("Yes")) {
                careTips.append("persistent.\n");
            }
            else {
                careTips.append("not persistent.\n");
            }
        }
        if (hasMeaningfulData(bloomPeriod)) {
            careTips.append("• This plant blooms in ").append(bloomPeriod).append(".\n");
        }
        if (hasMeaningfulData(vigor)) {
            careTips.append("• Seeds of this plant have a ").append(vigor).append(" probability of survival.\n");
        }
        if (hasMeaningfulData(wildAnimalPalate)) {
            careTips.append("• Wild animals find this plant ").append(wildAnimalPalate).append("ly palatable to eat.\n");
        }
        if (hasMeaningfulData(grazeAnimalPalate)) {
            careTips.append("• Domestic (grazing) animals find this plant ").append(grazeAnimalPalate).append("ly palatable to eat.\n");
        }
        if (hasMeaningfulData(humanPalate)) {
            if (humanPalate.equals("Yes")) {
                careTips.append("• Edible to humans\n");
            }
            else {
                careTips.append("• Not edible to humans\n");
            }
        }
        if (hasMeaningfulData(fertility)) {
            if (fertility.equals("High") || fertility.equals("Medium")) {
                careTips.append("• This plant must be grown in fertilized soil\n");
            }
            else {
                careTips.append("• This plant does not need to be grown in fertilized soil\n");
            }
        }
        if (hasMeaningfulData(endangered)) {
            careTips.append("• U.S. Law lists this plant as ").append(endangered).append("\n");
        }
        if (hasMeaningfulData(baseHeight)) {
            careTips.append("• This plant's height at base age is ").append(baseHeight).append(" feet.\n");
        }
        if (hasMeaningfulData(minRain) && hasMeaningfulData(maxRain)) {
            careTips.append("• In the wild, this plant receives ").append(minRain).append(" to ").append(maxRain).append(" inches of precipitation per year.\n");
        }
        return careTips.toString();
    }
}