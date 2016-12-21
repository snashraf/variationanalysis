package org.campagnelab.dl.genotype.mappers;

import org.campagnelab.dl.framework.mappers.ConfigurableFeatureMapper;
import org.campagnelab.dl.genotype.predictions.GenotypePrediction5Out;
import org.campagnelab.dl.varanalysis.protobuf.BaseInformationRecords;

import java.util.Properties;

/**
 * Label that encodes how many distinct alleles are present in a sample. Assume two alleles are present and ploidy is 2.
 * <p>
 * This mapper will produce [ 0 1 ]. With one allele and ploidy=2: [ 1 0 ].
 * With ploidy=3 and three alleles: [0 0 1]
 * Created by fac2003 on 12/20/16.
 */
public class NumDistinctAllelesLabelMapper extends CountSortingLabelMapper implements ConfigurableFeatureMapper {
    protected int ploidy;

    public NumDistinctAllelesLabelMapper(boolean sortCounts) {
        super(sortCounts);
    }

    @Override
    public int numberOfLabels() {
        return ploidy;
    }

    @Override
    public float produceLabel(BaseInformationRecords.BaseInformation record, int labelIndex) {
        final String trueGenotype = record.getTrueGenotype();
        return label(labelIndex, trueGenotype);
    }

    protected float label(int labelIndex, String trueGenotype) {
        int numDistinctAlleles = GenotypePrediction5Out.alleles(trueGenotype).size();
        return (labelIndex == numDistinctAlleles - 1) ? 1f : 0f;
    }

    @Override
    public void configure(Properties readerProperties) {
        String value = readerProperties.getProperty("genotypes.ploidy");
        try {
            ploidy = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Unable to read ploidy from sbi properties file.");
        }
    }
}
