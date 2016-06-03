package org.campagnelab.dl.varanalysis.learning.iterators;

import org.campagnelab.dl.varanalysis.learning.features.Features;
import org.campagnelab.dl.varanalysis.learning.mappers.QualityFeatures;
import org.campagnelab.dl.varanalysis.protobuf.BaseInformationRecords;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static org.junit.Assert.assertEquals;

/**
 * Created by rct66 on 6/2/16.
 */
public class QualityFeatureCalculatorTest {

    /**
     * Check that features generated to DL4J inputs are the same as features generated with produceFeature:
     */

    float gf;
    float gr;
    float sf;
    float sr;

    @Test
    public void makeCheckFeatures() {

        QualityFeatures calc = new QualityFeatures();
        BaseInformationRecords.BaseInformation record = prepareRecord();
        calc.prepareToNormalize(record, 0);
        Features features = new Features(calc.numberOfFeatures());
        for (int i = 0; i < calc.numberOfFeatures(); i++) {
            features.setFeatureValue(calc.produceFeature(record, i), i);

        }
        features.setFeatureValue(gr, 0);
        features.setFeatureValue(gf, 1);
        features.setFeatureValue(sr, 10);
        features.setFeatureValue(sf, 11);
        INDArray inputs = Nd4j.zeros(1, calc.numberOfFeatures());
        calc.mapFeatures(record, inputs, 0);
        Features featuresFromArray=new Features(inputs,1);
        assertEquals(features, featuresFromArray);
        System.out.println(features);
    }

    private BaseInformationRecords.BaseInformation prepareRecord() {
        BaseInformationRecords.BaseInformation.Builder builder = BaseInformationRecords.BaseInformation.newBuilder();
        builder.setPosition(1);
        builder.setReferenceIndex(0);
        builder.setReferenceBase("A");


        //germline counts
        BaseInformationRecords.SampleInfo.Builder sampleBuilder = BaseInformationRecords.SampleInfo.newBuilder();
        BaseInformationRecords.CountInfo.Builder builderInfo = BaseInformationRecords.CountInfo.newBuilder();
        builderInfo.setFromSequence("A");
        builderInfo.setToSequence("C");
        builderInfo.setMatchesReference(true);
        builderInfo.setGenotypeCountForwardStrand(100);
        builderInfo.setGenotypeCountReverseStrand(100);
        builderInfo.addQualityScoresForwardStrand(5);
        builderInfo.addQualityScoresForwardStrand(15);
        builderInfo.addQualityScoresReverseStrand(10);
        builderInfo.addQualityScoresReverseStrand(60);
        sampleBuilder.addCounts(builderInfo.build());
        builder.addSamples(sampleBuilder.build());

        gf = (float)((getError(5)+getError(15))/2);
        gr = (float)((getError(10)+getError(60))/2);

        //somatic counts
        BaseInformationRecords.SampleInfo.Builder sampleBuilderS = BaseInformationRecords.SampleInfo.newBuilder();
        sampleBuilderS.setIsTumor(true);
        BaseInformationRecords.CountInfo.Builder builderInfoS = BaseInformationRecords.CountInfo.newBuilder();
        builderInfoS.setFromSequence("A");
        builderInfoS.setToSequence("T");
        builderInfoS.setMatchesReference(true);
        builderInfoS.setGenotypeCountForwardStrand(5);
        builderInfoS.setGenotypeCountReverseStrand(5);
        builderInfoS.addQualityScoresForwardStrand(20);
        builderInfoS.addQualityScoresForwardStrand(1);
        builderInfoS.addQualityScoresForwardStrand(25);
        builderInfoS.addQualityScoresReverseStrand(27);
        builderInfoS.addQualityScoresReverseStrand(0);
        builderInfoS.addQualityScoresReverseStrand(60);
        sampleBuilderS.addCounts(builderInfoS.build());
        builder.addSamples(sampleBuilderS.build());


        sf = (float)((getError(20)+getError(1)+getError(25))/(double)3);
        sr = (float)((getError(27)+getError(0)+getError(60))/(double)3);


        return builder.build();

    }

    double getError (int p) {
        return Math.pow((double)10,-((double)p/(double)10));
    }
}
