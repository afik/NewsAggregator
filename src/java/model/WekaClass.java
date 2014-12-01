package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
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
    public double percentCorrect;
    
    //load data train/test, full path
    public Instances loadData(String path) throws Exception {
        Instances dat = null;
        dat = DataSource.read(path);
        dat.setClassIndex(dat.numAttributes()-1);
        System.out.println("Instances loaded");

        return dat;
    }
    
    //load model, nama modelnya aja ga perlu .model
    public Classifier loadModel(String path) throws Exception {
        Classifier classifiers;
        classifiers = (Classifier)SerializationHelper.read(path+".model");
        return classifiers;
    }
    
    // kalo make ga perlu preprocces, udah ada preprocces di sini, 
    // tp perlu remove attribute kalo pake testData
    public void buildModel(Instances source) throws Exception{
        System.out.println("Load model....");
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
        WordTokenizer token = new WordTokenizer();
        String wd = " \n +	.,;:'\"()?!--*0123456789` /\"%” \\0 ";
        token.setDelimiters(wd);
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
        System.out.println(eval.toSummaryString());
        SerializationHelper.write("src\\java\\resource\\news.model", fc);
    }
    
    //add new train yg baru ke file training, ntar rebuildnya tinggal build
    public void addToFile(ArrayList<String> NewTrain) throws IOException {
        FileWriter pw = new FileWriter("src\\java\\resource\\trainFile.csv", true);
        for(String s:NewTrain){
            System.out.println(s);
            pw.append(s);
            pw.append("\n");
        }
        pw.flush();
        pw.close();
    }
    
    //ambil instance ke idx
    public ArrayList<String> getNewTrain(ArrayList<Integer> idx, Instances source) throws Exception {
        ArrayList<String> res = new ArrayList<String>();
        for (int i=0; i<source.numInstances(); i++){
            for (Integer idx1 : idx) {
                if (i == idx1) {
                    res.add(source.instance(i).toString());
                }
            }
        }
        return res;
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
    public ArrayList<Integer> classifyCSV (Instances dataTest, Instances dataTrain) throws Exception {
        System.out.println("Clasify CSV......");
        Classifier cl = loadModel("src\\java\\resource\\news");
        ArrayList<Integer> listWrong = new ArrayList<Integer>();
        ArrayList<String> outputString = new ArrayList<String>();
        Instances rawDataTest = dataTest;
        
        //bikin list label
        ArrayList<String> listLabel = new ArrayList();
        for (int i=0; i<dataTrain.classAttribute().numValues(); i++){
            listLabel.add(i, dataTrain.classAttribute().value(i));
        }
        
        //preproccess
        Instances temp = RemoveAttribute(dataTest);
        
        temp = Preproccess(temp);
        
        System.out.println("--- Do classify");
        for (int i = 0; i<temp.numInstances(); i++) {
            String actual = temp.classAttribute().value((int) temp.instance(i).classValue()); //Ngeluarin label bertipe string dari actual
            double actualDouble = listLabel.indexOf(actual); //mencari index dari label actual di listLabel
            double ipred = cl.classifyInstance(temp.instance(i));
            String label = listLabel.get((int)ipred);
            outputString.add(label);
            if (ipred!=actualDouble) {
                listWrong.add(i);
            }
        }
        
        percentCorrect = 100-(((double)listWrong.size()/(double)temp.numInstances())*100);
        System.out.println("percentCorrect : " + percentCorrect + "%");
        
        writePrediction(rawDataTest, outputString);
        
        return listWrong;
    }
    
    //classify text
    public String classifyIns (Instances inst, Instances dataTrain) throws Exception {
        Classifier cl = loadModel("src\\java\\resource\\bayes");
        Instances temp =null;
        
        //bikin list label
        ArrayList<String> listLabel = new ArrayList();
        for (int i=0; i<dataTrain.classAttribute().numValues(); i++){
            listLabel.add(i, dataTrain.classAttribute().value(i));
        }
        
        //preproccess
        temp = Preproccess(inst);
        
        System.out.println("--- Do classify");
        double ipred = cl.classifyInstance(temp.instance(0));
        String label = listLabel.get((int)ipred);
        return label;
    }
    
    //bikin instance dari file text
    public Instances makeInstances(String text) {
        //Create instance form text
        FastVector fvNominalVal = new FastVector(2); 
        fvNominalVal.addElement("Hiburan"); 
        fvNominalVal.addElement("Pendidikan");
        fvNominalVal.addElement("Politik");
        fvNominalVal.addElement("Hukum dan Kriminal"); 
        fvNominalVal.addElement("Sosial Budaya");
        fvNominalVal.addElement("Olahraga"); 
        fvNominalVal.addElement("Teknologi dan Sains");
        fvNominalVal.addElement("Ekonomi dan Bisnis");
        fvNominalVal.addElement("Kesehatan");
        fvNominalVal.addElement("Bencana dan Kecelakaan");
        Attribute attribute2 = new Attribute("LABEL", fvNominalVal); 
        Attribute attribute1 = new Attribute("FULL_TEXT",(FastVector) null);
        // Create list of instances with one element 
        FastVector fvWekaAttributes = new FastVector(2); 
        fvWekaAttributes.addElement(attribute1); 
        fvWekaAttributes.addElement(attribute2); 
        Instances instances = new Instances("Test relation", fvWekaAttributes, 1); 
        // Set class index 
        instances.setClassIndex(1); 
        // Create and add the instance 
        DenseInstance instance = new DenseInstance(2); 
        instance.setValue(attribute1, text); 
        // instance.setValue((Attribute)fvWekaAttributes.elementAt(1), text); 
        instances.add(instance);
        return instances;
    }
    
    public void writePrediction(Instances dataSource, ArrayList<String> result) throws IOException {
        System.out.println("write Prediction to CSV");
        FileWriter fw = new FileWriter("src\\java\\resource\\output.csv");
        String s = "";
       
        if (dataSource.numInstances()==result.size()){
            fw.append("ID_ARTIKEL,LABEL\n");
            for(int i=0; i<result.size(); i++){
                //System.out.println(i);
                System.out.println((int)dataSource.instance(i).value(dataSource.attribute(1)) + "," + result.get(i));
                s = (int)dataSource.instance(i).value(dataSource.attribute(1)) + "," + result.get(i);
                fw.append(s);
                fw.append("\n");
            }
            fw.flush();
            fw.close();
        }
        else {
            System.out.println("Ukuran ngga sama!");
        }
        
    }
    
    public static void main (String[] args) throws Exception {
        WekaClass weka = new WekaClass();
        Instances inst;
        Instances dataTest = weka.loadData("src\\java\\resource\\news_aggregator_full.csv");
        Instances dataTrain = weka.loadData("src\\java\\resource\\news_aggregator_text_label.csv");
        Classifier classifiers;
        
        ///instance dari text
        String text;
        text = "Metrotvnews.com, Surabaya: Djarum Black Autoblackthrough (ABT) tahun 2013 memulai seri pembuka di Surabaya 13-14 April. Kota yang tak pernah lepas dari gelaran ABT ini selalu menampilkan tren modifikasi paling terbaru dan selalu menjadi magnet bagi kota-kota di sekitaran seperti� Malang, Yogyakarta, Solo hingga Bali.\\nTren modifikasi di Autoblackthrough Surabaya setiap tahunnya selalu berubah. Beberapa tahun lalu sempat tampil model elegan hingga terakhir gelaran adalah aliran street racing. Hal inilah yang membuat pesertanya tak pernah surut.\\nDipastikan sudah lebih dari 100 perserta ikut ambil bagian di Gramedia Expo. Tak salah bila Djarum Black Autoblackthrough 2013 masih menjadi trend setter dan `The Hottest Hi Tech Modified Motorshow`.\\n\\\"Autoblackthrough merupakan sebuah ajang lomba dan pamer kehebatan modifikasi otomotif paling inovatif, kreatif dan ekstrem di tanah air. Program ini ditujukan untuk memotivasi para peserta untuk mengeluarkan hobi, self expression, eksistensi diri dan komersil dalam mempromosikan bengkel modifikasinya,\\\" ujar Raymond Portier, Brand Manager Djarum Black.\\nAutoblackthrough Surabaya tak sekadar menggelar kontes modifikasi mobil, namun juga menantang para penggemar performa mesin melalui kontes Dyno Attraction. Djarum Black mendatangkan mesin dinamometer MainLine DynoLog yang khusus didatangkan dari Sydney, Australia.\\nMainline DynoLog merupakan alat ukur performa mesin (horse power dan torsi) atau disebut dinamometer berstandar internasional yang menghitung langsung performa mesin melalui penggerak roda sistem 2WD dan 4WD.\\n\\\"Keandalan para engine tuner Indonesia akan dibuktikan oleh mesin ukur Mainline DynoLog yang didatangkan khusus untuk ajang Djarum Black Autoblackthrough ini. Mainline DynoLog dari Australia yakni mesin dinamo meter yang mampu mengukur Horse Power (HP) pada roda mobil dengan penggerak sistem 2WD dan 4WD,\\\" yakin Raymond.\\nUntuk sesi Black Out Loud di Autoblackthrough ini terdapat kompetisi SQ, SQL dan SPL. Autoblackthrough tahun ini menggandeng EASCA (European Auto Sound Association), asosiasi audio mobil eropa yang bermarkas di Jerman dan memiliki regulasi sejumlah sistem penjurian yang digunakan di seluruh dunia.\\nSaat penilaian SPL Autoblackthrough 2013 pihak juri menggunakan CD Blackxperience.com Soundtraxx� dan alat ukur Term Lab yang didatangkan langsung dari Amerika, sedangkan SQ Autoblackthrough 2013 menggunakan CD dan sistem penjurian EASCA.\\nSementara untuk SQL (Sound Quality Loud) juga dihadirkan dalam kontes Black Out Loud. Saat penjurian menggunakan dua buah CD berbeda. CD pertama adalah BlackXperience.com Soundtraxx dan CD kedua merupakan standar EASCA.\\nMeramaikan gelaran ABT Surabaya sepanjang dua hari, para pengunjung dan peserta akan dimanjakan oleh hiburan musik dari band terkenal dan female DJ Djarum Black secara spesial mendatangkan band terkenal seperti NAIF. Kemudian Spinach Candies serta Girlkhana dan Female DJ Chantal Dewi 1945MF. ";
        //inst = weka.makeInstances(text);
       
        ///print atribut dan instance
//        for (int i=0; i<inst.numAttributes(); i++){
//            System.out.println("attr_name:" +inst.attribute(i).name());
//            //System.out.println(inst.attribute(i).isString());
//        }
//        System.out.println(inst.classIndex());
//        for (int i=0; i<inst.numInstances(); i++) {
//            System.out.println("ins ke" + i + ": " + inst.instance(i).toString());
//        }
        
        //inst = weka.RemoveAttribute(inst);
        
        weka.buildModel(dataTrain);
        
//        Instances toCSV = weka.RemoveAttribute(dataTrain);
//        ArrayList<Integer> idxWrong = new ArrayList<Integer>();
//        ArrayList<String> st = null;
//        //idxWrong.add(1);
//        //idxWrong.add(8);
//        idxWrong = weka.classifyCSV(dataTest, dataTrain);
//       
//        st = weka.getNewTrain(idxWrong, toCSV);
//        for (String s:st) {
//            System.out.println(s);
//        }
        
        //weka.addToFile(st);
        //Instances newTrain = weka.loadData("src\\java\\resource\\trainFile.csv");
        //weka.buildModel(newTrain);
        
    }
}
