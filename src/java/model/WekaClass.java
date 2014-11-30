package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;



/**
 *
 * @author Afik
 */
public class WekaClass {
    
    public Instances loadData(String path) throws Exception {
        Instances dat = null;
        dat = DataSource.read(path);
        dat.setClassIndex(dat.numAttributes()-1);
//        System.out.println("class index load data: " + dat.classIndex());
//        System.out.println("Instances loaded");
//        for (int i=0; i<10; i++) {
//            System.out.println("ins ke" + i + ": " + dat.instance(i).toString());
//        }
        
        return dat;
    }
    
    public Classifier loadModel(String path) throws Exception {
        Classifier classifiers;
        classifiers = (Classifier)SerializationHelper.read(path+".model");
        return classifiers;
    }
    
    // kalo make ga perlu preprocces, udah ada preprocces di sini, tp perlu remove attribute
    public void buildModel(Instances source) throws Exception{
        Instances temp = null;
        Classifier classifiers;
        FilteredClassifier fc;
        Evaluation eval;
        StringToWordVector filter;
        
        classifiers = new NaiveBayesMultinomial();
        filter = new StringToWordVector();
        fc = new FilteredClassifier();
        
        //preproccess
        temp = Preproccess(source);
        
        //filter
        filter.setAttributeIndices("first-last");
        filter.setIDFTransform(true);
        filter.setTFTransform(true);
        Tokenizer token = new WordTokenizer();
        String wd = " \n +	.,;:'\"()?!--*0123456789 /\"%” \\0 ";
        token.tokenize(wd);
        filter.setTokenizer(token);
        filter.setLowerCaseTokens(true);
        filter.setOutputWordCounts(true);
        File stopword = new File("src\\java\\resource\\stopwords_id.txt");
        filter.setStopwords(stopword);
        filter.setInputFormat(temp);
        
        //filterClassifier
        fc.setClassifier(classifiers);
        fc.setFilter(filter);
        fc.buildClassifier(temp);
        
        eval = new Evaluation(temp);
        eval.crossValidateModel(fc, temp, 10, new Random(1));
        SerializationHelper.write("src\\java\\resource\\bayes.model", classifiers);
    }
    
    public void reBuildModel(String trainFile) {
        
    }
    
    public Instances RemoveAttribute(Instances source) throws Exception {
        Instances res = null;
        int[] idx = new int[2];
        
        for (int i=0; i<source.numAttributes(); i++) {
            if(source.attribute(i).name().equals("ID_KELAS")) {
                idx[1] = i;
            }
            else if (source.attribute(i).name().equals("FULL_TEXT")) {
                idx[0] = i;
            }
        }
//        idx[0] = 2;
//        idx[1] = 13;
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(idx);
        remove.setInvertSelection(true);
        remove.setInputFormat(source);
        res = Filter.useFilter(source,remove);
        res.setClassIndex(res.numAttributes()-1);
        return res;
    }
    
    
    public Instances Preproccess (Instances source) throws Exception {
        Instances temp1 = null;
        Instances res = null;
        NumericToNominal ntn = new NumericToNominal();
        NominalToString nts = new NominalToString();
        StringToWordVector stw = new StringToWordVector();
        
//        //Numeric to Nominal
//        ntn.setInputFormat(source);
//        String[] opt1 = new String[2];
//        opt1[0] = "-R";
//        opt1[1] = "last";
//        temp1 = Filter.useFilter(source,ntn);
        
        System.out.println("After Numeric To nominal");
        for (int i=0; i<temp1.numAttributes(); i++) {
            System.out.print(temp1.attribute(i).name());
            System.out.print(",");
        }
        System.out.println();
        for (int i=0; i<10; i++) {
            System.out.println("ins ke" + i + ": " + temp1.instance(i).classValue());
        }
        
        //Nominal to String
        String[] opt2 = new String[2];
        opt2[0] = "-C";
        opt2[1] = "first";
        nts.setOptions(opt2);
        nts.setInputFormat(temp1);
        res = Filter.useFilter(temp1, nts);
        
        System.out.println("After NOminal To String");
        for (int i=0; i<res.numAttributes(); i++) {
            System.out.print(res.attribute(i).name());
            System.out.print(",");
        }
        System.out.println();
        for (int i=0; i<10; i++) {
            System.out.println("ins ke" + i + ": " + res.instance(i).classValue());
        }
        
        return res;
    }
    
    public void classify (Instances dataTest) throws Exception {
        Classifier cl = loadModel("src\\java\\resource\\bayes");
        ArrayList<Integer> listWrong = new ArrayList<Integer>();
        
        
        System.out.println("Before preproccess");
        for (int i=0; i<dataTest.numAttributes(); i++) {
            System.out.print(dataTest.attribute(i).name());
            System.out.print(",");
        }
        System.out.println();
        for (int i=0; i<10; i++) {
            System.out.println("ins ke" + i + ": " + dataTest.instance(i).classValue());
        }
            
        //preproccess
        Instances temp = RemoveAttribute(dataTest);
        System.out.println("After remove attr");
        System.out.println("class index: " + temp.classIndex());
        for (int i=0; i<temp.numAttributes(); i++) {
            System.out.print(temp.attribute(i).name());
            System.out.print(",");
        }
        System.out.println();
        for (int i=0; i<10; i++) {
            System.out.println("ins ke" + i + ": " + temp.instance(i).classValue());
        }
        
        temp = Preproccess(temp);
        
        System.out.println("After preproccess");
        System.out.println("class index: " + temp.classIndex());
        for (int i=0; i<temp.numAttributes(); i++) {
            System.out.print(temp.attribute(i).name());
            System.out.print(",");
        }
        System.out.println();
        for (int i=0; i<10; i++) {
            System.out.println("ins ke" + i + ": " + temp.instance(i).classValue());
        }
        
        System.out.println("After Preproccess: ");
        System.out.println("temp class index: " + temp.classIndex());
        
        
        System.out.println("--- Do classify");
        System.out.println("class index: " + temp.classIndex());
        
        for (int i = 0; i<temp.numInstances(); i++) {
            double actual = temp.instance(i).classValue();
            double pred = cl.classifyInstance(temp.instance(i));
            System.out.println(i);
            System.out.println("pred: " + pred + "actual: " + actual);
            if (pred!=actual) {
                listWrong.add(i);
            }
        }
        for (int i = 0; i<listWrong.size(); i++) {
            System.out.println(listWrong.get(i));
        }
    }
    
    public static void main (String[] args) throws Exception {
        WekaClass weka = new WekaClass();
        Instances inst;
        Classifier classifiers;
        
        inst = weka.loadData("src\\java\\resource\\testFile.csv");
        
        
//        for (int i=0; i<inst.numAttributes(); i++){
//            System.out.println("attr_name:" +inst.attribute(i).name());
//            //System.out.println(inst.attribute(i).isString());
//        }
//        System.out.println(inst.classIndex());
        
        weka.classify(inst);
        
        //inst = weka.Preproccess(inst);
        //weka.buildModel(inst);
    }
}
