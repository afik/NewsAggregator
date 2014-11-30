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
//        System.out.println("class index load data: " + dat.classIndex() + ", name :" + dat.attribute(dat.classIndex()).name() + ", isNominal: " + dat.attribute(dat.classIndex()).isNominal());
        System.out.println("Instances loaded");
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
    
    // kalo make ga perlu preprocces, udah ada preprocces di sini, 
    // tp perlu remove attribute kalo pake testData
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
        
        System.out.println("After Filter");
        for (int i=0; i<temp.numAttributes(); i++) {
            System.out.print(temp.attribute(i).name());
            System.out.print(",");
        }
        System.out.println();
        for (int i=0; i<10; i++) {
            System.out.println("ins ke" + i + ": " + temp.instance(i).toString());
        }
        for (int i =0; i<temp.numAttributes(); i++){
            System.out.println("attr : "+ temp.attribute(i).name() + "isstring : " + temp.attribute(i).isString() );
        }
        
        eval = new Evaluation(temp);
        eval.crossValidateModel(fc, temp, 10, new Random(1));
        System.out.println(eval.toSummaryString());
        SerializationHelper.write("src\\java\\resource\\bayes.model", fc);
    }
    
    public void reBuildModel(String trainFile) {
        
    }
    
    //cuman ambil atribut yg namanya FULL_TEXT sama LABEL
    public Instances RemoveAttribute(Instances source) throws Exception {
        Instances res = null;
        int[] idx = new int[2];
        
        for (int i=0; i<source.numAttributes(); i++) {
            if(source.attribute(i).name().equals("LABEL")) {
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
        
        System.out.println("After Remove Attribute");
//        for (int i=0; i<res.numAttributes(); i++) {
//            System.out.print(res.attribute(i).name());
//            System.out.print(",");
//        }
//        System.out.println();
//        for (int i=0; i<res.numInstances(); i++) {
//            System.out.println("ins ke" + i + ": " + res.instance(i).toString());
//        }
//        for (int i =0; i<res.numAttributes(); i++){
//            System.out.println("attr : "+ res.attribute(i).name() + "isnominal : " + res.attribute(i).isNominal() );
//        }
        
        return res;
    }
    
    
    //Nominal To string doang
    public Instances Preproccess (Instances source) throws Exception {
        Instances res = null;
        //NumericToNominal ntn = new NumericToNominal();
        NominalToString nts = new NominalToString();
        
        //Nominal to String
        String[] opt2 = new String[2];
        opt2[0] = "-C";
        opt2[1] = "first";
        nts.setOptions(opt2);
        nts.setInputFormat(source);
        res = Filter.useFilter(source, nts);
        
        System.out.println("After Nominal To String");
//        for (int i=0; i<res.numAttributes(); i++) {
//            System.out.print(res.attribute(i).name());
//            System.out.print(",");
//        }
//        System.out.println();
//        for (int i=0; i<10; i++) {
//            System.out.println("ins ke" + i + ": " + res.instance(i).toString());
//        }
//        for (int i =0; i<res.numAttributes(); i++){
//            System.out.println("attr : "+ res.attribute(i).name() + "isstring : " + res.attribute(i).isString() );
//        }
        
        return res;
    }
    
    //load model sm preproccess udah disini
    public void classify (Instances dataTest) throws Exception {
        Classifier cl = loadModel("src\\java\\resource\\bayes");
        ArrayList<Integer> listWrong = new ArrayList<Integer>();
      
            
        //preproccess
        Instances temp = RemoveAttribute(dataTest);
        
        temp = Preproccess(temp);
        
        System.out.println("--- Do classify");
        System.out.println("class index: " + temp.classIndex());
        
        for (int i = 0; i<temp.numInstances(); i++) {
            String actual = temp.instance(i).stringValue(1);
            double ipred = cl.classifyInstance(temp.instance(i));
            System.out.println("ipred: " + ipred);
            String pred = temp.classAttribute().value((int)ipred);
            System.out.println(i);
            System.out.println("pred: " + pred + ", actual: " + actual);
            if (!pred.equals(actual)) {
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
        
        //inst = weka.RemoveAttribute(inst);
        //weka.buildModel(inst);
    }
}
